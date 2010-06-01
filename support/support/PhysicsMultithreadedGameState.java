/*
 * Copyright (c) 2005-2007 jME Physics 2
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of 'jME Physics 2' nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package support;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.scene.Node;
import com.jme.system.DisplaySystem;
import com.jme.util.NanoTimer;
import com.jme.util.Timer;
import com.jmex.game.StandardGame;
import com.jmex.game.state.BasicGameState;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.impl.ode.OdePhysicsSpace;


/**
 * <code>PhysicsGameState</code> provides physics encapsulation into a GameState.
 * Note: PhysicsDebugger is NOT thread-safe in its current state, and cannot be used with this implementation
 * @author Matthew D. Hicks
 * @author Sam Bayless
 */
public class PhysicsMultithreadedGameState extends BasicGameState {

	private PhysicsRunnable physicsRunnable;
	
	/**
	 * The game is used to lock and unlock the GLthread for physics updating so that rendering doesn't occur while the physics updates.
	 */
	private final StandardGame game;
	
	/**
	 * Keep a reference to this so that it can be shut down if necessary
	 */
	private Thread physicsThread;
	
	/**
	 * This node should be the parent for all physics objects.
	 * It will be updated in the physics thread.
	 */
	private final Node physicsRoot;
	
	/**
	 * This node should be the parent for all physics objects.
	 * It will be updated in the physics thread.
	 * @return the root physics node.
	 */
	public Node getPhysicsRoot() {
		return physicsRoot;
	}

	/**
	 * Build a new physics space, and remove all reference to the old one. This does not delete the physics space.
	 * It is safe to call this if the current physics space has become frozen.
	 */
	public void rebuildPhysics()
	{
		
		physicsRunnable.shutdown();
		physicsThread = new Thread(null, physicsRunnable, "Physics Thread");
		physicsThread.setDaemon(true);		
		physicsThread.start();

	}
	
	/**
	 * Completely delete this game state, and all associated physics objects. 
	 * Destroy this physics thread and its resources.
	 * If physics are referred to after this point, it may result in unspecified behavior
	 */
	public void delete()
	{
		super.getParent().detachChild(this);
		this.physicsRunnable.setEnabled(false);
		shutdown();
		
		this.physicsRunnable.physics.delete();
		
		if (this.physicsRunnable.physics instanceof OdePhysicsSpace)
		{
			OdePhysicsSpace odePhysics = (OdePhysicsSpace) physicsRunnable.physics;
			odePhysics.getWorld().delete();
			
		}
		
		physicsRoot.removeFromParent();
		
		physicsRunnable.physics= null;
	}
	
	public PhysicsMultithreadedGameState(String name,StandardGame game) {
		this(name, 30,game);
	}
	
	public PhysicsMultithreadedGameState(String name, int desiredUpdatesPerSecond,StandardGame game) {
		super(name);
		this.game = game;
		physicsRunnable = new PhysicsRunnable(desiredUpdatesPerSecond);
		
		physicsThread = new Thread(null, physicsRunnable, "Physics Thread",2*(long)Math.pow(2, 25));//64 mB.. give it alot of stack space for complicated physics
		physicsRoot = new Node();
		physicsThread.setDaemon(true);
		physicsThread.start();

	}
	
	public PhysicsSpace getPhysicsSpace() {
		return this.physicsRunnable.getPhysics();
	}
	
	public void update(float tpf) {
		super.update(tpf);
	}
	
	public void setUpdatesPerSecond(long updates)
	{
		physicsRunnable.setDesiredUpdatesPerSecond(updates);
	}
	
	public long getUpdatesPerSecond()
	{
		return this.physicsRunnable.getDesiredUpdatesPerSecond();
	}
	
	/**
	 * Set whether or not the physics is active.
	 * This is automatically set to false if this state is disabled; but the inverse is not true.
	 * @param enabled
	 */
	public void setPhysicsEnabled(boolean enabled)
	{
		physicsRunnable.setEnabled(enabled);
	}
	

	
	
