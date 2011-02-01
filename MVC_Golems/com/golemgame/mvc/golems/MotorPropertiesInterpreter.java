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
import com.golemgame.mvc.FloatType;
import com.golemgame.mvc.PropertyStore;

public class MotorPropertiesInterpreter extends StoreInterpreter {

	public static enum IOType
	{
		POSITION("Position"),VELOCITY("Velocity"),ACCELERATION("Force");
		private final String name;
	
		private IOType(String name) {
			this.name = name;
		}
	
		@Override
		public String toString() {
			return name;
		}
		
	}

	public static final String SPRING_FRICTION = "spring.position";
	public static final String INPUT_TYPE = "input.type";
	public static final String OUTPUT_TYPE = "output.type";
	public static final String ACCELERATION = "acceleration.max";
	public static final String VELOCITY = "velocity.max";
	//public static final String MASS = "mass";
	public static final String SPRING_CONSTANT = "spring.constant";
	public static final String IS_SPRING = "isSpring";
	public static final String WRAPS = "wraps";
	public static final String MIN_POSITION = "position.min";
	public static final String MAX_POSITION = "position.max";
	
	public static final String PID_KP = "pid.Kp";
	public static final String PID_KI = "pid.Ki";
	public static final String PID_KD = "pid.Kd";
	
	
	public static final float DEFAULT_ACCELERATION = 10;
	public static final MotorPropertiesInterpreter.IOType DEFAULT_OUTPUT_TYPE = MotorPropertiesInterpreter.IOType.VELOCITY;
	public static final float DEFAULT_SPRING_CONSTANT = 10;
	public static final float DEFAULT_SPRING_FRICTION = 0.5f;
	public static final float DEFAULT_MIN_POSITION = 0;
	public static final float DEFAULT_MAX_POSITION = 0;
	public static final MotorPropertiesInterpreter.IOType DEFAULT_INPUT_TYPE = MotorPropertiesInterpreter.IOType.ACCELERATION;
	public static final float DEFAULT_VELOCITY = 10;
	public static final float DEFAULT_KP = 0.1f;  
	public static final float DEFAULT_KI =  0.0002f;
	public static final float DEFAULT_KD = 0.001f;
	

	

	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(SPRING_FRICTION);
		keys.add(INPUT_TYPE);
		keys.add(OUTPUT_TYPE);
		keys.add(ACCELERATION);
		keys.add(VELOCITY);
		//keys.add(MASS);
		keys.add(SPRING_CONSTANT);
		keys.add(IS_SPRING);
		keys.add(WRAPS);
		keys.add(MIN_POSITION);
		keys.add(MAX_POSITION);
		keys.add(PID_KP);
		keys.add(PID_KI);
		keys.add(PID_KD);
		
		return super.enumerateKeys(keys);
	}
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(SPRING_FRICTION))
			return defaultFloat;
		if(key.equals(INPUT_TYPE))
			return defaultEnum;
		if(key.equals(OUTPUT_TYPE))
			return defaultEnum;
		if(key.equals(ACCELERATION))
			return defaultFloat;
		if(key.equals(VELOCITY))
			return defaultFloat;
		if(key.equals(PID_KP))
			return new FloatType(DEFAULT_KP);
		if(key.equals(PID_KI))
			return new FloatType(DEFAULT_KI);
		if(key.equals(PID_KD))
			return new FloatType(DEFAULT_KD);
		
		//if(key.equals(MASS))
	///		return defaultFloat;		
		if(key.equals(SPRING_CONSTANT))
			return defaultFloat;
		if(key.equals(WRAPS))
			return defaultBool;
		if(key.equals(MIN_POSITION))
			return defaultFloat;
		if(key.equals(MAX_POSITION))
			return defaultFloat;

		return super.getDefaultValue(key);
	}
	
	public MotorPropertiesInterpreter() {
		this( new PropertyStore());
	}

	public MotorPropertiesInterpreter(PropertyStore store) {
		super(store);
		super.getStore().setClassName(GolemsClassRepository.MOTOR_PROPERTIES_CLASS);
	}

/*	public float getMass() {
		return this.getStore().getFloat(MASS,1f);
	}

	public void setMass(float mass) {
		getStore().setProperty(MASS,mass);
		
	}*/

	public float getSpringFriction() {
		return getStore().getFloat(SPRING_FRICTION,DEFAULT_SPRING_FRICTION);
	}

	public void setSpringFriction(float springFriction) {
		getStore().setProperty(SPRING_FRICTION, springFriction);
	}

	public MotorPropertiesInterpreter.IOType getInputType() {
		return getStore().getEnum(INPUT_TYPE, MotorPropertiesInterpreter.IOType.ACCELERATION);
	}

	public void setInputType(MotorPropertiesInterpreter.IOType inputType) {
		getStore().setProperty(INPUT_TYPE, inputType);
	}

	public MotorPropertiesInterpreter.IOType getOutputType() {
		return getStore().getEnum(OUTPUT_TYPE, MotorPropertiesInterpreter.IOType.VELOCITY);
	}

	public void setOutputType(MotorPropertiesInterpreter.IOType inputType) {
		getStore().setProperty(OUTPUT_TYPE, inputType);
	}


	
	public float getMaxAcceleration() {	
		return getStore().getFloat(ACCELERATION,DEFAULT_ACCELERATION);
	}

	
	public float getMaxVelocity() {
		return getStore().getFloat(VELOCITY,DEFAULT_VELOCITY);
	}


	
	public void setMaxAcceleration(float acceleration) {
		getStore().setProperty(ACCELERATION, acceleration);

	}

	public float getPID_Kp() {
		return getStore().getFloat(PID_KP,DEFAULT_KP);
	}

	public void setPID_Kp(float p) {
		getStore().setProperty(PID_KP, p);
	}

	public float getPID_Ki() {
		return getStore().getFloat(PID_KI,DEFAULT_KI);
	}

	public void setPID_Ki(float i) {
		getStore().setProperty(PID_KI, i);
	}
	
	public float getPID_Kd() {
		return getStore().getFloat(PID_KD,DEFAULT_KD);
	}

	public void setPID_Kd(float d) {
		getStore().setProperty(PID_KD, d);
	}
	
	public void setMaxVelocity(float velocity) {
		getStore().setProperty(VELOCITY, velocity);

	}

	public float getMaxPosition() {
		return getStore().getFloat(MAX_POSITION,DEFAULT_MAX_POSITION);
	}

	public void setMaxPosition(float position) {
		getStore().setProperty(MAX_POSITION, position);
	}

	public float getMinPosition() {
		return getStore().getFloat(MIN_POSITION,DEFAULT_MIN_POSITION);
	}

	public void setMinPosition(float minPosition) {
		getStore().setProperty(MIN_POSITION, minPosition);
	}

	public boolean doesPositionWrap() {
		return getStore().getBoolean(WRAPS);
	}

	public void setPositionWraps(boolean wraps) {
		getStore().setProperty(WRAPS,wraps);
	}

	public boolean isSpring() {
		return getStore().getBoolean(IS_SPRING);
	}

	public void setSpring(boolean isSpring) {
		getStore().setProperty(IS_SPRING,isSpring);
	}

	public float getSpringConstant() {
		return getStore().getFloat(SPRING_CONSTANT,DEFAULT_SPRING_CONSTANT);
	}

	public void setSpringConstant(float springConstant) {
		getStore().setProperty(SPRING_CONSTANT,springConstant);
	}
	
	
	

}
