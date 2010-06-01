package com.golemgame.properties;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.ColorType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.NullType;
import com.golemgame.mvc.PropertyState;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.QuaternionType;
import com.golemgame.mvc.SimplePropertyState;
import com.golemgame.mvc.Vector2Type;
import com.golemgame.mvc.Vector3Type;
import com.golemgame.mvc.DataType.Type;
import com.golemgame.mvc.golems.validate.GolemsValidator;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionDependencySet;

public class PropertyStoreAdjuster {
	/*
	 * Plan: Create some sort of standard property store setting tool, which can be given a set of properties to 
	 * change, and then be applied to each property store independently, in the process creating a before and after state
	 * change that can be used for efficient undo
	 * 
	 * Also need a system to deal with multiply valued things...
	 * the tab should recognize when things have multiple values,
	 * and only make changes from them when edits occur.
	 * 
	 * the property interpreter will operate over the whole set of property stores
	 * and it will report a) inconsistent values among the set
	 * and b)handle this stuff, creating at the end a single do undo operation...
	 */
	
	private final Collection<PropertyStore> stores;
	
	private final PropertyStore prototype;
	private final PropertyStore comparison = new PropertyStore();
	private final Set<String> alteredKeys = new HashSet<String>();
	private final Map<String, BitSet> alteredIndices = new HashMap<String,BitSet>();//some values can have individual parts altered independantly
	private Map<String, float[]> floatValueMap = new HashMap<String,float[]>();//for doing per axis rotation on quaternions
	private final String name;

	public PropertyStore getPrototype() {
		return prototype;
	}

	public PropertyStoreAdjuster(Collection<PropertyStore> stores,String name) {
		super();
		this.stores = stores;
		this.name = name;
		prototype = new PropertyStore();
	}
	
	public void setValueAltered(String key, boolean altered)
	{
		if(altered)
			alteredKeys.add(key);
		else
			alteredKeys.remove(key);
	}
	
	public void setIndexAltered(String key, int index, boolean altered)
	{
		if(altered)
		{
			alteredKeys.add(key);
		}
		BitSet set = alteredIndices.get(key);
		if(set == null)
		{
			set = new BitSet();
			alteredIndices.put(key, set);
			
		}
		
		set.set(index, altered);
	}
	
	public boolean isAltered(String key, int index)
	{
		BitSet set = alteredIndices.get(key);
		if(set == null)
		{
			return false;
		}
		return set.get(index);
	}
	
	public boolean hasMultipleValues(String key)
	{
		if (stores.isEmpty())
			return false;
		
	
		
		DataType value = null;
		for (PropertyStore store:stores)
		{
			if (store.hasProperty(key))
			{
				value = store.getProperty(key);
			}
			
		}
		
		if(value == null)
			return false;
		
		for (PropertyStore store:stores)
		{
			if (!store.hasProperty(key))
				continue;
			
			DataType v = store.getProperty(key);
			if (v != value )
			{
				if (value == null || v == null)
					return true;
				
				if (!value.equals(v))
					return true;
			}
		}
		return false;
	}
	
	/**
	 * BROKEN
	 * Get the union of all collection types belonging to each value (or to the prototype if a store doesnt have a collection)
	 * @param key
	 * @return
	 * @throws NoValueException
	 */
	public CollectionType getCurrentCollection(String key,ArrayList<DataType> multipleValuesStore)
	{
		
		
		
		CollectionType unionVals = null;
		
		
		multipleValuesStore.clear();
		
		for(PropertyStore store:stores)
		{
			DataType curVal;
			if(store.hasProperty(key))
			{
				curVal = store.getProperty(key);
			}else if (prototype.hasProperty(key))
			{
				curVal = prototype.getProperty(key);
			}else
				continue;
			
			if(unionVals == null)
			{
				if (curVal.getType() == DataType.Type.COLLECTION)
				{
					unionVals =(CollectionType) curVal.deepCopy();
				}
			}else if (curVal.getType() == DataType.Type.COLLECTION)
			{
				CollectionType collection = (CollectionType)curVal;
				Iterator<DataType> it = unionVals.getValues().iterator();
				
				while(it.hasNext())
				{
					DataType val = it.next();
					if(val ==null)
					{
						it.remove();
					}else
					{
						if (! collection.getValues().contains(val))
						{
							multipleValuesStore.add(val);
							it.remove();
						}
					}
				}
				
			}else
				continue;
			
	
		}
		
		return unionVals;
		
	}
	/**
	 * Return the first available value for this key, in a deterministic way
	 * @param key
	 * @return
	 * @throws NoValueException
	 */
	public DataType getFirstValue(String key) throws NoValueException{
	
	
		DataType value = this.prototype.getProperty(key);
		
		for (PropertyStore store:stores)
		{
			if(store.hasProperty(key))
			{
				value = store.getProperty(key);
				break;
			}
		}
		
		if(value == null || (value instanceof NullType))
			throw new NoValueException();
		return value;
	}
	
