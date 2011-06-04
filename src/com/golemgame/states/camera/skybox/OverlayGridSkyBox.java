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
import java.nio.FloatBuffer;

import com.golemgame.util.PaintableImage;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.Skybox;
import com.jme.scene.shape.Quad;

public class OverlayGridSkyBox extends SkyBoxData{
	private Texture grid;
	private static final float REPEAT= 32;
	public OverlayGridSkyBox() {
		super("Grid");
	}
	
	@Override
	public void build(int width, int height) {
		
		PaintableImage gridImage = new PaintableImage((int)(width/REPEAT),(int)(height/REPEAT),true)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics2D g) {
			

				g.setColor( new Color(0,0,0,0));
				g.fillRect(0, 0, getWidth(),getHeight());
				g.setColor( new Color(128,128,128,255));
				
				g.drawRect(0, 0, getWidth()-1,getHeight()-1);
				
			}
			
		};

		 grid= new Texture(); 
		 grid.setApply(Texture.AM_DECAL);

		
		 grid.setFilter(Texture.FM_LINEAR);
		 grid.setImage(gridImage);
		 grid.setMipmapState(Texture.MM_NONE);
        
		 grid.setWrap(Texture.WM_WRAP_S_WRAP_T);

		grid.setScale(new Vector3f(1.00001f,1.00001f,1.00001f));//this is to force the scaling to be refreshed...
		grid.setTranslation(new Vector3f());
	}

	public void apply(Face face,Skybox skybox, int unit)
	{
		  Quad quad = skybox.getSide(face.getSkyboxEnum());
		  FloatBuffer tbuf = quad.getTextureBuffer(0,unit);
		  if(tbuf == null)
		  {
			 // tbuf = ByteBuffer.allocateDirect(quad.getTextureBuffer(0,0).capacity()*Float.SIZE/Byte.SIZE).asFloatBuffer();
			 quad.copyTextureCoords(0, 0, 1);
			 tbuf = quad.getTextureBuffer(0,unit);
			//  quad.setTextureBuffer(0, tbuf,unit);
		  }
				
		  tbuf.clear();		
		  tbuf.put(0).put(REPEAT);
		  tbuf.put(0).put(0);
		  tbuf.put(REPEAT).put(0);
		  tbuf.put(REPEAT).put(REPEAT);
		 
		  skybox.setTexture(face.getSkyboxEnum(), grid,unit);
		//super.apply(face, skybox, unit);
	}
	
}
