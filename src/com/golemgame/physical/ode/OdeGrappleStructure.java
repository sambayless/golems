package com.golemgame.physical.ode;

import java.util.Collection;
import java.util.Map;

import org.odejava.IllegalOdejavaOperation;

import com.golemgame.constructor.Updatable;
import com.golemgame.functional.component.BComponent;
import com.golemgame.functional.component.BGenericInput;
import com.golemgame.functional.component.BGenericSource;
import com.golemgame.functional.component.BMind;
import com.golemgame.model.spatial.shape.LineModel;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.Reference;
import com.golemgame.mvc.golems.GrappleInterpreter;
import com.golemgame.mvc.golems.WirePortInterpreter;
import com.golemgame.physical.PhysicsComponent;
import com.golemgame.physical.ode.compile.OdePhysicsEnvironment;
import com.golemgame.states.physics.ode.PhysicsSpatialMonitor;
import com.golemgame.states.physics.ode.PhysicsSpatialMonitor.PhysicsCollisionListener;
import com.golemgame.structural.collision.CollisionMember;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.state.AlphaState;
import com.jme.system.DisplaySystem;
import com.jmex.physics.CollisionGroup;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.PhysicsSpatial;
import com.jmex.physics.PhysicsUpdateCallback;
import com.jmex.physics.contact.ContactInfo;
import com.jmex.physics.geometry.PhysicsRay;
import com.jmex.physics.impl.ode.OdeCollisionGroup;
import com.jmex.physics.impl.ode.OdePhysicsSpace;
import com.jmex.physics.impl.ode.geometry.OdeRay;

public class OdeGrappleStructure extends OdeCapsuleStructure{
	private final GrappleInterpreter interpreter;

	public OdeGrappleStructure(PropertyStore store) {
		super(store);
		interpreter = new GrappleInterpreter(store);
		collisionColor = interpreter.getBeamPullColor();
		normalColor = interpreter.getBeamPushColor();

	}
	
	private PhysicsRay physicsRay;
	private float forceScalar = 0f;
	private float maxForce=0;
	private float maxDistance=0;
	private ColorRGBA collisionColor = ColorRGBA.red;
	private ColorRGBA normalColor = ColorRGBA.blue;
	
	
	
//	private BGenericSource forceMeter=null;
	private BGenericSource distanceMeter=null;
	
	@Override
	public void buildMind(BMind mind, Map<OdePhysicalStructure, PhysicsNode> physicalMap, Map<Reference, BComponent> wireMap, OdePhysicsEnvironment environment) {
		 maxForce = interpreter.getMaxForce();
		maxDistance = interpreter.getMaxDistance();
		BGenericInput forceInput = new BGenericInput(){

			@Override
			protected float signalRecieved(float signal,float time) {
				forceScalar= signal;
				if( line!=null)
				{
					line.setVisible(forceScalar != 0f);
				}
				return 0;
			}
			
		};	
		mind.addComponent(forceInput);

	/*	 forceMeter = new BGenericSource("Force Output");
		 forceMeter.setNormalizingMax(1f);
		 forceMeter.setNormalizingMin(-1f);

		 
		WirePortInterpreter forceOut = new WirePortInterpreter(interpreter.getForceOutput());
		wireMap.put(forceOut.getID(), forceMeter);
		forceMeter.setLastingObservations(false);
		mind.addComponent(forceMeter);*/
		
		
		 distanceMeter = new BGenericSource("Distance Output");
			WirePortInterpreter distance = new WirePortInterpreter(interpreter.getDistanceOutput());
			wireMap.put(distance.getID(), distanceMeter);
			distanceMeter.setLastingObservations(false);
			distanceMeter.setNormalizingMax(1f);
			distanceMeter.setNormalizingMin(-1f);
			distanceMeter.setSensorObservation(0f);
			mind.addComponent(distanceMeter);
		//WirePortInterpreter out = new WirePortInterpreter(interpreter.getOutput());
		WirePortInterpreter in = new WirePortInterpreter(interpreter.getInput());
		
		//wireMap.put(out.getID(), source);
		wireMap.put(in.getID(), forceInput);
		
	//	mind.addSource(forceMeter);
		mind.addSource(distanceMeter);
		
		super.buildMind(mind, physicalMap, wireMap, environment);
	}

