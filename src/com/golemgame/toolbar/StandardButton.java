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
package com.golemgame.toolbar;

import com.jme.image.Texture;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;

public class StandardButton extends ButtonWidget {

	private Node buttonNode;
	private Quad buttonQuad;
//	private String name = "";
	private Texture inactive;
	private Texture hover;
	private Texture active;
	private  TextureState buttonTexture;
	
	public StandardButton(int width, int height, Texture active, Texture inactive, Texture hover)
	{
		this(width, height, active, inactive, hover, null, "");
	}
	
	public StandardButton(int width, int height, Texture active, Texture inactive, Texture hover, String name)
	{
		this(width, height, active, inactive, hover, null, name);
	}
	public StandardButton(int width, int height, Texture active, Texture inactive, Texture hover, Texture icon)
	{
		this(width, height, active, inactive, hover, icon, "");
	}
	
	public StandardButton(int width, int height, Texture active, Texture inactive, Texture hover, Texture icon, String name)
	{	
		super();
		
		//this.name = name;
		this.inactive = inactive;
		this.hover = hover;
		this.active = active;
	
		buttonQuad = new Quad("button", 1, 1);
		buttonNode = new Node();
		buttonNode.attachChild(buttonQuad);
		buttonQuad.getLocalTranslation().set(1f/2f, 1f/2f,0);

		
		super.setSize(width,height);
		super.setMinSize(width,height);
		super.getMinContentSize().setSize(width,height);
		super.setExpandable(false);
		super.setShrinkable(false);
	       buttonTexture =  DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
	       buttonTexture.setTexture(inactive,0);

	       if (icon != null)
	       {
	           icon.setApply(Texture.AM_DECAL);	       
		       buttonTexture.setTexture(icon, 1);
		       buttonQuad.copyTextureCoords(0, 0, 1); 
	       }	       
	    
	       buttonQuad.setRenderState(buttonTexture);
	       
	       buttonQuad.updateRenderState();
	       
	       this.getSpatial().updateRenderState();

	       addButtonListener(new ButtonListener()
	       {

			
			public void buttonStateChange(ButtonState state) {
				if (state == ButtonState.HOVER)
				{
					buttonTexture.setTexture(StandardButton.this.hover, 0);
				}else if (state == ButtonState.ANTI_HOVER)
				{
					buttonTexture.setTexture(StandardButton.this.inactive, 0);
				}else if (state == ButtonState.ACTIVE)
				{
					buttonTexture.setTexture(StandardButton.this.active, 0);
				}else if (state == ButtonState.INACTIVE)
				{
					buttonTexture.setTexture(StandardButton.this.inactive, 0);
				}   
				buttonQuad.updateRenderState();
			}
	   	
	       });
	       
	}

	
	public Spatial getSpatial() {
		return buttonNode;
	}
	
	

	
	
	
	
}
