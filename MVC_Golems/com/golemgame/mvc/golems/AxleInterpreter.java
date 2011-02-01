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

public class AxleInterpreter extends StandardFunctionalInterpreter {

	public static final String LEFT_JOINT_LENGTH = "left.length";
	public static final String RIGHT_JOINT_LENGTH = "right.length";
	
	public static final String LEFT_JOINT_RADIUS = "left.radius";
	public static final String RIGHT_JOINT_RADIUS = "right.radius";
	
	public static final String BEARING_JOINT_LENGTH = "bearing.length";
	public static final String BEARING_JOINT_RADIUS = "bearing.radius";
	
	public static final String IS_BEARING = "isBearing";
	
	public AxleInterpreter(PropertyStore store) {
		super(store);	
		store.setClassName(GolemsClassRepository.AXLE_CLASS);
	}

	public AxleInterpreter() {
		this(new PropertyStore());		
	}
	
	
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {

		keys.add(LEFT_JOINT_LENGTH);	
		keys.add(RIGHT_JOINT_LENGTH);	
		keys.add(LEFT_JOINT_RADIUS);	
		keys.add(RIGHT_JOINT_RADIUS);	
		return super.enumerateKeys(keys);
	}
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(LEFT_JOINT_LENGTH))
			return defaultFloat;	
		if(key.equals(RIGHT_JOINT_LENGTH))
			return defaultFloat;	
		if(key.equals(LEFT_JOINT_RADIUS))
			return defaultFloat;	
		if(key.equals(RIGHT_JOINT_RADIUS))
			return defaultFloat;	
		return super.getDefaultValue(key);
	}
	public float getJointLength(boolean left)
	{
		if(left)
			return super.getStore().getFloat(LEFT_JOINT_LENGTH, 1f);
		else
			return super.getStore().getFloat(RIGHT_JOINT_LENGTH,1f);
	}

	public void setJointLength(float length, boolean left)
	{
		if(left)
			super.getStore().setProperty(LEFT_JOINT_LENGTH, length);
		else
			super.getStore().setProperty(RIGHT_JOINT_LENGTH, length);
	}

	
	public float getJointRadius(boolean left)
	{
		if(left)
			return super.getStore().getFloat(LEFT_JOINT_RADIUS, 0.5f);
		else
			return super.getStore().getFloat(RIGHT_JOINT_RADIUS,0.1f);
	}

	public void setJointRadius(float radius, boolean left)
	{
		if(left)
			super.getStore().setProperty(LEFT_JOINT_RADIUS, radius);
		else
			super.getStore().setProperty(RIGHT_JOINT_RADIUS, radius);
	}
	
	public boolean isBearing()
	{
		//return true;
		return super.getStore().getBoolean(IS_BEARING,false);
	}
	public void setIsBearing(boolean isBearing)
	{
		super.getStore().setProperty(IS_BEARING,isBearing);
	}
	
	public float getBearingLength()
	{
		return getJointLength(false);
			//return super.getStore().getFloat(BEARING_JOINT_LENGTH,0.1f);
	}

	public void setBearingLength(float length)
	{	
			super.getStore().setProperty(BEARING_JOINT_LENGTH, length);	
	}
	
	public float getBearingRadius()
	{
		return getJointRadius(false);
			//return super.getStore().getFloat(BEARING_JOINT_RADIUS,0.1f);
	}

	public void setBearingRadius(float radius)
	{
	
			super.getStore().setProperty(BEARING_JOINT_RADIUS, radius);

	}
	
	public static final String MOTOR_PROPERTIES = "motor.properties";
	public PropertyStore getMotorPropertiesStore() {
		return super.getStore().getPropertyStore(MOTOR_PROPERTIES);
	}
	

}
