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
