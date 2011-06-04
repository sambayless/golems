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

public class PyramidInterpreter extends PhysicalStructureInterpreter {

	public final static String PYRAMID_SCALE = "extent";

	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(PYRAMID_SCALE);
	
		return super.enumerateKeys(keys);
	}
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(PYRAMID_SCALE))
			return defaultVector3;
		return super.getDefaultValue(key);
	}
	
	public PyramidInterpreter(PropertyStore store) {
		super(store);
		store.setClassName(GolemsClassRepository.PYRAMID_CLASS);
	}

	public PyramidInterpreter() {
		this(new PropertyStore());		
	}
	
	public void setExtent(Vector3f scale)
	{
		getStore().setProperty(PYRAMID_SCALE, scale);
	}
	
	public Vector3f getPyramidScale()
	{
		return getStore().getVector3f(PYRAMID_SCALE,new Vector3f(1,1,1));
	}
}
