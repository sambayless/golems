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
package com.golemgame.mvc.golems;

import java.util.Collection;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;

public class InputInterpreter  extends StandardFunctionalInterpreter{


	public static final String INPUT_DEVICE = "input.device";
	public static final String NAME = "input.name";
	public static final String INSTRUMENT_X = "instrument.x";
	public static final String INSTRUMENT_Y = "instrument.y";
	public static final String INSTRUMENT_WIDTH = "instrument.width";
	public static final String INSTRUMENT_HEIGHT = "instrument.height";
	public static final String INSTRUMENT_WINDOWED= "instrument.windowed";
	public static final String INSTRUMENT_LOCKED= "instrument.locked";
	public static final String INSTRUMENT_USER_POSITIONED= "instrument.userPositioned";
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(NAME);
		keys.add(INPUT_DEVICE);
		keys.add(INSTRUMENT_X);
		keys.add(INSTRUMENT_Y);
		keys.add(INSTRUMENT_WIDTH);
		keys.add(INSTRUMENT_HEIGHT);
		keys.add(INSTRUMENT_WINDOWED);
		keys.add(INSTRUMENT_LOCKED);
		keys.add(INSTRUMENT_USER_POSITIONED);
		

		return super.enumerateKeys(keys);
	}
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(NAME))
			return defaultString;
		if(key.equals(INPUT_DEVICE))
			return defaultStore;
		return super.getDefaultValue(key);
	}
	
	
	
	public String getName()
	{
		return getStore().getString(NAME,"(Unnamed)");
	}
	
	public void setName(String name)
	{
		getStore().setProperty(NAME,name);
	}
	
	public InputInterpreter(PropertyStore store) {
		super(store);
	}

	public InputInterpreter() {
		this(new PropertyStore());		
		getStore().setClassName(GolemsClassRepository.INPUT_CLASS);
	}
	
	public PropertyStore getInputDevice()
	{
		return getStore().getPropertyStore(INPUT_DEVICE);
	}
	
	public void setInputDevice(PropertyStore key)
	{
		getStore().setProperty(INPUT_DEVICE, key);
	}
	
}
