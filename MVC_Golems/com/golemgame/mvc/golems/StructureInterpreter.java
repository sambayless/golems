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
