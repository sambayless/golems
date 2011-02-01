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
import com.jme.math.Vector3f;

public class BoxInterpreter extends PhysicalStructureInterpreter {
	
	
	public final static String BOX_EXTENT = "extent";
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {

		keys.add(BOX_EXTENT);	
		return super.enumerateKeys(keys);
	}
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(BOX_EXTENT))
			return defaultVector3;	

		return super.getDefaultValue(key);
	}
	
	public BoxInterpreter(PropertyStore store) {
		super(store);
		store.setClassName(GolemsClassRepository.BOX_CLASS);
	}
	
	public BoxInterpreter() {
		this(new PropertyStore());		
	}

	public void setExtent(Vector3f extent)
	{
		super.getStore().setProperty(BOX_EXTENT,extent);
	}
	
	public Vector3f getExtent()
	{
		return super.getStore().getVector3f(BOX_EXTENT, new Vector3f(0.5f,0.5f,0.5f));
	}
}
