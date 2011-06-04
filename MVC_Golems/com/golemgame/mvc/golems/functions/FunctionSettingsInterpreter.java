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
package com.golemgame.mvc.golems.functions;

import java.util.Collection;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.EnumType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.StringType;
import com.golemgame.mvc.golems.GolemsClassRepository;
import com.golemgame.mvc.golems.StoreInterpreter;

public class FunctionSettingsInterpreter extends StoreInterpreter implements  Comparable<FunctionSettingsInterpreter>{
	
	public final static String FUNCTION_NAME = "function.name";
	public final static String SCALEX = "scale.x";
	public final static String SCALEY = "scale.y";
	public final static String PERIODIC = "periodic";
	public final static String MAX_X = "max.x";
	public final static String MAX_Y = "max.y";
	
	public final static String MIN_X = "min.x";
	public final static String MIN_Y = "min.y";
	public static final String FUNCTION_TYPE = "function.type";
	
	public final static String FUNCTION = "function";
	
	public enum FunctionType
	{
		Function(), Differentiate(), AntiDifferentiate();
	}
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(FUNCTION_NAME);
		keys.add(SCALEX);
		keys.add(SCALEY);
		keys.add(PERIODIC);
		keys.add(MAX_X);
		keys.add(MAX_Y);
		keys.add(MIN_X);
		keys.add(MIN_Y);
		keys.add(FUNCTION_TYPE);
		keys.add(FUNCTION);
		return super.enumerateKeys(keys);
	}
	
	private static final StringType defName = new StringType("(Function)");
	
	private static final EnumType defaultFunctionType = new EnumType(FunctionType.Function);
	
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(FUNCTION_NAME))
			return defName;
		if(key.equals(SCALEX))
			return defaultFloat;
		if(key.equals(SCALEY))
			return defaultFloat;
		if(key.equals(PERIODIC))
			return defaultBool;
		if(key.equals(MAX_X))
			return defaultFloat;
		if(key.equals(MAX_Y))
			return defaultFloat;
		if(key.equals(MIN_X))
			return defaultFloat;
		if(key.equals(MIN_Y))
			return defaultFloat;
		if(key.equals(FUNCTION_TYPE))
			return defaultFunctionType;
		if(key.equals(FUNCTION))
			return defaultStore;
		
		return super.getDefaultValue(key);
	}
	
	
	public FunctionSettingsInterpreter() {
		this(new PropertyStore());
	}

	public FunctionSettingsInterpreter(PropertyStore store) {
		super(store);
		super.getStore().setClassName(GolemsClassRepository.FUNCTION_SETTINGS_CLASS);
	}

	
	public PropertyStore getFunction()
	{
		return getStore().getPropertyStore(FUNCTION);
	}

	public void setFunction(PropertyStore store)
	{
		getStore().setProperty(FUNCTION, store);
	}
	
	public void setScaleX(float scale)
	{
		super.getStore().setProperty(SCALEX, scale);
	}
	public float getScaleX()
	{
		return super.getStore().getFloat(SCALEX, 1f);
	}
	
	public void setScaleY(float scale)
	{
		super.getStore().setProperty(SCALEY, scale);
	}
	public float getScaleY()
	{
		return super.getStore().getFloat(SCALEY, 1f);
	}
	
	public void setMaxX(float scale)
	{
		super.getStore().setProperty(MAX_X, scale);
	}
	public float getMaxX()
	{
		return super.getStore().getFloat(MAX_X, 1f);
	}
	
	public void setMaxY(float scale)
	{
		super.getStore().setProperty(MAX_Y, scale);
	}
	public float getMaxY()
	{
		return super.getStore().getFloat(MAX_Y, 1f);
	}
	public void setMinX(float scale)
	{
		super.getStore().setProperty(MIN_X, scale);
	}
	public float getMinX()
	{
		return super.getStore().getFloat(MIN_X, -1f);
	}
	public void setMinY(float scale)
	{
		super.getStore().setProperty(MIN_Y, scale);
	}
	public float getMinY()
	{
		return super.getStore().getFloat(MIN_Y, -1f);
	}
	
	public void setPeriodic(boolean periodic)
	{
		super.getStore().setProperty(PERIODIC, periodic);
	}
	public boolean isPeriodic()
	{
		return super.getStore().getBoolean(PERIODIC,false);
	}


	
	public FunctionType getFunctionType()
	{
		return getStore().getEnum(FUNCTION_TYPE, FunctionType.Function);
	}
	
	public void setFunctionType(FunctionType key)
	{
		getStore().setProperty(FUNCTION_TYPE, key);
	}
	
	public String getFunctionName()
	{
		return getStore().getString(FUNCTION_NAME, "Unnamed");
	}
	
	public void setFunctionName(String name)
	{
		getStore().setProperty(FUNCTION_NAME, name);
	}


	public int compareTo(FunctionSettingsInterpreter o) {
		
		return this.getFunctionName().compareTo(o.getFunctionName());
	}


	
}