	private static final Vector3f _normal = new Vector3f();
	private static final Vector3f _position1= new Vector3f();
	private static final Vector3f _position2= new Vector3f();
	
	private float closestCollisionSqr = Float.POSITIVE_INFINITY;
	private PhysicsCollisionGeometry closestOtherPhysics = null;
	private Vector3f closestNormal = new Vector3f();
	private Vector3f closestPosition = new Vector3f();
	
	private Vector3f closestPositionRelOther = new Vector3f();
	private Vector3f closestPositionRelThis = new Vector3f();
	
	private void processCollision(PhysicsCollisionGeometry otherGeom, ContactInfo info) {
		
		//change this to wait until all collisions have been processed and then select only the closest collision.
		
		Vector3f collisionPosition =  info.getContactPosition(_position1);
		
		float distSqr = collisionPosition.subtract(physicsRay.getWorldTranslation(),_position2).lengthSquared();
		if (distSqr < closestCollisionSqr)
		{
			closestCollisionSqr = distSqr;
			info.getContactNormal(closestNormal);
			closestPosition.set(collisionPosition);
			closestOtherPhysics = otherGeom;
	//		System.out.println(closestCollisionSqr);
			closestPositionRelOther.set(closestPosition).subtractLocal(otherGeom.getWorldTranslation());
			closestPositionRelThis.set(closestPosition).subtractLocal(physicsRay.getWorldTranslation());
			
			if(closestOtherPhysics == info.getGeometry1())
			{
				//force is against normal
				closestNormal.multLocal(-1f);
			}else{
				//force is with normal
				
			}
			
	
			//	forceMeter.setSensorObservation(forceScalar);
				distanceMeter.setSensorObservation(1f - FastMath.sqrt(closestCollisionSqr)/maxDistance);
				
		
			
		}

	
		
	}
	private boolean collisionApplied = false;
	private static Vector3f _tempForce = new Vector3f();
	private void applyCollision()
	{
		
			Vector3f normal =  _tempForce.set(forceNormal).multLocal(forceScalar * maxForce * -1f);//closestNormal;
			
			thisPhysics.getWorldRotation().multLocal(normal);
			
		//	System.out.println(forceScalar * maxForce );
		
			
		//	boolean forceApplied = false;
			
			if(!thisPhysics.isStatic())
			{
			
				((DynamicPhysicsNode)thisPhysics).addForce(normal,forcePosition);
				
				//((DynamicPhysicsNode)thisPhysics).addForce(normal.multLocal(-1f),closestPositionRelThis.subtract(physicsRay.getLocalTranslation()));
				//forceApplied = true;
			}
			
			if(closestOtherPhysics!=null && closestOtherPhysics != physicsRay)
			{
				
				collisionApplied = true;
			//	System.out.println("Collide");
				if(!closestOtherPhysics.getPhysicsNode().isStatic())
				{
					//otherPhysics.getWorldRotation().multLocal(position1);
					
					((DynamicPhysicsNode)closestOtherPhysics.getPhysicsNode()).addForce(normal.multLocal(-1f),closestPositionRelOther.subtractLocal(closestOtherPhysics.getLocalTranslation()));
				//	forceApplied = true;
				}
	
	
		
				
		
			}
			
			
		
	}
	
	private LineModel line;
	@Override
	public void buildCollidable(CollisionMember collidable) {
		super.buildCollidable(collidable);
		
		
	//	line.getSpatial().setCullMode(SceneElement.CULL_NEVER);
	}

