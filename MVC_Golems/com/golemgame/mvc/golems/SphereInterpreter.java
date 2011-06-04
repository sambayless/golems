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

public class SphereInterpreter extends PhysicalStructureInterpreter {
	
	public static final String ELLIPSOID = "ellipsoid";
	public static final String RADII = "extent";
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(ELLIPSOID);
		keys.add(RADII);	
		return super.enumerateKeys(keys);
	}
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(ELLIPSOID))
			return defaultBool;
		if(key.equals(RADII))
			return defaultVector3;
		return super.getDefaultValue(key);
	}
	
	public SphereInterpreter(PropertyStore store) {
		super(store);
		store.setClassName(GolemsClassRepository.SPHERE_CLASS);
	}

	public SphereInterpreter() {
		this(new PropertyStore());		
	}

	public void setEllispoid(boolean ellipsoid)
	{
		getStore().setProperty(ELLIPSOID, ellipsoid);
	}
	
	public boolean isEllipsoid()
	{
		return getStore().getBoolean(ELLIPSOID);
	}
	
	public void setRadius(float radius)
	{
		getStore().getVector3f(RADII, new Vector3f(radius,radius,radius)).set(radius,radius,radius);
	}
	
	public void setExtent(Vector3f extent)
	{
		getStore().setProperty(RADII,extent);
	}
	
	public float getRadius()
	{
		return getStore().getVector3f(RADII, new Vector3f(0.5f,0.5f,0.5f)).x;
	}
	
	public Vector3f getExtent()
	{
		return getStore().getVector3f(RADII, new Vector3f(0.5f,0.5f,0.5f));
	}
	
}
