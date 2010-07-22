package com.golemgame.states.physics.ode;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.golemgame.states.StateManager;
import com.golemgame.util.event.ListenerMap;
import com.jme.input.InputHandler;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.input.util.SyntheticButton;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.PhysicsSpatial;
import com.jmex.physics.PhysicsUpdateCallback;
import com.jmex.physics.contact.ContactInfo;
/**
 * This is a support class used to add various types of observers to the jme-physics api.
 * Should move this into the ode implementation, make the an abstract interface...
 * @author Sam
 *
 */
public class PhysicsSpatialMonitor implements PhysicsUpdateCallback {

	private static final PhysicsSpatialMonitor instance = new PhysicsSpatialMonitor();
	
	private ListenerMap<PhysicsNode, PhysicsRestingListener> physicsListenerMap = new ListenerMap<PhysicsNode, PhysicsRestingListener>();

	private ListenerMap<PhysicsNode, PhysicsCollisionListener> physicsCollisionMap = new ListenerMap<PhysicsNode, PhysicsCollisionListener>();

	private ListenerMap<PhysicsCollisionGeometry, PhysicsCollisionListener> physicsCollisionGeometryMap = new ListenerMap<PhysicsCollisionGeometry, PhysicsCollisionListener>();

	
	private ArrayList<ListenerPair> testPairs = new ArrayList<ListenerPair>();
	
	private InputHandler inputHandler = new InputHandler(); 
	
	private Lock updateLock = new ReentrantLock();
	
	private boolean isUpdating = false;
	
	private HashMap<PhysicsRestingListener,PhysicsNode> restingListenersToBeDeleted = new  HashMap<PhysicsRestingListener,PhysicsNode>();
	
	/**
	 * List of listeners to be removed after they next register a collision
	 */
	private HashMap<PhysicsCollisionListener,PhysicsNode> collisionListenersToBeDeleted = new  HashMap<PhysicsCollisionListener,PhysicsNode>();
	
	private Set<PhysicsSpatial> ghostObjects = new HashSet<PhysicsSpatial>();
	
	
	public InputHandler getInputHandler() {
		return inputHandler;
	}

	public static PhysicsSpatialMonitor getInstance() {
		return instance;
	}
	
	public void clear()
	{
		updateLock.lock();
		try{
			testPairs.clear();
			physicsListenerMap.clear();
			physicsCollisionMap.clear();
			physicsCollisionGeometryMap.clear();
			collisionListenersToBeDeleted.clear();
			restingListenersToBeDeleted.clear();
			ghostObjects.clear();
			getInputHandler().clearActions();
			this.inputHandler = new InputHandler();
		}finally{
			updateLock.unlock();
		}
	}

	public void setPhysics(PhysicsSpace physicsSpace)
	{
		
		physicsSpace.addToUpdateCallbacks(this);
		
		SyntheticButton synth = physicsSpace.getCollisionEventHandler();
		
		 InputAction buttonAction = new InputAction() {

			
			public void performAction(InputActionEvent evt) {
				ContactInfo info = (ContactInfo)  evt.getTriggerData();
				PhysicsCollisionGeometry geom1 = info.getGeometry1();
				PhysicsCollisionGeometry geom2 = info.getGeometry2();
				info.getGeometry2();
				List<PhysicsCollisionListener> list = physicsCollisionGeometryMap.getListeners(geom1);
				if(list != null)
				{
					Iterator<PhysicsCollisionListener> it = list.iterator();
					while(it.hasNext()){
						PhysicsCollisionListener listener = it.next();
						if(listener == null)
						{
							it.remove();
						}else if(collisionListenersToBeDeleted.containsKey(listener))
						{
							collisionListenersToBeDeleted.remove(listener);
							it.remove();
						}else
							listener.collisionOccured(info,geom1);
					}
				}
				list = physicsCollisionGeometryMap.getListeners(geom2);
				if(list != null)
				{
					Iterator<PhysicsCollisionListener> it = list.iterator();
					while(it.hasNext()){
						PhysicsCollisionListener listener = it.next();
						if(listener == null)
						{
							it.remove();
						}else if(collisionListenersToBeDeleted.containsKey(listener))
						{
							collisionListenersToBeDeleted.remove(listener);
							it.remove();
						}else
							listener.collisionOccured(info,geom2);
					}
				}
			}
			 
		 };
		   getInputHandler().addAction( buttonAction,synth.getDeviceName(),synth.getIndex(),InputHandler.AXIS_NONE, false );
	
	}
	
	
	public void afterStep(PhysicsSpace space, float time) {
		//determine is any listeners should be added first
		updateLock.lock();//neccesary to prevent a concurrent mod execption if an attempt is made to clear maps while iterating over them.
		try{
			isUpdating = true;
			getInputHandler().update(time);
			try{
			for (ListenerPair pair:testPairs)
			{
				if (pair.getPhysicsNode().isResting())
				{
					addPhysicsRestingListener(pair.getPhysicsNode(),pair.getListener());
				}
			}
			testPairs.clear();
		
				for(PhysicsNode node:physicsListenerMap)
				{
					boolean isResting = node.isResting();
					
					
					List<PhysicsRestingListener> list = physicsListenerMap.getListeners(node);
					if(list!= null)
					{
						Iterator<PhysicsRestingListener> it = list.iterator(); 
						while(it.hasNext())
						{
							PhysicsRestingListener listener = it.next();
							if(listener == null)
								it.remove();
							else if(restingListenersToBeDeleted.containsKey(listener))
							{
								restingListenersToBeDeleted.remove(listener);
								it.remove();
							}
							else
							{
								listener.notifyResting(isResting, node);
								
							}
						}
					}
				
				}
			}catch(ConcurrentModificationException e)
			{
				StateManager.logError(e);
			}
		}finally{
			isUpdating = false;
			updateLock.unlock();
		}
	}

	
	public void beforeStep(PhysicsSpace space, float time) {
		

	}
	
