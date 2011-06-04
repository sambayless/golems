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

import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.FloatType;
import com.golemgame.mvc.PropertyStore;

public class GeneralSensorSettingsInterpreter extends StoreInterpreter{
	public static final String FLOAT_SETTINGS = "floatSettings";
	public static final String RELATIVE = "relative";
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(RELATIVE);		
		keys.add(FLOAT_SETTINGS);		
		return super.enumerateKeys(keys);
	}
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(RELATIVE))
			return defaultBool;
		if(key.equals(FLOAT_SETTINGS))
			return defaultCollection;
		return super.getDefaultValue(key);
	}
	
	public GeneralSensorSettingsInterpreter() {
		this(new PropertyStore());
	}

	public GeneralSensorSettingsInterpreter(PropertyStore store) {
		super(store);
		this.getStore().setClassName(GolemsClassRepository.GENERAL_SENSOR_SETTINGS_CLASS);
	}

	public CollectionType getSettings()
	{
		return getStore().getCollectionType(FLOAT_SETTINGS);
	}
	
	public float getFloat(int pos)
	{
		DataType data = getSettings().getElement(pos);
		if(data.getType() != DataType.Type.FLOAT)
		{
			data = new FloatType();
			getSettings().setElement(data, pos);
		}
		return ((FloatType)data).getValue();
	}

	public int getNumberOfValues() {
		return getSettings().getValues().size();
	}

	public void setValue(int index, float value) {
		getSettings().setElement(new FloatType(value), index);
		
	}
	
	public boolean isRelative()
	{
		return getStore().getBoolean(RELATIVE,true);
	}
	public void setRelative(boolean relative)
	{
		getStore().setProperty(RELATIVE, relative);
	}
}
