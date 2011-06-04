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


public class CylinderInterpreter extends PhysicalStructureInterpreter {

	public final static String CYL_RADIUS = "radius";
	public final static String CYL_HEIGHT = "height";
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(CYL_RADIUS);
		keys.add(CYL_HEIGHT);
	
		return super.enumerateKeys(keys);
	}
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(CYL_RADIUS))
			return defaultFloat;
		if(key.equals(CYL_HEIGHT))
			return defaultFloat;	

		return super.getDefaultValue(key);
	}
	
	public CylinderInterpreter() {
		this(new PropertyStore());		
	}

	public CylinderInterpreter(PropertyStore store) {
		super(store);
		store.setClassName(GolemsClassRepository.CYL_CLASS);
	}

	public void setRadius(float radius)
	{
		this.getStore().setProperty(CYL_RADIUS, radius);
	
	}
	
	public void setHeight(float height)
	{
		this.getStore().setProperty(CYL_HEIGHT, height);
	}
	
	public float getRadius()
	{
		return this.getStore().getFloat(CYL_RADIUS,0.5f);
	}
	
	public float getHeight()
	{
		return this.getStore().getFloat(CYL_HEIGHT,1f);
	}
	
	
}
