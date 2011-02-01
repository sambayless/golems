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
package com.golemgame.mvc.golems;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.jme.math.FastMath;
import com.jme.renderer.ColorRGBA;

public class RocketPropellantInterpreter extends StoreInterpreter {
	
	public static final String PARTICLE_EFFECTS = "particleEffects";
	public static final String EFFECTS_ENABLED = "effects.enabled";
	
	public RocketPropellantInterpreter() {
		this( new PropertyStore());
	}

	public RocketPropellantInterpreter(PropertyStore store) {
		super(store);
		super.getStore().setClassName(GolemsClassRepository.ROCKET_PROPELLANT_CLASS);
	}
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(PARTICLE_EFFECTS))
			return defaultStore;
		if(key.equals(EFFECTS_ENABLED))
			return defaultBool;

		return super.getDefaultValue(key);
	}
	
	public PropertyStore getParticleEffects()
	{
		return getStore().getPropertyStore(PARTICLE_EFFECTS, new PropertyStore(getDefaultParticleEffect()));
	}
	
	public boolean effectsEnabled()
	{
		return getStore().getBoolean(EFFECTS_ENABLED,false);
	}
	
	public void setEffectsEnabled(boolean enabled)
	{
		getStore().setProperty(EFFECTS_ENABLED, enabled);
	}
	public static PropertyStore getDefaultParticleEffect()
	{
		ParticleEffectInterpreter rocket = new ParticleEffectInterpreter();
		rocket.setEffectName("Rocket");
		rocket.setMinLifeSpan(100);
		rocket.setMaxLifeSpan(1000);
		rocket.setMinAngle(0);
		rocket.setMaxAngle(FastMath.PI/12f);
		rocket.setInitialSize(1f);
		rocket.setFinalSize(5f);
		rocket.setInitialVelocity(0.01f);
		rocket.setNumberOfParticles(30);
		rocket.setStartColor( new ColorRGBA( 1.0f, 1.0f, 1.0f, 1.0f ));
		rocket.setEndColor( new ColorRGBA( 0.6f, 0.2f, 0.0f, 0.0f ));
		return rocket.getStore();
		
	}
}
