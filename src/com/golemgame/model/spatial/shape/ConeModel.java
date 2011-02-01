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

import com.jme.bounding.BoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Cone;

public class ConeModel extends CylinderModel {
	private static final long serialVersionUID = 1L;
	public static final float INIT_SIZE = 1f;
	
	public ConeModel() {
		super();
	}

	public ConeModel(boolean registerSpatial) {
		super(registerSpatial);
	}

	protected Spatial buildSpatial() {
	//	Node node = new Node();
		Spatial	spatial = new Cone("cone",4,8, INIT_SIZE/2,INIT_SIZE);// new Box("built", new Vector3f(), INIT_SIZE/2,INIT_SIZE/2,INIT_SIZE/2);		 
		spatial.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, new Vector3f(1,0,0));
		//Spatial spatial= new Pyramid("pyramid", INIT_SIZE, INIT_SIZE);// new Box("box", new Vector3f(), INIT_SIZE/2f,INIT_SIZE/2f,INIT_SIZE/2f);		 
		spatial.setCullMode(SceneElement.CULL_ALWAYS);
		spatial.getLocalTranslation().zero();
		//spatial.getLocalRotation().loadIdentity();
		spatial.getLocalScale().set(1,1,1);
		spatial.setModelBound(new BoundingBox());
		spatial.updateModelBound();
		return spatial;
	}


}
