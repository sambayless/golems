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
package com.golemgame.structural.structures.particles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SustainedView;
import com.golemgame.mvc.golems.ParticleEffectInterpreter;
import com.golemgame.mvc.golems.ParticleEffectRepositoryInterpreter;

/**
 * This just holds user defined particle effects.
 * @author Sam
 *
 */
public class ParticleEffectRepository implements SustainedView{
	private List<ParticleEffectInterpreter> effects = new ArrayList<ParticleEffectInterpreter>();
	
	private ParticleEffectRepositoryInterpreter interpreter;

	public ParticleEffectRepository(PropertyStore store) {
		super();
		interpreter = new ParticleEffectRepositoryInterpreter(store);
		store.setSustainedView(this);
	}

	public void invertView(PropertyStore store) {
		
	}

	public void refresh() {
		effects.clear();
		for (DataType data:interpreter.getParticleEffects().getValues())
		{
			if (data.getType()!= DataType.Type.PROPERTIES)
				continue;			
			effects.add(new ParticleEffectInterpreter((PropertyStore)data));
		}
		Collections.sort(effects);
	}

	public void remove() {
		
	}

	public PropertyStore getStore() {
		return interpreter.getStore();
	}
	
	public void addEffect(PropertyStore effect)
	{
		ParticleEffectInterpreter tempInterp = new ParticleEffectInterpreter(effect);
		//String name = tempInterp.getEffectName();
		//find any matching named effect and remove if from the repository
		int pos = Collections.binarySearch(effects, tempInterp);
		if (pos>=0)
		{
			interpreter.removeParticleEffect(	effects.get(pos).getStore());
		}		
		interpreter.addParticleEffect(effect);
		interpreter.refresh();
	}
	
	public void removeEffect(PropertyStore effect)
	{
		interpreter.removeParticleEffect(effect);
		interpreter.refresh();
	}

	public List<ParticleEffectInterpreter> getEffects() {
		return effects;
	}
	
	
}
