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

import com.golemgame.model.spatial.shape.CapsuleCombinedFacade;
import com.golemgame.model.spatial.shape.CapsuleCombinedModel;
import com.golemgame.model.spatial.shape.CylinderFacade;
import com.golemgame.model.spatial.shape.CylinderModel;
import com.golemgame.model.spatial.shape.SphereFacade;
import com.golemgame.model.spatial.shape.SphereModel;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.BallAndSocketInterpreter;
import com.golemgame.physical.PhysicsComponent;
import com.golemgame.physical.ode.compile.OdePhysicsEnvironment;
import com.golemgame.structural.collision.CollisionMember;
import com.golemgame.structural.structures.decorators.PhysicalDecorator;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.Joint;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.RotationalJointAxis;

public class OdeBallAndSocketStructure extends OdePhysicalStructure{
	private BallAndSocketInterpreter interpreter;
	
	private OdeSphereStructure right;
	private OdeSocketJointStructure left;
	private OdeUniversalJointStructure universalLeft;
	public OdeBallAndSocketStructure(PropertyStore store) {
		super(store);
		interpreter = new BallAndSocketInterpreter(store);
		if (interpreter.isUniversalJoint()){
			universalLeft = new OdeUniversalJointStructure(store);
		}else
			left = new OdeSocketJointStructure(store);
		right = new OdeSphereStructure(store);
	}
	
	@Override
	public Collection<OdePhysicalStructure> getSubstructures() {
		ArrayList<OdePhysicalStructure> subs = new ArrayList<OdePhysicalStructure>();
		if (interpreter.isUniversalJoint()){
			subs.add(universalLeft);
		}else
			subs.add(left);
		subs.add(right);
		return subs;
	}


	
	public void buildCollidable(CollisionMember collidable) {

	}
	

	public float buildCollisionGeometries(PhysicsNode physicsNode,Collection<PhysicsComponent> components,
			Vector3f store) {
		return 0;
	}




	
	public void buildRelationships(
			 Map<OdePhysicalStructure, PhysicsNode> physicsMap, OdePhysicsEnvironment compiledEnvironment) {
		
			PhysicsNode leftPhysicsNode;
			
			if(interpreter.isUniversalJoint())
			{
				leftPhysicsNode= physicsMap.get(universalLeft);
			}else{
				leftPhysicsNode= physicsMap.get(left);
			}
			PhysicsNode rightPhysicsNode = physicsMap.get(right);
			PhysicsCollisionGeometry[] involved ;
			if(interpreter.isUniversalJoint())
			{
				buildHinge(leftPhysicsNode,universalLeft.jointCollision,rightPhysicsNode, right.collision,compiledEnvironment);
				involved = new PhysicsCollisionGeometry[]{universalLeft.jointCollision,right.collision};
			}else
			{
				buildHinge(leftPhysicsNode,left.jointCollision,rightPhysicsNode, right.collision,compiledEnvironment);
				involved = new PhysicsCollisionGeometry[]{left.jointCollision,right.collision};
			     
			}
			
		
		     
			
			for(PhysicalDecorator decorator:getPhysicalDecorators())
				decorator.attach(leftPhysicsNode,involved);
			

			
	
		}

	private void buildHinge(PhysicsNode physicsNode1,PhysicsCollisionGeometry axle1,PhysicsNode physicsNode2, PhysicsCollisionGeometry axle2, OdePhysicsEnvironment compiledEnvironment) 
	{
		if (physicsNode1 == physicsNode2)
			return;
		
		boolean universal = interpreter.isUniversalJoint();
		Vector3f hingeCenter = new Vector3f().set( interpreter.getLocalTranslation());		
		if (!physicsNode1.isStatic())
		{
			if (!physicsNode2.isStatic())
			{
				 makeJoint((DynamicPhysicsNode)physicsNode1,axle1,(DynamicPhysicsNode) physicsNode2,axle2,hingeCenter,universal,compiledEnvironment);	
				//makeJoint(coJoint,this);
			}else
				 makeStaticJoint((DynamicPhysicsNode)physicsNode1,axle1,axle2, true,hingeCenter,universal,compiledEnvironment);
		}else			
		{
			if (!physicsNode2.isStatic())
				 makeStaticJoint((DynamicPhysicsNode)physicsNode2,axle2,axle1,false,hingeCenter,universal,compiledEnvironment);				
			else
				return;//no action
		}	
		
	}
	
	private static void makeStaticJoint(DynamicPhysicsNode physicsNode, PhysicsCollisionGeometry axle,PhysicsCollisionGeometry staticAxle, boolean useLeft,Vector3f hingeCenter, boolean universal, OdePhysicsEnvironment compiledEnvironment)
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
		dif.normalizeLocal();
		
