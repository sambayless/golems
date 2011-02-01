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
import com.golemgame.mvc.NullType;
import com.golemgame.mvc.PropertyStore;

public class TouchSensorInterpreter extends BoxInterpreter {

	private final StandardFunctionalInterpreter portInterpreter;
	
	public TouchSensorInterpreter() {
		this(new PropertyStore());
	}

	public TouchSensorInterpreter(PropertyStore store) {
		super(store);
		portInterpreter = new StandardFunctionalInterpreter(store);
		store.setClassName(GolemsClassRepository.TOUCH_SENSOR_CLASS);
	}
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		portInterpreter.enumerateKeys(keys);	
		return super.enumerateKeys(keys);
	}
	@Override
	public DataType getDefaultValue(String key) {
		
		DataType val = this.portInterpreter.getDefaultValue(key);
		if (val != null && val != NullType.get())
			return val;
		return super.getDefaultValue(key);
	}
	//echo the port interpreters functions here... in case we ever need them
	
	public PropertyStore getOutput()
	{
		return portInterpreter.getOutput();
	}
	
/*	public void setOutput(PropertyStore reference)
	{
		portInterpreter.setOutput(reference);
	}*/
	
	public PropertyStore getInput()
	{
		return portInterpreter.getInput();
	}
	
/*	public void setInput(PropertyStore reference)
	{
		portInterpreter.setInput(reference);
	}*/
	
	public PropertyStore getAuxInput()
	{
		return portInterpreter.getAuxInput();
	}
	
/*	public void setAuxInput(PropertyStore reference)
	{
		portInterpreter.setAuxInput(reference);
	}*/
}
