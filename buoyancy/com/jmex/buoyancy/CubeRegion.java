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
package com.jmex.buoyancy;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;

/**
 * This is just a convenience class to create a cube BoundingVolumeRegion
 * @author Sam Bayless
 *
 */
public class CubeRegion extends BoundingVolumeRegion {
	
	public CubeRegion(float fluidDensity, float fluidLevel, float fluidViscocity, Vector3f center, Vector3f extent) {
		super(fluidDensity, fluidLevel, fluidViscocity,new BoundingBox(center, extent.x, extent.y, extent.z));
	}
}
