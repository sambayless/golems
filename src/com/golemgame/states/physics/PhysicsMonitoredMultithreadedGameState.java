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

package com.golemgame.states.physics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.odejava.Body;

import com.golemgame.constructor.UpdateManager;
import com.golemgame.constructor.UpdateManager.Stream;
import com.golemgame.model.spatial.ThreadGuardingNode;
import com.golemgame.states.MemoryManager;
import com.golemgame.states.StateManager;
import com.golemgame.util.ManualExecutorService;
import com.jme.scene.Node;
import com.jme.system.DisplaySystem;
import com.jme.util.Timer;
import com.jmex.game.state.BasicGameState;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.impl.ode.OdePhysicsSpace;



/**
 * <code>PhysicsGameState</code> provides physics encapsulation into a GameState.
 * Note: PhysicsDebugger is NOT thread-safe in its current state, and cannot be used with this implementation
 * @author Matthew D. Hicks
 * @author Sam Bayless
 */
public abstract class PhysicsMonitoredMultithreadedGameState extends BasicGameState {

	private PhysicsRunnable physicsRunnable;
	
	private PhysicsMonitor physicsMonitor;
	
	/**
	 * Keep a reference to this so that it can be shut down if necessary
	 */
	private Thread physicsThread;
	
	/**
	 * Keep a reference to this so that it can be shut down if necessary
	 */
	private Thread monitorThread;
	
	/**
	 * This node should be the parent for all physics objects.
	 * It will be updated in the physics thread.
	 */
	private ThreadGuardingNode physicsRoot;
	
	private ManualExecutorService physicsExecutor = new ManualExecutorService();
	
	/**
	 * This node should be the parent for all physics objects.
	 * It will be updated in the physics thread.
	 * @return the root physics node.
	 */
	public Node getPhysicsRoot() {
		return physicsRoot;
	}
	
	/**
	 * As a consequence of the current locking model (where physics has to be released before opengl can be locked), sometimes the physics thread
	 * releases the lock before updating the world bounds of the objects after having moved them, which leaves everything with 0 0 0 world translations, etc.
	 * this is annavoidable because the opengl must be locked before updating geometric state, but before updating geometric state, the physiscs thread must be released,
	 * meaning that sometimes the render thread will pick up the lock and render unpositioned objects.
	 * So, this flag is set by physics to indicate whether the objects need to be updated in the render thread before rendering.
	 */
	private volatile boolean renderUpdateRequired = false;
	
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
		

		physicsRoot.removeFromParent();
		
	
		
		//physicsRoot = null;
		
	//	physicsRunnable.physics = null;
		
	
		
	}
	
/*	public PhysicsMonitoredMultithreadedGameState(String name) {
		this(name, 30);
	}*/
	
	public PhysicsMonitoredMultithreadedGameState(String name, int desiredUpdatesPerSecond) {
		super(name);

		//DynamicPhysicsNodeImpl.PHYSICSLOCK = this.physicsRunnable.updateLock;
	}
	
	/**
	 * Must be called by subclasses in the constructor.
	 * @param physics
	 */
	protected void setPhysics(PhysicsRunnable physicsRunnable)
	{
		this.physicsRunnable = physicsRunnable;
		
		physicsMonitor = new PhysicsMonitor(0.01);
		
		physicsThread = new Thread(null, physicsRunnable, "Physics Thread",MemoryManager.getPhysicsMemory()*MemoryManager.MEGABYTE);//4*32MB = 128 MB
		physicsRoot = new ThreadGuardingNode(physicsRunnable.updateLock);
		physicsThread.setDaemon(true);
		physicsThread.start();
	
		monitorThread = new Thread(null, physicsMonitor, "Physics Monitoring Thread");
		monitorThread.setDaemon(true);
		monitorThread.start();
	}

	public boolean inPhysicsThread()
	{
		return Thread.currentThread()==physicsThread;
	}
	
	/**
	 * Execute code in physics at some point in the future
	 * @param run
	 */
	public void executeInPhysics(Runnable run)
	{
		if(inPhysicsThread())
			run.run();
		else
		{
			physicsExecutor.execute(run);
		}
	}
	
	/**
	 * Execute code in physics at some point in the future, wait until execution has completed.
	 * @param run
	 */
	public void blockingExecuteInPhysics(Runnable run)
	{
		if(inPhysicsThread())
			run.run();
		else
		{
			try {
				physicsExecutor.submit(run).get();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			}
		}
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
		physicsMonitor.setEnabled(enabled);
	}
	
	public boolean isPhysicsEneabled()
	{
		return physicsRunnable.isEnabled();
	}
	
