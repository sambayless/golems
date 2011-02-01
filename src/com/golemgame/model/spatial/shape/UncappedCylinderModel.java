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
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Cylinder;

public class UncappedCylinderModel extends SpatialModelImpl{
	private static final long serialVersionUID = 1L;
	public static final float INIT_SIZE = 1f;

	public UncappedCylinderModel() {
		super();
	}

	public UncappedCylinderModel(boolean registerSpatial) {
		super(registerSpatial);
	}

	protected Spatial buildSpatial()
	{
		Spatial spatial= new Cylinder("built", 4,8, INIT_SIZE/2,INIT_SIZE,false);	
		spatial.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.HALF_PI, new Vector3f(1,0,0)));		
		spatial.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, new Vector3f(0,1,0)));
		spatial.setCullMode(SceneElement.CULL_ALWAYS);
		spatial.setModelBound(new BoundingBox());
		spatial.updateModelBound();
		return spatial;
	}

}
