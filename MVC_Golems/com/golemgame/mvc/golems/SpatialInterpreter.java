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
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

/**
 * These are NOT the properties of a SpatialModel, by of a mechanical component that happens to have a spatial position.
 * @author Sam
 *
 */
public class SpatialInterpreter extends StoreInterpreter {
	public final static String LOCALTRANSLATION = "translation";
	public final static String LOCALROTATION = "rotation";
	public final static String LOCALSCALE= "scale";
	
	public SpatialInterpreter(PropertyStore store) {
		super(store);
	}
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(LOCALTRANSLATION))
			return defaultVector3;
		if(key.equals(LOCALROTATION))
			return defaultVector3;
		if(key.equals(LOCALSCALE))
			return defaultVector3;
		return super.getDefaultValue(key);
	}
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(LOCALTRANSLATION);
		keys.add(LOCALROTATION);
		keys.add(LOCALSCALE);		
		return super.enumerateKeys(keys);
	}


	public SpatialInterpreter() {
		this(new PropertyStore());		
	}

	public Vector3f getLocalTranslation()
	{
		return getStore().getVector3f(LOCALTRANSLATION,new Vector3f(0,0,0));
	}
	
	public Vector3f getLocalTranslation(Vector3f defaultTranslation)
	{
		return getStore().getVector3f(LOCALTRANSLATION,defaultTranslation);
	}
	
	public Vector3f getLocalScale()
	{
		return getStore().getVector3f(LOCALSCALE,new Vector3f(1,1,1));
	}
	public Quaternion getLocalRotation()
	{
		return getStore().getQuaternion(LOCALROTATION, new Quaternion());
	}
	
	public void setLocalTranslation(Vector3f translation)
	{
		getStore().setProperty(LOCALTRANSLATION, translation);
	}
	
	public void setLocalRotation(Quaternion rotation)
	{
		getStore().setProperty(LOCALROTATION, rotation);
	}
	
	public void setLocalScale(Vector3f scale)
	{
		getStore().setProperty(LOCALSCALE, scale);
	}

}