	private PhysicsNode thisPhysics;
	private Vector3f forceNormal = new Vector3f();
	private Vector3f forcePosition = new Vector3f();
	@Override
	public float buildCollisionGeometries(final PhysicsNode physicsNode, Collection<PhysicsComponent> components, Vector3f store) {
		float mass = super.buildCollisionGeometries(physicsNode, components, store);
	

		thisPhysics = physicsNode;
	
		
		return mass;
	}

	private Updatable particleUpdate;
	@Override
	public void buildRelationships(Map<OdePhysicalStructure, PhysicsNode> physicsMap, OdePhysicsEnvironment compiledEnvironment) {
		super.buildRelationships(physicsMap, compiledEnvironment);
		
		//not used any more
		final boolean ignoreStatics = false;// interpreter.isIgnoreStatics();
		
		Vector3f laserStart = new Vector3f();
		laserStart.set(super.physicsModel.getLocalTranslation());
		Vector3f offset = new Vector3f();
		Vector3f extraLaserOffset = new Vector3f();
		extraLaserOffset.x = 0.005f;
	
		offset.x = super.getInterpreter().getHeight()/2f + super.getInterpreter().getRadius();//physicsModel.getLocalScale().x/2f + 0.01f;		
		super.physicsModel.getLocalRotation().multLocal(offset);
		
		super.physicsModel.getLocalRotation().multLocal(extraLaserOffset);
		forcePosition.set(laserStart).addLocal(offset);
		forceNormal.set(Vector3f.UNIT_X);
		super.physicsModel.getLocalRotation().multLocal(forceNormal);
		
		/*	 line = new LineModel();

		//		line.updateModelData();
		//		line.updateWorldData();
				line.setLineLength(10f);
		
			//	super.getStructuralAppearanceEffect().applyEffect(line);
				physicsNode.attachChild(line.getSpatial());*/
		
			physicsRay = thisPhysics.createRay("Grapple Ray");
		/*	OdePhysicsComponent comp = new OdePhysicsComponent(physicsRay,null);
			comp.setMass(0.001f);
			components.add(comp);
			*/
			
			
			
			
			thisPhysics.attachChild(physicsRay);
			if(thisPhysics.isStatic())
			{
				((OdePhysicsSpace)thisPhysics.getSpace()).removeGeom(((OdeRay)physicsRay).getOdeGeom(), ((OdePhysicsSpace)thisPhysics.getSpace()).getStaticCollisionGroup());
			
			}
		//	if(!thisPhysics.isStatic())
				((OdePhysicsSpace)thisPhysics.getSpace()).addGeom(((OdeRay)physicsRay).getOdeGeom(),(OdeCollisionGroup) compiledEnvironment.getRayGroup());

			
		/*	try{
				((OdePhysicsSpace)physicsNode.getSpace()).addGeom(((OdeRay)physicsRay).getOdeGeom(), ((OdePhysicsSpace)physicsNode.getSpace()).getDefaultCollisionGroup());

			}catch(IllegalOdejavaOperation o)
			{
				System.out.println(o);
				//this is ok
			}*/
		
			physicsRay.getLocalTranslation().set(laserStart.add(offset).add(extraLaserOffset)); //super.physicsModel.getLocalTranslation()).x += super.physicsModel.getLocalScale().x/2f + 0.01f;
			
			//	physicsRay.getLocalTranslation().y = 2;
			//physicsRay.getLocalTranslation().set(physicsNode.getWorldTranslation());
			physicsRay.getLocalScale().set(interpreter.getMaxDistance()-0.01f ,0,0);
			physicsRay.getLocalRotation().set(super.physicsModel.getLocalRotation());
			PhysicsSpatialMonitor.getInstance().registerGhost(physicsRay);
			PhysicsSpatialMonitor.getInstance().addPhysicsCollisionListener(physicsRay, new PhysicsCollisionListener()
			{
				public void collisionOccured(ContactInfo info, PhysicsSpatial source) {
				 // System.out.println("Collision" + Math.random());
					  
					  final PhysicsCollisionGeometry otherGeom = (info.getGeometry1()==physicsRay?info.getGeometry2():info.getGeometry1());
					  //have to grab the geom, because for some configurations of static/default group for physics ray the physics node is null
					  if(otherGeom == null || otherGeom == physicsRay || otherGeom == OdeGrappleStructure.super.collision) //yes this occasionally happens at certain angles, but it is rare
						  return;
					  
					  final PhysicsNode otherPhysics = otherGeom.getPhysicsNode();
					//  if(otherPhysics == thisPhysics || otherPhysics == null)//it would be better if these were not found in the first place....
					//	  return;
					  
				
					  
		          /*    if(ignoreStatics && otherPhysics.isStatic())
		            	  return;
		              */
		        
		        	  if(PhysicsSpatialMonitor.getInstance().isGhost(otherGeom))
						  return;//ignore ghosts
		              
		              processCollision(otherGeom,info);
		        
		        }

		
		    });
		
		
		
		
		
		if(interpreter.isBeamEnabled())
		{
		
			line = new LineModel();
			line.setVisible(false);
			line.updateModelData();
			line.updateWorldData();
			line.setLineLength(interpreter.getMaxDistance());
			line.setLineColor(normalColor);
			
			//offset.zero();
			//offset.x = physicsModel.getLocalScale().x/2f + 0.01f;
		//	super.physicsModel.getLocalRotation().multLocal(offset);
			
			
		//	super.getStructuralAppearanceEffect().applyEffect(line);
			thisPhysics.attachChild(line.getSpatial());
	
			line.updateWorldData();
			line.updateModelData();
			line.getSpatial().updateRenderState();
			line.getLocalTranslation().set(laserStart.add(offset) );//super.physicsModel.getLocalTranslation()).x +=  super.physicsModel.getLocalScale().x/2f ;
			line.getLocalRotation().set(super.physicsModel.getLocalRotation());
			

		     AlphaState as1 = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
		     
		     if (interpreter.isBeamLuminous())
		     {
		      as1.setBlendEnabled( true );
		      as1.setSrcFunction( AlphaState.SB_SRC_ALPHA );
		      as1.setDstFunction( AlphaState.DB_ONE );
		      as1.setTestEnabled( true );
		      as1.setTestFunction( AlphaState.TF_GREATER );
		      as1.setEnabled( true );
		     }else
		     {		    
		    	 as1.setBlendEnabled(true);
		    	 as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		    	 as1.setDstFunction(AlphaState.SB_ONE_MINUS_SRC_ALPHA);					
		    	 as1.setTestEnabled(true);						
		    	 as1.setTestFunction(AlphaState.TF_ALWAYS);	
		    	 as1.setEnabled(true);
		     }
		     
			line.getSpatial().setRenderState(as1);
			
			
			
	
		
		/*	particleUpdate = new Updatable()
			{
	
			
				public void update(float time) {
					line.getLocalTranslation().y = 2;
					line.getLocalScale().set(10f,1f,1f);
					line.updateWorldData();
				//	physicsRay.updateWorldVectors();
					//System.out.println(physicsRay.getWorldRotation());
				}
	
		
			};*/
			
	/*		if(particleUpdate!=null)
				UpdateManager.getInstance().add(particleUpdate,Stream.PHYSICS_RENDER);*/
		}
		
		thisPhysics.getSpace().addToUpdateCallbacks(new PhysicsUpdateCallback(){

			public void afterStep(PhysicsSpace space, float time) {
				applyCollision();
				if(line!=null)
				{
					if(collisionApplied)
					{			
						line.setLineLength(FastMath.sqrt(closestCollisionSqr));
					
					}else{
						line.setLineLength(maxDistance);
					}
					if(forceScalar>0)
						line.setLineColor(normalColor);
					else
						line.setLineColor(collisionColor);
				}
			
				closestOtherPhysics = null;
				closestCollisionSqr = Float.POSITIVE_INFINITY;
			
			}

			public void beforeStep(PhysicsSpace space, float time) {
				collisionApplied = false;
				distanceMeter.setSensorObservation(0f);
			}
			
		});
	}
	
	

}
