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
package com.golemgame.states;

import com.jmex.game.state.GameState;

public class PhysicsTimeout extends GameState {
	
	/**
	 * Time till physics is considered frozen.
	 */
	private float timeOutLength = 5;
	
	private float time = 0;
	
	private float timeInDelay = 0;
	
	private boolean timedOut;
	
	private boolean timedIn;
	
	public PhysicsTimeout() {
		super();
		timedOut=  false;
		timedIn= false;
	}

	public float getTimeOut() {
		return timeOutLength;
	}

	/**
	 * Set the time to wait before considering physics to be timed out
	 * @param timeOut
	 */
	public void setTimeOut(float timeOut) {
		this.timeOutLength = timeOut;
	}

	public float getTime() {
		return time;
	}

	@Override
	public void cleanup() {
		
		
	}

	@Override
	public void render(float tpf) {
		
		
	}

	@Override
	public void update(float tpf) 
	{	
		time+= tpf;
		if (time>timeOutLength)
		{
			timedOut = true;
			timeout(time);
			time = 0;
			
		}else if (timedIn)
		{
			timedIn = false;
			timein(timeInDelay);
		}
		
	}
	
	/**
	 * Call when physics completes an update cycle.
	 * Resets the time count.
	 */
	public final void physicsCompleted()
	{		
		if (timedOut)
		{
			timeInDelay = time;
			timedIn = true;
		}
		time = 0;
		timedOut = false;
	}
	
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

	public boolean isTimedOut() {
		return timedOut;
	}

}
