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
package com.golemgame.mvc.golems.transform;

import com.golemgame.mvc.PropertyStore;

/**
 * This class applies a particular transformation to a property store.
 * @author Sam
 *
 */
public abstract class Transformation {
	/**
	 * This is the app version at which this transformation was added to golems
	 */
	private final String versionIntroduced;
	
	private int majorVersion = -1;
	private int minorVersion = -1;
	private int revision = -1;
	
	/**
	 * -1 if unknown, or before 0.54
	 * @return
	 */
	public int getMajorVersion() {
		return majorVersion;
	}

	/**
	 * -1 if unknown, or before 0.54
	 * @return
	 */
	public int getMinorVersion() {
		return minorVersion;
	}

	/**
	 * -1 if unknown, or before 0.54
	 * @return
	 */
	public int getRevision() {
		return revision;
	}

	public void setVersion(int major, int minor, int revision)
	{
		this.majorVersion = major;
		this.minorVersion = minor;
		this.revision = revision;
	}
	
	/**
	 * 
	 * @param versionIntroduced  This is the app version at which this transformation was added to golems
	 */
	public Transformation(String versionIntroduced) {
		super();
		this.versionIntroduced = versionIntroduced;
	}


	public abstract void apply(PropertyStore store);


	@Override
	public String toString() {
		return "Transformation (" + versionIntroduced + ")";
	}
	
	
}
