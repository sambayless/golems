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

import com.jme.bounding.BoundingVolume;

/**
 * A region that uses a <code>BoundingVolume</code> to test whether <code>BuoyantObject</code>s are in the fluid or not.
 * @author Sam Bayless
 *
 */
public class BoundingVolumeRegion extends FluidRegion {

	private BoundingVolume checkBound;
	
	public BoundingVolumeRegion(float fluidDensity, float fluidLevel, float fluidViscocity, BoundingVolume bound) {
		super(fluidDensity, fluidLevel, fluidViscocity);
		checkBound = bound;
	}

	public BoundingVolumeRegion( BoundingVolume bound) {
		this(IFluidRegion.DENSITY_WATER,0,IFluidRegion.VISCOSITY_WATER,bound);
	}
	
	@Override
	public boolean checkBounds(BuoyantObject object) {
		if (object.getSpatial().getWorldBound().intersects(checkBound))
			return true;		
		return false;
	}

	public BoundingVolume getCheckBound() {
		return checkBound;
	}

	public void setCheckBound(BoundingVolume checkBound) {
		this.checkBound = checkBound;
	}
	
}
