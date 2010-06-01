package com.golemgame.mvc.golems;

import java.util.Collection;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;

public class DistanceSensorInterpreter extends PyramidInterpreter{
private final StandardFunctionalInterpreter portInterpreter;
	
	public static enum SensorMode
	{
		
		/**
		 * Ignore all non-static elements
		 */
		IGNORE_NON_STATIC(),
		
		/**
		 * Ignore only the physics object that this sensor is embedded in.
		 */
		IGNORE_NONE(), 
		
		/**
		 * Ignore anythign connected by joints to this sensor
		 */
		IGNORE_SELF(), 
		
		/**
		 * Ignore anything that is a member of this 'machine'
		 */
		IGNORE_SIMILAR();
	}


	public final static String GHOST = "ghost";

	public static final String SENSOR_MODE = "sensor.type";
	public static final String SENSOR_IGNORE_STATICS = "sensor.ignoreStatics";
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(GHOST);
		keys.add(SENSOR_MODE);
		keys.add(SENSOR_IGNORE_STATICS);
	
		return super.enumerateKeys(keys);
	}
	

	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(GHOST))
			return defaultStore;
		if(key.equals(SENSOR_MODE))
			return defaultEnum;
		if(key.equals(SENSOR_IGNORE_STATICS))
			return defaultBool;
	

		return super.getDefaultValue(key);
	}
	
	public DistanceSensorInterpreter() {
		this(new PropertyStore());
	}

	public DistanceSensorInterpreter(PropertyStore store) {
		super(store);
		portInterpreter = new StandardFunctionalInterpreter(store);
		store.setClassName(GolemsClassRepository.DISTANCE_SENSOR_CLASS);
	}
	
	//echo the port interpreters functions here... in case we ever need them
	
	public PropertyStore getOutput()
	{
		return portInterpreter.getOutput();
	}
	
/*	public void setOutput(PropertyStore reference)
	{
		portInterpreter.setOutput(reference);
	}*/
	
	public PropertyStore getInput()
	{
		return portInterpreter.getInput();
	}
	
/*	public void setInput(PropertyStore reference)
	{
		portInterpreter.setInput(reference);
	}*/
	
	public PropertyStore getAuxInput()
	{
		return portInterpreter.getAuxInput();
	}
	
/*	public void setAuxInput(PropertyStore reference)
	{
		portInterpreter.setAuxInput(reference);
	}*/

	public PropertyStore getGhost() {
		
		GhostPyramidInterpreter defaultPyramid = new GhostPyramidInterpreter();
		defaultPyramid.getLocalTranslation().x = 1;
		defaultPyramid.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Z);
		return super.getStore().getPropertyStore(GHOST, defaultPyramid.getStore());
	}
	
	public void setGhost(PropertyStore ghost)
	{
		super.getStore().setProperty(GHOST, ghost);
	}

	public SensorMode getSensorMode() {
		
		return getStore().getEnum(SENSOR_MODE, SensorMode.IGNORE_SELF);
	}
	
	public void setSensorMode(SensorMode sensorMode)
	{
		getStore().setProperty(SENSOR_MODE, sensorMode);
	}

	public void setIgnoreStatics(boolean ignoreStatics) {
		getStore().setProperty(SENSOR_IGNORE_STATICS, ignoreStatics);
		
	}

	public boolean ignoreStatics() {
		return getStore().getBoolean(SENSOR_IGNORE_STATICS);
	}
}
