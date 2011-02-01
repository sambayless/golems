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
package com.golemgame.functional.component;

import com.golemgame.mvc.SustainedView;
import com.golemgame.mvc.golems.MotorPropertiesInterpreter;

public interface MotorProperties extends SustainedView
{
	public MotorPropertiesInterpreter.IOType getInputType();
	
	public void setInputType(MotorPropertiesInterpreter.IOType inputType);

	public MotorPropertiesInterpreter.IOType getOutputType();

	public void setOutputType(MotorPropertiesInterpreter.IOType outputType);

	public float getMaxAcceleration();
	public void setMaxAcceleration(float scale);
	public float getMaxVelocity();
	public void setMaxVelocity(float scale);

	public float getMaxPosition();

	public float getMinPosition();
	
	public float getPID_KP();

	public void setPID_KP(float p);
	
	public float getPID_KI();

	public void setPID_KI(float i);
	
	
	public float getPID_KD();

	public void setPID_KD(float d);
	
	public float getMass1();
	public void setMass1(float mass);
	public float getMass2();
	public void setMass2(float mass);
	public boolean doesPositionWrap();
	
	public boolean isSpring();
	public void setSpring(boolean isSpring);
	public float getSpringConstant();
	public void setSpringConstant(float spring);
	public float getSpringFriction();
	public void setSpringFriction(float spring);
	
}
