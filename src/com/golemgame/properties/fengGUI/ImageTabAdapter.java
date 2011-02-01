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

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.util.TextureManager;

public class ImageTabAdapter extends TabAdapter implements ImageTab {
	
	private static final String DEFAULT_TEXTURE_HOVER = "buttons/glass/Glass_Blue.png";
	private static final String DEFAULT_TEXTURE_ACTIVE = "buttons/solid/Solid_White.png";
	private static final String DEFAULT_TEXTURE_INACTIVE = "buttons/solid/Solid_Blue.png";
	private static final String DEFAULT_TEXTURE_ICON = "buttons/polyhedrons/Sphere.png";
	
	public ImageTabAdapter(String title) {
		super(title);
	}

	public Texture getHover() {
		return loadTexture(DEFAULT_TEXTURE_HOVER);
		
	}

	public Texture getInactive() {
		return loadTexture(DEFAULT_TEXTURE_INACTIVE);
		
	}
	
	public Texture getActive() {
		return loadTexture(DEFAULT_TEXTURE_ACTIVE);
		
	}
	
	public Texture getIcon() {
		return loadTexture(DEFAULT_TEXTURE_ICON);
		
	}
	public Texture loadTexture(String path)
	{
		Image textureImage = TextureManager.loadImage(getClass().getClassLoader().getResource(
	            "com/golemgame/data/textures/" + path), false);
	
	
	
	Texture texture = new Texture();
	texture.setImageLocation(path);

	 texture.setAnisoLevel(0);
	 texture.setMipmapState( Texture.MM_LINEAR);
	 texture.setFilter(Texture.FM_LINEAR);
	 texture.setImage(textureImage);
	 
	 return texture;
	}
}
