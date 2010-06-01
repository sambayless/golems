package com.golemgame.mvc.golems;

import java.util.Collection;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;

public class BallAndSocketInterpreter extends PhysicalStructureInterpreter {
	public static final String LEFT_JOINT_LENGTH = "left.length";
	public static final String LEFT_JOINT_RADIUS = "left.radius";
	public static final String RIGHT_JOINT_RADIUS = "right.radius";
	public static final String MOTOR_PROPERTIES = "motor.properties";
	public static final String IS_UNIVERSAL = "universal";
	private static final float DEFAULT_RADIUS = 0.5f;
	private static final float DEFAULT_LENGTH = 1f;

	public BallAndSocketInterpreter(PropertyStore store) {
		super(store);
		store.setClassName(GolemsClassRepository.BALL_SOCKET_CLASS);
	}

	public BallAndSocketInterpreter() {
		this(new PropertyStore());		
	}

	
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {

		keys.add(LEFT_JOINT_LENGTH);	
		keys.add(LEFT_JOINT_RADIUS);	
		keys.add(RIGHT_JOINT_RADIUS);	
		keys.add(IS_UNIVERSAL);
		return super.enumerateKeys(keys);
	}

	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(LEFT_JOINT_LENGTH))
			return defaultVector3;	
		if(key.equals(LEFT_JOINT_RADIUS))
			return defaultVector3;	
		if(key.equals(RIGHT_JOINT_RADIUS))
			return defaultVector3;	
		if(key.equals(IS_UNIVERSAL))
			return defaultBool;	
		return super.getDefaultValue(key);
	}
	
	public void setLeftRadius(float radius)
	{
		this.getStore().setProperty(LEFT_JOINT_RADIUS, radius);
	
	}
	
	public void setRightRadius(float radius)
	{
		this.getStore().setProperty(RIGHT_JOINT_RADIUS, radius);
	
	}
	
	public void setLeftLength(float length)
	{
		this.getStore().setProperty(LEFT_JOINT_LENGTH, length);
	
	}
	public boolean isUniversalJoint()
	{
		return this.getStore().getBoolean(IS_UNIVERSAL, false);
	
	}
	public void setUniversalJoint(boolean universal)
	{
		this.getStore().setProperty(IS_UNIVERSAL, universal);
	
	}
	public float getLeftRadius()
	{
		return this.getStore().getFloat(LEFT_JOINT_RADIUS, DEFAULT_RADIUS);
	
	}
	
	public float getRightRadius()
	{
		return this.getStore().getFloat(RIGHT_JOINT_RADIUS, DEFAULT_RADIUS);
	
	}
	
	public float getLeftLength()
	{
		return this.getStore().getFloat(LEFT_JOINT_LENGTH, DEFAULT_LENGTH);
	
	}


	public PropertyStore getMotorPropertiesStore() {
		return super.getStore().getPropertyStore(MOTOR_PROPERTIES);
	}
	

}
