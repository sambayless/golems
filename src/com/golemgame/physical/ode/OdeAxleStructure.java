package com.golemgame.physical.ode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.golemgame.functional.component.BComponent;
import com.golemgame.functional.component.BJointAMotor;
import com.golemgame.functional.component.BMind;
import com.golemgame.functional.component.BSpring;
import com.golemgame.functional.component.BJointAMotor.MotorCallback;
import com.golemgame.model.spatial.SpatialModel;
import com.golemgame.model.spatial.shape.CylinderFacade;
import com.golemgame.model.spatial.shape.CylinderModel;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.Reference;
import com.golemgame.mvc.golems.AxleInterpreter;
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

public class OdeAxleStructure extends OdePhysicalStructure {
	private AxleInterpreter interpreter;
	
	private OdeAxleJointStructure right;
	private OdeAxleJointStructure left;
	
	public OdeAxleStructure(PropertyStore store) {
		super(store);
		interpreter = new AxleInterpreter(store);
		left = new OdeAxleJointStructure(store,true);
		right = new OdeAxleJointStructure(store,false);
	}
	
	@Override
	public Collection<OdePhysicalStructure> getSubstructures() {
		ArrayList<OdePhysicalStructure> subs = new ArrayList<OdePhysicalStructure>();
		subs.add(left);
		subs.add(right);
		return subs;
	}

	private transient MotorCallback motorCallback = null;
	
	
	@Override
	public void buildMind(BMind mind,
			Map<OdePhysicalStructure, PhysicsNode> physicalMap,
			Map<Reference, BComponent> wireMap, OdePhysicsEnvironment environment) {
		
		if (this.motorCallback == null)//occurs if both sides are on the same physics node
			return;

		MotorPropertiesImpl motorProperties = new MotorPropertiesImpl(interpreter.getMotorPropertiesStore());
		
		BJointAMotor amotor;
		if (!motorProperties.isSpring())
			amotor = new BJointAMotor();
		else
			amotor = new BSpring(motorProperties.getSpringConstant(),true);
		amotor.setMotorCallback(motorCallback);
		
		motorProperties.setMaxPosition(FastMath.PI);
		motorProperties.setMinPosition(-FastMath.PI);
		motorProperties.setPositionWraps(true);
		amotor.setMotorProperties(motorProperties);
		mind.addSource(amotor.getMotorFeedback());
		mind.addComponent(amotor.getMotorFeedback());
		if (motorProperties.isSpring())
		{
			/*BConstantSource source = new BConstantSource(1);
			source.attachOutput(amotor);
			mind.addSource(source);*/
			//mind.addComponent(source);
		}

		WirePortInterpreter out = new WirePortInterpreter(interpreter.getOutput());
		WirePortInterpreter in = new WirePortInterpreter(interpreter.getInput());
	
		wireMap.put(in.getID(), amotor);
		
		mind.addComponent(amotor);
		wireMap.put(out.getID(), amotor.getMotorFeedback());
		
	}

	public void buildCollidable(CollisionMember collidable) {

	}
	

	public float buildCollisionGeometries(PhysicsNode physicsNode,Collection<PhysicsComponent> components,
			Vector3f store) {
		return 0;
	}




	
	public void buildRelationships(
			 Map<OdePhysicalStructure, PhysicsNode> physicsMap, OdePhysicsEnvironment compiledEnvironment) {

			PhysicsNode leftPhysicsNode = physicsMap.get(left);
			
			PhysicsNode rightPhysicsNode = physicsMap.get(right);

			buildAxle(leftPhysicsNode,left.jointCollision,rightPhysicsNode, right.jointCollision,compiledEnvironment);
			
			PhysicsCollisionGeometry[] involved = new PhysicsCollisionGeometry[]{left.jointCollision,right.jointCollision};
		     
		     
			
			for(PhysicalDecorator decorator:getPhysicalDecorators())
				decorator.attach(leftPhysicsNode,involved);
			

			
	
		}


