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

import com.golemgame.constructor.Updatable;
import com.golemgame.constructor.UpdateManager;
import com.golemgame.constructor.UpdateManager.Stream;
import com.golemgame.functional.component.BComponent;
import com.golemgame.functional.component.BJointAMotor;
import com.golemgame.functional.component.BMind;
import com.golemgame.functional.component.BSpring;
import com.golemgame.functional.component.BJointAMotor.MotorCallback;
import com.golemgame.model.Model;
import com.golemgame.model.spatial.SpatialModel;
import com.golemgame.model.spatial.shape.CylinderFacade;
import com.golemgame.model.spatial.shape.CylinderModel;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.Reference;
import com.golemgame.mvc.golems.HydraulicInterpreter;
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
import com.jmex.physics.JointAxis;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.TranslationalJointAxis;

public class OdeHydraulicStructure extends OdePhysicalStructure {
	private HydraulicInterpreter interpreter;
	
	private OdeHydraulicJointStructure right;
	private OdeHydraulicJointStructure left;
	private final float MIN_DISTANCE = 0.0001f;
	
	public OdeHydraulicStructure(PropertyStore store) {
		super(store);
		interpreter = new HydraulicInterpreter(store);
		left = new OdeHydraulicJointStructure(store,true);
		right = new OdeHydraulicJointStructure(store,false);
	}
	
	@Override
	public Collection<OdePhysicalStructure> getSubstructures() {
		ArrayList<OdePhysicalStructure> subs = new ArrayList<OdePhysicalStructure>();
		subs.add(left);
		subs.add(right);
		return subs;
	}

	private HydraulicMotorCallback motorCallback = null;
	
	
	@Override
	public void buildMind(BMind mind,
			Map<OdePhysicalStructure, PhysicsNode> physicalMap,
			Map<Reference, BComponent> wireMap, OdePhysicsEnvironment environment) {
		if (this.motorCallback == null)
			return;
		BJointAMotor amotor;
		MotorPropertiesImpl motorProperties = new MotorPropertiesImpl(interpreter.getMotorPropertiesStore());
		
		if (!motorProperties.isSpring())
			amotor = new BJointAMotor();
		else
			amotor = new BSpring(motorProperties.getSpringConstant(),false);
		
		
		amotor.setMotorCallback(motorCallback);
		
		//FIXME: This somehow had a null pointer error... The axis must be null.
		motorProperties.setMaxPosition(motorCallback.getAxis().getPositionMaximum() + motorCallback.getStartingPosition());
		motorProperties.setMinPosition(motorCallback.getAxis().getPositionMinimum()+ motorCallback.getStartingPosition());
		
		amotor.setMotorProperties(motorProperties);


		
		WirePortInterpreter out = new WirePortInterpreter(interpreter.getOutput());
		WirePortInterpreter in = new WirePortInterpreter(interpreter.getInput());
	
		wireMap.put(in.getID(), amotor);
	
		mind.addComponent(amotor);
		mind.addSource(amotor.getMotorFeedback());
		mind.addComponent(amotor.getMotorFeedback());
		wireMap.put(out.getID(), amotor.getMotorFeedback());
	}

	public void buildCollidable(CollisionMember collidable) {
		//buildConnection(collidable);
	}
	

