package com.golemgame.mvc.golems;

import java.util.Collection;

import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.Reference;

public class StructureInterpreter extends SpatialInterpreter {
	
	public final static String PHYSICAL_DECORATORS = "decorators";
	public final static String APPEARANCE = "appearance";
	public final static String REFERENCE = "reference";
	public static final String LAYER = "layer";
	
	public StructureInterpreter(PropertyStore store) {
		super(store);
		
	}

	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(PHYSICAL_DECORATORS);
		keys.add(APPEARANCE);
		keys.add(REFERENCE);
		keys.add(LAYER);
		return super.enumerateKeys(keys);
	}
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(PHYSICAL_DECORATORS))
			return defaultCollection;
		if(key.equals(APPEARANCE))
			return defaultStore;
		if(key.equals(REFERENCE))
			return defaultReference;
		if(key.equals(LAYER))
			return defaultReference;
		return super.getDefaultValue(key);
	}

	public Reference getLayer()
	{
		return getStore().getReference(LAYER);
	}
	
	public void setLayer(Reference reference)
	{
		getStore().setProperty(LAYER,reference);
	}
	
	public StructureInterpreter() {
		this(new PropertyStore());		
	}

	public CollectionType getPhysicalDecorators()
	{
		return getStore().getCollectionType(PHYSICAL_DECORATORS);
	}
	
	public void addPhysicalDecorator(PropertyStore store)
	{
		this.getPhysicalDecorators().addElement(store);
	}
	
	public void removePhysicalDecorator(PropertyStore store)
	{
		this.getPhysicalDecorators().removeElement(store);
	}
	
	public PropertyStore getAppearanceStore()
	{
		return getStore().getPropertyStore(APPEARANCE);
	}
	
	public Reference getReference()
	{
		return getStore().getReference(REFERENCE, Reference.createUniqueReference());
	}
	
	public void setReference(Reference reference)
	{
		getStore().setProperty(REFERENCE, reference);
	}

}
