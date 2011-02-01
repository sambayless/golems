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
package com.golemgame.states.physics.ode;


import java.util.concurrent.CopyOnWriteArrayList;

import com.golemgame.functional.component.BSpace;
import com.golemgame.functional.component.BUpdateCallback;
import com.golemgame.states.PhysicsTimeout;
import com.golemgame.states.physics.PhysicsMonitoredMultithreadedGameState;
import com.jme.system.DisplaySystem;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.PhysicsUpdateCallback;


public class FunctionalPhysicsState extends OdePhysicsMonitoredMultithreadedGameState {
	static final long serialVersionUID =1;
	protected BSpace bSpace;
	protected DisplaySystem display;
	
	protected PhysicsTimeout timeout;
	

	public void setBSpace(BSpace space) {
		bSpace = space;
	}

	public FunctionalPhysicsState(String name) {
		super(name,120);
		timeout = new PhysicsTimeoutImpl();
		display = DisplaySystem.getDisplaySystem();

		bSpace = new BSpace();	

		super.getPhysicsSpace().addToUpdateCallbacks(new PhysicsUpdateCallback()
		{
			public void beforeStep(PhysicsSpace space, float time) {
	
			}	
			public void afterStep(PhysicsSpace space, float time) {
				if (time>1f/30f)
					bSpace.update(1f/30f);	
				else if (time <= 0)
					return;
				else
					bSpace.update(time);
				timeout.physicsCompleted();				
			}

		});
	}
	
	public BSpace getBSpace() {
		return bSpace;
	}


	@Override
	public void update(float tpf) {
		super.update(tpf);
		timeout.update(tpf);
	}

	/**
	 * Clears physics, but adds back physics call backs that should not be deleted.
	 * This does not DESTROY the physics world - it just empties it so it can be reused.
	 */
/*	public void deletePhysics()
	{
		//for ( PhysicsNode physicsNode : super.getPhysicsSpace().getNodes() ) physicsNode.delete();

	}
*/
	
	/**
	 * This method is called when a timeout occurs.
	 * @param time
	 */
	protected void timeout(float time)
	{
		
	}
	
	/**
	 * This method is called when a timeout ends.
	 * @param time The total length of the time out period that preceded the time in.
	 */
	protected void timein(float time)
	{
		
	}
	
	private class PhysicsTimeoutImpl extends PhysicsTimeout
	{

		@Override
		protected void timeout(float time) 
		{			
			super.timeout(time);
			FunctionalPhysicsState.this.timeout(time);
		}

		@Override
		protected void timein(float time) {
			FunctionalPhysicsState.this.timein(time);
		}
		
		
	}



}
