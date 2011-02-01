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
package com.golemgame.mvc.golems.input;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GolemsClassRepository;

public class SliderInputDeviceInterpreter extends InputDeviceInterpreter{
	//note: Unusually, these keys are enumerated in the device interpreter, not here
	public static final String INITIAL_VALUE = "device.initial";
	public static final String SHOW_ARROWS = "device.showArrows";
	public static final String IS_HORIZONTAL = "device.horizontal";
	
	public SliderInputDeviceInterpreter() {
		this(new PropertyStore());
		super.setNumberOfOutputs(1);
		super.setDeviceType(InputType.SLIDER);
		getStore().setClassName(GolemsClassRepository.SLIDER_INPUT_DEVICE_CLASS);
	}
	
	public SliderInputDeviceInterpreter(PropertyStore store) {
		super(store);		
	}
	
	public float getInitialValue()
	{
		return getStore().getFloat(INITIAL_VALUE,0f);
	}
	
	public void setInitialValue(float keyCode)
	{
		getStore().setProperty(INITIAL_VALUE,keyCode);		
	}
	

	public void setHorizontal(boolean horizontal) {
		 getStore().setProperty(IS_HORIZONTAL,horizontal);
	}

	public void setShowArrows(boolean showArrows) {
		
		 getStore().setProperty(SHOW_ARROWS,showArrows);
	}

	public boolean isHorizontal() {
		return getStore().getBoolean(IS_HORIZONTAL,false);
	}

	public boolean showArrows() {
		
		return getStore().getBoolean(SHOW_ARROWS,false);
	}
	
}
