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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.simplemonkey.Container;



public class ButtonGroup extends Container implements Button{
	private List<ButtonListener> listeners = new CopyOnWriteArrayList<ButtonListener>();
	
	private ButtonState state = ButtonState.INACTIVE;
	
	public ButtonGroup() {
		super();
	}
	
	
	public void addButtonListener(ButtonListener listener) {
		listeners.add(listener);
	}

	
	public void removeButtonListener(ButtonListener listener) {
		listeners.remove(listener);
	}

	
	public boolean mouseMove(float x, float y) {
		if(this.state != ButtonState.HOVER && this.state != ButtonState.ACTIVE)
			setState(ButtonState.HOVER);
		return super.mouseMove(x, y);
	
	}

	
	public void mouseOff() {
		if(this.state == ButtonState.HOVER)
			setState(ButtonState.ANTI_HOVER);
		super.mouseOff();
	}

	
	public boolean mousePress(boolean pressed, int button, float x, float y) {
		if(pressed && button == 0)
		{
			this.setState( ButtonState.ACTIVE);
		}else if(button == 0)
		{
			this.setState(ButtonState.INACTIVE);
		}
		
		
		return super.mousePress(pressed, button, x, y);
	}	
	
	private void setState(ButtonState state)
	{
		this.state = state;
		for (ButtonListener listener:this.listeners)
			listener.buttonStateChange(state);
	}
}
