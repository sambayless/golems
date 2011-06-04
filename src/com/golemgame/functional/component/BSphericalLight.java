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
package com.golemgame.functional.component;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;

public class BSphericalLight extends BLight {
	
	public BSphericalLight(LightDataCallback lightData) {
		super(lightData);
	}
	
	private float power = 0;
	
	
	public float generateSignal(float time) {
		power = this.state;
		return super.generateSignal(time);
	}

	
	public float getLightIntensity(Vector3f position, float wavelength) {
		//wavelength is ignored right now... this is white light.
		//light intensity for a sphere is P/(4*PI*r^2)
	
		
		float distanceSquared= getPosition().distanceSquared(position);
		float result =  power/(4f*FastMath.PI * distanceSquared);
		
		return (result * 100f);
	}

}
