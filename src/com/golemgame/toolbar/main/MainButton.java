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
package com.golemgame.toolbar.main;


import com.golemgame.toolbar.StandardButton;
import com.golemgame.toolbar.tooltip.IconToolTip;
import com.golemgame.toolbar.tooltip.ToolTipData;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.util.TextureManager;
import com.simplemonkey.util.Dimension;

public class MainButton extends StandardButton {

	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MainButton(String name, Texture icon, MainButtonGroup group) {
		this(name,icon,group.getInactive(),group.getHover(),group.getActive());
	}

	public MainButton(String name, Texture icon, Texture inactive,	Texture hover, Texture active, float textureWidth, float textureHeight) {
		super(64, 64, active, inactive, hover, icon, name);	
		this.name = name;
		super.setToolTip(name);
		super.setSize(64,64);
		super.setMinSize(32,32);
		this.setShrinkable(true);
		this.setExpandable(true);
		
		
		Texture tooltipIcon = loadTexture(icon.getImageLocation());
		Texture tooltipBack = loadTexture(hover.getImageLocation());
		tooltipIcon.setApply(Texture.AM_DECAL);
		tooltipBack.setApply(Texture.AM_MODULATE);
		ToolTipData data = new ToolTipData(tooltipIcon,tooltipBack,name);
		
		IconToolTip.setTooltipData(this, data);
		
		
	}
	
	private static Texture loadTexture(String path)
	{
	/*	if(mipmaps)
		{
			System.out.println();
		}*/
		Image textureImage = TextureManager.loadImage(MainButton.class.getClassLoader().getResource(
		            "com/golemgame/data/textures/" + path), true);
		Texture texture = new Texture();
		texture.setImageLocation(path);
				
		 texture.setAnisoLevel(0);
		 texture.setMipmapState( Texture.MM_LINEAR);
		 texture.setFilter(Texture.FM_LINEAR);
		 texture.setImage(textureImage);
		 
		 return texture;
	}
	
	public MainButton(String name, Texture icon, Texture inactive,
			Texture hover, Texture active) {
		this(name,icon, inactive,hover,active, icon.getImage().getWidth(),icon.getImage().getHeight());
	
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public void setSize(Dimension s) {
		float minSize = Math.min(s.getWidth(), s.getHeight());
		Dimension size = new Dimension(minSize,minSize);
		
		super.setSize(size);
	}


	

	

}
