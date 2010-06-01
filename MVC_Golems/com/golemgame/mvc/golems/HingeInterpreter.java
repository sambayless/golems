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
