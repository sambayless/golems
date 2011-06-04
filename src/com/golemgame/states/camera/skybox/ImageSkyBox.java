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
package com.golemgame.states.camera.skybox;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.Skybox;
import com.jme.util.TextureManager;

public class ImageSkyBox extends SkyBoxData{
	private Map<Face,Texture> textures = new HashMap<Face,Texture>();
	
	
	public ImageSkyBox(String name, BufferedImage... img) {
		super(name);
		int i = 0;
		for(Face face:Face.values())
		{
			BufferedImage image = img[i++];
			Texture imageTex= new Texture(); 
			imageTex.setApply(Texture.AM_DECAL);

			
			imageTex.setFilter(Texture.FM_LINEAR);
			imageTex.setImage(TextureManager.loadImage(image,true));
			imageTex.setMipmapState(Texture.MM_NONE);
	        
			//imageTex.setWrap(Texture.WM_WRAP_S_WRAP_T);

			//imageTex.setScale(new Vector3f(1.00001f,1.00001f,1.00001f));//this is to force the scaling to be refreshed...
			imageTex.setTranslation(new Vector3f());
			textures.put(face, imageTex);
			
		}
		 
	
	}
	

	public void apply(Face face,Skybox skybox, int unit)
	{
		skybox.setTexture(face.getSkyboxEnum(), textures.get(face),unit);
	}
	
	
}
