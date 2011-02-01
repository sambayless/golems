/*******************************************************************************
 * Copyright 2008, 2009, 2010 Sam Bayless.
 * 
 *     This file is part of Golems.
 * 
 *     Golems is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     Golems is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with Golems. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.golemgame.physical.ode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.golemgame.functional.component.BComponent;
import com.golemgame.functional.component.BJointAMotor;
import com.golemgame.functional.component.BMind;
import com.golemgame.functional.component.BSpring;
import com.golemgame.functional.component.BJointAMotor.MotorCallback;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.model.spatial.SpatialModel;
import com.golemgame.model.spatial.shape.BoxModel;
import com.golemgame.model.spatial.shape.CylinderFacade;
import com.golemgame.model.spatial.shape.CylinderModel;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.Reference;
import com.golemgame.mvc.golems.HingeInterpreter;
import com.golemgame.mvc.golems.MotorPropertiesInterpreter;
import com.golemgame.mvc.golems.WirePortInterpreter;
import com.golemgame.physical.PhysicsComponent;
import com.golemgame.physical.ode.compile.OdePhysicsEnvironment;
import com.golemgame.structural.collision.CollisionMember;
import com.golemgame.structural.structures.MotorPropertiesImpl;
import com.golemgame.structural.structures.decorators.PhysicalDecorator;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.Joint;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.RotationalJointAxis;
import com.jmex.physics.impl.ode.joints.OdeJoint;

public class OdeHingeStructure extends OdePhysicalStructure{
	private HingeInterpreter interpreter;
	
	private BoxModel leftHinge;

	private CylinderModel connectionModel;
	private CylinderFacade connectionFacade;
	
	private MotorPropertiesInterpreter motorProperties;
	
	private Hinge right;
	
	public OdeHingeStructure(PropertyStore store) {
		super(store);
		interpreter = new HingeInterpreter(store);
		motorProperties = new MotorPropertiesInterpreter(interpreter.getMotorPropertiesStore());
		right = new Hinge(store);
	}
	
	@Override
	public Collection<OdePhysicalStructure> getSubstructures() {
		ArrayList<OdePhysicalStructure> subs = new ArrayList<OdePhysicalStructure>();
		subs.add(right);
		return subs;
	}

	private transient MotorCallback motorCallback = null;
	
	
	public void buildCollidable(CollisionMember collidable) {
		float width = interpreter.getJointLength(true);
		
	    physicsModel = new NodeModel();
		
		leftHinge = new BoxModel(true);

		physicsModel.getLocalTranslation().set(interpreter.getLocalTranslation());
		physicsModel.getLocalRotation().set(interpreter.getLocalRotation());

		leftHinge.getLocalScale().set(width,0.2f,width);
	
		connectionModel = new CylinderModel(true);
		connectionFacade = new CylinderFacade();
		
		connectionModel.getLocalScale().set(.19f,.19f,width);
		connectionModel.getLocalRotation().fromAngleNormalAxis(interpreter.getJointAngle(true),Vector3f.UNIT_Z);
		
		
		connectionFacade.loadModelData(connectionModel);
		physicsModel.addChild(connectionModel);
		physicsModel.addChild(connectionFacade);
		physicsModel.addChild(leftHinge);
		
		leftHinge.getLocalRotation().fromAngleNormalAxis(interpreter.getJointAngle(true),Vector3f.UNIT_Z);
		
		leftHinge.getLocalTranslation().set(-width/2f-0.1f ,0,0);
		leftHinge.getLocalRotation().multLocal(leftHinge.getLocalTranslation());
	
		collidable.registerCollidingModel(leftHinge);		
		collidable.registerCollidingModel(connectionModel);		
		collidable.getModel().addChild(physicsModel);

		physicsModel.getListeners().clear();
		physicsModel.updateModelData();
		connectionModel.updateWorldData();
		leftHinge.updateWorldData();
		physicsModel.updateModelData();
		
		
		
		super.getStructuralAppearanceEffect().attachModel(connectionFacade);
		super.getStructuralAppearanceEffect().attachModel(leftHinge);
	}
	

	public float buildCollisionGeometries(PhysicsNode physicsNode,Collection<PhysicsComponent> components,
			Vector3f store) {
		
		
		float mass = 0;

		
		
		
		leftHinge.updateWorldData();

		
		leftCollision=physicsNode.createBox("hinge");			
		leftCollision.getLocalTranslation().set(leftHinge.getWorldTranslation());
		leftCollision.getLocalScale().set(leftHinge.getLocalScale());
		leftCollision.getLocalRotation().set(leftHinge.getWorldRotation());	
		
	/*	physicsModel.getLocalRotation().multLocal(leftHinge.getLocalTranslation());
		leftHinge.getLocalTranslation().addLocal(physicsModel.getLocalTranslation());
		leftHinge.getLocalRotation().multLocal(physicsModel.getLocalRotation());*/
		
		leftCollision.setIsCollidable(false);
		leftCollision.setMaterial(getMaterial());
		physicsNode.attachChild(leftCollision);		
		leftCollision.updateWorldVectors();
		
		
		cylCollision = physicsNode.createCylinder("connection");
		cylCollision.getLocalTranslation().set(connectionModel.getWorldTranslation());
		cylCollision.getLocalScale().set(connectionModel.getLocalScale());
		cylCollision.getLocalRotation().set(connectionModel.getWorldRotation());	
		
		cylCollision.getLocalScale().x /=2f;
		cylCollision.getLocalScale().y /=2f;
		
		cylCollision.setIsCollidable(false);
		cylCollision.setMaterial(getMaterial());
		physicsNode.attachChild(cylCollision);
		cylCollision.updateWorldVectors();
		mass = leftCollision.getVolume() * this.getMaterial().getDensity();
		//mass =  leftCollision.getVolume() * this.getMaterial().getDensity();
		mass += cylCollision.getVolume()*this.getMaterial().getDensity();
		Vector3f tempPos = new Vector3f(leftCollision.getLocalTranslation()).multLocal(leftCollision.getVolume() * this.getMaterial().getDensity());
		
		store.zero();
		store.addLocal(tempPos);
		
		tempPos.set(cylCollision.getLocalTranslation()).multLocal(cylCollision.getVolume() * this.getMaterial().getDensity());
		store.addLocal(tempPos);
		store.divideLocal( mass);

		OdePhysicsComponent comp = new OdePhysicsComponent(cylCollision,connectionModel);
		comp.setMass( cylCollision.getVolume()*getMaterial().getDensity());
		components.add(comp);
		
		OdePhysicsComponent hingeComp = new OdePhysicsComponent(leftCollision,leftHinge);
		hingeComp.setMass( leftCollision.getVolume()*getMaterial().getDensity());
		components.add(hingeComp);
		
		return mass ;
	}

	
	
	
	private  NodeModel physicsModel = null;
	//private  PhysicsMesh meshCollision = null;
	protected  PhysicsCollisionGeometry cylCollision = null;
	protected  PhysicsCollisionGeometry leftCollision = null;

