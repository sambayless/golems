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
package com.golemgame.functional.component;



public class BDifferentiator extends BComponent {
	private static final long serialVersionUID =1;
	private float previousState = 0;
	private boolean antidifferentiate;
	private final BAuxInput switchInput = new BAuxInput();
	private float threshold = 0;
	private boolean open = false;
	private boolean thresholdInverted;
	
	public BDifferentiator(boolean antidifferentiate) {
		super();
		this.antidifferentiate = antidifferentiate;
	}


	public BDifferentiator() {
		this(false);
	}


	@Override
	public float generateSignal(float time) 
	{
		if(!open)
		{
			if (!antidifferentiate)
			{
				float signal =((state - previousState)/time);
				previousState = state;
				state = 0;
				return signal;
			}else
			{
				float signal =((state - previousState)*time);
				previousState = state;
				state = 0;
				return signal;
			}
		}
		state = 0;
		return 0;
	}
	
	protected class BAuxInput extends BComponent
	{
		private static final long serialVersionUID =1;
		@Override
		public float generateSignal(float time) 
		{

				if(state>threshold)
					setOpen(!thresholdInverted);
				else
					setOpen(thresholdInverted);
			
			return 0;
		}
		
	}
	public float getThreshold() {
		return threshold;
	}
	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}
	
	public BAuxInput getSwitchInput() {
		return switchInput;
	}
	
	/**
	 * Set whether the function applier is 'closed' (allows signals through) 
	 * or 'open' (outputs only 0).
	 * @param b
	 */
	public void setOpen(boolean open) {
		this.open = open;
	}


	public boolean isThresholdInverted() {
		return thresholdInverted;
	}


	public void setThresholdInverted(boolean thresholdInverted) {
		this.thresholdInverted = thresholdInverted;
	}

}
