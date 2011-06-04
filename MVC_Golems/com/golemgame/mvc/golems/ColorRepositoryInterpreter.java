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
import com.golemgame.mvc.ColorType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;

public class ColorRepositoryInterpreter extends StoreInterpreter {

	public static final String COLORS = "colors";
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(COLORS);
		return super.enumerateKeys(keys);
	}
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(COLORS))
			return defaultCollection;
		return super.getDefaultValue(key);
	}
	
	public ColorRepositoryInterpreter() {
		this( new PropertyStore());
	}

	public ColorRepositoryInterpreter(PropertyStore store) {
		super(store);
		super.getStore().setClassName(GolemsClassRepository.COLOR_REPOSITORY_CLASS);
	}
	
	public CollectionType getColors()
	{
		return getStore().getCollectionType(COLORS);
	}

	public void addColor(ColorType color)
	{
		getColors().addElement(color);
	}
	
	public void removeColor(ColorType color)
	{
		getColors().removeElement(color);
	}

}
