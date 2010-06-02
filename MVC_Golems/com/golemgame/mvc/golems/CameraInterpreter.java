package com.golemgame.mvc.golems;

import java.util.Collection;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;

public class CameraInterpreter extends StandardFunctionalInterpreter {

	public static final String LOCK_ROLL = "lock.roll";
	public static final String LOCK_PITCH = "lock.pitch";
	public static final String LOCK_YAW = "lock.yaw";
	
	public static final String LOCK_ALL = "lock.all";
	
	public static final String VIEW_THRESHOLD = "view.threshold";
	
	public CameraInterpreter() {
		this(new PropertyStore());	
	}

	public CameraInterpreter(PropertyStore store) {
		super(store);
		super.getStore().setClassName(GolemsClassRepository.CAMERA_CLASS);
	}

	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(LOCK_ROLL);
		keys.add(LOCK_PITCH);	
		keys.add(LOCK_YAW);	
		keys.add(LOCK_ALL);	
		keys.add(VIEW_THRESHOLD);
		return super.enumerateKeys(keys);
	}
	
	@Override
	public DataType getDefaultValue(String key) {

		if(key.equals(LOCK_ROLL))
			return defaultBool;
		if(key.equals(LOCK_PITCH))
			return defaultBool;
		if(key.equals(LOCK_YAW))
			return defaultBool;
		if(key.equals(LOCK_ALL))
			return defaultBool;
		if(key.equals(VIEW_THRESHOLD))
			return defaultFloat;
		return super.getDefaultValue(key);
	}
	
	public float getViewThreshold()
	{
		return getStore().getFloat(VIEW_THRESHOLD);
	}
	
	public void setViewThreshold(float threshold)
	{
		getStore().setProperty(VIEW_THRESHOLD, threshold);
	}
	
	public boolean isOrientationLocked()
	{
		return getStore().getBoolean(LOCK_ALL);
	}
	
	public boolean isRollLocked()
	{
		return getStore().getBoolean(LOCK_ROLL);
	}
	public boolean isPitchLocked()
	{
		return getStore().getBoolean(LOCK_PITCH);
	}
	public boolean isYawLocked()
	{
		return getStore().getBoolean(LOCK_YAW);
	}
	public void setOrientationLocked(boolean locked)
	{
		getStore().setProperty(LOCK_ALL, locked);
	}
	public void setRollLocked(boolean locked)
	{
		getStore().setProperty(LOCK_ROLL, locked);
	}
	public void setPitchLocked(boolean locked)
	{
		getStore().setProperty(LOCK_PITCH, locked);
	}	
	public void setYawLocked(boolean locked)
	{
		getStore().setProperty(LOCK_YAW, locked);
	}
}
