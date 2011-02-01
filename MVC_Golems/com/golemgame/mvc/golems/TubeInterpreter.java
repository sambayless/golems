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
import com.golemgame.mvc.FloatType;
import com.golemgame.mvc.PropertyStore;
import com.jme.math.FastMath;

public class TubeInterpreter extends CylinderInterpreter {
	public static final String RADIUS_INNER = "radius.inner";
	//public static final String RADIUS_OUTER = CylinderInterpreter.CYL_RADIUS;
	
	public static final String ARC = "arc";
	
	public TubeInterpreter() {
		this(new PropertyStore());
	}
	
	public TubeInterpreter(PropertyStore store) {
		super(store);
		store.setClassName(GolemsClassRepository.TUBE_CLASS);
	}

	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(RADIUS_INNER);		
		//keys.add(RADIUS_OUTER);		
		keys.add(ARC);		
	
	
		return super.enumerateKeys(keys);
	}

	private static final FloatType defRadInner = new FloatType(0.25f);
	private static final FloatType defRadOuter = new FloatType(0.5f);
	private static final FloatType defArc = new FloatType(FastMath.TWO_PI);
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(RADIUS_INNER))
			return defRadInner;
	//	if(key.equals(RADIUS_OUTER))
	//		return defRadOuter;
		if(key.equals(ARC))
			return defArc;
	
		return super.getDefaultValue(key);
	}
	
	public float getInnerRadius()
	{
		return getStore().getFloat(RADIUS_INNER,0.25f);
	}
/*	public float getOuterRadius()
	{
		return getStore().getFloat(RADIUS_OUTER,0.5f);
	}
	*/
	public float getArc()
	{
		return getStore().getFloat(ARC,FastMath.TWO_PI);
	}
	
	
	public void setInnerRadius(float radius)
	{
		getStore().setProperty(RADIUS_INNER,radius);
	}
/*	public void setOuterRadius(float radius)
	{
		getStore().setProperty(RADIUS_OUTER,radius);
	}*/
	
	public void setArc(float arc)
	{
		getStore().setProperty(ARC,arc);
	}
	
}
