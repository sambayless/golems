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
