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
import com.golemgame.mvc.DoubleType;
import com.golemgame.mvc.EnumType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.functions.FunctionSettingsInterpreter;
import com.golemgame.mvc.golems.functions.PolynomialFunctionInterpreter;

public class BatteryInterpreter extends StandardFunctionalInterpreter{

	public static final String FUNCTION_SETTINGS = "function.settings";

	public static final String SWITCH_TYPE = "switchType";

	public static final String THRESHOLD = "threshold";
	
	public static final String THRESHOLD_TYPE = "threshold.type";




	public enum SwitchType
	{
		On("Turn Off/On"),Invert("Invert Signal"),Reset("Reset Time"),Pause("Pause/Unpause Time");
		
		private final String description;
		
		private SwitchType (String description)
		{
			this.description = description;
		}
		
		public String getDescription()
		{
			return description;
		}
	}
	
	public enum ThresholdType {
		GREATER_THAN(">"),GREATER_EQUAL(">="),LESSER_THAN("<"),LESSER_EQUAL("<=");
		private final String description;
		
		private ThresholdType (String description)
		{
			this.description = description;
		}
		
		public String getDescription()
		{
			return description;
		}
	}
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {

		keys.add(FUNCTION_SETTINGS);	
		keys.add(THRESHOLD_TYPE);
		keys.add(SWITCH_TYPE);	
		//keys.add(INTERACTION_TYPE);	
		keys.add(THRESHOLD);	
	//	keys.add(THRESHOLD_INVERTED);
		//keys.add(INTERACTION_TYPE);
		//keys.add(INTERACT);	
		//keys.add(KEY_CODE);	
		//keys.add(FUNCTION_TYPE);	

		return super.enumerateKeys(keys);
	}
	
	private static final EnumType defaultSwitchType = new EnumType(SwitchType.On);
	public static final EnumType defaultThresholdType = new EnumType(ThresholdType.GREATER_EQUAL);

	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(FUNCTION_SETTINGS))
			return defaultStore;	
		if(key.equals(THRESHOLD))
			return defaultFloat;

		//if(key.equals(INTERACTION_TYPE))
		//	return defaultInteractionType;
		if(key.equals(SWITCH_TYPE))
			return defaultSwitchType;
		/*if(key.equals(INTERACT))
			return defaultBool;*/
	//	if(key.equals(KEY_CODE))
	//		return defaultInt;
		if(key.equals(THRESHOLD_TYPE))
			return defaultThresholdType;
		return super.getDefaultValue(key);
	}
	public BatteryInterpreter() {
		this(new PropertyStore());
	}

	public BatteryInterpreter(PropertyStore store) {
		super(store);
		store.setClassName(GolemsClassRepository.BATTERY_CLASS);
	}

	public PropertyStore getFunctionStore() {
		if (!getStore().hasProperty(FUNCTION_SETTINGS))
		{
			FunctionSettingsInterpreter settings = new FunctionSettingsInterpreter(getStore().getPropertyStore(FUNCTION_SETTINGS));

			PolynomialFunctionInterpreter interp = new PolynomialFunctionInterpreter(settings.getFunction());
			interp.setCoefficient(0, new DoubleType(1));
			interp.setCoefficient(1, new DoubleType(0));
		}
		return getStore().getPropertyStore(FUNCTION_SETTINGS);
	}

	public void setFunctionStore(PropertyStore functionStore) {
		getStore().setProperty(FUNCTION_SETTINGS, functionStore);
		
	}

	
/*
	public boolean interactsWithUser()
	{
		return getStore().getBoolean(INTERACT, false);
	}
	
	public void setInteractsWithUser(boolean interacts)
	{
		getStore().setProperty(INTERACT, interacts);
	}
	*/
/*	public boolean isThresholdInverted()
	{
		return getStore().getBoolean(THRESHOLD_INVERTED, false);
	}
	
	public void setThresholdInverted(boolean interacts)
	{
		getStore().setProperty(THRESHOLD_INVERTED, interacts);
	}
	*/
	public void setThresholdType(ThresholdType threshold)
	{
		getStore().setProperty(THRESHOLD_TYPE, threshold);
	}
	
	public ThresholdType getThresholdType()
	{
		return getStore().getEnum(THRESHOLD_TYPE, ThresholdType.GREATER_EQUAL);
	}
	
	/*public int getInteractionKey()
	{
		return getStore().getInt(KEY_CODE, 0);
	}
	
	public void setInteractionKey(int key)
	{
		getStore().setProperty(KEY_CODE, key);
	}
	*/
	
	public SwitchType getSwitchType()
	{
		return getStore().getEnum(SWITCH_TYPE, SwitchType.On);
	}
	
	public void setSwitchType(SwitchType key)
	{
		getStore().setProperty(SWITCH_TYPE, key);
	}
	
	/*public KeyboardInteractionType getInteractionType()
	{
		return getStore().getEnum(INTERACTION_TYPE, KeyboardInteractionType.HeldDown);
	}
	
	public void setInteractionType(KeyboardInteractionType key)
	{
		getStore().setProperty(INTERACTION_TYPE, key);
	}*/
	

	public float getThreshold()
	{
		return getStore().getFloat(THRESHOLD, 0f);
	}
	
	public void setThreshold(float key)
	{
		getStore().setProperty(THRESHOLD, key);
	}
	
}