	private void buildAxle(PhysicsNode physicsNode1,PhysicsCollisionGeometry axle1,PhysicsNode physicsNode2, PhysicsCollisionGeometry axle2,OdePhysicsEnvironment compiledEnvironment) 
	{
		if (physicsNode1 == physicsNode2)
			return;
			
		if (!physicsNode1.isStatic())
		{
			if (!physicsNode2.isStatic())
			{
				makeJoint((DynamicPhysicsNode)physicsNode1,axle1,(DynamicPhysicsNode) physicsNode2,axle2,compiledEnvironment);	
			//	makeJoint((DynamicPhysicsNode)physicsNode2,axle2,(DynamicPhysicsNode) physicsNode1,axle1);	
				//makeJoint(coJoint,this);
			}else
				 makeStaticJoint((DynamicPhysicsNode)physicsNode1,axle1,axle2, true,compiledEnvironment);
		}else			
		{
			if (!physicsNode2.isStatic())
				 makeStaticJoint((DynamicPhysicsNode)physicsNode2,axle2,axle1,false,compiledEnvironment);				
			else
				return;//no action
		}	
		
	}
	
	

	
	private   RotationalJointAxis makeStaticJoint(final DynamicPhysicsNode physicsNode, final PhysicsCollisionGeometry axle,PhysicsCollisionGeometry staticAxle, boolean leftDynamic,OdePhysicsEnvironment compiledEnvironment)
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

        joint.attach(physicsNode) ;
        
      
        
        Vector3f offSet = new Vector3f().set(axle.getLocalTranslation());//passes through center of node
        
        joint.setAnchor(physicsNode.localToWorld(offSet, offSet)); //the anchor has to be set at the center of the SECOND node
		
        
       final RotationalJointAxis axis = joint.createRotationalAxis();	
     
