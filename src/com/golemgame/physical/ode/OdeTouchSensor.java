package com.golemgame.physical.ode;

import java.util.Collection;
import java.util.Map;

import com.golemgame.functional.component.BComponent;
import com.golemgame.functional.component.BMind;
import com.golemgame.functional.component.BTouchSensor;
import com.golemgame.model.Model;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.Reference;
import com.golemgame.mvc.golems.TouchSensorInterpreter;
import com.golemgame.mvc.golems.WirePortInterpreter;
import com.golemgame.physical.PhysicsComponent;
import com.golemgame.physical.ode.compile.OdePhysicsEnvironment;
import com.golemgame.states.physics.ode.PhysicsSpatialMonitor;
import com.golemgame.states.physics.ode.PhysicsSpatialMonitor.PhysicsCollisionListener;
import com.golemgame.states.physics.ode.PhysicsSpatialMonitor.PhysicsRestingListener;
import com.golemgame.structural.collision.CollisionMember;
import com.jme.math.Vector3f;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.PhysicsSpatial;
import com.jmex.physics.contact.ContactInfo;

public class OdeTouchSensor extends OdeBoxStructure{

	private TouchSensorInterpreter interpreter;
	
	public OdeTouchSensor(PropertyStore store) {
		super(store);
		interpreter = new TouchSensorInterpreter(store);
	}

	private transient Model physicsModel;
	private transient PhysicsCollisionGeometry physicsGeom;
	
	public void buildCollidable(CollisionMember collidable) {
		super.buildCollidable(collidable);
		physicsModel = super.physicsModel;
	}

	
	public float buildCollisionGeometries(PhysicsNode physicsNode,Collection<PhysicsComponent> components,
			Vector3f store) {
		
		float mass =  super.buildCollisionGeometries(physicsNode,components, store);
		physicsGeom = super.getCollisionGeometry();
		return mass;
	}

	@Override
	public void buildMind(BMind mind,
			Map<OdePhysicalStructure, PhysicsNode> physicalMap,
			Map<Reference, BComponent> wireMap, OdePhysicsEnvironment environment) {
		final PhysicsNode physicsNode = physicalMap.get(this);
		
		if (physicsModel == null || physicsGeom == null ||physicsNode==null)
			return;
		
		final PhysicsCollisionGeometry physicsCollisionGeom = physicsGeom;
		
		final BTouchSensor touchSensor = new BTouchSensor();
		mind.addComponent(touchSensor);
		PhysicsSpatialMonitor.getInstance().addPhysicsCollisionListener(physicsCollisionGeom, new PhysicsCollisionListener()
		{
			private PhysicsRestingListener restingListener;
			
			public void collisionOccured(ContactInfo info, PhysicsSpatial source) {
				  final PhysicsNode otherPhysics = (info.getNode1() == (physicsNode)?info.getNode2():info.getNode1());
	             
				  if((PhysicsSpatialMonitor.getInstance().isGhost(info.getGeometry1())) ||PhysicsSpatialMonitor.getInstance().isGhost(info.getGeometry2()) )
					  return;//ignore ghosts
				  
				  touchSensor.notifyOfCollision();
	              
	               restingListener = new PhysicsRestingListener()
	               {

					
					public void notifyResting(boolean resting,
							PhysicsNode physicsNode) {
						if(resting)
						{
							 touchSensor.notifyOfCollision();
						}
						else
						{
							PhysicsSpatialMonitor.getInstance().removePhysicsRestingListener(physicsNode, restingListener);
							PhysicsSpatialMonitor.getInstance().removePhysicsRestingListener(otherPhysics, restingListener);
						}
					}
					
	               };
	               
	               if(!physicsNode.isStatic())
	            	   PhysicsSpatialMonitor.getInstance().addPhysicsRestingListener(physicsNode,restingListener);
	               if(!otherPhysics.isStatic())
	            	   PhysicsSpatialMonitor.getInstance().addPhysicsRestingListener(otherPhysics,restingListener);
	         
	            }
	    });
	        //register the action with all devices (mouse, keyboard, joysticks, etc) for all buttons

		
		/*
		 * Collisions only report initial collisions - not resting force
		 * So: one alternative is, each time a physics object collides with this, add a listener.
		 * This listener is notified when that same object moves again in the future.
		 * This way, if a collision occurs but the object fails to move, then force(gravity*mass) can be assumed to count permanently
		 * 
		 * so: collision occurs - add listener - assume force is gravity*mass of colliding object until listener is notified.
		 * 
		 * 
		 */
		
		/*
		 * Either an object is colliding with you, OR its at rest on you. 
		 * So, have a physics monitoring object added to physics callback.
		 * You can ask it to notify you if a given object goes to rest after it has collided with you,
		 * and you can ask it to notify you when that object is no longer at rest afterwards.
		 * 
		 * Between those times, force from the object is a constant.
		 * 
		 * this can be ignored altogether is auto-rest threshold is not used
		 */
	    	   
	    	   /*
	    	    * EITHER object can move.
	    	    * 
	    	    * if either this or the other object are moving, THEN wait for collision again.
	    	    * only if they are BOTH at rest do you need to do this.
	    	    * 
	    	    */


		

		WirePortInterpreter out = new WirePortInterpreter(interpreter.getOutput());

		mind.addSource(touchSensor);
		wireMap.put(out.getID(), touchSensor);
	}
	
	
}
