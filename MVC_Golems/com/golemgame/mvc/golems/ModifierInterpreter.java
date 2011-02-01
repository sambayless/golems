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
import com.golemgame.mvc.golems.BatteryInterpreter.ThresholdType;
import com.golemgame.mvc.golems.functions.FunctionSettingsInterpreter;
import com.golemgame.mvc.golems.functions.PolynomialFunctionInterpreter;

public class ModifierInterpreter extends StandardFunctionalInterpreter {

	public static enum ModifierSwitchType
	{
		On("Turn Off/On"),Invert("Invert Signal"),Pause("Hold Value"),Multiply("Multiply Value");
		
		private String description;
		
		private ModifierSwitchType (String description)
		{
			this.description = description;
		}
		
		public String getDescription()
		{
			return description;
		}
	}

	public static final String FUNCTION_SETTINGS = "function.settings";

	public static final String MODIFIER_TYPE = "switchType";
	public static final String THRESHOLD_TYPE = "threshold.type";
	public static final String THRESHOLD = "threshold";
	//public static final String THRESHOLD_INVERTED = "threshold.inverted";

	//public static final String FUNCTION_TYPE = "function.type";
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		//keys.add(FUNCTION_TYPE);
		keys.add(THRESHOLD_TYPE);
		//keys.add(THRESHOLD_INVERTED);
		keys.add(THRESHOLD);
		keys.add(MODIFIER_TYPE);

		keys.add(FUNCTION_SETTINGS);

		return super.enumerateKeys(keys);
	}
	
	private static final EnumType defaultSwitch = new EnumType(ModifierSwitchType.On);
	private static final EnumType defaultThresholdType = new EnumType(ThresholdType.GREATER_EQUAL);
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(FUNCTION_SETTINGS))
			return defaultStore;
		if(key.equals(MODIFIER_TYPE))
			return defaultSwitch;
		if(key.equals(THRESHOLD))
			return defaultFloat;
	/*	if(key.equals(THRESHOLD_INVERTED))
			return defaultBool;
*/
		if(key.equals(THRESHOLD_TYPE))
			return defaultThresholdType;

		return super.getDefaultValue(key);
	}
	/*
	 *	private FunctionType functionType  = FunctionType.Function;
	
	private float threshold;
	private boolean thresholdInverted;
	
	private ModifierSwitchType switchType= ModifierSwitchType.On;
	 */
	
	public ModifierInterpreter() {
		this(new PropertyStore());
	}

	public ModifierInterpreter(PropertyStore store) {
		super(store);
		store.setClassName(GolemsClassRepository.MODIFIER_CLASS);
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
	public boolean isThresholdInverted()
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
	
	
	public ModifierSwitchType getSwitchType()
	{
		return getStore().getEnum(MODIFIER_TYPE, ModifierSwitchType.On);
	}
	
	public void setSwitchType(ModifierSwitchType key)
	{
		getStore().setProperty(MODIFIER_TYPE, key);
	}
	

	
	/*public FunctionType getFunctionType()
	{
		return getStore().getEnum(FUNCTION_TYPE, FunctionType.Function);
	}
	
	public void setFunctionType(FunctionType key)
	{
		getStore().setProperty(FUNCTION_TYPE, key);
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

