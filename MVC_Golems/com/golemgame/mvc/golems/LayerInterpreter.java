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

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.Reference;
import com.golemgame.mvc.ReferenceType;
import com.jme.renderer.ColorRGBA;

public class LayerInterpreter extends StoreInterpreter {
//	public static final String MEMBERS = "members";
	public static final String COLOR = "color";
	public static final String NAME = "layer.name";
	public static final String ID = "layer.id";
	

	public LayerInterpreter() {
		this(new PropertyStore());
	}

	public LayerInterpreter(PropertyStore store) {
		super(store);
		getStore().setClassName(GolemsClassRepository.LAYER_CLASS);
	}

	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
	//	keys.add(MEMBERS);
		keys.add(COLOR);
		keys.add(NAME);
		keys.add(ID);
		return super.enumerateKeys(keys);
	}
	
	@Override
	public DataType getDefaultValue(String key) {
	//	if(key.equals(MEMBERS))
	//		return defaultCollection;
		if(key.equals(COLOR))
			return defaultColor;
		if(key.equals(NAME))
			return defaultString;
		if(key.equals(ID))
			return defaultReference;
		return super.getDefaultValue(key);
	}
		
	public ColorRGBA getColor()
	{
		return getStore().getColor(COLOR, new ColorRGBA());
	}
	
	public void setColor(ColorRGBA color)
	{
		getStore().setProperty(COLOR, color);
	}
	
	public Reference getID()
	{
		return getStore().getReference(ID, Reference.createUniqueReference());
	}
	
	public String getLayerName()
	{
		return getStore().getString(NAME,"(Unnamed)");
	}
	
	public void setLayerName(String name)
	{
		getStore().setProperty(NAME, name);
	}

	public void setID(Reference reference) {
		getStore().setProperty(ID, new ReferenceType(reference));//important to put in a new ref type
		
	}
	
/*	public CollectionType getMembers()
	{
		return getStore().getCollectionType(MEMBERS);
	}
	
	public void addMember(Reference member)
	{
		for(DataType data:getMembers().getValues())
		{
			if(data instanceof ReferenceType)
			{
				if(((ReferenceType)data).getID().equals(member))
				{
					return;
				}
			}
		}
		getMembers().addElement(new ReferenceType( member));
	}
	
	public void removeMember(Reference member)
	{
		DataType toRemove = null;
		for(DataType data:getMembers().getValues())
		{
			if (data.getType() == DataType.Type.REFERENCE)
			{
				if (((ReferenceType)data).getID().equals(member))
				{
					toRemove = data;
					break;
				}
			}
		}
		getMembers().removeElement(toRemove);
	}*/
}
