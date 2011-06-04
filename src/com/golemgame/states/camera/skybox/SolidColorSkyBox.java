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

import java.awt.Color;
import java.awt.Graphics2D;

import com.golemgame.util.PaintableImage;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Skybox;

public class SolidColorSkyBox extends SkyBoxData{
	private Texture texture;

	public SolidColorSkyBox() {
		this(Color.black);
	}
	private Color color;
	public SolidColorSkyBox(ColorRGBA color) {
		this(new Color(color.r, color.g,color.b,color.a));
	}

	private SolidColorSkyBox(Color color) {
		super("Color");
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public void build(int width, int height) {
		
		PaintableImage image = new PaintableImage(1,1,false)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics2D g) {
				g.setColor( color);
				g.fillRect(0, 0, getWidth(),getHeight());
			}
		};
		
		 texture= new Texture(); 
		 texture.setApply(Texture.AM_MODULATE);
		
		
		 texture.setFilter(Texture.MM_LINEAR);
		 texture.setImage(image);
		 texture.setMipmapState(Texture.FM_LINEAR);
        
 

		texture.setScale(new Vector3f(1f,1f,1.0001f));//this is to force the scaling to be refreshed...
		texture.setTranslation(new Vector3f());
	}

	public void apply(Face face,Skybox skybox, int unit)
	{
	
		  skybox.setTexture(face.getSkyboxEnum(), texture,unit);
	}
	
}