	public float buildCollisionGeometries(PhysicsNode physicsNode,Collection<PhysicsComponent> components,
			Vector3f store) {
		return 0;
	}




//	private final Vector3f store = new Vector3f();
	public void buildRelationships(
			 Map<OdePhysicalStructure, PhysicsNode> physicsMap, OdePhysicsEnvironment compiledEnvironment) {

			PhysicsNode leftPhysicsNode = physicsMap.get(left);
			
			
			
			
			PhysicsNode rightPhysicsNode = physicsMap.get(right);

			buildHinge(leftPhysicsNode,left.jointCollision,rightPhysicsNode, right.jointCollision,compiledEnvironment);
			
			PhysicsCollisionGeometry[] involved = new PhysicsCollisionGeometry[]{left.jointCollision,right.jointCollision};
		     
		     
			
			for(PhysicalDecorator decorator:getPhysicalDecorators())
				decorator.attach(leftPhysicsNode,involved);
			

	
					
		final Vector3f store = new Vector3f();
		final float radius = interpreter.getJointRadius();
			Updatable controller = new Updatable(){
						
			
					private static final long serialVersionUID = 1L;

					public void update(float time) {
						physicsConnectionModel.updateWorldData();
						
						physicsConnectionModel.getSpatial().getParent().updateWorldVectors();
						right.jointFacade.updateWorldData();
						left.jointFacade.updateWorldData();
						 left.jointFacade.getSpatial().updateWorldVectors();
						Vector3f rightLocalPosition =   left.jointModel.getSpatial().getParent().worldToLocal(right.jointModel.getWorldTranslation(),store);
						float length =  left.jointModel.getLocalTranslation().distance(rightLocalPosition) - left.jointModel.getLocalScale().z/2f - right.jointModel.getLocalScale().z/2f ;
			
						if(length<=0)
							length = 0.01f; 
	
						Vector3f dir = store.set(Vector3f.UNIT_Z);
					 	left.jointModel.getLocalRotation().multLocal(dir);
						
						
						physicsConnectionModel.getLocalTranslation().set(dir).multLocal(length/2f +radius).addLocal(left.jointModel.getLocalTranslation());
						physicsConnectionFacade.getLocalTranslation().set(physicsConnectionModel.getLocalTranslation());
						physicsConnectionFacade.getLocalScale().setZ(length + 0.02f);
						
						physicsConnectionModel.getLocalScale().setZ(length);
					//	physicsConnectionFacade.updateWorldData();
						//changes to scale are ignored...
				
					}
				
				};
				
				UpdateManager.getInstance().add(controller, Stream.PHYISCS_UPDATE);
			
	
		}



