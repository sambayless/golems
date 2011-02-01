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

public class KeyboardInputDeviceInterpreter extends InputDeviceInterpreter{
	
	//note: Unusually, these keys are enumerated in the device interpreter, not here
	public static final String KEY = "device.key";
	public static final String KEY_EVENT = "device.event";
	public static final String OUTPUT_NEGATIVE = "output.negative";
	
	public enum KeyEventType
	{
		HeldDown(), Toggle();
	}
	
	public KeyboardInputDeviceInterpreter() {
		this(new PropertyStore());
		super.setNumberOfOutputs(1);
		super.setDeviceType(InputType.KEYBOARD);
		getStore().setClassName(GolemsClassRepository.KEY_INPUT_DEVICE_CLASS);
	}
	
	public KeyboardInputDeviceInterpreter(PropertyStore store) {
		super(store);		
	}
	
	public boolean outputNegative()
	{
		return getStore().getBoolean(OUTPUT_NEGATIVE,true);
	}
	
	public void setOutputNegative(boolean zero)
	{
		getStore().setProperty(OUTPUT_NEGATIVE, zero);		
	}
	
	
	public int getKeyCode()
	{
		return getStore().getInt(KEY, 'A');
	}
	
	public void setKeyCode(int keyCode)
	{
		getStore().setProperty(KEY, keyCode);		
	}
	
	public KeyEventType getInteractionType()
	{
		return getStore().getEnum(KEY_EVENT, KeyEventType.HeldDown);
	}
	
	public void setInteractionType(KeyEventType key)
	{
		getStore().setProperty(KEY_EVENT, key);
	}
}