        joint.attach(physicsNode) ;
        
        joint.setAnchor(new Vector3f(hingeCenter)); //the anchor has to be set at the center of the SECOND node
		
        
        RotationalJointAxis axis1 = joint.createRotationalAxis();	     
        //node.axis1.setDirection( dif.cross(normal) );
        axis1.setDirection(axle.getWorldRotation().mult(new Vector3f(0,0,1)));
        
        RotationalJointAxis axis2 = joint.createRotationalAxis();	     
        axis2.setDirection( axle.getWorldRotation().mult(new Vector3f(0,1,0)) );
        axis2.setRelativeToSecondObject(true);
        if(!universal){
        RotationalJointAxis axis3 = joint.createRotationalAxis();	     
        axis3.setDirection( axle.getWorldRotation().mult(new Vector3f(1,0,0)) );
        axis3.setRelativeToSecondObject(true);
        }
	}
	
	
	
	private static void makeJoint(DynamicPhysicsNode physicsNode1, PhysicsCollisionGeometry axle1,DynamicPhysicsNode physicsNode2, PhysicsCollisionGeometry axle2,Vector3f hingeCenter, boolean universal, OdePhysicsEnvironment compiledEnvironment)
	{

			
		Joint joint = physicsNode1.getSpace().createJoint();//this already is set, but for clarity its here too
		

		joint.setCollisionEnabled(true);
		physicsNode1.updateWorldVectors();
		physicsNode2.updateWorldVectors();
		
		axle1.updateWorldVectors();
		axle2.updateWorldVectors();
			
		
		Vector3f posOffset = physicsNode1.worldToLocal(hingeCenter, new Vector3f());	
		if(!universal){
	        joint.attach(physicsNode1, physicsNode2) ;
	        joint.setAnchor(new Vector3f().set(posOffset)); //the anchor has to be set at the center of the SECOND node
				        
			final RotationalJointAxis axis1 = joint.createRotationalAxis();
			axis1.setDirection( new Vector3f( 0, 0, 1 ) );
			//axis1.setRelativeToSecondObject(true);
			
			final RotationalJointAxis axis2 = joint.createRotationalAxis();
			axis2.setDirection( new Vector3f( 0, 1, 0 ) );
			axis2.setRelativeToSecondObject(true);
			
	
			final RotationalJointAxis axis3 = joint.createRotationalAxis();
			axis3.setDirection( new Vector3f( 1, 0, 0 ) );
			axis3.setRelativeToSecondObject(true);
		}else{
			Vector3f rotationAxisUnit = new Vector3f(Vector3f.UNIT_X);//Rotates around this axis, 
			axle2.updateWorldVectors();
			axle2.getWorldRotation().multLocal(rotationAxisUnit).normalizeLocal();
			
			Vector3f axisUnit1 = new Vector3f(Vector3f.UNIT_Z);
			axle2.getWorldRotation().multLocal(axisUnit1).normalizeLocal();
	        joint.attach(physicsNode1, physicsNode2) ;
	        joint.setAnchor(new Vector3f().set(posOffset)); //the anchor has to be set at the center of the SECOND node
				        
			final RotationalJointAxis axis1 = joint.createRotationalAxis();
			axis1.setDirection(axisUnit1 );
			//axis1.setRelativeToSecondObject(true);
			
			Vector3f axisUnit2 = new Vector3f(Vector3f.UNIT_Y);
			axle2.getWorldRotation().multLocal(axisUnit2).normalizeLocal();
			final RotationalJointAxis axis2 = joint.createRotationalAxis();
			axis2.setDirection(axisUnit2 );
			axis2.setRelativeToSecondObject(true);
			
			
		}
			// Attach it
			 	        
	        //the joints are in the physicsnode's coordinate space!
		}	

	private static final float INNER_DISTANCE = 0.001f;
	
	
	
	private class OdeSocketJointStructure extends OdePhysicalStructure
	{

		CapsuleCombinedModel jointModel;
		CapsuleCombinedFacade jointFacade;

		

		
		public OdeSocketJointStructure(PropertyStore store) {
			super(store);
		
		}

		@Override
		public void buildCollidable(CollisionMember physicsCollision) {

				jointModel = new CapsuleCombinedModel(true);
				jointFacade = new CapsuleCombinedFacade();
	
	
	
			jointModel.getLocalTranslation().x = interpreter.getRightRadius() + interpreter.getLeftRadius() + interpreter.getLeftLength()/2f +INNER_DISTANCE  ;
			float radius = interpreter.getLeftRadius();
			
			
			jointModel.rebuild(radius- INNER_DISTANCE, interpreter.getLeftLength()+radius*2f - INNER_DISTANCE);
			
			jointFacade.rebuild(radius- INNER_DISTANCE, interpreter.getLeftLength()+radius*2f  - INNER_DISTANCE);
			
			jointModel.updateWorldData();
			
			Vector3f worldTranslation = interpreter.getLocalTranslation();
			Quaternion worldRotation = interpreter.getLocalRotation();
			
			worldRotation.multLocal(jointModel.getLocalTranslation());
			jointModel.getLocalTranslation().addLocal(worldTranslation);
			jointModel.getLocalRotation().set(worldRotation.mult(jointModel.getLocalRotation()));
			
			this.jointFacade.loadModelData(jointModel);
			
	
				if(interpreter.getLeftLength()>0f)
					physicsCollision.registerCollidingModel(jointModel.getCyl());
				
				physicsCollision.registerCollidingModel(jointModel.getLeft());
				physicsCollision.registerCollidingModel(jointModel.getRight());
				jointModel.getCyl().getListeners().clear();
				jointModel.getLeft().getListeners().clear();
				jointModel.getRight().getListeners().clear();
			
			
			physicsCollision.getModel().addChild(jointModel);
			physicsCollision.getModel().addChild(jointFacade);
			jointModel.updateModelData();
			jointModel.updateWorldData();
			jointFacade.updateWorldData();
		
		
			
			super.getStructuralAppearanceEffect().attachModel(jointFacade);
		}
		
		PhysicsCollisionGeometry jointCollision = null;
		
		@Override
		public float buildCollisionGeometries(PhysicsNode physicsNode,Collection<PhysicsComponent> components,
				Vector3f store) {
			float radius = interpreter.getLeftRadius();
			
			float length = interpreter.getLeftLength();
			
			if(length>0f)
			{
				jointCollision=physicsNode.createCapsule("joint");	
				jointCollision.getLocalTranslation().set(jointModel.getLocalTranslation());
				jointCollision.getLocalScale().z= interpreter.getLeftLength();
				jointCollision.getLocalScale().x= radius- INNER_DISTANCE;
				jointCollision.getLocalScale().y= radius- INNER_DISTANCE;
				jointCollision.getLocalRotation().set(jointModel.getLocalRotation());
				jointCollision.getLocalRotation().multLocal(new Quaternion().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Y));
				jointCollision.setIsCollidable(false);
				jointCollision.setMaterial(getMaterial());
				physicsNode.attachChild(jointCollision);		
				
			}else{
				jointCollision=physicsNode.createSphere("joint");	
				jointCollision.getLocalTranslation().set(jointModel.getLocalTranslation());
				jointCollision.getLocalScale().z= radius- INNER_DISTANCE;
				jointCollision.getLocalScale().x= radius- INNER_DISTANCE;
				jointCollision.getLocalScale().y= radius- INNER_DISTANCE;
				jointCollision.getLocalRotation().set(jointModel.getLocalRotation());
				jointCollision.getLocalRotation().multLocal(new Quaternion().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Y));
				jointCollision.setIsCollidable(false);
				jointCollision.setMaterial(getMaterial());
				physicsNode.attachChild(jointCollision);		
				
			}
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
	
	private class OdeSphereStructure extends OdePhysicalStructure
	{


		public OdeSphereStructure(PropertyStore store) {
			super(store);
			
		}

		protected SphereModel physicsModel = null;
		protected SphereFacade physicsFacade = null;
		
		public void buildCollidable(CollisionMember collidable) {

			physicsModel = new SphereModel(true);

			physicsModel.getLocalTranslation().set(interpreter.getLocalTranslation());
			
			physicsModel.getLocalRotation().set(interpreter.getLocalRotation());
			
			
				//sometimes these are not the same, so this is important.
				physicsModel.getLocalScale().set(interpreter.getRightRadius()-INNER_DISTANCE/2f,interpreter.getRightRadius()-INNER_DISTANCE/2f,interpreter.getRightRadius()-INNER_DISTANCE/2f).multLocal(2f);
			
			
			collidable.registerCollidingModel(physicsModel);
			collidable.getModel().addChild(physicsModel);
			physicsModel.getListeners().clear();//?
			physicsModel.updateModelData();
			physicsModel.updateWorldData();
			
			physicsFacade = new SphereFacade();
			physicsFacade.loadModelData(physicsModel);
			collidable.getModel().addChild(physicsFacade);
			
			super.getStructuralAppearanceEffect().attachModel(physicsFacade);
		}
		

		private transient PhysicsCollisionGeometry collision = null;
		
		
		@Override
		public float buildCollisionGeometries(PhysicsNode physicsNode,Collection<PhysicsComponent> components,
				Vector3f store) {

			
			{
				collision = physicsNode.createSphere("sphere");
			}
			//and construct physics
			
			collision.getLocalTranslation().set(physicsModel.getLocalTranslation());
			collision.getLocalRotation().set(physicsModel.getLocalRotation());
		
				
				physicsModel.getSpatial().updateWorldVectors();
				collision.getLocalScale().set(physicsModel.getSpatial().getWorldScale()).multLocal(0.5f);
				collision.updateWorldVectors();
			

			collision.setIsCollidable(false);
			
			collision.setMaterial(this.getMaterial());

			physicsNode.attachChild(collision);
			collision.updateWorldVectors();//note: it is critical to update world scale before calculating the volume!

			OdePhysicsComponent jointComp = new OdePhysicsComponent(collision,physicsModel);
			jointComp.setMass( collision.getVolume()*getMaterial().getDensity());
			components.add(jointComp);
			
			store.set(collision.getLocalTranslation());
			return collision.getVolume() * this.getMaterial().getDensity();
		}

		
		public void buildRelationships( Map<OdePhysicalStructure, PhysicsNode> physicsMap, OdePhysicsEnvironment compiledEnvironment) 
		{
			for(PhysicalDecorator decorator:super.getPhysicalDecorators())
				decorator.attach(physicsMap.get(this), collision);
		}

	}

	private class OdeUniversalJointStructure extends OdePhysicalStructure
	{
	
		CylinderModel jointModel;
		CylinderFacade jointFacade;
	
		
	
		
		public OdeUniversalJointStructure(PropertyStore store) {
			super(store);
		
		}
	
		@Override
		public void buildCollidable(CollisionMember physicsCollision) {
			
	
			jointModel = new CylinderModel(true);
			jointFacade = new CylinderFacade();
			

			jointModel.getLocalTranslation().x = interpreter.getRightRadius() +  interpreter.getLeftLength()/2f +INNER_DISTANCE  ;
			float radius = interpreter.getLeftRadius();
			
			jointModel.getLocalScale().z =  interpreter.getLeftLength();
			jointModel.getLocalScale().y = radius*2f;
			jointModel.getLocalScale().x = radius*2f;
			jointFacade.loadModelData(jointModel);
	/*		capsuleModel.rebuild(radius- INNER_DISTANCE, interpreter.getLeftLength()+radius*2f - INNER_DISTANCE);
			
			capsuleFacade.rebuild(radius- INNER_DISTANCE, interpreter.getLeftLength()+radius*2f  - INNER_DISTANCE);
	*/
			jointModel.updateWorldData();
			
			Vector3f worldTranslation = interpreter.getLocalTranslation();
			Quaternion worldRotation = interpreter.getLocalRotation();
			
			worldRotation.multLocal(jointModel.getLocalTranslation());
			jointModel.getLocalTranslation().addLocal(worldTranslation);
			jointModel.getLocalRotation().set(worldRotation.mult(jointModel.getLocalRotation()));
			
			this.jointFacade.loadModelData(jointModel);
			
		/*	if(!universal){
				if(interpreter.getLeftLength()>0f)
					physicsCollision.registerCollidingModel(capsuleModel.getCyl());
				
				physicsCollision.registerCollidingModel(capsuleModel.getLeft());
				physicsCollision.registerCollidingModel(capsuleModel.getRight());
				capsuleModel.getCyl().getListeners().clear();
				capsuleModel.getLeft().getListeners().clear();
				capsuleModel.getRight().getListeners().clear();
			}*/
			physicsCollision.registerCollidingModel(jointModel);
			jointModel.getListeners().clear();
			physicsCollision.getModel().addChild(jointModel);
			physicsCollision.getModel().addChild(jointFacade);
			jointModel.updateModelData();
			jointModel.updateWorldData();
			jointFacade.updateWorldData();
		
		
			
			super.getStructuralAppearanceEffect().attachModel(jointFacade);
		}
		
		PhysicsCollisionGeometry jointCollision = null;
		
		@Override
		public float buildCollisionGeometries(PhysicsNode physicsNode,Collection<PhysicsComponent> components,
				Vector3f store) {
			float radius = interpreter.getLeftRadius();
			
			float length = interpreter.getLeftLength();
			{
				jointCollision=physicsNode.createCylinder("joint");	
				jointCollision.getLocalTranslation().set(jointModel.getLocalTranslation());
				jointCollision.getLocalScale().z= interpreter.getLeftLength()- INNER_DISTANCE;
				jointCollision.getLocalScale().x= radius;
				jointCollision.getLocalScale().y= radius;
				jointCollision.getLocalRotation().set(jointModel.getLocalRotation());
			//	jointCollision.getLocalRotation().multLocal(new Quaternion().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Y));
				jointCollision.setIsCollidable(false);
				jointCollision.setMaterial(getMaterial());
				physicsNode.attachChild(jointCollision);		
				
			}
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

}
