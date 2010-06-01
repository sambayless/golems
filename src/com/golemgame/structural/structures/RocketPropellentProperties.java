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
