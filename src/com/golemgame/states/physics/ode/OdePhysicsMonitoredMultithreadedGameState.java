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

package com.golemgame.states.physics.ode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.golemgame.constructor.UpdateManager;
import com.golemgame.constructor.UpdateManager.Stream;
import com.golemgame.model.spatial.ThreadGuardingNode;
import com.golemgame.states.MemoryManager;
import com.golemgame.states.StateManager;
import com.golemgame.states.physics.PhysicsMonitorListener;
import com.golemgame.states.physics.PhysicsMonitoredMultithreadedGameState;
import com.golemgame.util.ManualExecutorService;
import com.jme.scene.Node;
import com.jme.system.DisplaySystem;
import com.jme.util.NanoTimer;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.impl.ode.OdePhysicsSpace;
import com.jphya.body.Body;



/**
 * <code>PhysicsGameState</code> provides physics encapsulation into a GameState.
 * Note: PhysicsDebugger is NOT thread-safe in its current state, and cannot be used with this implementation
 * @author Matthew D. Hicks
 * @author Sam Bayless
 */
public class OdePhysicsMonitoredMultithreadedGameState extends PhysicsMonitoredMultithreadedGameState {


	private PhysicsSpace physics;
	

	
	@Override
	public void delete()
	{
		this.physics.delete();
		
		if (this.physics instanceof OdePhysicsSpace)
		{
			OdePhysicsSpace odePhysics = (OdePhysicsSpace) physics;
			odePhysics.getWorld().delete();
			
		}
		physics= null;
		
		//physicsRoot = null;
		
	//	physicsRunnable.physics = null;
		
	
		
	}
	
	public OdePhysicsMonitoredMultithreadedGameState(String name) {
		this(name, 30);
	}
	
	public OdePhysicsMonitoredMultithreadedGameState(String name, int desiredUpdatesPerSecond) {
		super(name, desiredUpdatesPerSecond);
	
		super.setPhysics(new OdePhysicsRunnable(desiredUpdatesPerSecond));
	
	}
	
	public PhysicsSpace getPhysicsSpace(){
		return physics;
	}
	

	private class OdePhysicsRunnable extends PhysicsRunnable{
		
	

		public OdePhysicsRunnable(int desiredUpdatesPerSecond) {
			
			physics = PhysicsSpace.create();
	/*		((OdePhysicsSpace) physics).setSceneUpdateLock(new SceneUpdateLock()
			{

				public void lock() {
				//	StateManager.getGame().lock();
					
				}

				public void unlock() {
				//	StateManager.getGame().unlock();
				}
				
			});*/
			physics.update(0.01f);
			enabled = false;
			keepAlive = true;
			this.desiredUpdatesPerSecond = desiredUpdatesPerSecond;
			updateLock = new ReentrantLock(true); // Make our lock be fair (first come, first serve)
		
			enabledCondition = updateLock.newCondition();
			timePauseCondition = updateLock.newCondition();
			timer = new NanoTimer();
			if (desiredUpdatesPerSecond == -1) {
				limitUpdates = false;
			} else {
				preferredTicksPerFrame = Math.round((float)timer.getResolution() / (float)desiredUpdatesPerSecond);
				limitUpdates = true;
			}
			physics.setAutoRestThreshold(0.05f);//for unknown reasons, sometimes this seems to freeze?
		
			((OdePhysicsSpace) physics).setMaxStepContactsPerNearcallback(1024*16);//default is 4096
			((OdePhysicsSpace) physics).setInteraction(1);
			((OdePhysicsSpace) physics).setStepSize(0.01f);
		//((OdePhysicsSpace) physics).setInteraction(300);
			//System.out.println(((OdePhysicsSpace) physics).getCFM());
			//((OdePhysicsSpace) physics).setCFM(0.0002f);
			//((OdePhysicsSpace) physics).setERP(0.05f);
			
		//	((OdePhysicsSpace) physics).getWorld().setMaxCorrectionVelocity(100f);
		//	((OdePhysicsSpace) physics).getWorld().setErrorReductionParameter(0.5f);
			//((OdePhysicsSpace) physics).setStepFunction(OdePhysicsSpace.SF_STEP_SIMULATION);
			((OdePhysicsSpace) physics).setERP(0.75f);
			((OdePhysicsSpace) physics).setCFM(0.00001f);
			((OdePhysicsSpace) physics).setUpdateRate(-1);//the physics runnable handles timing issues, dont let the physics space do so.
		
			/*
			 * > I have an ERP of 1.0 and CFM of 0.1. When I change their value that
> doesn't work correctly.
> I use dWorldStep my time step is 0.001.

Usually erp should be close to one but not one (I use value around 0.75,
0.8)
CFM should be a really small value (I use from 1e-10 to 1e-6).

Remi

- Hide quoted text -
- Show quoted text -
> Maybe that could come from another way, but I tried to find it in the
> ode's documentation without success.

> On 29 juil, 13:40, "Daniel K. O." <danielko.lis...@gmail.com> wrote:

>> Tyke91 escreveu:

>>> How could I make my joint stronger? Like this it could support the
>>> weight of my arm.

>> * Increase ERP, reduce CFM.
>> * dWorldStep instead dWorldQuickStep.
>> * Smaller time steps.

>> --
>> Daniel K. O.
>> "The only way to succeed is to build success yourself"


    Reply to author    Forward  
		
		
		
You must Sign in before you can post messages.
To post a message you must first join this group.
Please update your nickname on the subscription settings page before posting.
You do not have the permission required to post.
	
		
Daniel K. O.   	
View profile
Tyke91 escreveu: > I have an ERP of 1.0 and CFM of 0.1. The manual says ERP should be in the 0.1 to 0.8 range; I believe values higher than 0.8 hardly help, and might generate instabilities. CFM is very high, so I think this is what is causing the non-rigidity. The default is 0.00001 for single precision (which give pretty rigid joints), so you might try values smaller than that. > When I change their value that > doesn't work correctly. What do you mean by "doesn't work correctly"? Does the simulation crash? -- Daniel K. O. "The only way to succeed is to build success yourself"
	 More options Jul 29, 7:35 am
From: "Daniel K. O." <danielko.lis...@gmail.com>
Date: Tue, 29 Jul 2008 11:35:45 -0300
Local: Tues, Jul 29 2008 7:35 am
Subject: [ode-users] Re: Hinge Issue
Reply to author | Forward | Print | Individual message | Show original | Report this message | Find messages by this author
Tyke91 escreveu:

> I have an ERP of 1.0 and CFM of 0.1.

The manual says ERP should be in the 0.1 to 0.8 range; I believe values
higher than 0.8 hardly help, and might generate instabilities.

CFM is very high, so I think this is what is causing the non-rigidity.
The default is 0.00001 for single precision (which give pretty rigid
joints), so you might try values smaller than that. 
			 */
		
		}
	


		@Override
		protected void updatePhysics(float tpf) {
			physics.update(tpf);
		}
		

	}
	
	@Override
	public void clear() {
		super.clear();
		lock();
		try{
			if(this.getPhysicsSpace()!=null)
				this.getPhysicsSpace().delete();
		}finally{
			unlock();
		}
	}
}
