package com.golemgame.model.effect;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import com.golemgame.model.Model;
import com.golemgame.model.Model.ModelTypeException;



public abstract class ModelEffectImpl implements ModelEffect {
	
	private static final long serialVersionUID = 1L;
	
	private final Collection<WeakReference<Model>> models;
	
	/**
	 * Use a set/map for constant speed operations on large effects that will be used on many models
	 */
	private final HashMap<MutableInt,WeakReference<Model>> modelMap;
	
	private final boolean useSets;
	
	public void set(ModelEffect setFrom) throws ModelEffectTypeException {
		
		
	}
	
	private static final MutableInt testHash = new MutableInt(0);

	public void attachModel(Model model) throws ModelTypeException {
		
		if(!useSets)
		{		
			if(model!=null)
			{
				for(WeakReference<Model> ref:models)
				{
					if(ref.get()==model)
					{
						refreshEffect(model);//otherwise sometimes effects that should be applied wont be
						return;
					}
				}
				models.add(new WeakReference<Model>(model));
				refreshEffect(model);
			}
		}else{
			MutableInt hash = hash(model,testHash);
			WeakReference<Model> cur = modelMap.get(hash);
			if(cur!=null && cur.get()== model)
			{
			
			}else{
				modelMap.put(new MutableInt(hash), new WeakReference<Model>(model));
			}
			refreshEffect(model);//just refresh
		}
	}

	private MutableInt hash(Model model,MutableInt res) {
		int hash = model==null? 0:model.hashCode();
		res.setVal(hash);
		return res;
	}

	public void refreshEffect() {
		for(WeakReference<Model> ref:getModels())
		{
			Model m = ref.get();
			if(m!=null)
			{
				refreshEffect(m);
			}
		}
	}
	
	
	protected abstract void refreshEffect(Model model);

	protected Collection<WeakReference<Model>> getModels() {
		if(!useSets)
		{
			return models;
		}else{
			return modelMap.values();
		}
	
	}
	

	public void removeModel(Model model) {
		if(!useSets)
		{
			boolean res = models.remove(model);
			while(res)
				res = models.remove(model);//remove any extra copies of this mdoel in the list.
		}else{
			MutableInt hash = hash(model,testHash);	
			modelMap.remove(hash);
		}
	}

	public ModelEffectImpl(boolean useSets)
	{
		this.useSets = useSets;
		if(!useSets)
		{
			models = new ArrayList<WeakReference<Model>>();
			modelMap= null;
		}else{
			modelMap = new HashMap<MutableInt,WeakReference<Model>>();
			models =null;
			//models = new HashSet<WeakReference<Model>>();
		}
	}
	
	
	public ModelEffectImpl()
	{
		this(false);
		
	}
	
	private static class MutableInt
	{
		private int val;
		public MutableInt()
		{
			
		}
		
		public MutableInt(MutableInt from)
		{
			val = from.getVal();
		}
		public MutableInt(int val)
		{
			this.val = val;
		}
		public int getVal() {
			return val;
		}

		public void setVal(int val) {
			this.val = val;
		}

		@Override
		public int hashCode() {
		
			return val;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MutableInt other = (MutableInt) obj;
			if (val != other.val)
				return false;
			return true;
		}
		
	}


	
}
