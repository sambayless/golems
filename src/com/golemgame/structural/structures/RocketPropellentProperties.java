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
package com.golemgame.structural.structures;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SustainedView;
import com.golemgame.mvc.golems.ParticleEffectInterpreter;
import com.golemgame.mvc.golems.RocketPropellantInterpreter;

/**
 * This is a repository for any needed properties of rocket propellents
 * @author Sam
 *
 */
public class RocketPropellentProperties implements SustainedView{
	private ParticleEffectInterpreter rocketEffects;

	private RocketPropellantInterpreter interpreter;
	
	public RocketPropellentProperties(PropertyStore store) {
		super();
		interpreter = new RocketPropellantInterpreter(store);
	}

	public ParticleEffectInterpreter getRocketEffects() {
		return rocketEffects;
	}

	public void invertView(PropertyStore store) {
		
		
	}

	public void refresh() {
		if (rocketEffects == null || !rocketEffects.getStore().equals(interpreter.getParticleEffects()))
		{
			rocketEffects = new ParticleEffectInterpreter(interpreter.getParticleEffects());
		}		
	}

	public void remove() {
		
		
	}

	public PropertyStore getStore() {
		return interpreter.getStore();
	}
	
	public boolean effectsEnabled()
	{
		return interpreter.effectsEnabled();
	}
	
	public void setEffectsEnabled(boolean enabled)
	{
		interpreter.setEffectsEnabled(enabled);
	}

	public RocketPropellantInterpreter getInterpreter() {
		return interpreter;
	}
	
	
	
}
