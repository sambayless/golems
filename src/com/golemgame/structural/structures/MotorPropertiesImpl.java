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
package com.golemgame.structural.structures;

import java.io.Serializable;

import com.golemgame.functional.component.MotorProperties;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.MotorPropertiesInterpreter;


public class MotorPropertiesImpl implements MotorProperties, Serializable {

	private static final long serialVersionUID = 1L;

	private float acceleration = 10;
	private float velocity = 10;
	
	private MotorPropertiesInterpreter.IOType inputType = MotorPropertiesInterpreter.IOType.ACCELERATION;
	private MotorPropertiesInterpreter.IOType outputType = MotorPropertiesInterpreter.IOType.VELOCITY;
	
	public float maxPosition;
	public float minPosition;
	public boolean wraps;
	
	private float PID_KP;
	private float PID_KI;
	private float PID_KD;
	
	private boolean isSpring = false;
	private float springConstant = 10;
	private float springFriction = 0.5f;
	
	private float mass1 = 1;
	private float mass2= 1;
	public float getMass1() {
		return mass1;
	}

	public void setMass1(float mass) {
		this.mass1 = mass;
	}
	public float getMass2() {
		return mass2;
	}

	public void setMass2(float mass) {
		this.mass2 = mass;
	}

	/*public void set(MotorPropertiesImpl from)
	{
		this.interpreter.getStore().set(from.getStore());
		this.maxPosition = from.maxPosition;
		this.minPosition = from.minPosition;
		this.acceleration = from.acceleration;
		this.velocity = from.velocity;
		this.inputType = from.inputType;
		this.outputType = from.outputType;
		this.wraps = from.wraps;
		this.isSpring = from.isSpring;
		this.springConstant = from.springConstant;
		this.springFriction = from.springFriction;
	}
	*/
	public float getSpringFriction() {
		return this.springFriction;
	}

	public void setSpringFriction(float springFriction) {
		interpreter.setSpringFriction( springFriction);
		interpreter.refresh();
	}

	public MotorPropertiesInterpreter.IOType getInputType() {
		return this.inputType;
	}

	public void setInputType(MotorPropertiesInterpreter.IOType inputType) {
		interpreter.setInputType(inputType);
		interpreter.refresh();
	}

	public MotorPropertiesInterpreter.IOType getOutputType() {
		return this.outputType;
	}

	public void setOutputType(MotorPropertiesInterpreter.IOType outputType) {
		interpreter.setOutputType(outputType);
		interpreter.refresh();
	}

	
	public float getMaxAcceleration() {	
		return this.acceleration;
	}

	
	public float getMaxVelocity() {
	
		return this.velocity;
	}


	
	public void setMaxAcceleration(float acceleration) {
		interpreter.setMaxAcceleration( acceleration);
		interpreter.refresh();
	}



	
	public void setMaxVelocity(float velocity) {
		interpreter.setMaxVelocity(velocity);
		interpreter.refresh();
	}

	public float getMaxPosition() {
		return this.maxPosition;
	}

	public void setMaxPosition(float maxPosition) {
		this.interpreter.setMaxPosition(maxPosition);
		interpreter.refresh();
	}

	public float getMinPosition() {
		return this.minPosition;
	}

	public void setMinPosition(float minPosition) {
		interpreter.setMinPosition( minPosition);
		interpreter.refresh();
	}
	
	public float getPID_KP() {
		return this.PID_KP;
	}

	public void setPID_KP(float p) {
		interpreter.setPID_Kp( p);
		interpreter.refresh();
	}
	
	public float getPID_KI() {
		return this.PID_KI;
	}

	public void setPID_KI(float i) {
		interpreter.setPID_Ki( i);
		interpreter.refresh();
	}
	
	
	public float getPID_KD() {
		return this.PID_KD;
	}

	public void setPID_KD(float d) {
		interpreter.setPID_Kd( d);
		interpreter.refresh();
	}
	

	public boolean doesPositionWrap() {
		return this.wraps;
	}

	public void setPositionWraps(boolean wraps) {
		interpreter.setPositionWraps( wraps);
		interpreter.refresh();
	}

	public boolean isSpring() {
		return this.isSpring;
	}

	public void setSpring(boolean isSpring) {
		this.interpreter.setSpring( isSpring);
		interpreter.refresh();
	}

	public float getSpringConstant() {
		return this.springConstant;
	}

	public void setSpringConstant(float springConstant) {
		interpreter.setSpringConstant(springConstant);
		interpreter.refresh();
	}
	
	private MotorPropertiesInterpreter interpreter;

	public MotorPropertiesImpl(PropertyStore store) {
		super();
		this.interpreter = new MotorPropertiesInterpreter(store);
		this.interpreter.getStore().setSustainedView(this);
		this.refresh();
	}
	public void remove() {
		// TODO Auto-generated method stub
		
	}
	

	public void refresh() {
		this.acceleration = interpreter.getMaxAcceleration();
		this.velocity = interpreter.getMaxVelocity();
	//	this.mass = interpreter.getMass();
		this.maxPosition = interpreter.getMaxPosition();
		this.minPosition = interpreter.getMinPosition();
		this.isSpring = interpreter.isSpring();
		this.outputType = interpreter.getOutputType();
		this.inputType = interpreter.getInputType();
		this.springConstant = interpreter.getSpringConstant();
		this.springFriction = interpreter.getSpringFriction();
		this.wraps = interpreter.doesPositionWrap();
	
		this.PID_KP = interpreter.getPID_Kp();
		this.PID_KI = interpreter.getPID_Ki();
		this.PID_KD = interpreter.getPID_Kd();
		
	}

	public PropertyStore getStore() {
		return interpreter.getStore();
	}

}
