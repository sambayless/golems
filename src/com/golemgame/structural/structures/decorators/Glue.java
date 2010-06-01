package com.golemgame.structural.structures.decorators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.decorators.GlueInterpreter;
import com.golemgame.states.StateManager;
import com.golemgame.states.physics.PhysicsSpatialMonitor;
import com.golemgame.states.physics.PhysicsSpatialMonitor.PhysicsCollisionListener;
import com.jme.math.Vector3f;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.Joint;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.PhysicsSpatial;
import com.jmex.physics.RotationalJointAxis;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.contact.ContactInfo;

public class Glue implements PhysicalDecorator{

	private static final long serialVersionUID = 1L;
	private static final float breakingLinearForce = 0.00001f;
	private static final float breakingTorque = 0.0000001f;
	
	private GlueInterpreter interpreter;
	
	
	public void attach(PhysicsNode physicsNode,
			PhysicsCollisionGeometry[] involvedGeometries) {
	
		final List<Joint> currentJoints = new ArrayList<Joint>();
		final List<Joint> usedJoints = new ArrayList<Joint>();
		PhysicsCollisionListener listener = new PhysicsCollisionListener()
		{

			
			public void collisionOccured(ContactInfo info, PhysicsSpatial source) {
				//when a contact occurs, do two things:
				//first, check if any glue joints exist between these two nodes.
				//secondly, if no such joints exist, add one.
				
				PhysicsNode node1 = info.getNode1();
				PhysicsNode node2  = info.getNode2();
				
				if(	PhysicsSpatialMonitor.getInstance().isGhost(node1)|| 	PhysicsSpatialMonitor.getInstance().isGhost(node2) || 	PhysicsSpatialMonitor.getInstance().isGhost(info.getGeometry1()) || 	PhysicsSpatialMonitor.getInstance().isGhost(info.getGeometry2()))
					return;
				
				boolean jointAlreadyExists = false;
		//		if(info.getContactVelocity(new Vector3f()).lengthSquared()>10)
		//			return;
				int removedNodes = 0;
				Iterator<Joint> it = currentJoints.iterator();
				while (it.hasNext())
				{
					Joint joint = it.next();
					if(joint.getNodes().isEmpty() || joint.getNodes().get(0) == null &&  joint.getNodes().get(1) == null)
					{
						removedNodes++;
						
						it.remove();
						joint.setActive(false);
						joint.detach();
						usedJoints.add(joint);
					}
				}
				it = currentJoints.iterator();
				while (it.hasNext())
				{
					Joint joint = it.next();
					
					
					if(!node1.isStatic() && !node2.isStatic() &&(joint.getNodes().contains(node1) && joint.getNodes().contains(node2)))
					{
							jointAlreadyExists = true;
							break;
					}else  if(!node1.isStatic() && node2.isStatic() &&(joint.getNodes().contains(node1)))
					{
						jointAlreadyExists = true;
						break;
					}else  if(node1.isStatic() && (!node2.isStatic()) &&(joint.getNodes().contains(node2)))
					{
						jointAlreadyExists = true;
						break;
					}
				}
				
				if(!jointAlreadyExists)
				{
					double threshold = 3.0/4.0;//((removedNodes<=0)?1.0:(removedNodes));
			
					if (Math.random()>threshold)
						return;
					//System.out.println("build" + this);
					if(info.getNode1().isStatic() && info.getNode2().isStatic())
					{
					
					}else if(!info.getNode1().isStatic() &! info.getNode2().isStatic())
					{
						currentJoints.add(buildJoint(info, (DynamicPhysicsNode)info.getNode1(),(DynamicPhysicsNode)info.getNode2(),usedJoints));
					}else if (info.getNode1().isStatic())
					{
						currentJoints.add(buildJoint(info, (DynamicPhysicsNode)info.getNode2(),(StaticPhysicsNode)info.getNode1(),usedJoints));

					}else if (info.getNode2().isStatic())
					{
						currentJoints.add(buildJoint(info, (DynamicPhysicsNode)info.getNode1(),(StaticPhysicsNode)info.getNode2(),usedJoints));

					}
				}
				
			}
			
		};

		for(PhysicsCollisionGeometry geometry:involvedGeometries)
		{

			if (geometry == null)
				continue;
			

			PhysicsSpatialMonitor.getInstance().addPhysicsCollisionListener(geometry, listener);
		}
		
	}
	