	public void render(float tpf) {
		DisplaySystem.getDisplaySystem().getRenderer().draw(physicsRoot);	
		super.render(tpf);		
	}
	
	

	public void setActive(boolean active) {
		super.setActive(active);
		if (!active) 
		{
			this.setPhysicsEnabled(false);
		}
	}
	
	public void shutdown() {
		physicsRunnable.shutdown();		
	}
	
	/**
	 * A safe method to acquire locks on graphics and physics simultaneously, to avoid deadlock.
	 * In general, the graphics lock MUST be acquired before the physics lock to ensure safe operation.
	 * If you already hold the physics lock, it MUST be released before acquiring the openGL lock.
	 */
	public void acquireGraphicsAndPhysicsLocks()
	{
		int lockReleases = 0;
		//the physics lock MUST be released to other owners before taking the graphics lock.
		while (physicsRunnable.updateLock.isHeldByCurrentThread())
		{
			physicsRunnable.updateLock.unlock();
			lockReleases ++;
		}
		game.lock();
		lock();	
		for (int i = 0;i<lockReleases;i++)
			physicsRunnable.updateLock.lock();
		//lock again as many locks as were unlocked before
	}
	
	public void releaseGraphicsAndPhysicsLocks()
	{	
		unlock();	
		game.unlock();	
	}
	
	/**
	 * Note: You must control BOTH the physics thread and the open gl thread to modify the physics scene graph (remove or attach nodes/geometries).
	 */
	public void lock() {		
		physicsRunnable.lock();
	}
	
	public void unlock() 
	{
		
		physicsRunnable.unlock();
	}


	protected ReentrantLock getUpdateLock() {
		return this.physicsRunnable.updateLock;
	}
	
	private class PhysicsRunnable implements Runnable {
		private PhysicsSpace physics;
		private long preferredTicksPerFrame;
		private volatile boolean enabled;
		private boolean keepAlive;
		private Timer timer;
		private boolean limitUpdates;
		private ReentrantLock updateLock;

		private long desiredUpdatesPerSecond;
		private Condition enabledCondition;
		private Condition timePauseCondition;
		
		public PhysicsRunnable(int desiredUpdatesPerSecond) {
			
			this.physics = PhysicsSpace.create();
		
			enabled = false;
			keepAlive = true;
			this.desiredUpdatesPerSecond = desiredUpdatesPerSecond;
			updateLock = new ReentrantLock(true); 
		
			enabledCondition = updateLock.newCondition();
			timePauseCondition = updateLock.newCondition();
			timer = new NanoTimer();
			if (desiredUpdatesPerSecond == -1) {
				limitUpdates = false;
			} else {
				preferredTicksPerFrame = Math.round((float)timer.getResolution() / (float)desiredUpdatesPerSecond);
				limitUpdates = true;
			}	

		}
		
		public void setDesiredUpdatesPerSecond(long updates) {
			updateLock.lock();
			try{
				desiredUpdatesPerSecond = updates;
				if (desiredUpdatesPerSecond == -1) {
					limitUpdates = false;
				}else if (updates == Long.MIN_VALUE) 
				{
					preferredTicksPerFrame = Long.MIN_VALUE;
					limitUpdates = true;
				}
				else {
					preferredTicksPerFrame = Math.round((float)timer.getResolution() / (float)desiredUpdatesPerSecond);
					limitUpdates = true;
				}
				timePauseCondition.signalAll();
			}finally
			{
				updateLock.unlock();
			}
		}

		public PhysicsSpace getPhysics() {
			return physics;
		}
		
		public synchronized void setEnabled(boolean enabled) 
		{//only one thread can change enabled at a time
			
			if (this.enabled != enabled)
			{
			
				//this is only reached if the state of enabled has changed
				if (enabled)
				{
					//reset timer so it doesn't overestimate the tpf if the thread has been disabled for a while
					timer.reset();	
					this.enabled = enabled;//set enabled AFTER reseting the timer.
					if (( updateLock.tryLock()))//try to get the lock but don't block
					{
						try{
							enabledCondition.signalAll();//signal the main thread to check the enabled condition and acquire the lock
						}finally
						{
							updateLock.unlock();
						}
					}
				}else
					this.enabled = enabled;
				
			}
			
		}
		
