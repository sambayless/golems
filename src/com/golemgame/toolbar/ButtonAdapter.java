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
package com.golemgame.toolbar;

import com.golemgame.toolbar.Button.ButtonState;

public class ButtonAdapter implements ButtonListener {

	
	public void buttonStateChange(ButtonState state) {
		switch(state)
		{
			case ACTIVE:
				activate();break;
			case INACTIVE:
				inactive();break;
			case HOVER:
				hover();break;
			case ANTI_HOVER:
				anti_hover();break;
				
		}

	}

	public void activate() {
		
	}
	public void inactive() {
		
	}
	public void hover() {
		
	}
	public void anti_hover() {
		
	}
	
}
