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