package com.golemgame.model.effect;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.golemgame.model.Model;
import com.golemgame.model.effect.ModelEffect.ModelEffectType;


/**
 * A class for managing the effects applied to a one or more models
 * @author Sam
 *
 */
public class Appearance implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private transient Map<ModelEffectType, ModelEffect> exclusiveTransientEffects;
	private transient ArrayList<ModelEffect> permissiveTransientEffects;
	private Map<ModelEffectType, ModelEffect> exclusiveLastingEffects;
	private ArrayList<ModelEffect> permissiveLastingEffects;
	
	private Model[] models;
	
	//private EffectListener effectListener = new EffectListener(this);
	
	public Appearance(Model model) {
		this (new Model[]{model});
	}
	
	public ModelEffect getEffect(ModelEffectType type,boolean lasting)
	{
		if (type.isExclusive())
		{
			Map<ModelEffectType, ModelEffect> effectMap = lasting?exclusiveLastingEffects:exclusiveTransientEffects;
	
			{
				ModelEffect effect = effectMap.get(type);
				if (effect != null)
				{
					return effect;
				}
	
			}
		}else
		{
		
			ArrayList<ModelEffect> effectList = lasting?permissiveLastingEffects:permissiveTransientEffects;		
			for (ModelEffect effect:effectList)
			{
				for (ModelEffectType effectType: effect.getEffectTypes() )
				{
					if (effectType == type)
					{
						
						return effect;
					}
				}
			}
		}
		return null;
	}
	
	public Collection<ModelEffect> getAllEffects(ModelEffectType type,boolean lasting)
	{
		if (type.isExclusive())
		{
			Map<ModelEffectType, ModelEffect> effectMap = lasting?exclusiveLastingEffects:exclusiveTransientEffects;
	
			{
				ModelEffect effect = effectMap.get(type);
				if (effect != null)
				{
					ArrayList<ModelEffect>  list = new ArrayList<ModelEffect>();
					list.add(effect);
					return  list;
				}
	
			}
		}else
		{
			ArrayList<ModelEffect>  list = new ArrayList<ModelEffect>();
		
			ArrayList<ModelEffect> effectList = lasting?permissiveLastingEffects:permissiveTransientEffects;		
			for (ModelEffect effect:effectList)
			{
				for (ModelEffectType effectType: effect.getEffectTypes() )
				{
					if (effectType == type)
					{
						
						list.add(effect);
					}
				}
			}
		
			return  list;
		}
		return null;
	}
	
	public Appearance(Appearance copyFrom, Model[] models,boolean copyLastingOnly) {
		//note: this makes a shallow copy of the model effects.
		this.models = models;
	
			exclusiveTransientEffects = new  HashMap<ModelEffectType, ModelEffect>();
			permissiveTransientEffects = new ArrayList<ModelEffect>();

		exclusiveLastingEffects  = new HashMap<ModelEffectType, ModelEffect>();
		permissiveLastingEffects = new ArrayList<ModelEffect>();
		
		for (ModelEffect effect:copyFrom.permissiveLastingEffects)
		{
			this.addEffect(effect.copy(), true);
		}
		for (ModelEffect effect:copyFrom.exclusiveLastingEffects.values())
		{
			this.addEffect(effect.copy(), true);
		}
		
		if (!copyLastingOnly)
		{
			for (ModelEffect effect:copyFrom.permissiveTransientEffects)
			{
				this.addEffect(effect.copy(), true);
			}
			for (ModelEffect effect:copyFrom.exclusiveTransientEffects.values())
			{
				this.addEffect(effect.copy(), true);
			}
		}
		
	//	reapply();
	}
	
	public Appearance(Model[] models) {
		this.models = models;
		exclusiveTransientEffects = new  HashMap<ModelEffectType, ModelEffect>();
		permissiveTransientEffects = new ArrayList<ModelEffect>();
		exclusiveLastingEffects  = new HashMap<ModelEffectType, ModelEffect>();
		permissiveLastingEffects = new ArrayList<ModelEffect>();
	}
	
	public void addEffect(ModelEffect effect, boolean lasting)
	{
		if (vetoEffect(effect, lasting))
			return;
		ModelEffectType[] types = effect.getEffectTypes();
		boolean newEffect = false;
		for (ModelEffectType type : types)
			newEffect |= addEffect(effect, type, lasting);
		
	//	if (newEffect)
		{
		//	effect.addUpdateListener(effectListener);
			applyEffect(effect, lasting);
		}
	}
	
	/**
	 * Subclasses can refuse to accept an entire effect.
	 * @param effect
	 * @param lasting
	 * @return
	 */
	protected boolean vetoEffect(ModelEffect effect, boolean lasting)
	{
		return false;
	}
	
	/**
	 * Subclasses can refuse to apply a particular effect to a particular model
	 * @param effect
	 * @param lasting
	 * @return
	 */
	protected boolean vetoModelApplication(Model model, ModelEffect effect, boolean lasting)
	{
		return false;
	}
	
	
	private boolean addEffect(ModelEffect effect, ModelEffectType type, boolean lasting)
	{
		if (type.isExclusive())
		{
			Map<ModelEffectType, ModelEffect> effectMap = lasting?exclusiveLastingEffects:exclusiveTransientEffects;
			//Find each model effect that DOES need to be removed.
			ModelEffect previousEffect = effectMap.get(type);
		
			if (previousEffect != null)
			{
				removeEffect(previousEffect, lasting);
			}
			effectMap.put(type, effect);
			
		}else
		{//don't  have to remove the previous effect - it is the effects responsibility to sort out conflicts
			
			ArrayList<ModelEffect> effectList = lasting?permissiveLastingEffects:permissiveTransientEffects;
			if(!effectList.contains(effect))
				effectList.add(effect);
		}
		return true;
		
	}
	
	/**
	 * Remove all effects that have that type from the given set of effects.
	 * @param type
	 * @param lasting
	 */
	public void removeEffect(ModelEffectType type,boolean lasting)
	{
		Map<ModelEffectType, ModelEffect> effectMap = lasting?exclusiveLastingEffects:exclusiveTransientEffects;
		ArrayList<ModelEffect> effectList = lasting?permissiveLastingEffects:permissiveTransientEffects;
		

		ArrayList<ModelEffect> removalQueue = new ArrayList<ModelEffect>();
		
		{
			ModelEffect previousEffect = effectMap.get(type);
			if (previousEffect != null)
			{
				removalQueue.add(previousEffect);
			}
		}
	
		for (ModelEffect previousEffect:effectList)
		{
			for (ModelEffectType effectType: previousEffect.getEffectTypes() )
			{
				if (effectType == type)
				{
					removalQueue.add(previousEffect);
					break;
				}
			}
		}

		for (ModelEffect effect:removalQueue)
			removeEffect(effect,lasting);
		
	}
	
	public void removeEffect(ModelEffect effect, boolean lasting)
	{
		Map<ModelEffectType, ModelEffect> effectMap = lasting?exclusiveLastingEffects:exclusiveTransientEffects;
		ArrayList<ModelEffect> effectList = lasting?permissiveLastingEffects:permissiveTransientEffects;
		
		boolean exclusiveRemoved = false;
		for (ModelEffectType effectType:effect.getEffectTypes())
		{
			if (effectType.isExclusive())
			{
				//if(effectMap.get(effectType)==effect)
				//{
					exclusiveRemoved = true;
					for (Model model:models)
						effect.removeModel(model);
					//effect.removeUpdateListener(effectListener);
				
					effectMap.remove(effectType);
				//}
			}else
			{
				//if(effectList.contains(effect))
			//	{
					effectList.remove(effect);
				//	effect.removeUpdateListener(effectListener);
					for (Model model:models)
						effect.removeModel(model);
				//}
			}
		}
		
		//If a transient, eclusive effect was removed, check if there is a matching lasting effect
		//if so, apply it to the model
		for (ModelEffectType effectType:effect.getEffectTypes())
		{
			if (exclusiveRemoved &! lasting)
			{
				ModelEffect lastingExclusive = exclusiveLastingEffects.get(effectType);
				if (lastingExclusive != null)
					for (Model model:models)
					{
						lastingExclusive.attachModel(model);
					
					}
			}
		}
	}

	
	
	
	/**
	 * Apply the effect to the models. If the effect is lasting, check first to ensure there isn't an exclusive applied transient effect that supersedes it.
	 * If the effect is transient, remove any lasting effect of the same type.
	 * @param effect
	 * @param lasting
	 */
	private void applyEffect(ModelEffect effect, boolean lasting)
	{
		
		if (isExclusive(effect))
		{
			if (!lasting)
			{
				//if there is a lasting effect of this type, remove it
				for (ModelEffectType effectType: effect.getEffectTypes() )
				{
					ModelEffect lastingExclusive = exclusiveLastingEffects.get(effectType);
					if (lastingExclusive!= null)
						for (Model model:models)
						{	//ensure that the lasting exclusive is removed
							//unless this model vetos the effect
							if (!vetoModelApplication(model, effect, lasting))
							{
								lastingExclusive.removeModel(model);
							}
						}
					
				}
				for (Model model:models)
				{	
					if (!vetoModelApplication(model, effect, lasting))
					{
						effect.attachModel(model);
					
					}
				}	
			}else
			{
				
				//if this IS a lasting effect, only apply it if there is no transient effect.
				boolean superseded = false;
				for (ModelEffectType effectType: effect.getEffectTypes() )
				{
					ModelEffect transientExclusive = exclusiveTransientEffects.get(effectType);
					if (transientExclusive!= null)
					{
						superseded = true;
						break;
					}
					
				}
				if (!superseded)
					for (Model model:models)
					{	
						if (!vetoModelApplication(model, effect, lasting))
						{
							effect.attachModel(model);
							
						}
					}
			}

		}else
			for (Model model:models)
			{	
				if (!vetoModelApplication(model, effect, lasting))
				{
					effect.attachModel(model);
			
				}
			}
		
	}
	
	private boolean isExclusive(ModelEffect effect)
	{
		for (ModelEffectType effectType: effect.getEffectTypes() )
		{
			if (effectType.isExclusive())
				return true;
		}
		return false;
	}
	
	

	
	/**
	 * Reapply the effects to the models
	 */
	public void reapply()
	{
		Set<ModelEffect> effectsToApply = new HashSet<ModelEffect>();

		for(ModelEffect effect:exclusiveLastingEffects.values())
		{
			effectsToApply.add(effect);
		}
		for(ModelEffect effect:permissiveLastingEffects)
		{
			effectsToApply.add(effect);
		}
		
		for (ModelEffect effect:effectsToApply)
			applyEffect(effect,true);
		
		
		effectsToApply.clear();
		for(ModelEffect effect:exclusiveTransientEffects.values())
		{
			effectsToApply.add(effect);
		}
		for(ModelEffect effect:permissiveTransientEffects)
		{
			effectsToApply.add(effect);
		}
		
		for (ModelEffect effect:effectsToApply)
			applyEffect(effect,false);
	}
	
	/**
	 * Make a copy of this appearance, and apply it to these models
	 * @param models
	 * @return
	 */
	public Appearance makeCopy(Model[] models,boolean lastingOnly)
	{
		Appearance copy= new Appearance(this, models,lastingOnly);
	//	copy.reapply();
		return copy;
	}
	
	
	/**
	 * Make a copy of this appearance, and apply it to these models
	 * @param models
	 * @return
	 */
	public Appearance makeCopy(Model model,boolean lastingOnly)
	{
		Appearance copy= new Appearance(this, new Model[]{model},lastingOnly);
	//	copy.reapply();
		return copy;
	}
	
	/*public static class EffectListener implements ModelEffectListener
	{//have to re-add this on loading.
		private static final long serialVersionUID = 1L;
		private Appearance appearance;
		
		public EffectListener(Appearance appearance) {
			super();
			this.appearance = appearance;
		}
		
	
		public void update(ModelEffect toUpdate) {
			for(Model model:appearance.getModels())
			{
				toUpdate.attachModel(model);
				toUpdate.r
			}
			
		}



		
	}*/

	public Model[] getModels() {
		return models;
	}

	public void setModels(Model[] models) {
		this.models = models;
	};
	
	
	

	
	
}
