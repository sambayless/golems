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
