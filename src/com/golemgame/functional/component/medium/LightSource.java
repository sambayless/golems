package com.golemgame.functional.component.medium;

import com.jme.math.Vector3f;

/**
 * A light source can be a BComponent, but it doesn't have to be.
 * @author Sam
 *
 */
public interface LightSource {
	public float getLightIntensity(Vector3f position, float wavelength);
}
