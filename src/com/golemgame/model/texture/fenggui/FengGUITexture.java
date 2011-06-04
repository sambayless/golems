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
package com.golemgame.model.texture.fenggui;


import org.fenggui.binding.render.ITexture;

import com.golemgame.model.texture.TextureServer;
import com.golemgame.model.texture.TextureTypeKey;
import com.golemgame.model.texture.TextureWrapper;
import com.golemgame.model.texture.TextureServer.NoTextureException;
import com.golemgame.mvc.golems.AppearanceInterpreter.ImageType;
import com.golemgame.states.StateManager;
import com.jme.renderer.ColorRGBA;

public class FengGUITexture  extends TextureWrapper {
	
	
	private static final long serialVersionUID = 1L;

	private transient ITexture texture;

	//private transient TextureState textureState;
	private ColorRGBA tintcolor;
	private TextureTypeKey textureTypeKey;
	private ApplyMode mode;
	private FengGUITextureLoadListener listener;
//	private transient Lock loadLock = new ReentrantLock();
	
	@Override
	public TextureTypeKey getTextureTypeKey(int element) {
		return textureTypeKey;
	}



	protected FengGUITexture()
	{
		
		this(new TextureTypeKey(ImageType.VOID,512,512, TextureFormat.RGB,false) );
	}

	
	
	public TextureTypeKey getTextureTypeKey() {

		return textureTypeKey;
	}


	public void setTexture(ITexture texture) {
		this.texture = texture;
		this.setApplyMode(this.getApplyMode());
	}



	//public void setTextureState(TextureState textureState) {
	//	this.textureState = textureState;
	//}



	
	public ApplyMode getApplyMode() {
		return mode;
	}



	public FengGUITexture( TextureTypeKey TextureTypeKey) {
		super();
		this.textureTypeKey = TextureTypeKey;
		this.setApplyMode(ApplyMode.MODULATE);
	}
	
	

	
	public void setApplyMode(ApplyMode mode) {
		this.mode=mode;
			
	}



	
	public TextureWrapper copyTextureWrapper() {

			return TextureServer.getInstance().getTexture(this.getTextureTypeKey());

	}

	
	public void setTint(ColorRGBA tintColor) {
		

		this.tintcolor = tintColor;
		if (this.textureTypeKey.getImage()==ImageType.VOID)
			return;
		
		/*try{
			if (texture == null)
				TextureServer.getInstance().loadTextureWrapper(this);
		}catch(NoTextureException e)
		{
			StateManager.logError(e);
			return;
		}*/
		
		
	}

	
	
	
	public ColorRGBA getTint() {

		return tintcolor;
	}



	public ITexture getTexture() throws NoTextureException
	{
		//lock this
		if (this.texture == null)
		{
			load(this.getTextureTypeKey());
			
			return this.texture;
		}
		
		return this.texture;
	}



	
	public boolean isLoaded() {
		return texture != null;
	}



	public synchronized FengGUITextureLoadListener getListener() {
		return listener;
	}



	public synchronized void setListener(FengGUITextureLoadListener listener) {
		this.listener = listener;
		if(this.isLoaded() && listener!=null)
			listener.textureLoaded(this);
	}



	@Override
	public synchronized void load(TextureTypeKey loadedKey) {
		
		this.texture = FengGUITextureServer.getFengGUIInstance().getLoadedFengGUITexture(loadedKey);
		
		if(listener!=null)
			listener.textureLoaded(this);
		
	}

	

}


	