/*	public void reportPhysicsStatus()
	{
		List<Body>  bodies =((OdePhysicsSpace) physicsRunnable.physics).getWorld().getBodies();
		
		String report = "World Status: number of bodies=" + bodies.size() + ", ";
		for (Body body:bodies)
		{
			report += "\t" + body + "(" + body.getGeoms()+ ")";
		}
		StateManager.getLogger().log(Level.INFO, report);
		
	}
	*/
/*	public int countPhysicsBodies()
	{
		
		List<Body>  bodies =((OdePhysicsSpace) physicsRunnable.physics).getWorld().getBodies();
		if(bodies == null)
			return 0;
		else 
			return bodies.size();
	}
	*/
	
	public void render(float tpf) {
		int lockReleases = 0;
	
		try{
			
			while (physicsRunnable.updateLock.isHeldByCurrentThread())
			{
				physicsRunnable.updateLock.unlock();
				lockReleases ++;
			}
			StateManager.getGame().lock();
			try {			
				//the physics lock MUST be released to other owners before taking the graphics lock.			
				if(this.physicsRunnable.updateLock.tryLock(1L,TimeUnit.MILLISECONDS))
				{						
					try{
						
						if(	renderUpdateRequired)
						{
							physicsRoot.updateGeometricState(tpf, true);
							renderUpdateRequired=false;
							UpdateManager.getInstance().update(tpf, Stream.PHYISCS_POST_UPDATE);
						}
						
						if (isPhysicsEneabled() &! isPaused())
						{
							UpdateManager.getInstance().update(tpf,UpdateManager.Stream.PHYSICS_RENDER);
						}
					/*	System.out.println(((Node)physicsRoot.getChild(0) ).getChild(3).getLocalTranslation());*/
						
						DisplaySystem.getDisplaySystem().getRenderer().draw(physicsRoot);
					
					}finally{
						unlock();
					}
				}else
				{
					//draw it anyways, unsafely.
					if (isPhysicsEneabled() &! isPaused())
					{
						UpdateManager.getInstance().update(tpf,UpdateManager.Stream.PHYSICS_RENDER);
					}
					DisplaySystem.getDisplaySystem().getRenderer().draw(physicsRoot);
			
				}
			} catch (InterruptedException e) {
				StateManager.logError(e);
			}finally{
				StateManager.getGame().unlock();
			}
		}finally{
			for (int i = 0;i<lockReleases;i++)
				physicsRunnable.updateLock.lock();
		}	
		//this is probably slightly unsafe... but worth it time wise.
		//but as a result: you cannot safely modify the physics root scene graph without locking
		//BOTH the gl thread and the physics thread
/*		if (isPhysicsEneabled() &! isPaused())
		{
			UpdateManager.getInstance().update(tpf,UpdateManager.Stream.PHYSICS_RENDER);
		}
		
		
		renderer.draw(physicsRoot);*/
		
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
		physicsMonitor.shutdown();
	}
	
	public boolean isPaused()
	{
		return physicsRunnable.isPaused();
	}
	
	/**
	 * A safe method to acquire locks on graphics and physics simultaneously.
	 * In general, the graphics lock MUST be acquired before the physics lock to ensure safe operation.
	 * If you already hold the physics lock, it MUST be released before acquiring the openGL lock.
	 */
	public void acquireGraphicsAndPhysicsLocks(){
		acquireGraphicsAndPhysicsLocks(-1L);
	}
	/**
	 * A safe method to acquire locks on graphics and physics simultaneously.
	 * In general, the graphics lock MUST be acquired before the physics lock to ensure safe operation.
	 * If you already hold the physics lock, it MUST be released before acquiring the openGL lock.
	 * @param timeOut Max time (ms) to attempt to lock the PHYSICS thread (gl thread will be locked at any expense). Use a value less than 0 to wait indefinately
	 */
	public void acquireGraphicsAndPhysicsLocks(long timeOut)
	{
		int lockReleases = 0;
		//the physics lock MUST be released to other owners before taking the graphics lock.
		try{
			while (physicsRunnable.updateLock.isHeldByCurrentThread())
			{
				physicsRunnable.updateLock.unlock();
				lockReleases ++;
			}
			StateManager.getGame().lock();
			if(timeOut<0L)
			{		
				lock();		
			}else{
				try {
					this.physicsRunnable.updateLock.tryLock(timeOut,TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					StateManager.logError(e);
				}
			}
		}finally{
			for (int i = 0;i<lockReleases;i++)
				physicsRunnable.updateLock.lock();
		}
		//lock again as many locks as were unlocked before
	}
	
	public void releaseGraphicsAndPhysicsLocks()
	{
		
		unlock();	
		StateManager.getGame().unlock();
	

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

	/**
	 * Add a listener that will be notified if the physics freezes.
	 * @param listener
	 */
	public void addMonitorListener(PhysicsMonitorListener listener)
	{
		physicsMonitor.addListener(listener);
	}
	
	public void removeMonitorListener(PhysicsMonitorListener listener)
	{
		physicsMonitor.removeListener(listener);
	}
	
	/**
	 * Get the amount of time physics since the last time physics started an update cycle, in ms.
	 * @return
	 */
	public long getTimeLagging()
	{
		return physicsMonitor.getTimeLagging()/1000;
	}
	protected ReentrantLock getUpdateLock() {
		return this.physicsRunnable.updateLock;
	}
	
	

	public abstract class PhysicsRunnable implements Runnable{

		protected long preferredTicksPerFrame;
		protected volatile boolean enabled;
		protected boolean keepAlive;
		protected Timer timer;
		protected boolean limitUpdates;
		protected ReentrantLock updateLock;

		protected long desiredUpdatesPerSecond;
		protected Condition enabledCondition;
		protected Condition timePauseCondition;
		
		protected volatile boolean paused = false;;
		
		public boolean isPaused() {
			return paused;
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
			//long time[] = new long[5];
	
			Throwable thrown = null;
			lock();
		
			timer.reset();
	
			this.setEnabled(false);

			try{
				long frameStartTick = 0;
				long frameDurationTicks = 0;
				float tpf;
				while (keepAlive) {
				
					//time[0] = System.nanoTime();
					try{
						while (!enabled && keepAlive)							
							enabledCondition.await();//put the thread to sleep if it is not enabled	
					}catch(InterruptedException e)
					{
						//Logger.getLogger( PhysicsSpace.LOGGER_NAME ).log(Level.SEVERE, "Interrupted while disabled",e);
					}
					if(!keepAlive)
						break;
					
					try{
						while(preferredTicksPerFrame == Long.MIN_VALUE && keepAlive)
						{			
							paused = true;
							timePauseCondition.await();//put the thread to sleep if it is not enabled	
							paused = false;
						}
					}catch(InterruptedException e)
					{
						//Logger.getLogger( PhysicsSpace.LOGGER_NAME ).log(Level.SEVERE, "Interrupted while disabled",e);
					}
					
					physicsExecutor.manualExecuteUntil(50);
					
				//	time[1] = System.nanoTime();
					if (limitUpdates) {
						frameStartTick = timer.getTime();
					}
					timer.update();
				
					tpf = timer.getTimePerFrame();
				//	time[2] = System.nanoTime();
					update(tpf);
				//	time[3] = System.nanoTime();
					unlock();
					try{
						if ((limitUpdates) && (preferredTicksPerFrame >= 0)) {
							frameDurationTicks = timer.getTime() - frameStartTick;
							while (frameDurationTicks < preferredTicksPerFrame) {
								long sleepTime = ((preferredTicksPerFrame - frameDurationTicks) * 1000) / timer.getResolution();
								try {
									Thread.sleep(sleepTime);
								} catch (InterruptedException exc) {
								/*	Logger.getLogger( PhysicsSpace.LOGGER_NAME ).log(Level.SEVERE, "Interrupted while sleeping in fixed-framerate",
													exc);*/
								}
								frameDurationTicks = timer.getTime() - frameStartTick;
							}
						}
				//	time[4] = System.nanoTime();
					Thread.yield();
					}finally{
						lock();
					}
/*					
						String times  = "Selecting: ";
					
						for(int i = 1;i<time.length;i++)
							times+="\t" + (time[i] - time[i-1]);
							
						System.out.println(times );//
*/				}
			}catch(Exception e)
			{
				StateManager.logError(e);
				thrown = e;
			
				
			}catch(Error e)
			{
				StateManager.logError(e);
				thrown = e;
				if (e instanceof StackOverflowError)
				{
			
				}else
					throw (e);
			}finally
			{
				unlock();
				if (keepAlive)//unless the thread was intentionally terminated, inform the monitor.
				{
					keepAlive= false;
					physicsMonitor.physicsDied(thrown);
				}
			}
			
		}
		
		protected abstract void updatePhysics(float tpf);
		
		public void update(float tpf) {
			if (!enabled) return;
			// Open the lock up for any work that needs to be done
		//	long time[] = new long[5];
		//	time[0] = System.nanoTime();
	
		//	time[1] = System.nanoTime();
			//update the geometry of the physics node here
			
		
			acquireGraphicsAndPhysicsLocks();
			try{
			physicsRoot.updateGeometricState(tpf, true);
			}finally{
				releaseGraphicsAndPhysicsLocks();
			}
			renderUpdateRequired = true;
		//	time[2] = System.nanoTime();
			//StateManager.getGame().lock();
			
			UpdateManager.getInstance().update(tpf, Stream.PHYISCS_UPDATE);
			try{
				updatePhysics(0.02f);
				//physics.update(0.02f);
			}finally{
			//	StateManager.getGame().unlock();
			}
			
			acquireGraphicsAndPhysicsLocks();
			try{
			physicsRoot.updateGeometricState(tpf, true);
			
			renderUpdateRequired = false;
			}finally{
				releaseGraphicsAndPhysicsLocks();
			}
			UpdateManager.getInstance().update(tpf, Stream.PHYISCS_POST_UPDATE);
	/*		updateLock.unlock();//call this directly (not through unlock) so that it doesn't check the enabled condition
			try{
				// Now lock up again so nothing can happen while we're updating			
				Thread.yield();
			}finally{
				updateLock.lock();
			}*/
			
			if (keepAlive)
				physicsMonitor.update();
	
		//	throw new RuntimeException("test");
			
/*			String times  = "Updating: ";
			
			for(int i = 1;i<time.length;i++)
				times+="\t" + (time[i] - time[i-1]);
				
			System.out.println(times );*/
		}
		
		public void lock() {
		//	System.out.println(Arrays.toString( Thread.currentThread().getStackTrace()));
			updateLock.lock();
		}
		
		public void unlock() {
			if (enabled)//this handles the case where the thread could not be locked at the time enabled was called
				enabledCondition.signalAll();	//note: this is probably not needed in the internal update calls which is why it is not used there.	
		//	System.out.println(Arrays.toString( Thread.currentThread().getStackTrace()));
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
				PhysicsMonitoredMultithreadedGameState.this.physicsThread.interrupt();
			}
		
		
		}
	
		public long getDesiredUpdatesPerSecond() {
			return desiredUpdatesPerSecond;
		}

	}
	
	/**
	 * This class tests to make sure that the physicsThread is not frozen.
	 * @author Sam
	 *
	 */
	private class PhysicsMonitor implements Runnable
	{
		private CopyOnWriteArrayList<PhysicsMonitorListener> listeners = new CopyOnWriteArrayList<PhysicsMonitorListener>();
		private boolean enabled;
		private long sleepPeriod = 2*1000;
		private boolean keepAlive;
		private ReentrantLock enabledLock;
		private Condition enabledCondition;
		
		private int minimumUpdates = 0;
		private int minimumUpdateThreshold = 0;

		private boolean isFrozen = false;
		private AtomicInteger updateCount = new AtomicInteger(0);
		
		private long lastUpdate = 0;

		public PhysicsMonitor( double allowedLag) {
			super();
			this.setAllowedLag(allowedLag);
			keepAlive = true;
			enabledLock = new ReentrantLock();
			enabledCondition = enabledLock.newCondition();
		}
		
		public void physicsDied(final Throwable e)
		{
			//drop this call into a new thread

			final ArrayList<PhysicsMonitorListener> finalListeners = new ArrayList<PhysicsMonitorListener>(listeners);
			StateManager.getThreadPool().execute(new Runnable()
			{

				
				public void run() {
					for (PhysicsMonitorListener listener:finalListeners)
					{
						listener.physicsDied(e);
					}
				}
				
			});
		}
		
		public void update()
		{
			updateCount.addAndGet(1);
			lastUpdate = System.nanoTime();
		}
		
		/**
		 * Set the percentage of desired updates per second below which the physics is considered to be frozen.
		 * @param percentage
		 */
		public void setAllowedLag(double percentage)
		{
			minimumUpdates = (int) Math.round(((double)physicsRunnable.getDesiredUpdatesPerSecond())*percentage);
			minimumUpdateThreshold =(int) Math.round(((double)minimumUpdates)*1.10);

		}
		
		public long getTimeLagging()
		{
			return System.nanoTime() - lastUpdate;

		}
		
		
		public void run() {
			
			enabledLock.lock();
			
			while(keepAlive)
			{
				
				
				
				
			
				enabledLock.unlock();
				long startTime = System.nanoTime();
				long remainingTime = sleepPeriod;
				
				while(remainingTime > 0)
				{
					try{
						Thread.sleep(remainingTime);
						long ellapsedTime = System.nanoTime() - startTime;
						remainingTime -= ellapsedTime;
					}catch(InterruptedException  e)
					{
						
					}
				}
				Thread.yield();
				enabledLock.lock();
				
				while (!enabled && keepAlive)//sleep until the thread is enabled.
				{
					try{
						enabledCondition.await();
					}catch(InterruptedException e)
					{
						
					}
				}
				if(!keepAlive)
					break;
				
				//check the value of updateCount is large enough.
				int currentCount = updateCount.get();
				if (currentCount<minimumUpdates)
				{
					if (!isFrozen)
					{
					//	isFrozen = true;//flag that physics appears to be frozen
			/*			enabledLock.unlock();
						try{
							for (PhysicsMonitorListener listener:listeners)
							{
								listener.frozen();
							}
						}finally
						{
							enabledLock.lock();
						}*/
					}
				}else if (currentCount>minimumUpdateThreshold)
				{
					if (isFrozen)
					{
						isFrozen = false;
						enabledLock.unlock();
						try{
							for (PhysicsMonitorListener listener:listeners)
							{
								listener.unfrozen();
							}
						}finally{
							enabledLock.lock();
						}
					}
				}
				updateCount.set(0);//reset the update count.
			
				Thread.yield();
			
			}
			
			
		}
		
		public void addListener(PhysicsMonitorListener listener)
		{
			listeners.add(listener);
		}
		
		public void removeListener(PhysicsMonitorListener listener)
		{
			listeners.remove(listener);
		}
		
		public synchronized void setEnabled(boolean enabled) 
		{//only one thread can change enabled at a time
			
			try {
				if ((enabledLock.isHeldByCurrentThread() || enabledLock.tryLock(500,TimeUnit.MILLISECONDS)))//try to get the lock but don't block
				{
					try{
						if (this.enabled != enabled)
						{

								this.enabled = enabled;
								//this is only reached if the state of enabled has changed
								if (enabled)
								{
									isFrozen = false;
									this.update();
									
									enabledCondition.signalAll();//signal the main thread to check the enabled condition and acquire the lock
									
									
								}else
								{
									
										if (isFrozen)
										{
										
												isFrozen = false;
												for (PhysicsMonitorListener listener:listeners)
												{
													listener.unfrozen();
												}						
											
										}
									
									
									
								}
						}
					
					}finally
					{
						if(enabledLock.isHeldByCurrentThread())
							enabledLock.unlock();
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				StateManager.logError(e);
			}
		}
		
		public boolean isEnabled() {
			return enabled;
		}

		/**
		 * Return the sleep period (in milliseconds)
		 * @return
		 */
		public long getSleepPeriod() {
			return sleepPeriod;
		}

		/**
		 * Set the sleep period (in milliseconds)
		 * @param sleepPeriod
		 */
		public void setSleepPeriod(long sleepPeriod) {
			this.sleepPeriod = sleepPeriod;
		}
		
		public void shutdown() {
			keepAlive = false;
			
			if ((enabledLock.isHeldByCurrentThread() || enabledLock.tryLock()))//try to get the lock but don't block
			{
			
				enabledCondition.signalAll();//signal the main thread to check the enabled condition and acquire the lock
				enabledLock.unlock();
			}else
			{	//if the lock cannot be acquired, this will wake the thread up from Condition.await() and cause the physics thread to test whether it is alive or not
				PhysicsMonitoredMultithreadedGameState.this.monitorThread.interrupt();
			}
			for (PhysicsMonitorListener listener:listeners)
			{
				listener.unfrozen();
			}
		}
	}

	/**
	 * Clear and reset physics - but don't destroy this physics state.
	 */
	public void clear() {
		//do nothing by default
	}
}
