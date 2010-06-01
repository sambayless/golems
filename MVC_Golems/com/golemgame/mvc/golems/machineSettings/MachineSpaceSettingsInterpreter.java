package com.golemgame.mvc.golems.machineSettings;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GolemsClassRepository;
import com.golemgame.mvc.golems.StoreInterpreter;
import com.jme.math.Vector3f;

public class MachineSpaceSettingsInterpreter extends StoreInterpreter{
	public final static String GRAVITY = "gravity";
	public final static String GRAVITY_MAGNITUDE = "gravity.magnitude";
	public final static String GRID = "grid";
	
	public MachineSpaceSettingsInterpreter() {
		this(new PropertyStore());
	
	}

	public MachineSpaceSettingsInterpreter(PropertyStore store) {
		super(store);
		store.setClassName(GolemsClassRepository.MACHINE_SPACE_SETTINGS_CLASS);
	}

	public Vector3f getGravityDirection()
	{
		return getStore().getVector3f(GRAVITY, new Vector3f(0f,-1f,0f));
	}
	
	public float getGravityMagnitude()
	{
		return getStore().getFloat(GRAVITY_MAGNITUDE,9.81f);
	}
	
	public void setGravityMagnitude(float mag)
	{
		getStore().setProperty(GRAVITY_MAGNITUDE, mag);
	}
	
	public Vector3f getGrids()
	{
		return getStore().getVector3f(GRID, new Vector3f(1f,1f,1f));
	}
}
