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
