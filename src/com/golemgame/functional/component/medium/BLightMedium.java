package com.golemgame.functional.component.medium;

import java.util.ArrayList;

import com.jme.math.Vector3f;

public class BLightMedium extends BMedium {
	private ArrayList<LightSource> lights= new ArrayList<LightSource>();
	
	public void attachLight(LightSource light)
	{
		if (!lights.contains(light))
			lights.add(light);
	}
	
	public void removeLight(LightSource light)
	{
		lights.remove(light);
	}
	
	/**
	 * Get the combined light intensity at a given position.
	 * @param position
	 * @param wavelength the wavelength to querry. If < 0, this parameter is ignored.
	 * @return
	 */
	public float getLightIntensity(Vector3f position, float wavelength)
	{
		float intensity = 0;
		
		for (LightSource light : lights)
		{
			intensity += light.getLightIntensity(position,wavelength);
		}
		
		return intensity;
	}
	
}