	public void addPhysicsRestingListener(PhysicsNode listenTo, PhysicsRestingListener listener)
	{
		updateLock.lock();
		try{
			physicsListenerMap.addListener(listenTo, listener);
		}finally{
			updateLock.unlock();
		}
	}
	
	public void removePhysicsRestingListener(PhysicsNode listenTo, PhysicsRestingListener toRemove)
	{
		updateLock.lock();
		try{
			//if you are currently updating, add it to the to be deleted list instead.
			if(isUpdating)
			{
				restingListenersToBeDeleted.put(toRemove,listenTo);
			}else
				physicsListenerMap.removeListener(listenTo, toRemove);
		}finally{
			updateLock.unlock();
		}
	}
	
	public void addPhysicsCollisionListener(PhysicsCollisionGeometry geometry, PhysicsCollisionListener listener)
	{
		updateLock.lock();
		try{
		
			physicsCollisionGeometryMap.addListener(geometry, listener);
	
		}finally{updateLock.unlock();}
	}
	
	public void addPhysicsCollisionListener(PhysicsNode physicsNode, PhysicsCollisionListener listener)
	{
		updateLock.lock();
		try{
			
			physicsCollisionMap.addListener(physicsNode, listener);
			
		}finally{
			updateLock.unlock();
		}
	}
	
	public void removePhysicsCollisionListener(PhysicsNode physicsNode, PhysicsCollisionListener listener)
	{
		updateLock.lock();
		try{
			if(isUpdating)
			{
				collisionListenersToBeDeleted.put(listener,physicsNode);
			}else
				physicsCollisionMap.removeListener(physicsNode, listener);
			
		}finally{
			updateLock.unlock();
		}
	}
	
	public void registerGhost(PhysicsSpatial ghost)
	{
		this.ghostObjects.add(ghost);
	}
	
	public boolean isGhost(PhysicsSpatial ghost)
	{
		return this.ghostObjects.contains(ghost);
	}
	
	public static interface PhysicsRestingListener
	{
		public void notifyResting(boolean resting, PhysicsNode physicsNode);
	}
	
	public static interface PhysicsCollisionListener
	{
		/**
		 * 
		 * @param info
		 * @param source Either a physics node, or collision geometry
		 */
		public void collisionOccured(ContactInfo info,PhysicsSpatial source);
	}
	
	/**
	 * Call this method to request that the monitor test, at the end of the current physics iteration,
	 * whether or not the given physics node is resting.
	 * If it is, add the given listener to the physics node.
	 * @param physicsNode
	 * @param listener
	 */
	public void addListenerIfStartedResting(DynamicPhysicsNode physicsNode, PhysicsRestingListener listener)
	{
		updateLock.lock();
		try{
			testPairs.add(new ListenerPair(listener,physicsNode));
		}finally{
			updateLock.unlock();
		}
	}
	
	private static class ListenerPair
	{
		private final DynamicPhysicsNode physicsNode;
		private final PhysicsRestingListener listener;
		public ListenerPair(PhysicsRestingListener listener,
				DynamicPhysicsNode physicsNode) {
			super();
			this.listener = listener;
			this.physicsNode = physicsNode;
		}
		public DynamicPhysicsNode getPhysicsNode() {
			return physicsNode;
		}
		public PhysicsRestingListener getListener() {
			return listener;
		}
		
	}
	

}
