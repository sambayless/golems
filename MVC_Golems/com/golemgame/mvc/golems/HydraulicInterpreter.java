package com.golemgame.mvc.golems;

import java.util.Collection;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.FloatType;
import com.golemgame.mvc.PropertyStore;

public class HydraulicInterpreter extends StandardFunctionalInterpreter {

	public static final String JOINT_DISTANCE = "distance.cur";
	public static final String JOINT_MAX_DISTANCE = "distance.max";
	public static final String JOINT_MIN_DISTANCE = "distance.min";
	public static final String JOINT_RADIUS = "radius";
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(JOINT_DISTANCE);
		keys.add(JOINT_MAX_DISTANCE);
		keys.add(JOINT_MIN_DISTANCE);
		keys.add(JOINT_RADIUS);
		return super.enumerateKeys(keys);
	}
	
	private static final FloatType defaultMinDistance = new FloatType(0.01f);
	private static final FloatType defaultMaxDistance = new FloatType(5.00f);
	private static final FloatType defaultDistance = new FloatType(2.00f);
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(JOINT_DISTANCE))
			return defaultDistance;
		if(key.equals(JOINT_MAX_DISTANCE))
			return defaultMaxDistance;
		if(key.equals(JOINT_MIN_DISTANCE))
			return defaultMinDistance;
		if(key.equals(JOINT_RADIUS))
			return defaultFloat;
		return super.getDefaultValue(key);
	}
	
	public HydraulicInterpreter(PropertyStore store) {
		super(store);
		store.setClassName(GolemsClassRepository.HYDRAULIC_CLASS);
	}

	public HydraulicInterpreter() {
		this(new PropertyStore());		
	}
	public float getMaxJointDistance()
	{
			return super.getStore().getFloat(JOINT_MAX_DISTANCE,5f);
	}

	public void setMaxJointDistance(float length)
	{
		super.getStore().setProperty(JOINT_MAX_DISTANCE, length);
	}
	
	public float getMinJointDistance()
	{
			return super.getStore().getFloat(JOINT_MIN_DISTANCE,0.01f);
	}

	public void setMinJointDistance(float length)
	{
		super.getStore().setProperty(JOINT_MIN_DISTANCE, length);
	}
	
	
	public float getJointDistance()
	{
			return super.getStore().getFloat(JOINT_DISTANCE,2f);
	}

	public void setJointDistance(float length)
	{
		super.getStore().setProperty(JOINT_DISTANCE, length);
	}
	
	public float getJointRadius()
	{
			return super.getStore().getFloat(JOINT_RADIUS,0.5f);
	}

	public void setJointRadius(float radius)
	{
		super.getStore().setProperty(JOINT_RADIUS, radius);
	}
	public static final String MOTOR_PROPERTIES = "motor.properties";
	public PropertyStore getMotorPropertiesStore() {
		return super.getStore().getPropertyStore(MOTOR_PROPERTIES);
	}
	

	
}

