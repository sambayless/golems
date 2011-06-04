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
import com.jme.renderer.ColorRGBA;

public class SkyboxInterpreter extends StoreInterpreter {
//	public static final String IS_IMAGE = "isImage";
	public static final String IMAGE= "image";
	public static final String COLOR = "color";

	public static enum Sky
	{
		NONE("(None)"),COLOR("Color"),CLOUDS("Clouds"),SERENE("Serene")
		,SUNSET("Sunset"),STARS("Stars")
		;//add more later
		
		private final String name;

		private Sky(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
		
	}
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		//keys.add(IS_IMAGE);
		keys.add(IMAGE);	
		keys.add(COLOR);
		return super.enumerateKeys(keys);
	}
	
	@Override
	public DataType getDefaultValue(String key) {

	/*	if(key.equals(IS_IMAGE))
			return defaultBool;*/
		if(key.equals(IMAGE))
			return defaultEnum;
		if(key.equals(COLOR))
			return defaultColor;
		return super.getDefaultValue(key);
	}
	
	public SkyboxInterpreter(PropertyStore store) {
		super(store);
	}
/*	
	public boolean isImage()
	{
		return getStore().getBoolean(IS_IMAGE);
	}
	
	public void setIsImage(boolean isImage)
	{
		getStore().setProperty(IS_IMAGE, isImage);
	}*/
	
	public void setImage(Sky image)
	{
		getStore().setProperty(IMAGE, image);
	}
	
	public Sky getImage()
	{
		return getStore().getEnum(IMAGE,Sky.NONE);
	}
	
	public void setColor(ColorRGBA color)
	{
		getStore().setProperty(COLOR, color);
	}
	public ColorRGBA getColor()
	{
		return getStore().getColor(COLOR, ColorRGBA.black);
	}
	
}
