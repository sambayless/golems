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

public class GeneralSensorInterpreter extends PhysicalStructureInterpreter {
	public static final String SENSOR_TYPE = "sensor.type";
	public static final String SENSOR_SETTINGS = "sensor.settings";
	public static final String POSITION_SENSOR_SETTINGS = "sensor.settings.position";
	public static final String ACCELERATION_SENSOR_SETTINGS = "sensor.settings.acceleration";
	public static final String VELOCITY_SENSOR_SETTINGS = "sensor.settings.velocity";
	public static final String ALTITUDE_SENSOR_SETTINGS = "sensor.settings.altitude";
	public static final String ORIENTATION_SENSOR_SETTINGS = "sensor.settings.orientation";

	
	public static enum SensorType{
		ALTITUDE(1,"Altitude"),ORIENTATION(3,"Orientation"),POSITION(3,"Position"),VELOCITY(3,"Velocity"),ACCELERATION(3,"Acceleration");
		
		private final int numberOfConnections;

		private final String name;
		
		public String getName() {
			return name;
		}

		public int getNumberOfConnections() {
			return numberOfConnections;
		}

		private SensorType(int numberOfConnections,String name) {
			this.numberOfConnections = numberOfConnections;
			this.name = name;
		}
		
		
	}
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(ORIENTATION_SENSOR_SETTINGS);		
		keys.add(ALTITUDE_SENSOR_SETTINGS);		
		keys.add(VELOCITY_SENSOR_SETTINGS);		
		keys.add(ACCELERATION_SENSOR_SETTINGS);		
		keys.add(POSITION_SENSOR_SETTINGS);		
		keys.add(SENSOR_SETTINGS);		
		keys.add(SENSOR_TYPE);		
	
		return super.enumerateKeys(keys);
	}
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(ORIENTATION_SENSOR_SETTINGS))
			return defaultStore;
		if(key.equals(ALTITUDE_SENSOR_SETTINGS))
			return defaultStore;
		if(key.equals(VELOCITY_SENSOR_SETTINGS))
			return defaultStore;
		if(key.equals(ACCELERATION_SENSOR_SETTINGS))
			return defaultStore;
		if(key.equals(POSITION_SENSOR_SETTINGS))
			return defaultStore;
		if(key.equals(SENSOR_SETTINGS))
			return defaultStore;
		if(key.equals(SENSOR_TYPE))
			return defaultEnum;
		return super.getDefaultValue(key);
	}
	
	public GeneralSensorInterpreter() {
		this(new PropertyStore());
	}

	public GeneralSensorInterpreter(PropertyStore store) {
		super(store);
		store.setClassName(GolemsClassRepository.GENERAL_SENSOR_CLASS);
	}
	public SensorType getSensorType()
	{
		return this.getStore().getEnum(SENSOR_TYPE, SensorType.POSITION);
	}
	public void setSensorType(SensorType type)
	{
		this.getStore().setProperty(SENSOR_TYPE,type);
	}
	
	public PropertyStore getPositionSettings()
	{
		return getStore().getPropertyStore(POSITION_SENSOR_SETTINGS);
	}
	
	public PropertyStore getAltitudeSettings()
	{
		return getStore().getPropertyStore(ALTITUDE_SENSOR_SETTINGS);
	}
	
	public PropertyStore getVelocitySettings()
	{
		return getStore().getPropertyStore(VELOCITY_SENSOR_SETTINGS);
	}
	public PropertyStore getAccelerationSettings()
	{
		return getStore().getPropertyStore(ACCELERATION_SENSOR_SETTINGS);
	}
	public PropertyStore getOrientationSettings()
	{
		return getStore().getPropertyStore(ORIENTATION_SENSOR_SETTINGS);
	}
}