        if(leftDynamic)
        {
        	axis.setDirection( axle.getWorldRotation().mult(Vector3f.UNIT_Z.mult(-1f)) );//if there is rotation, this might be messed...
        }else{
        	  axis.setDirection( axle.getWorldRotation().mult(Vector3f.UNIT_Z) );//if there is rotation, this might be messed...
        }
      //  System.out.println(axle.getWorldRotation().mult(Vector3f.UNIT_Z));
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
				Vector3f forceVector = axis.getDirection(new Vector3f()).multLocal(-force);
				physicsNode.addForce(forceVector, axle.getLocalTranslation());
			}

			
			public void applyTorque(float torque, float time) {
				Vector3f forceVector = axis.getDirection(new Vector3f()).multLocal(torque);
				physicsNode.addTorque(forceVector);
			}

        };
        
        return axis;
	}
	
	private  RotationalJointAxis makeJoint(final DynamicPhysicsNode physicsNode1,final PhysicsCollisionGeometry axle1,final DynamicPhysicsNode physicsNode2, final PhysicsCollisionGeometry axle2,OdePhysicsEnvironment compiledEnvironment)
	{

			
			Joint joint = physicsNode1.getSpace().createJoint();//this already is set, but for clarity its here too
	
			   OdeJoint odeJoint =(OdeJoint)joint;
			     // odeJoint.setCFM(.01f);
			    //  odeJoint.setERP(0.2f);
		/*	if(this.getStructuralAppearanceEffect().getText().contains("test"))
			{
				odeJoint.setCFM(100f);
				// odeJoint.setERP(0.00008f);
			}*/
			joint.setCollisionEnabled(true);
			physicsNode1.updateWorldVectors();
			physicsNode2.updateWorldVectors();
			
			axle1.updateWorldVectors();
			axle2.updateWorldVectors();

			Vector3f offSet = new Vector3f().set(axle1.getLocalTranslation());//passes through center of node

	        joint.attach(physicsNode1,physicsNode2) ;
	        joint.setAnchor(offSet); 
	        
	       final RotationalJointAxis axis = joint.createRotationalAxis();	
	      
	        
	       final Vector3f dir = axle1.getLocalRotation().mult(Vector3f.UNIT_Z).multLocal(-1);
	        axis.setDirection(dir );
	       
	        
	        
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
		    	//	Vector3f forceVector = axis.getDirection(new Vector3f()).multLocal(force);
					physicsNode1.addForce(forceVector.divide(2f), axle1.getLocalTranslation());
					physicsNode2.addForce(forceVector.divide(-2f), axle2.getLocalTranslation());
					
				}

				
				public void applyTorque(float torque, float time) {
					
					Vector3f forceVector = physicsNode1.getWorldRotation().mult(dir).multLocal(torque);
				//	Vector3f forceVector = axis.getDirection(new Vector3f()).multLocal(torque);
					physicsNode1.addTorque(forceVector.divide(2f));
					physicsNode2.addTorque(forceVector.divide(-2f));
				}
				
				
	        };
	        
	        
	        //the joints are in the physicsnode's coordinate space!
	        return axis;
	}	
	

	private class OdeAxleJointStructure extends OdePhysicalStructure
	{
		final static float shift = 0.01f;//0.005f;
		SpatialModel jointModel;
		SpatialModel jointFacade;
		
		SpatialModel jointBearingModel;
		SpatialModel jointBearingFacade;
		
		boolean left = false;
		boolean isBearing = false;
		public OdeAxleJointStructure(PropertyStore store, boolean left) {
			super(store);
			this.left = left;
			if(!left)
			{
				if(interpreter.isBearing())
				{
					isBearing = true;
				}
			}
		}
		
		@Override
		public void buildCollidable(CollisionMember physicsCollision) {
		
			

			float jointLength =  interpreter.getJointLength(left);
			//float rightLength = interpreter.getJointLength(false);
			float bearingLength = interpreter.getBearingLength();
			float jointRadius = interpreter.getJointRadius(left);
			float bearingRadius = interpreter.getBearingRadius();
			//float rightRadius = interpreter.getJointRadius(false);
			
			jointModel = new CylinderModel(true);
			jointFacade = new CylinderFacade();
	
		//	jointModel.getLocalScale().set(jointRadius*2f,jointRadius*2f,jointLength - shift/2f);
		
			if(left)
			{
				jointModel.getLocalTranslation().setX(-jointLength/2f - shift*0);
				jointModel.getLocalScale().set(jointRadius*2f,jointRadius*2f,jointLength);
			}
			else
			{
				jointModel.getLocalScale().set(jointRadius*2f,jointRadius*2f,jointLength - shift);
				jointModel.getLocalTranslation().setX(jointLength/2f  + shift/2f);
			}
			
			if(isBearing)
			{
				jointBearingModel = new CylinderModel(true);
				jointBearingModel.getLocalScale().set(bearingRadius*2f,bearingRadius*2f,bearingLength - shift);
				
				jointBearingModel.getLocalTranslation().setX(-interpreter.getJointLength(!left) - bearingLength/2f  - shift/2f);
				jointBearingFacade = new CylinderFacade();
				jointBearingModel.updateWorldData();
			}
			
			jointModel.updateWorldData();
			
			Vector3f worldTranslation = interpreter.getLocalTranslation();
			Quaternion worldRotation = interpreter.getLocalRotation();
			
			worldRotation.multLocal(jointModel.getLocalTranslation());
			jointModel.getLocalTranslation().addLocal(worldTranslation);
			jointModel.getLocalRotation().set(worldRotation.mult(jointModel.getLocalRotation()));
			jointModel.getLocalRotation().multLocal(new Quaternion().fromAngleNormalAxis(-FastMath.HALF_PI, Vector3f.UNIT_Z));

			if(isBearing)
			{
				worldTranslation = interpreter.getLocalTranslation();
				worldRotation = interpreter.getLocalRotation();
				
				worldRotation.multLocal(jointBearingModel.getLocalTranslation());
				jointBearingModel.getLocalTranslation().addLocal(worldTranslation);
				jointBearingModel.getLocalRotation().multLocal(new Quaternion().fromAngleNormalAxis(-FastMath.HALF_PI, Vector3f.UNIT_Z));

				jointBearingModel.getLocalRotation().set(worldRotation.mult(jointBearingModel.getLocalRotation()));
			
			}
		
			
			physicsCollision.registerCollidingModel(jointModel);
			physicsCollision.getModel().addChild(jointModel);
			physicsCollision.getModel().addChild(jointFacade);
			jointModel.updateWorldData();
			jointFacade.updateWorldData();
		
			this.jointFacade.loadModelData(jointModel);
			
			if(left){
		//		jointFacade.getLocalTranslation().z += shift;
			/*	Vector3f shifter = new Vector3f(0,0,+shift/2f);
				jointModel.getLocalRotation().multLocal(shifter);
				jointFacade.getLocalTranslation().addLocal(shifter);*/
			}else{
				jointFacade.getLocalScale().z += shift/1f;
				Vector3f shifter = new Vector3f(0,0,-shift/2f);
				jointModel.getLocalRotation().multLocal(shifter);
				jointFacade.getLocalTranslation().addLocal(shifter);
			}
			super.getStructuralAppearanceEffect().attachModel(jointFacade);
			
			if(isBearing)
			{
				physicsCollision.registerCollidingModel(jointBearingModel);
				physicsCollision.getModel().addChild(jointBearingModel);
				physicsCollision.getModel().addChild(jointBearingFacade);
				jointBearingModel.updateWorldData();
				jointBearingFacade.updateWorldData();
			
				this.jointBearingFacade.loadModelData(jointBearingModel);
			//	jointBearingFacade.getLocalScale().z += shift/2f;
				
				jointBearingFacade.getLocalScale().z += shift/1f;
				Vector3f shifter = new Vector3f(0,0,+shift/2f);
				jointBearingModel.getLocalRotation().multLocal(shifter);
				jointBearingFacade.getLocalTranslation().addLocal(shifter);
				
				super.getStructuralAppearanceEffect().attachModel(jointBearingFacade);
			}
			
		
		}
		
		PhysicsCollisionGeometry jointCollision = null;
		PhysicsCollisionGeometry jointBearingCollision = null;
		
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
			
			
			OdePhysicsComponent jointComp = new OdePhysicsComponent(jointCollision,jointModel);
			jointComp.setMass( jointCollision.getVolume()*getMaterial().getDensity());
			components.add(jointComp);
			
			if(isBearing)
			{
				
				jointBearingCollision=physicsNode.createCylinder("joint");	
				jointBearingCollision.getLocalTranslation().set(jointBearingModel.getLocalTranslation());
				jointBearingCollision.getLocalScale().set(jointBearingModel.getLocalScale());
				jointBearingCollision.getLocalScale().x/=2f;
				jointBearingCollision.getLocalScale().y/=2f;
				jointBearingCollision.getLocalRotation().set(jointBearingModel.getLocalRotation());		
				jointBearingCollision.setIsCollidable(false);
				jointBearingCollision.setMaterial(getMaterial());
				physicsNode.attachChild(jointBearingCollision);		
				jointBearingCollision.updateWorldVectors();
				
				float rightMass = jointCollision.getVolume()*getMaterial().getDensity();
				float bearingMass = jointBearingCollision.getVolume()*getMaterial().getDensity();
				
				store.set(jointCollision.getLocalTranslation()).multLocal(rightMass).addLocal(jointBearingCollision.getLocalTranslation().mult(bearingMass)).divideLocal(bearingMass+rightMass);
				
				
				OdePhysicsComponent bearingComp = new OdePhysicsComponent(jointBearingCollision,jointBearingModel);
				bearingComp.setMass( jointBearingCollision.getVolume()*getMaterial().getDensity());
				components.add(bearingComp);
				
				return rightMass+bearingMass;
				
				
				
			}else{
				store.set(jointCollision.getLocalTranslation());
				
				return jointCollision.getVolume()*getMaterial().getDensity();
			}
			
		
		}

		@Override
		public void buildRelationships(
				Map<OdePhysicalStructure, PhysicsNode> physicalMap,
				OdePhysicsEnvironment compiledEnvironment) {
	
			super.buildRelationships(physicalMap, compiledEnvironment);
		}
		
		
		
	}
}
