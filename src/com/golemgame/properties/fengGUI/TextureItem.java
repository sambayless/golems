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
package com.golemgame.properties.fengGUI;

import com.golemgame.model.texture.Images;
import com.golemgame.model.texture.TextureTypeKey;
import com.golemgame.model.texture.fenggui.FengGUITexture;
import com.golemgame.mvc.golems.AppearanceInterpreter.ImageType;


public class TextureItem implements Comparable<TextureItem>
{
	private final TextureTypeKey TextureTypeKey;
	private final FengGUITexture fengGUItexture;
	private String name;
	
	public TextureItem(TextureTypeKey TextureTypeKey, FengGUITexture fengGUItexture) {
		super();
		this.TextureTypeKey = TextureTypeKey;
		this.fengGUItexture = fengGUItexture;
		name = Images.getInstance().getName(TextureTypeKey.getImage());
	}
	
	public TextureTypeKey getTextureTypeKey() {
		return TextureTypeKey;
	}

	public FengGUITexture getFengGUItexture() {
		return fengGUItexture;
	}

	
	public String toString() {
		return name;
	}

	
	public int compareTo(TextureItem o) {
		if (o.TextureTypeKey.getImage() == null || o.getTextureTypeKey().getImage() == ImageType.VOID)
		{
			return 1;
		}else if(this.TextureTypeKey.getImage() == null || this.getTextureTypeKey().getImage() == ImageType.VOID)
		{
			return -1;
		}else
		{
			return this.name.compareTo(o.name);
		}
	}
	
	
}
