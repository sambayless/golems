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


/**
 * The switch is an intermediate component. It contains a secondary component - the switch input.
 * When the signal to the switch input is greater than threshold, then the switch is 'on'.
 * @author Sam
 *
 */
public class BSwitch extends BComponent{
	private static final long serialVersionUID =1;
	private final BAuxInput switchInput = new BAuxInput();
	private boolean on = false;
	private float threshold = 0;
	@Override
	public float generateSignal(float time) 
	{		
		float signal = 0;
		if (on)
			signal= state;
		state = 0;
		
		return signal;
	}
	
	protected class BAuxInput extends BComponent
	{
		private static final long serialVersionUID =1;
		@Override
		public float generateSignal(float time) 
		{
			on = state>threshold;
			state = 0;
			return 0;
		}
		
	}

	public BAuxInput getSwitchInput() {
		return switchInput;
	}
}
