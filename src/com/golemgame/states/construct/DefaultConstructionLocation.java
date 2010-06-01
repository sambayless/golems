package com.golemgame.states.construct;

import com.jme.math.Vector3f;

public class DefaultConstructionLocation implements ConstructionLocation{
	private static final Vector3f defaultBuildLocation = new Vector3f(0,2,0);
	public static Vector3f getDefaultbuildlocation() {
		return defaultBuildLocation;
	}
	public void getBuildLocation(Vector3f store) {
		store.set(defaultBuildLocation);
	}
	
	
}
