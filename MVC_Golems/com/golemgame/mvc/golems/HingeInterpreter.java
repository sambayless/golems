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

public class HingeInterpreter extends StandardFunctionalInterpreter {


	public static final String LEFT_JOINT_ANGLE = "left.angle";
	public static final String RIGHT_JOINT_ANGLE = "right.angle";
	public static final String LEFT_JOINT_LENGTH = "left.length";
	public static final String RIGHT_JOINT_LENGTH = "right.length";
	
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(LEFT_JOINT_ANGLE);
		keys.add(RIGHT_JOINT_ANGLE);
		keys.add(LEFT_JOINT_LENGTH);
		keys.add(RIGHT_JOINT_LENGTH);
		return super.enumerateKeys(keys);
	}
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(LEFT_JOINT_ANGLE))
			return defaultFloat;
		if(key.equals(RIGHT_JOINT_ANGLE))
			return defaultFloat;
		if(key.equals(LEFT_JOINT_LENGTH))
			return defaultFloat;
		if(key.equals(RIGHT_JOINT_LENGTH))
			return defaultFloat;
		return super.getDefaultValue(key);
	}
	
	public HingeInterpreter(PropertyStore store) {
		super(store);	
		store.setClassName(GolemsClassRepository.HINGE_CLASS);
	}

	public HingeInterpreter() {
		this(new PropertyStore());		
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
	
	

	public float getJointAngle(boolean left)
	{
		if (left)
			return super.getStore().getFloat(LEFT_JOINT_ANGLE,0);
		else
			return super.getStore().getFloat(RIGHT_JOINT_ANGLE,0);
	}

	public void setJointAngle(float angle, boolean left)
	{
		if (left)
			super.getStore().setProperty(LEFT_JOINT_ANGLE, angle);
		else
			super.getStore().setProperty(RIGHT_JOINT_ANGLE, angle);
	}
	public static final String MOTOR_PROPERTIES = "motor.properties";
	public PropertyStore getMotorPropertiesStore() {
		return super.getStore().getPropertyStore(MOTOR_PROPERTIES);
	}
	

}
