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
package com.golemgame.states.physics;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.golemgame.states.StateManager;
import com.jme.bounding.CollisionTreeManager;
import com.jmex.physics.CollisionGroup;
import com.jmex.physics.impl.ode.OdePhysicsSpace;

public class PhysicsController {
	private final PhysicsMonitoredMultithreadedGameState physicsState;
	
	public enum State
	{
		Unprepared(),Prepared(),Loaded(),Started(),Running(), Stopped(), Unloaded();
		
	}
	
	private final Lock stateLock = new ReentrantLock();
	
	private State currentState = State.Unprepared;
	private AtomicBoolean inTransition = new AtomicBoolean(false);
	
	public State getCurrentState() {
		return currentState;
	}

	public AtomicBoolean getInTransition() {
		return inTransition;
	}

	public PhysicsController(PhysicsMonitoredMultithreadedGameState physicsState) {
		super();
		this.physicsState = physicsState;
	}


	/**
	 * Subclasses are recommended to lock the state in the same way.
	 */
	protected void lockState()
	{
		this.physicsState.lock();
		stateLock.lock();
	}
	
	protected void unlockState()
	{
		try{
		stateLock.unlock();
		}finally{
		this.physicsState.unlock();
		}
	}
	
	/**
	 * Clean up any remnants of previous physics sessions
	 */
	public synchronized void preparePhysics() throws IllegalTransitionException
	{
		lockState();	
		try{
			StateManager.getLogger().info("Preparing Physics");
			if( this.currentState != State.Unprepared && this.currentState != State.Unloaded && this.currentState != State.Prepared)			
				throw new IllegalTransitionException("Cannot transition from " + currentState + " to " + State.Prepared);
				
				System.gc();
				physicsState.setPhysicsEnabled(false);				
				physicsState.clear();
				System.gc();
				
	
			
			
			this.currentState = State.Prepared;
		}finally
		{
			unlockState();
		}
	}
	
	/**
	 * Load a new physics session
	 */
	public synchronized void load()throws IllegalTransitionException
	{
		lockState();		
		try{
			StateManager.getLogger().info("Loading Physics");
			if(this.currentState != State.Prepared)			
				throw new IllegalTransitionException("Cannot transition from " + currentState + " to " + State.Loaded);
			
			
			
			this.currentState = State.Loaded;
		}finally
		{
			unlockState();
		}
	}
	
	/**
	 * Begin running physics
	 */
	public synchronized void start()throws IllegalTransitionException
	{
		lockState();		
		try{
			StateManager.getLogger().info("Starting Physics");
			if(this.currentState != State.Loaded)			
				throw new IllegalTransitionException("Cannot transition from " + currentState + " to " + State.Started);
			
			this.physicsState.setPhysicsEnabled(true);

		/*	
			for (Reference<Geom> ref:Geom.geomNativeAddr.values())
			{
				if (ref.get()==null)
				{
					System.out.println();
				}
			}
			System.gc();
			for (Reference<Geom> ref:Geom.geomNativeAddr.values())
			{
				if (ref.get()==null)
				{
					System.out.println();
				}
			}
			*/
			this.currentState = State.Started;
		}finally
		{
			unlockState();
		}
	}
	
	/**
	 * Stop running physics
	 */
	public synchronized void stop()throws IllegalTransitionException
	{
		lockState();
		try{
			StateManager.getLogger().info("Stopping Physics");
			if(this.currentState != State.Started)			
				throw new IllegalTransitionException("Cannot transition from " + currentState + " to " + State.Stopped);
			
			physicsState.setPhysicsEnabled(false);	
			
			this.currentState = State.Stopped;
		}finally
		{
			unlockState();
		}
	}
	
	/**
	 * Delete and cleanup current physics session
	 */
	public synchronized void unload()throws IllegalTransitionException
	{
		lockState();
		try{
			StateManager.getLogger().info("Unloading Physics");
			if(this.currentState != State.Loaded && this.currentState != State.Stopped)			
				throw new IllegalTransitionException("Cannot transition from " + currentState + " to " + State.Unloaded);
		
	/*		StateManager.getGame().lock();
			try{
				physicsState.getPhysicsRoot().detachAllChildren();
			}finally{
				StateManager.getGame().unlock();
			}*/
			//physicsState.reportPhysicsStatus();
			physicsState.clear();
		
			
			
	/*		ArrayList<CollisionGroup> cols= new ArrayList<CollisionGroup>( physicsState.getPhysicsSpace().getCollisionGroups());
			OdePhysicsSpace oSpace = (OdePhysicsSpace) physicsState.getPhysicsSpace();
			for(CollisionGroup c:cols)
			{
				if(c==oSpace.getDefaultCollisionGroup() || c == oSpace.getStaticCollisionGroup())
				{
					
				}else{
					try{
						c.delete();
					}catch(Exception e)
					{
						StateManager.logError(e);
					}
				}
			}
		*/
			CollisionTreeManager.getInstance().clear();
			System.gc();
			Thread.yield();
			System.gc();
			Thread.yield();
			System.gc();
			
			
			this.currentState = State.Unloaded;
		}finally
		{
			unlockState();
		}
	}
	
	/**
	 * Attempt to get this controller into a state from which it can be prepared.
	 * @throws IllegalTransitionException 
	 * @throws IllegalStateException if the state is null.
	 */
	public synchronized void cleanup() throws IllegalTransitionException
	{
		switch(currentState)
		{
			case Unprepared:
				break;//do nothing
			case Prepared: 
			
					//do nothing
		
				break;
			case Loaded:
			
					unload();
		
				break;
			case Started:
			
					stop();
					unload();
		
				break;
			case Stopped:
			
					unload();
			
				break;
			case Unloaded:
				break;//do nothing
			default:
				throw new IllegalStateException(String.valueOf(currentState));
		}
	}
}