	public DataType getCurrentValue(String key) throws NoValueException
	{
		if(stores.isEmpty())
			throw new NoValueException();
		
		if(hasMultipleValues(key))
			throw new NoValueException();
		
		PropertyStore first = stores.iterator().next();
		
		DataType value = first.getProperty(key);
		if (value == null || value.getType() == Type.NULL)
		{
			//get the default from the prototype...
			value = prototype.getProperty(key);
			if (value == null || value.getType() == Type.NULL)
			{
				throw new NoValueException();
			}
		}
		return value;
	}
	/**
	 * apply the current state of the prototype to the stores, generate an action for undo/redo-ing
	 * only changes to the prototype since the compare point was set will be used.	
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Action<?> apply()
	{		
		prototype.setSustainedView(null);//this isnt a key, but just to prevent memory leaks
		
		Collection<String>keysToNullify = new ArrayList<String>();
		Collection<String> keySet = new HashSet<String>( this.prototype.getKeys());
		keySet.addAll(this.alteredKeys);
		for (String key:keySet)
		{
			if(!isAltered(key))
				keysToNullify.add(key);
		/*	DataType curVal = prototype.getProperty(key);
			DataType prevVal = this.comparison.getProperty(key);
			if (curVal == prevVal)
				keysToNullify.add(key);
			else if (curVal != null && curVal.equals(prevVal))
			{
				keysToNullify.add(key);
			}*/
				
		}
		for (String key:keysToNullify)
		{
			prototype.nullifyKey(key);
			keySet.remove(key);
		}
		prototype.nullifyKey(PropertyStore.CLASS_KEY);
		final Collection<PropertyState> beforeStates =new ArrayList<PropertyState>();
		final Collection<PropertyState> afterStates =new ArrayList<PropertyState>();
		//String[] keys = this.prototype.getKeys().toArray(new String[0]);
		String[] keys = keySet.toArray(new String[0]);

		for (PropertyStore store:this.stores)
		{		
			PropertyState beforePropertyState = new SimplePropertyState(store, keys);
			beforeStates.add(beforePropertyState);
		//	store.apply(prototype, prototype.getKeys());
			for(String key:keySet)
			{
				DataType val = prototype.getProperty(key);
				if(val == null)
					continue;
				//if individual indices are controlled, do special processing here...
				if(alteredIndices.containsKey(key) && !alteredIndices.get(key).isEmpty())
				{
					switch(val.getType())
					{
						case VECTOR3:{
							Vector3Type v  =( Vector3Type) val;
							Vector3Type p = (Vector3Type)store.getProperty(key, val).deepCopy();
							if(isAltered(key,0))
								p.getValue().setX(v.getValue().x);
							if(isAltered(key,1))
								p.getValue().setY(v.getValue().y);
							if(isAltered(key,2))
								p.getValue().setZ(v.getValue().z);
							store.setProperty(key, p);
							break;
							}
							
						case VECTOR2:{
							Vector2Type v  =( Vector2Type) val;
							Vector2Type p = (Vector2Type)store.getProperty(key, val).deepCopy();
							if(isAltered(key,0))
								p.getValue().x=v.getValue().x;
							if(isAltered(key,1))
								p.getValue().x=v.getValue().y;
							store.setProperty(key, p);
							break;
							}
						
						case QUATERNION:{
							QuaternionType v  =( QuaternionType) val;
							QuaternionType p = (QuaternionType)store.getProperty(key, val).deepCopy();
							
							//optional special handling for per-axis quaternion rotation settings
							if (floatValueMap.containsKey(key))
							{
								float[] vals = floatValueMap.get(key);
								float[] currentVals = p.getValue().toAngles(new float[3]);
								boolean changed = false;
								for (int i =  0;i<vals.length;i++)
								{
									if(isAltered(key,i) &! Float.isNaN( vals[i]))
									{
										changed = true;
										currentVals[i] = vals[i];
									}
								}
								if (changed)
									p.getValue().fromAngles(currentVals);
								
							}else{
								if(isAltered(key,0))
									p.getValue().w=v.getValue().w;
								if(isAltered(key,1))
									p.getValue().x=v.getValue().x;
								if(isAltered(key,2))
									p.getValue().y=v.getValue().y;
								if(isAltered(key,3))
									p.getValue().z=v.getValue().z;
								
							}
							store.setProperty(key, p);
							break;
							}
						
						case COLOR:{
							ColorType v  =( ColorType) val;
							ColorType p = (ColorType)store.getProperty(key, val).deepCopy();
							if(isAltered(key,0))
								p.getValue().r = v.getValue().r;
							if(isAltered(key,1))
								p.getValue().g = v.getValue().g;
							if(isAltered(key,2))
								p.getValue().b = v.getValue().b;
							if(isAltered(key,3))
								p.getValue().a = v.getValue().a;
							
							store.setProperty(key, p);
							break;
							}
						default:
							store.setProperty(key,val);
					}
				}else
				{
					store.setProperty(key,val.deepCopy());
				}
			}
			GolemsValidator.getInstance().makeValid(store);
			PropertyState afterPropertyState = new SimplePropertyState(store, keys);
			afterStates.add(afterPropertyState);
		}
	
		for (PropertyStore store:this.stores)
		{	
			store.refresh();
		}
		
		Action<?> action = new Action()
		{

			@Override
			public String getDescription() {
				return name;
			}

			@Override
			public Type getType() {
				return null;
			}

			@Override
			public boolean doAction() {
				for (PropertyState state:afterStates)
				{
					state.restore();
				}
				for (PropertyState state:afterStates)
				{
					state.refresh();
				}
				return true;
			}

			@Override
			public boolean undoAction() {
				for (PropertyState state:beforeStates)
				{
					state.restore();
				}
				for (PropertyState state:beforeStates)
				{
					state.refresh();
				}
				return true;
			}
			
		};
		action.setDependencySet(dependencySet);
		return action;
	}

	private boolean isAltered(String key) {
		BitSet set = alteredIndices.get(key);
		return alteredKeys.contains(key) ||
		(set!=null && (!set.isEmpty()))
		
		;
	}

	public void setComparePoint() {
		comparison.clear();
		comparison.set(prototype);
		
	}

	private ActionDependencySet dependencySet;
	
	public ActionDependencySet getDependencySet() {
		return dependencySet;
	}

	public void setDependencySet(ActionDependencySet dependencySet) {
		this.dependencySet = dependencySet;
	}

	public PropertyStore getFirstStore() {
		
		if(this.stores.isEmpty())
			return prototype;
		
		return this.stores.iterator().next();
		
		
	}

	public Collection<PropertyStore> getStores() {
		return stores;
	}

	public void setIndexValue(String key, int index, float val) {
		float[] vals = floatValueMap.get(key);
		if(vals == null)
		{
			vals = new float[3];
			vals[0] = Float.NaN;
			vals[1] = Float.NaN;
			vals[2] = Float.NaN;
			floatValueMap.put(key, vals);
		}
		vals[index] = val;
	}


	
	/*
	 * Plan: Create an empty 'prototype' store. This store will be made public; other classes can apply interpreters
	 * etc, to modify it.
	 * 
	 * This store will be used to later alter the set of property stores, but it will also be used 
	 * for querrying whether the set of stores all have matching values of a given type.
	 */
	
	
	
}
