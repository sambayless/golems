package com.golemgame.physical.ode;

import java.util.Collection;
import java.util.Map;

import com.golemgame.functional.component.BComponent;
import com.golemgame.functional.component.BGenericInput;
import com.golemgame.functional.component.BGenericSource;
import com.golemgame.functional.component.BMind;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.Reference;
import com.golemgame.mvc.golems.ContactInterpreter;
import com.golemgame.mvc.golems.WirePortInterpreter;
import com.golemgame.physical.PhysicsComponent;
import com.golemgame.physical.ode.compile.OdePhysicsEnvironment;
import com.golemgame.states.physics.PhysicsSpatialMonitor;
import com.golemgame.states.physics.PhysicsSpatialMonitor.PhysicsCollisionListener;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.PhysicsSpatial;
import com.jmex.physics.contact.ContactInfo;
import com.jmex.physics.material.Material;

public class OdeContactStructure extends OdeSphereStructure {

	private ContactInterpreter interpreter;
	private BGenericSource contactOut;
	public OdeContactStructure(PropertyStore store) {
		super(store);
		interpreter = new ContactInterpreter(store);
	}
	
	protected PhysicsCollisionGeometry collision = null;
	private int contactID = 0;
	public int getContactID() {
		return contactID;
	}

	private int contactSet = -1;
	
	public int getContactSet() {
		return contactSet;
	}

	public void setContactSet(int contactSet) {
		this.contactSet = contactSet;
	}

	@Override
	public void buildMind(BMind mind, Map<OdePhysicalStructure, PhysicsNode> physicsMap, Map<Reference,BComponent> wireMap, final OdePhysicsEnvironment environment ) {
		PhysicsSpatialMonitor.getInstance().addPhysicsCollisionListener(collision,new PhysicsCollisionListener()
		{

			
			public void collisionOccured(ContactInfo info,
					PhysicsSpatial source) {
				
				PhysicsNode node1 = info.getNode1();
				//PhysicsNode node2  = info.getNode2();
				PhysicsSpatial otherSpatial;
				if (source.getPhysicsNode() == node1)
				{
					otherSpatial = info.getGeometry2();
				}else
					otherSpatial = info.getGeometry1();
				
				if(otherSpatial == null)
					return;
				
				if(environment.isContact(otherSpatial))
				{
					environment.getContactManager().reportContact(OdeContactStructure.this,environment.getContact(otherSpatial) );
					
				}				
			}

			
		});
		
		BGenericInput forceInput = new BGenericInput(){
			@Override
			protected float signalRecieved(float signal,float time) {
				environment.getContactManager().reportInputSignal(OdeContactStructure.this, signal);
				state = 0;
				return 0;
			}			
		};	
		WirePortInterpreter inPort = new WirePortInterpreter(interpreter.getInput());
		wireMap.put(inPort.getID(), forceInput);
		mind.addComponent(forceInput);
		
		 contactOut = new BGenericSource("Contact Output");
			WirePortInterpreter distance = new WirePortInterpreter(interpreter.getOutput());
			wireMap.put(distance.getID(), contactOut);
			contactOut.setLastingObservations(false);
			contactOut.setNormalizingMax(1f);
			contactOut.setNormalizingMin(-1f);
			contactOut.setSensorObservation(-1f);
			mind.addComponent(contactOut);
			mind.addSource(contactOut);

		super.buildMind(mind, physicsMap, wireMap, environment);
	}
	
	@Override
	public float buildCollisionGeometries(PhysicsNode physicsNode, Collection<PhysicsComponent> components, Vector3f store) {
	
		collision=physicsNode.createSphere("contact ghost");

		collision.getLocalTranslation().set(interpreter.getLocalTranslation());
		collision.getLocalRotation().set(interpreter.getLocalRotation());
		
		Vector3f radius = new Vector3f(0.1f,0.1f,0.1f);
		
		collision.getLocalScale().set(super.getInterpreter().getExtent());
		collision.getLocalScale().add(radius);
		
		
		collision.setIsCollidable(false);
		
		collision.setMaterial(Material.GHOST);
		PhysicsSpatialMonitor.getInstance().registerGhost(collision);
		physicsNode.attachChild(collision);
		
		store.set(collision.getLocalTranslation());
		
		OdePhysicsComponent comp = new OdePhysicsComponent(collision,null);
		comp.setMass( 0f);
		components.add(comp);
		
		return super.buildCollisionGeometries(physicsNode, components, store);
	}
	
	public void buildRelationships( Map<OdePhysicalStructure, PhysicsNode> physicalMap, OdePhysicsEnvironment environment) 
	{
		this.contactID = environment.generateUniqueContactID(this);
		environment.registerSensorSpatial(collision);
		environment.registerContact(super.getCollision(), this);
	}

	public void reportOutput(float outputSignal) {
		this.contactOut.setSensorObservation(outputSignal);
		
	}

}
