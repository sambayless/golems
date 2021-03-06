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
package com.golemgame.model.spatial.shape;

import com.golemgame.model.spatial.SpatialModelImpl;
import com.jme.bounding.BoundingBox;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Line;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;

public class LineModel  extends SpatialModelImpl{

	private static final long serialVersionUID = 1L;
	public LineModel() {
		super();

	}

	
	private Line line;
	@Override
	protected Spatial buildSpatial() {
		Vector3f[] vertices = new Vector3f[2];
		vertices[0] = new Vector3f(0,0,0);
		vertices[1] = new Vector3f(1,0,0);
		
		Vector3f[] normals = new Vector3f[]{Vector3f.UNIT_Y,Vector3f.UNIT_Y};
	
		Vector2f[] textureCoords = new Vector2f[2];
		textureCoords[0] = new Vector2f(0,0);
		textureCoords[1] = new Vector2f(1,1);
		
		ColorRGBA[] colors = null;// new ColorRGBA[]{startColor,endColor};
		line = new Line("Grapple Beam",vertices,normals, colors,textureCoords);
		line.setLineWidth(4f);
		line.setCullMode(SceneElement.CULL_NEVER);
		line.updateRenderState();
		
		line.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
		line.setLightCombineMode(LightState.OFF);
		line.setTextureCombineMode(TextureState.REPLACE);
		line.setModelBound(new BoundingBox());
		return line;
	}
	
	public void setLineWidth(float width)
	{
		line.setLineWidth(width);
	}
	public void setLineLength(float length)
	{
		line.getLocalScale().x = length;
	}

	public void setLineColor(ColorRGBA beamColor) {

		
	
		line.setDefaultColor(beamColor);
	}



}
