package com.golemgame.states.construct;

import com.jme.math.Vector3f;

public class ConstructionManager {
	private final ConstructionLocation defaultLocation = new DefaultConstructionLocation();
	private ConstructionLocation location = defaultLocation;

	protected void setLocation(ConstructionLocation location) {
		if(location == null)
		{
			this.location = defaultLocation;
		}else
			this.location = location;
	}
	
	/**
	 * Creates a new Vector3f to hold the build location.
	 * @return
	 */
	public Vector3f getBuildLocation() {
		return getBuildLocation(new Vector3f());
	}
	
	public Vector3f getBuildLocation(Vector3f store) {
		if(store == null)
			store = new Vector3f();
		location.getBuildLocation(store);
		return store;
	}
	public ConstructionManager() {
		super();
	}
	public ConstructionManager(ConstructionLocation location) {
		super();
		setLocation(location);
	}
	
}
