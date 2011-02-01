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
package com.golemgame.mvc.golems.output;

import java.util.Collection;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.StoreInterpreter;

public class OutputDeviceInterpreter extends StoreInterpreter {
	public static final String NUMBER_OF_INPUTS = "device.inputs";
	public static final String DEVICE_TYPE = "device.type";
	public static final String INSTRUMENT_X = "instrument.x";
	public static final String INSTRUMENT_Y = "instrument.y";
	public static final String INSTRUMENT_WIDTH = "instrument.width";
	public static final String INSTRUMENT_HEIGHT = "instrument.height";
	public static final String INSTRUMENT_WINDOWED= "instrument.windowed";
	public static final String INSTRUMENT_LOCKED= "instrument.locked";
	public static final String INSTRUMENT_USER_POSITIONED= "instrument.userPositioned";
	//properties for the resulting type will go into a property store; there will be keyboard input type stores, etc.

	public static enum OutputType
	{
		GRAPH(),COLOR(),TEXT();
	}
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(NUMBER_OF_INPUTS);
		keys.add(DEVICE_TYPE);
		
		//add the keys of subtypes, because they are all part of a general family
		keys.add(ColorOutputDeviceInterpreter.COLOR_NEGATIVE);
		keys.add(ColorOutputDeviceInterpreter.COLOR_POSITIVE);
		keys.add(ColorOutputDeviceInterpreter.COLOR_NEUTRAL);
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
		if(key.equals(NUMBER_OF_INPUTS))
			return defaultInt;		
		if(key.equals(DEVICE_TYPE))
			return defaultStore;
		if(key.equals(ColorOutputDeviceInterpreter.COLOR_NEGATIVE))
			return defaultColor;
		if(key.equals(ColorOutputDeviceInterpreter.COLOR_POSITIVE))
			return defaultColor;
		if(key.equals(ColorOutputDeviceInterpreter.COLOR_NEUTRAL))
			return defaultColor;
		
		if(key.equals(INSTRUMENT_X))
			return defaultDouble;
		if(key.equals(INSTRUMENT_Y))
			return defaultDouble;
		if(key.equals(INSTRUMENT_WIDTH))
			return defaultDouble;
		if(key.equals(INSTRUMENT_HEIGHT))
			return defaultDouble;
		if(key.equals(INSTRUMENT_WINDOWED))
			return defaultBool;
		if(key.equals(INSTRUMENT_LOCKED))
			return defaultBool;
		if(key.equals(INSTRUMENT_USER_POSITIONED))
			return defaultBool;
		return super.getDefaultValue(key);
	}
	
	
	public OutputDeviceInterpreter(PropertyStore store) {
		super(store);
	}

	public int getNumberOfInputs()
	{
		return getStore().getInt(NUMBER_OF_INPUTS, 1);
	}
	
	public void setNumberOfInputs(int outputs)
	{
		getStore().setProperty(NUMBER_OF_INPUTS, outputs);
	}
	
	
	public OutputType getDeviceType()
	{
		return getStore().getEnum(DEVICE_TYPE, OutputType.GRAPH);
	}
	
	public void setDeviceType(OutputType type)
	{
		getStore().setProperty(DEVICE_TYPE, type);
	}
	
	
	public void setInstrumentX(double proportionX)
	{
		getStore().setProperty(INSTRUMENT_X, proportionX);
	}
	public void setInstrumentY(double proportionY)
	{
		getStore().setProperty(INSTRUMENT_Y, proportionY);
	}	
	public void setInstrumentWidth(double proportionWidth)
	{
		getStore().setProperty(INSTRUMENT_WIDTH, proportionWidth);
	}	
	public void setInstrumentHeight(double proportionHeight)
	{
		getStore().setProperty(INSTRUMENT_HEIGHT, proportionHeight);
	}
	
	public double getInstrumentX()
	{
		return getStore().getDouble(INSTRUMENT_X,0);
	}
	public double getInstrumentY()
	{
		return getStore().getDouble(INSTRUMENT_Y,0);
	}
	public double getInstrumentWidth()
	{
		return getStore().getDouble(INSTRUMENT_WIDTH,0);
	}
	public double getInstrumentHeight()
	{
		return getStore().getDouble(INSTRUMENT_HEIGHT,0);
	}
	public boolean isInstrumentWindowed()
	{
		return getStore().getBoolean(INSTRUMENT_WINDOWED,true);
	}
	public void setInstrumentWindowed(boolean windowed)
	{
		getStore().setProperty(INSTRUMENT_WINDOWED, windowed);
	}
	public boolean isInstrumentLocked()
	{
		return getStore().getBoolean(INSTRUMENT_LOCKED,false);
	}
	public void setInstrumentLocked(boolean locked)
	{
		getStore().setProperty(INSTRUMENT_LOCKED, locked);
	}

	public boolean isInstrumentUserPositioned() {
		return getStore().getBoolean(INSTRUMENT_USER_POSITIONED,false);
	}
	
	public void setInstrumentUserPositioned(boolean userPositioned){
		getStore().setProperty(INSTRUMENT_USER_POSITIONED, userPositioned);
	}
}
