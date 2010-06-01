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
