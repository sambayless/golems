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

import java.util.Collection;

import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;

public class ParticleEffectRepositoryInterpreter extends StoreInterpreter{

	public static final String PARTICLE_EFFECTS = "particleEffects";
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(PARTICLE_EFFECTS);
		return super.enumerateKeys(keys);
	}
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(PARTICLE_EFFECTS))
			return defaultCollection;
		return super.getDefaultValue(key);
	}
	
	public ParticleEffectRepositoryInterpreter() {
		this( new PropertyStore());
	}

	public ParticleEffectRepositoryInterpreter(PropertyStore store) {
		super(store);
		super.getStore().setClassName(GolemsClassRepository.PARTICLE_EFFECTS_REPOSITORY_CLASS);
	}
	
	public CollectionType getParticleEffects()
	{
		return getStore().getCollectionType(PARTICLE_EFFECTS);
	}

	public void addParticleEffect(PropertyStore store)
	{
		getParticleEffects().addElement(store);
	}
	
	public void removeParticleEffect(PropertyStore store)
	{
		getParticleEffects().removeElement(store);
	}
	
}
