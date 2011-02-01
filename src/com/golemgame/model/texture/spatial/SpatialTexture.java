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
package com.golemgame.model.texture.spatial;


import java.util.ArrayList;

import com.golemgame.model.effect.ModelEffect;
import com.golemgame.model.texture.Images;
import com.golemgame.model.texture.TextureServer;
import com.golemgame.model.texture.TextureTypeKey;
import com.golemgame.model.texture.TextureWrapper;
import com.golemgame.model.texture.TextureServer.NoTextureException;
import com.golemgame.mvc.golems.AppearanceInterpreter.ImageType;
import com.jme.image.Texture;
import com.jme.renderer.ColorRGBA;

public class SpatialTexture extends TextureWrapper {
	
	
	private static final long serialVersionUID = 1L;
	
	public static final SpatialTexture DEFAULT = new SpatialTexture();
	//private transient Texture texture;
	private ArrayList<Texture> textures = new ArrayList<Texture>();
	//private transient TextureState textureState;
	private ColorRGBA tintcolor;
	private int elements = 1;

	
	public int getElements() {
		return elements;
	}


	private ArrayList<TextureTypeKey> textureKeys = new ArrayList<TextureTypeKey>();

	private ApplyMode mode;
	protected SpatialTexture()
	{
		
		this(new TextureTypeKey(ImageType.VOID,1024,1024, TextureFormat.RGB, true) );
	}
	
	
	public TextureTypeKey getTextureTypeKey() {
		return getTextureTypeKey(0);
	}
	
	public TextureTypeKey getTextureTypeKey(int element) {
		
		return textureKeys.get(element) == null?textureKeys.get(0):textureKeys.get(element);
	}

	

	@Override
	public synchronized void load(TextureTypeKey loadedKey) {
		for(int i = 0;i<getElements();i++)
		{
			TextureTypeKey e = getTextureTypeKey(i);
			if(e!=null && e.equals(loadedKey))
			{
				setTexture(TextureServer.getInstance().getLoadedTexture(loadedKey),i);
			}
		}
		
		if(associatedEffect!=null && isLoaded())
			associatedEffect.refreshEffect();
	}


	private void setTexture(Texture texture,int element) {
		
		this.textures.set(element, texture);
		this.setApplyMode(this.getApplyMode());
		this.setTint(this.getTint());
	
	}

	
	public boolean isLoaded() {
		if(textures.size()<elements)
			return false;
		for(int i = 0;i<elements;i++)
		{
			if(!isLoaded(i))
				return false;
		}
		return true;
	}

	
	public ApplyMode getApplyMode() {
		return mode;
	}



	public SpatialTexture( TextureTypeKey textureTypeKey) {
		super();
		this.elements = Math.max(1, Images.getInstance().elements(textureTypeKey.getImage(), textureTypeKey.getShape()));
		while(textures.size()< elements)
			textures.add(null);
		textureTypeKey.setElementNumber(0);
		this.textureKeys.add(textureTypeKey);
		for(int i =1;i<elements;i++)
		{
			TextureTypeKey tKey = new TextureTypeKey(textureTypeKey);
			tKey.setElementNumber(i);
			this.textureKeys.add(tKey);
		}
		this.tintcolor = new ColorRGBA(ColorRGBA.white);
		this.setApplyMode(ApplyMode.MODULATE);
	
	}
	
	

	
	public void setApplyMode(ApplyMode mode) {
		this.mode=mode;
		for(Texture texture:textures)
		{
			if (texture==null)
				continue;
			
			switch (mode)
			{		
				case BLEND:
					texture.setApply(Texture.AM_BLEND);break;
				case DECAL:
					texture.setApply(Texture.AM_DECAL);break;
				case MODULATE:
					texture.setApply(Texture.AM_MODULATE);break;
				case ADD:
					texture.setApply(Texture.AM_ADD);break;
				case REPLACE:
					texture.setApply(Texture.AM_REPLACE);break;
				case COMBINE:
					texture.setApply(Texture.AM_COMBINE);break;
			}
		}
	}



	
	public TextureWrapper copyTextureWrapper() {

			return TextureServer.getInstance().getTexture(this.getTextureTypeKey());

	}

	
	public void setTint(ColorRGBA tintColor) {
		

		this.tintcolor.set(tintColor);
	/*	if (this.getTextureTypeKey().getImage()==ImageType.VOID)
			return;*/
		for(Texture texture:textures)
		{
			if (texture == null)
				continue;
	
			
			if (texture!= null)
				texture.setBlendColor(this.tintcolor);
		}
	}

	
	
	
	public ColorRGBA getTint() {

		return tintcolor;
	}




	/**
	 * Get the texture (if this is part of a texture series, get the first element of that series).
	 *  (warning - may be null. check that this is loaded first)
	 * @return
	 */
	public Texture getTexture()
	{
		return getTexture(0);
	/*	try{
			if (texture == null)//lock this...
				TextureServer.getInstance().loadTextureWrapper(this);
		}catch(NoTextureException e)
		{
		//	e.printStackTrace();
			return null;
		}
		
		return this.texture;*/
	}
	
	
	/**
	 * Get the element in the series of textures 
	 * (warning - may be null. check that this is loaded first)
	 * @param number
	 * @return
	 */
	public Texture getTexture(int element) 
	{
		if(element>=this.getElements())
			return null;
/*		
		try{
			if (textures.get(element) == null)//lock this...
				TextureServer.getInstance().loadTexture(textureKeys.get(element));
		}catch(NoTextureException e)
		{
		//	e.printStackTrace();
			return null;
		}
		*/
		return textures.get(element);
	}


	public boolean isLoaded(int element) {
		TextureTypeKey key = getTextureTypeKey(element);
		if ( key== null || key.getImage() == null || key.getImage() == ImageType.VOID 
				||	(key.getImage() == ImageType.TEXT && (key.getText()== null || key.getText().length() ==0 )))
		{
			return true;
		}
		
		
/*		if(textures.size()<elements)
			return false;*/
		
		//for(int i = 0;i<elements;i++)
		{
			if(textures.get(element)==null)
				return false;
		}
		return true;
	}
	
	


}