/*	
	private float buildConnectionCollisionGeometries(PhysicsNode physicsNode,
			Vector3f store) {
	
	
		cylCollision = physicsNode.createCylinder("cylinder");
	
		cylCollision.getLocalTranslation().set(physicsModel.getLocalTranslation());
		cylCollision.getLocalRotation().set(physicsModel.getLocalRotation());
		cylCollision.getLocalScale().set(physicsModel.getLocalScale());
		cylCollision.getLocalScale().x /=2f;
		cylCollision.getLocalScale().y /=2f;
		cylCollision.setIsCollidable(false);
		
		cylCollision.setMaterial(this.getMaterial());
		
		
		float radius = cylCollision.getLocalScale().x;
		float length = cylCollision.getLocalScale().z;
		//only uses a capsule if the radius is <= half of the length
		if (radius <= length /2f)
		{
		
			PhysicsCapsule capsule = physicsNode.createCapsule("capsule");
			capsule.getLocalTranslation().set(cylCollision.getLocalTranslation());
			capsule.getLocalRotation().set(cylCollision.getLocalRotation());
			capsule.getLocalScale().set(cylCollision.getLocalScale());
		
			capsule.getLocalScale().z -= radius *2f;
			if (capsule.getLocalScale().z < 0.01f)
				capsule.getLocalScale().z = 0.01f;
			capsule.getLocalScale().multLocal(0.95f);
			capsule.setIsCollidable(false);
			
			
			capsule.setMaterial(MaterialWrapper.WEIGHTLESS);
			
			physicsNode.attachChild(capsule);
		}else
		{
			meshCollision = physicsNode.createMesh("cylMesh");
			Vector3f scale = new Vector3f(physicsModel.getLocalScale());
			physicsModel.getLocalScale().multLocal(0.95f);
			physicsModel.updateWorldData();
			meshCollision.copyFrom((TriMesh)physicsModel.getSpatial());
			physicsModel.getLocalScale().set(scale);
			physicsModel.updateWorldData();
			//and construct physics
		
			meshCollision.getLocalTranslation().set(physicsModel.getLocalTranslation());
			meshCollision.getLocalRotation().set(physicsModel.getLocalRotation());
		//	collision.getLocalScale().set(shared.getLocalScale());
	
			meshCollision.setIsCollidable(false);
			
			meshCollision.setMaterial(MaterialWrapper.WEIGHTLESS);
			physicsNode.attachChild(meshCollision);
		}
		physicsNode.attachChild(cylCollision);
		cylCollision.updateWorldVectors();//note: it is critical to update world scale before calculating the volume!
		store.set(cylCollision.getLocalTranslation());
		
		//System.out.println("Cyl:" + cylCollision.getVolume() + "\t" + this.getMaterial().getDensity());
		return cylCollision.getVolume() * this.getMaterial().getDensity();
	}
*/
	
	public void buildRelationships(
			 Map<OdePhysicalStructure, PhysicsNode> physicsMap, OdePhysicsEnvironment compiledEnvironment) {
		
	

			PhysicsNode leftPhysicsNode = physicsMap.get(this);
			
			PhysicsNode rightPhysicsNode = physicsMap.get(right);

			buildHinge(leftPhysicsNode,leftCollision,rightPhysicsNode, right.hingeCollision,compiledEnvironment);
			
			PhysicsCollisionGeometry[] involved = new PhysicsCollisionGeometry[]{leftCollision,right.hingeCollision,this.cylCollision};
		     
		     
			for(PhysicalDecorator decorator:getPhysicalDecorators())
				decorator.attach(leftPhysicsNode, involved);
			

			
	
		}



	
	@Override
	public void buildMind(BMind mind,
			Map<OdePhysicalStructure, PhysicsNode> physicalMap,
			Map<Reference, BComponent> wireMap, OdePhysicsEnvironment environment) {
		
		
		if (this.motorCallback == null)//occurs if both sides are on the same physics node
			return;
		
		
	
		
		MotorPropertiesImpl motorProp =	new MotorPropertiesImpl(motorProperties.getStore());
		motorProp.refresh();

		PhysicsNode p1 = physicalMap.get(this);
		if(p1 instanceof DynamicPhysicsNode)
		{
			DynamicPhysicsNode d = (DynamicPhysicsNode) p1; 
			motorProp.setMass1(d.getMass());
		}

		PhysicsNode p2 = physicalMap.get(right);
		if(p2 instanceof DynamicPhysicsNode)
		{
			DynamicPhysicsNode d = (DynamicPhysicsNode) p2; 
			motorProp.setMass2(d.getMass());
		}
		
		BJointAMotor amotor;
		if (!this.motorProperties.isSpring())
			amotor = new BJointAMotor();
		else
			amotor = new BSpring(this.motorProperties.getSpringConstant(),true);
		
		amotor.setMotorCallback(motorCallback);
		
		
		
		
		this.motorProperties.setMaxPosition(FastMath.HALF_PI);
		this.motorProperties.setMinPosition(-FastMath.HALF_PI);
		this.motorProperties.setPositionWraps(true);
		amotor.setMotorProperties(motorProp);

		WirePortInterpreter out = new WirePortInterpreter(interpreter.getOutput());
		WirePortInterpreter in = new WirePortInterpreter(interpreter.getInput());
	
		
		
		wireMap.put(in.getID(), amotor);
		
		mind.addComponent(amotor);
		mind.addComponent(amotor.getMotorFeedback());
		mind.addSource(amotor.getMotorFeedback());
		wireMap.put(out.getID(), amotor.getMotorFeedback());
	}

	private void buildHinge(PhysicsNode physicsNode1,PhysicsCollisionGeometry axle1,PhysicsNode physicsNode2, PhysicsCollisionGeometry axle2, OdePhysicsEnvironment compiledEnvironment) 
	{
		if (physicsNode1 == physicsNode2)
			return;
		//RotationalJointAxis axis = null;

		physicsModel.updateWorldData();
		Vector3f hingeCenter = new Vector3f().set( physicsModel.getWorldTranslation());		
		if (!physicsNode1.isStatic())
		{
			if (!physicsNode2.isStatic())
			{
				 makeJoint((DynamicPhysicsNode)physicsNode1,axle1,(DynamicPhysicsNode) physicsNode2,axle2,hingeCenter,compiledEnvironment);	
			//	 makeJoint((DynamicPhysicsNode)physicsNode2,axle2,(DynamicPhysicsNode) physicsNode1,axle1,hingeCenter);	
				 
				//makeJoint(coJoint,this);
			}else
				 makeStaticJoint((DynamicPhysicsNode)physicsNode1,axle1,axle2, true,hingeCenter,compiledEnvironment);
		}else			
		{
			if (!physicsNode2.isStatic())
				 makeStaticJoint((DynamicPhysicsNode)physicsNode2,axle2,axle1,false,hingeCenter,compiledEnvironment);				
			else
				return;//no action
		}	
		
	}
	private  RotationalJointAxis makeStaticJoint(final DynamicPhysicsNode physicsNode,final PhysicsCollisionGeometry axle,PhysicsCollisionGeometry staticAxle, boolean useLeft,Vector3f hingeCenter, OdePhysicsEnvironment compiledEnvironment)
	{
	//	motorProperties.setMass(physicsNode.getMass());
		Joint joint = physicsNode.getSpace().createJoint();//this already is set, but for clarity its here too
		  compiledEnvironment.setJointStatic(staticAxle.getPhysicsNode(),joint);

		joint.setCollisionEnabled(true);

	     

		physicsNode.updateWorldVectors();
		
		axle.updateWorldVectors();
		staticAxle.updateWorldVectors();
		
		Vector3f localNormal = new Vector3f(0,1,0);
		Vector3f normal = axle.getWorldRotation().mult(localNormal);
		normal.normalizeLocal();
		
		Vector3f pos1 = new Vector3f().set(axle.getWorldTranslation());//this wont work if, for instance, these aren't yet attached to their world...
		
	
		Vector3f pos2 = new Vector3f().set(staticAxle.getWorldTranslation());

		Vector3f dif = pos1.subtract(pos2, new Vector3f());
		dif.normalizeLocal();
		
        joint.attach(physicsNode) ;
	//	Vector3f posOffset = //physicsNode.worldToLocal(hingeCenter, new Vector3f());	

       // axle.getLocalRotation().
        
        
        
        joint.setAnchor(new Vector3f(hingeCenter)); //the anchor has to be set at the center of the SECOND node
		
        
       final  RotationalJointAxis axis = joint.createRotationalAxis();	
     
        axis.setDirection( normal.cross(dif)  );//dif.cross(normal) );//if there is rotation, this might be messed..
        
        //have to adjust the min max pos by relative rotation between the two sides
        
        float angleBetween = (FastMath.PI- interpreter.getJointAngle(true)) + interpreter.getJointAngle(false);
   
        axis.setPositionMinimum( angleBetween - FastMath.HALF_PI*(3f- FastMath.FLT_EPSILON));
        axis.setPositionMaximum(angleBetween - FastMath.PI/(2f- FastMath.FLT_EPSILON) );//might want to make this /1.99f to prevent collision w/ other side...
        
        
        motorCallback = new MotorCallback()
        {

			
			public void setAvailableAcceleration(float amount) {
				
				axis.setAvailableAcceleration(amount);
			}

			
			public void setDesiredVelocity(float amount) {
				axis.setDesiredVelocity(amount);
				
			}
			
			public float getVelocity() {
				return axis.getVelocity();
			}

			
			public float getPosition() {
				return axis.getPosition();
			}
        	
			

			
			public void applyForce(float force) {
				Vector3f forceVector = axis.getDirection(new Vector3f()).multLocal(force);
				physicsNode.addForce(forceVector, axle.getLocalTranslation());
			}

			
			public void applyTorque(float torque, float time) {
				Vector3f forceVector = axis.getDirection(new Vector3f()).multLocal(torque);
				physicsNode.addTorque(forceVector);
			}
        	
        	
        };
        
        return axis;
     //   node.axis.setPositionMinimum(-0.1f); //position might be relative to starting position? it is.
     //   node.axis.setPositionMaximum(1f);
	}
	
	private  RotationalJointAxis makeJoint(final DynamicPhysicsNode physicsNode1, final PhysicsCollisionGeometry axle1,final DynamicPhysicsNode physicsNode2, final PhysicsCollisionGeometry axle2,final Vector3f hingeCenter, OdePhysicsEnvironment compiledEnvironment)
	{
	//	motorProperties.setMass(physicsNode1.getMass()<physicsNode2.getMass()?physicsNode1.getMass():physicsNode2.getMass());
			
		Joint joint = physicsNode1.getSpace().createJoint();//this already is set, but for clarity its here too
		

		joint.setCollisionEnabled(true);
		physicsNode1.updateWorldVectors();
		physicsNode2.updateWorldVectors();
		
		axle1.updateWorldVectors();
		axle2.updateWorldVectors();
	
		final Vector3f posOffset = physicsNode1.worldToLocal(hingeCenter, new Vector3f());	
		final Vector3f posOffset2 = physicsNode2.worldToLocal(hingeCenter, new Vector3f());	
	        joint.attach(physicsNode1, physicsNode2) ;
	        joint.setAnchor(new Vector3f().set(posOffset)); //the anchor has to be set at the center of the SECOND node
			
	        
	      final  RotationalJointAxis axis = joint.createRotationalAxis();	
	        
	      final Vector3f dir = axle1.getLocalRotation().mult(Vector3f.UNIT_Z);
	       axis.setDirection(dir );//if there is rotation, this might be messed...
	     
	    OdeJoint odeJoint =(OdeJoint)joint;
	 //   odeJoint.setCFM(1f);
	  
	      //odeJoint.setERP(0.8f);
	        float angleBetween = (FastMath.PI- interpreter.getJointAngle(true)) + interpreter.getJointAngle(false);
	   
	    //    axis.setPositionMinimum( angleBetween - FastMath.HALF_PI*(2.8f- FastMath.FLT_EPSILON));
	     //   axis.setPositionMaximum(angleBetween - FastMath.PI/(2f- FastMath.FLT_EPSILON) );//might want to make this /1.99f to prevent collision w/ other side...
	        
	         
	   //    axis.setPositionMinimum( -FastMath.PI/2 );
	   //     axis.setPositionMaximum( FastMath.PI/2 );
	        
	       motorCallback = new MotorCallback()
	        {

				
				public void setAvailableAcceleration(float amount) {
					
					axis.setAvailableAcceleration(amount);
				}

				
				public void setDesiredVelocity(float amount) {
					axis.setDesiredVelocity(amount);
					
				}
				
				public float getVelocity() {
					return axis.getVelocity();
				}

				
				public float getPosition() {
					return axis.getPosition();
				}
	        	
				
				public void applyForce(float force) {
					Vector3f forceVector = physicsNode1.getWorldRotation().mult(dir).multLocal(force);
					//Vector3f forceVector = axis.getDirection(new Vector3f()).multLocal(force);
					physicsNode1.addForce(forceVector.divide(2f), axle1.getLocalTranslation());
					physicsNode2.addForce(forceVector.divide(-2f), axle2.getLocalTranslation());
					
				}
				private float reducer = 1f/(100f* (physicsNode1.getMass()<physicsNode2.getMass()?physicsNode1.getMass():physicsNode2.getMass()) );
				
				public void applyTorque(float torque, float time) {
							float smallerMass = physicsNode1.getMass()<physicsNode2.getMass()?physicsNode1.getMass():physicsNode2.getMass();

					if(reducer < 1f)
						reducer = 1f;
								
					float MAX_VEL = 100f;
					float oTorque = torque;
					torque/=reducer;
					
			
					 if (FastMath.abs(axis.getVelocity()) > MAX_VEL)
					{
						 reducer*= 10f;
						 torque/=10f;
						float curvel = axis.getVelocity();
						if (FastMath.sign(torque) == FastMath.sign(curvel))
						{
							
							return;
						}
					}else
					{
						float redAmount =FastMath.abs( oTorque/10f);
						
					
						if(redAmount> 0.002f)
							redAmount = 0.002f;
						reducer*= 1f-redAmount;
					}
					
					
				
					//if applying this torque will bring the velocity at the next time step above
					//max velocity, limit it to the most torque that you an apply without rasing the velocity too much
					//float curVel = axis.getVelocity();
					float predicted_velocity = (torque/smallerMass * time);
					if ( FastMath.abs(predicted_velocity) > MAX_VEL )
					{
			
						reducer *= 4f;
		
					    float newTorque =( (MAX_VEL * FastMath.sign(predicted_velocity) ))*smallerMass/time;
						if(FastMath.sign(newTorque) == FastMath.sign(torque))
						{
							if (FastMath.abs(newTorque) < FastMath.abs(torque))
							{
								torque = newTorque;
								
							}
				
						}else
							torque = 0;
						
					}
					
					if(FastMath.abs(torque)<FastMath.FLT_EPSILON)
						return;
				
					Quaternion rotation1 = physicsNode1.getWorldRotation();
					Quaternion rotation2 = physicsNode2.getWorldRotation();
				
			
					Vector3f forceVector1 = rotation1.mult(dir.cross(axle1.getLocalTranslation().subtract(posOffset))).normalize().multLocal(torque);
					
					Vector3f forceVector2 =rotation2.mult(dir.cross(axle2.getLocalTranslation().subtract(posOffset2))).normalize().multLocal(-torque); 
			
					Vector3f position1 =(axle1.getLocalTranslation());
		
					Vector3f position1b =  (posOffset.mult(2).subtract(axle1.getLocalTranslation()));
					
					Vector3f position2 =(axle2.getLocalTranslation());
					Vector3f position2b =(posOffset2.mult(2).subtract(axle2.getLocalTranslation()));
					
					Vector3f vlength1 = (axle1.getLocalTranslation().subtract(posOffset));
					Vector3f vlength2 = (axle2.getLocalTranslation().subtract(posOffset2));

					float length1 = vlength1.length();
					if(length1 > 0.01f)
						forceVector1.divideLocal(length1);
					
					float length2 = vlength2.length();
					if(length2 > 0.01f)
						forceVector2.divideLocal(length2);
					
					//dont let the spring VELOCITY go beyond a certain (small) amount - only accept countering forces in that case.
		
					physicsNode1.addForce(forceVector1, position1);
					physicsNode1.addForce(forceVector1.mult(-1), position1b);
					
					physicsNode2.addForce(forceVector2, position2);
					physicsNode2.addForce(forceVector2.mult(-1), position2b);

				}
				
				
	        };
	       
	       return axis;
	        //the joints are in the physicsnode's coordinate space!
		}	
	
	private class Hinge extends OdePhysicalStructure
	{

		SpatialModel hingeModel;
		
		public Hinge(PropertyStore store) {
			super(store);
		}

		@Override
		public void buildCollidable(CollisionMember physicsCollision) {
			float width = interpreter.getJointLength(true);
			
			hingeModel = new BoxModel(true);
			hingeModel.getLocalScale().set(width,0.2f,width);
			
			hingeModel.getLocalRotation().fromAngleNormalAxis(interpreter.getJointAngle(false),Vector3f.UNIT_Z);
			
			hingeModel.getLocalTranslation().set(width/2f+0.1f,0,0);
			hingeModel.getLocalRotation().multLocal(hingeModel.getLocalTranslation());
			
			
			Vector3f worldTranslation = interpreter.getLocalTranslation();
			Quaternion worldRotation = interpreter.getLocalRotation();
			
			worldRotation.multLocal(hingeModel.getLocalTranslation());
			hingeModel.getLocalTranslation().addLocal(worldTranslation);
			hingeModel.getLocalRotation().set(worldRotation.mult(hingeModel.getLocalRotation()));
			
			physicsCollision.registerCollidingModel(hingeModel);
			physicsCollision.getModel().addChild(hingeModel);
			
			hingeModel.updateWorldData();
			
			super.getStructuralAppearanceEffect().attachModel(hingeModel);
		}
		
		PhysicsCollisionGeometry hingeCollision = null;
		
		@Override
		public float buildCollisionGeometries(PhysicsNode physicsNode,Collection<PhysicsComponent> components,
				Vector3f store) {
			
			hingeCollision=physicsNode.createBox("hinge");	
			hingeCollision.getLocalTranslation().set(hingeModel.getLocalTranslation());
			hingeCollision.getLocalScale().set(hingeModel.getLocalScale());
			hingeCollision.getLocalRotation().set(hingeModel.getLocalRotation());		
			hingeCollision.setIsCollidable(false);
			hingeCollision.setMaterial(getMaterial());
			physicsNode.attachChild(hingeCollision);		
			hingeCollision.updateWorldVectors();
			
			store.set(hingeCollision.getLocalTranslation());
			
			OdePhysicsComponent comp = new OdePhysicsComponent(hingeCollision,hingeModel);
			comp.setMass( hingeCollision.getVolume()*getMaterial().getDensity());
			components.add(comp);
			
			return hingeCollision.getVolume()*getMaterial().getDensity();
		}

		@Override
		public void buildRelationships(
				Map<OdePhysicalStructure, PhysicsNode> physicalMap,
				OdePhysicsEnvironment compiledEnvironment) {
	
			super.buildRelationships(physicalMap, compiledEnvironment);
		}
		
		
		
	}
	

	
}