	private Joint buildJoint(ContactInfo info, DynamicPhysicsNode node1, StaticPhysicsNode node2,List<Joint> usedJoints)
	{

		
		Joint glueJoint;
		if(usedJoints.isEmpty())
			glueJoint = node1.getSpace().createJoint();
		else
		{
			glueJoint = usedJoints.get(usedJoints.size()-1);
			usedJoints.remove(usedJoints.size()-1);
		}
		glueJoint.attach(node1);
		glueJoint.setActive(true);
		//TranslationalJointAxis  axis = glueJoint.createTranslationalAxis();
		//RotationalJointAxis  rotational = glueJoint.createRotationalAxis();
		
		RotationalJointAxis axis;
		if(glueJoint.getAxes().size()>0)
			axis =(RotationalJointAxis) glueJoint.getAxes().get(0);
		else
			axis= glueJoint.createRotationalAxis();
		RotationalJointAxis axis2;
		if(glueJoint.getAxes().size()>1)
			axis2 =(RotationalJointAxis) glueJoint.getAxes().get(1);
		else
			axis2= glueJoint.createRotationalAxis();
		RotationalJointAxis axis3;
		if(glueJoint.getAxes().size()>2)
			axis3 =(RotationalJointAxis) glueJoint.getAxes().get(2);
		else
			axis3= glueJoint.createRotationalAxis();
		
		glueJoint.setBreakingLinearForce( 100f);
		glueJoint.setCollisionEnabled(true);
		glueJoint.setBreakingTorque( 100f);
		
		Vector3f crossVector = Vector3f.UNIT_X;
		if(info.getContactNormal(new Vector3f()).angleBetween(Vector3f.UNIT_X)< 0.01)
		{
			crossVector = Vector3f.UNIT_Y;
		}
		Vector3f direction = info.getContactNormal(new Vector3f()).cross(crossVector);

		axis.setDirection(direction);
		
		direction = info.getContactNormal(new Vector3f()).cross(crossVector).cross(info.getContactNormal(new Vector3f()));

		axis2.setDirection(direction);
		axis2.setRelativeToSecondObject(true);
		direction = info.getContactNormal(new Vector3f());//.cross(Vector3f.UNIT_X).cross(info.getContactNormal(new Vector3f()));

		axis3.setDirection(direction);
		axis3.setRelativeToSecondObject(true);
		
		Vector3f anchor =info.getContactPosition( new Vector3f());

		glueJoint.setAnchor(anchor);
		
		
		
		try{
			glueJoint.setSpring(2000,500);
		}catch(UnsupportedOperationException e)
		{
			StateManager.logError(e);
		}
		return glueJoint;
	}
	
	private Joint buildJoint(ContactInfo info, DynamicPhysicsNode node1, DynamicPhysicsNode node2,List<Joint> usedJoints)
	{

		
		Joint glueJoint = node1.getSpace().createJoint();
		if(usedJoints.isEmpty())
			glueJoint = node1.getSpace().createJoint();
		else
		{
			glueJoint = usedJoints.get(usedJoints.size()-1);
			usedJoints.remove(usedJoints.size()-1);
		}
		glueJoint.attach(node1,node2);
		glueJoint.setActive(true);
	
		RotationalJointAxis axis;
		if(glueJoint.getAxes().size()>0)
			axis =(RotationalJointAxis) glueJoint.getAxes().get(0);
		else
			axis= glueJoint.createRotationalAxis();
		RotationalJointAxis axis2;
		if(glueJoint.getAxes().size()>1)
			axis2 =(RotationalJointAxis) glueJoint.getAxes().get(1);
		else
			axis2= glueJoint.createRotationalAxis();
		RotationalJointAxis axis3;
		if(glueJoint.getAxes().size()>2)
			axis3 =(RotationalJointAxis) glueJoint.getAxes().get(2);
		else
			axis3= glueJoint.createRotationalAxis();
		
		glueJoint.setBreakingLinearForce(100f);
		glueJoint.setCollisionEnabled(true);
		glueJoint.setBreakingTorque(100f);

		Vector3f crossVector = Vector3f.UNIT_X;
		if(info.getContactNormal(new Vector3f()).angleBetween(Vector3f.UNIT_X)< 0.1f)
		{
			crossVector = Vector3f.UNIT_Y;
		}
		
		Vector3f direction = info.getContactNormal(new Vector3f()).cross(crossVector);
	
		axis.setDirection(direction);
		node1.getWorldRotation().inverse().multLocal(direction);
	

		axis.setDirection(direction);
		
		direction = info.getContactNormal(new Vector3f()).cross(crossVector).cross(info.getContactNormal(new Vector3f()));
		node2.getWorldRotation().inverse().multLocal(direction);
		axis2.setDirection(direction);
		axis2.setRelativeToSecondObject(true);
		
		direction = info.getContactNormal(new Vector3f());//.cross(Vector3f.UNIT_X).cross(info.getContactNormal(new Vector3f()));
		node2.getWorldRotation().inverse().multLocal(direction);
		axis3.setDirection(direction);
		axis3.setRelativeToSecondObject(true);
		
		
		
		Vector3f anchor =info.getContactPosition( new Vector3f());
		anchor = node1.worldToLocal(anchor,anchor);
		glueJoint.setAnchor(anchor);
		
		try{
			glueJoint.setSpring(2000, 400);
		}catch(UnsupportedOperationException e)
		{
			
		}
		return glueJoint;
	}


	
	
	public void attach(PhysicsNode physicsNode,
			PhysicsCollisionGeometry involvedGeometry) {
		this.attach(physicsNode, new PhysicsCollisionGeometry[]{involvedGeometry});

	}

	
	public Glue(PropertyStore store) {
		super();
		this.interpreter = new GlueInterpreter(store);
		interpreter.getStore().setSustainedView(this);
	}


	
	public void loadInitialize() {

	}

	public void invertView(PropertyStore store) {
		GlueInterpreter interp = new GlueInterpreter(store);
		
	}

	public void refresh() {
		
		
	}

	public PropertyStore getStore() {
		return interpreter.getStore();
	}
	
	
	public void remove() {

	}
	
}