		public boolean isEnabled() {
			return enabled;
		}
		
		public void run() {
			// We have to lock it up while we're starting
			lock();
		
			timer.reset();
	
			this.setEnabled(false);

			try{
				long frameStartTick = 0;
				long frameDurationTicks = 0;
				float tpf;
				while (keepAlive) {
				
					try{
						while (!enabled && keepAlive)							
							enabledCondition.await();//put the thread to sleep if it is not enabled	
					}catch(InterruptedException e)
					{
						Logger.getLogger( PhysicsSpace.LOGGER_NAME ).log(Level.SEVERE, "Interrupted while disabled",e);
					}
					if(!keepAlive)
						break;
					
					try{
						while(preferredTicksPerFrame == Long.MIN_VALUE && keepAlive)
						{				
							timePauseCondition.await();//put the thread to sleep if it is not enabled	
						}
					}catch(InterruptedException e)
					{
						Logger.getLogger( PhysicsSpace.LOGGER_NAME ).log(Level.SEVERE, "Interrupted while disabled",e);
					}
					
					if (limitUpdates) {
						frameStartTick = timer.getTime();
					}
					timer.update();
				
					tpf = timer.getTimePerFrame();
			
					update(tpf);
		
					unlock();
					try{
						if ((limitUpdates) && (preferredTicksPerFrame >= 0)) {
							frameDurationTicks = timer.getTime() - frameStartTick;
							while (frameDurationTicks < preferredTicksPerFrame) {
								long sleepTime = ((preferredTicksPerFrame - frameDurationTicks) * 1000) / timer.getResolution();
								try {
									Thread.sleep(sleepTime);
								} catch (InterruptedException exc) {
									Logger.getLogger( PhysicsSpace.LOGGER_NAME ).log(Level.SEVERE, "Interrupted while sleeping in fixed-framerate",
													exc);
								}
								frameDurationTicks = timer.getTime() - frameStartTick;
							}
						}
		
					Thread.yield();
					}finally{
						lock();
					}

				}
			}finally
			{
				unlock();
				if (keepAlive)
				{
					keepAlive= false;
				}
			}
			
		}
		
		public void update(float tpf) {
			if (!enabled) return;
	
			//update the geometry of the physics node here			
			
			acquireGraphicsAndPhysicsLocks();
			try{
			physicsRoot.updateGeometricState(tpf, true);
			}finally{
				releaseGraphicsAndPhysicsLocks();
			}
			
			try{
			physics.update(0.02f);
			}finally{
		
			}

		}
		
		public void lock() {

			updateLock.lock();
		}
		
		public void unlock() {
			if (enabled)//this handles the case where the thread could not be locked at the time enabled was called
				enabledCondition.signalAll();	//note: this is probably not needed in the internal update calls which is why it is not used there.	
			updateLock.unlock();
		}
		
		public void shutdown() {
			keepAlive = false;				
			
			if ((updateLock.isHeldByCurrentThread() || updateLock.tryLock()))//try to get the lock but don't block
			{				
				enabledCondition.signalAll();//signal the main thread to check the enabled condition and acquire the lock
				timePauseCondition.signalAll();
				updateLock.unlock();
			}else
			{	//if the lock cannot be acquired, this will wake the thread up from Condition.await() and cause the physics thread to test whether it is alive or not
				PhysicsMultithreadedGameState.this.physicsThread.interrupt();
			}
		
		
		}
		
		public Node getPhysicsRoot()
		{
			return physicsRoot;
		}

		public long getDesiredUpdatesPerSecond() {
			return desiredUpdatesPerSecond;
		}

	}

	/**
	 * Clear and reset physics - but don't destroy this physics state.
	 */
	public void clear() {
		lock();
		try{
		this.getPhysicsSpace().delete();
		}finally{
			unlock();
		}
	}
}
