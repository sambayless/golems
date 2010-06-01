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