	private void buildHinge(PhysicsNode physicsNode1,PhysicsCollisionGeometry axle1,PhysicsNode physicsNode2, PhysicsCollisionGeometry axle2, OdePhysicsEnvironment compiledEnvironment) 
	{
		if (physicsNode1 == physicsNode2)
		{
			return;
		}
		
		
		Vector3f hingeCenter = new Vector3f().set( interpreter.getLocalTranslation());		
		if (!physicsNode1.isStatic())
		{
			if (!physicsNode2.isStatic())
			{
				 makeJoint((DynamicPhysicsNode)physicsNode1,axle1,(DynamicPhysicsNode) physicsNode2,axle2,hingeCenter,compiledEnvironment);	
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
	
	private void makeStaticJoint(final DynamicPhysicsNode physicsNode, final PhysicsCollisionGeometry axle, final PhysicsCollisionGeometry staticAxle, boolean useLeft,Vector3f hingeCenter, OdePhysicsEnvironment compiledEnvironment)
	{

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
		
		Vector3f axisOfMovement = new Vector3f(1,0,0);

		axle.getLocalRotation().multLocal(axisOfMovement);
		float currentPosition = dif.length();
		dif.normalizeLocal();
		
        joint.attach(physicsNode) ;
    
        joint.setAnchor(new Vector3f().set(pos1)); //the anchor has to be set at the center of the SECOND node
		
        
       final  TranslationalJointAxis axis = joint.createTranslationalAxis();	
     
        axis.setDirection( dif );//if there is rotation, this might be messed...
  

        float adjustForCylinderSize = axle.getLocalScale().z/2f;
        adjustForCylinderSize += staticAxle.getLocalScale().z/2f;
        adjustForCylinderSize +=0.01f;
        //this is the total distance from the center of each cylidner to its edge 
        
        float minimumSize = interpreter.getMinJointDistance();
        if(minimumSize < 0f)
        	minimumSize = 0f;
        minimumSize+= adjustForCylinderSize;
    //    if (minimumSize<  adjustForCylinderSize)
    //    	minimumSize =  adjustForCylinderSize;
        
       float minPos= minimumSize -currentPosition;
        axis.setPositionMinimum(minPos);
        float maximumSize = interpreter.getMaxJointDistance();
        if(maximumSize<0)
        	maximumSize = 0;
        maximumSize+=adjustForCylinderSize;
        if(maximumSize<minimumSize)
        	maximumSize = minimumSize;
      
        float maxPos = maximumSize -currentPosition;
        
/*        float minimumSize =interpreter.getMinJointDistance();
        if (minimumSize<  adjustForCylinderSize)
        	minimumSize =  adjustForCylinderSize;

        
        float minPos= minimumSize -currentPosition;
        axis.setPositionMinimum(minPos);
        
        float maxPos = interpreter.getMaxJointDistance() -currentPosition;
        if(maxPos< minPos)
        	maxPos = minPos;*/
        axis.setPositionMaximum(maxPos );
     
        motorCallback = new HydraulicMotorCallback(axis, currentPosition)
        {

        	
			public void applyForce(float force) {
        	
        		Vector3f forceVector = axis.getDirection(new Vector3f()).multLocal(force);
				physicsNode.addForce(forceVector, axle.getLocalTranslation());
				
			}

			
			public void applyTorque(float torque, float time) {
				Vector3f forceVector = axis.getDirection(new Vector3f()).multLocal(torque);
				physicsNode.addTorque(forceVector);
			}
        	
        };
        
		
		

	}
	

	
	private void makeJoint(final DynamicPhysicsNode physicsNode1, final PhysicsCollisionGeometry axle1, final DynamicPhysicsNode physicsNode2, final PhysicsCollisionGeometry axle2,Vector3f hingeCenter, OdePhysicsEnvironment compiledEnvironment)
	{

			
		Joint joint = physicsNode1.getSpace().createJoint();//this already is set, but for clarity its here too
		

		joint.setCollisionEnabled(true);
		physicsNode1.updateWorldVectors();
		physicsNode2.updateWorldVectors();
		
		axle1.updateWorldVectors();
		axle2.updateWorldVectors();
			
		
		Vector3f posOffset = physicsNode1.worldToLocal(hingeCenter, new Vector3f());	

	        joint.attach(physicsNode1, physicsNode2) ;
	        joint.setAnchor(new Vector3f().set(posOffset)); //the anchor has to be set at the center of the SECOND node
				        
	        final TranslationalJointAxis axis = joint.createTranslationalAxis();	
	        final Vector3f dir = new Vector3f(0,0,1);

	    	  dir.multLocal(-1);
	    	  axle1.getLocalRotation().multLocal(dir); 
	    	  
	       axis.setDirection( dir);//if there is rotation, this might be messed...
	        
			Vector3f pos1 = new Vector3f().set(axle1.getWorldTranslation());//this wont work if, for instance, these aren't yet attached to their world...
			
			
			Vector3f pos2 = new Vector3f().set(axle2.getWorldTranslation());

			Vector3f dif = pos1.subtract(pos2, new Vector3f());
			
			Vector3f axisOfMovement = new Vector3f(dir);
		//	Quaternion baseRotation = new Quaternion().fromAngleAxis(FastMath.HALF_PI, new Vector3f(0,1,0));


			//baseRotation.mult(axle1.getWorldRotation()).multLocal(axisOfMovement);
			float currentPosition = FastMath.abs(dif.dot(axisOfMovement));
			dif.normalizeLocal();
	       
			 
		     
		       
        float adjustForCylinderSize = axle1.getLocalScale().z/2f;
        adjustForCylinderSize += axle2.getLocalScale().z/2f;
        adjustForCylinderSize +=0.01f;
        //this is the total distance from the center of each cylidner to its edge 
        
        float minimumSize = interpreter.getMinJointDistance();
        if(minimumSize < 0f)
        	minimumSize = 0f;
        minimumSize+= adjustForCylinderSize;
    //    if (minimumSize<  adjustForCylinderSize)
    //    	minimumSize =  adjustForCylinderSize;
        
       float minPos= minimumSize -currentPosition;
        axis.setPositionMinimum(minPos);
        float maximumSize = interpreter.getMaxJointDistance();
        if(maximumSize<0)
        	maximumSize = 0;
        maximumSize+=adjustForCylinderSize;
        if(maximumSize<minimumSize)
        	maximumSize = minimumSize;
      
        float maxPos = maximumSize -currentPosition;
        //if(maxPos< minPos)
        //	maxPos = minPos;
       
        axis.setPositionMaximum(maxPos );
        motorCallback = new HydraulicMotorCallback(axis, currentPosition)
        {

        	
			public void applyForce(float force) {
        		//Vector3f forceVector = dir.mult(force);
        		Vector3f forceVector = physicsNode1.getWorldRotation().mult(dir).multLocal(force);
				physicsNode1.addForce(forceVector.divide(2f), axle1.getLocalTranslation());
				physicsNode2.addForce(forceVector.divide(-2f), axle2.getLocalTranslation());
				
			}

			
			public void applyTorque(float torque, float time) {
				Vector3f forceVector = physicsNode1.getWorldRotation().mult(dir).multLocal(torque);
				physicsNode1.addTorque(forceVector.divide(2f));
				physicsNode2.addTorque(forceVector.divide(-2f));
			}
        	
        };
			// Attach it
			 	        
	        //the joints are in the physicsnode's coordinate space!
		}	



	private class OdeHydraulicJointStructure extends OdePhysicalStructure
	{

		SpatialModel jointModel;
		SpatialModel jointFacade;
		boolean left = false;
		
		public OdeHydraulicJointStructure(PropertyStore store, boolean left) {
			super(store);
			this.left = left;
		}

		@Override
		public void buildCollidable(CollisionMember physicsCollision) {
		
			float distance = interpreter.getJointDistance();
			
			if (distance<interpreter.getMinJointDistance())
			{
				interpreter.setJointDistance(interpreter.getMinJointDistance());
				 distance = interpreter.getJointDistance();
			}
			 if(distance<MIN_DISTANCE)
				 distance = MIN_DISTANCE;
			
			
			float jointRadius = interpreter.getJointRadius();

			jointModel = new CylinderModel(true);
			jointFacade = new CylinderFacade();
			
			jointModel.getLocalScale().set(jointRadius*2f,jointRadius*2f,jointRadius*2f);
		
			if(left)
				jointModel.getLocalTranslation().setX(-distance/2f  - jointRadius);
			else
				jointModel.getLocalTranslation().setX(distance/2f +  jointRadius);
			
			
			
			jointModel.updateWorldData();
			
			Vector3f worldTranslation = interpreter.getLocalTranslation();
			Quaternion worldRotation = interpreter.getLocalRotation();
			
			worldRotation.multLocal(jointModel.getLocalTranslation());
			jointModel.getLocalTranslation().addLocal(worldTranslation);
			jointModel.getLocalRotation().set(worldRotation.mult(jointModel.getLocalRotation()));
			
			physicsCollision.registerCollidingModel(jointModel);
			physicsCollision.getModel().addChild(jointModel);
			physicsCollision.getModel().addChild(jointFacade);
			jointModel.updateWorldData();
			jointFacade.updateWorldData();
		
			this.jointFacade.loadModelData(jointModel);
			
			super.getStructuralAppearanceEffect().attachModel(jointFacade);
			if(left)
				buildConnection(physicsCollision);
		}
		
		PhysicsCollisionGeometry jointCollision = null;
		
		@Override
		public float buildCollisionGeometries(PhysicsNode physicsNode,Collection<PhysicsComponent> components,
				Vector3f store) {
			
			jointCollision=physicsNode.createCylinder("joint");	
			jointCollision.getLocalTranslation().set(jointModel.getLocalTranslation());
			jointCollision.getLocalScale().set(jointModel.getLocalScale());
			jointCollision.getLocalScale().x/=2f;
			jointCollision.getLocalScale().y/=2f;
			jointCollision.getLocalRotation().set(jointModel.getLocalRotation());		
			jointCollision.setIsCollidable(false);
			jointCollision.setMaterial(getMaterial());
			physicsNode.attachChild(jointCollision);		
			jointCollision.updateWorldVectors();
			
			store.set(jointCollision.getLocalTranslation());
			

			
			OdePhysicsComponent jointComp = new OdePhysicsComponent(jointCollision,jointModel);
			jointComp.setMass( jointCollision.getVolume()*getMaterial().getDensity());
			components.add(jointComp);
			
			
			return jointCollision.getVolume()*getMaterial().getDensity();
		}

		@Override
		public void buildRelationships(
				Map<OdePhysicalStructure, PhysicsNode> physicalMap,
				OdePhysicsEnvironment compiledEnvironment) {
	
			super.buildRelationships(physicalMap, compiledEnvironment);
		}
		
		
		
	}
	
	private abstract class HydraulicMotorCallback  implements MotorCallback    {

		private final JointAxis axis;
		
		private final float startingPosition;

		public JointAxis getAxis() {
			return axis;
		}

		public float getStartingPosition() {
			return startingPosition;
		}

		private HydraulicMotorCallback(JointAxis axis, float startingPosition) {
			super();
			this.axis = axis;
			this.startingPosition = startingPosition;
		}

		
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
			float p =axis.getPosition() + startingPosition  ;
			return p;
		}
		
		
    	
    	
    };
    
    private transient SpatialModel physicsConnectionModel = null;
	private transient Model physicsConnectionFacade = null;

	private void buildConnection(CollisionMember collidable)
	{
		
	
		
		float length = interpreter.getJointDistance();// left.jointModel.getLocalTranslation().distance( right.jointModel.getLocalTranslation()) - left.jointModel.getLocalScale().z/2f - right.jointModel.getLocalScale().z/2f ;
		
		if(length<=0)
			length = 0.01f; 
		
		//construct a shared visual model from the visual box
		 physicsConnectionModel = new CylinderModel(false);//intentionally false right now, since these aren't really 'there'
		
		 physicsConnectionModel.getLocalTranslation().set(interpreter.getLocalTranslation());
		// collidable.getModel().worldToLocal(connectionModel.getWorldTranslation(), physicsConnectionModel.getLocalTranslation());
			float distance = interpreter.getJointDistance();
			float radius = interpreter.getJointRadius();
			physicsConnectionModel.getLocalRotation().set(interpreter.getLocalRotation()).multLocal(new Quaternion().fromAngleNormalAxis(FastMath.HALF_PI,Vector3f.UNIT_Y));
		 physicsConnectionModel.getLocalScale().setX(radius*.2f*2f);
		 physicsConnectionModel.getLocalScale().setY(radius*.2f*2f);
			
			//float distance = rightAxle.getLocalTranslation().distance(leftAxle.getLocalTranslation());
		 physicsConnectionModel.getLocalScale().setZ(distance);
		
		//make sure this only touches one of the cylinders...
		physicsConnectionModel.getLocalScale().z = length;
	//	physicsConnectionModel.getLocalTranslation().set(leftAxle.getLocalTranslation()).addLocal(rightAxle.getLocalTranslation()).divideLocal(2f);

		
		//collidable.registerCollidingModel(physicsConnectionModel);
		collidable.getModel().addChild(physicsConnectionModel);
		physicsConnectionModel.getListeners().clear();
		physicsConnectionModel.updateModelData();
		physicsConnectionModel.updateWorldData();
		
		
		
		Model physicsFacade = new CylinderFacade();
		
		physicsFacade.getLocalTranslation().set(physicsConnectionModel.getLocalTranslation());
		physicsFacade.getLocalScale().set(physicsConnectionModel.getLocalScale());
		physicsFacade.getLocalRotation().set(physicsConnectionModel.getLocalRotation());
		collidable.getModel().addChild(physicsFacade);
		physicsConnectionFacade = physicsFacade;
		
		Vector3f move = new Vector3f(0,0,-0.01f);
		physicsConnectionModel.getLocalRotation().multLocal(move);
		physicsConnectionModel.getLocalTranslation().addLocal(move);//shift it slightly to the right, so it only collides wiht one of these.
		physicsConnectionModel.updateModelData();
		physicsConnectionFacade.updateWorldData();
		
		super.getStructuralAppearanceEffect().attachModel(physicsConnectionFacade);
	
	}
	
}
