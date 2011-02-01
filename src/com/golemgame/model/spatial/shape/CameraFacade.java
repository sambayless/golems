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
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Cylinder;

public class CameraFacade extends SpatialModelImpl{

	public CameraFacade() {
		super();
	}

	public CameraFacade(boolean registerSpatial) {
		super(registerSpatial);
	}

	private static final long serialVersionUID = 1L;
	
	protected Spatial buildSpatial() {
		Node cameraNode = new Node();
		
		Box main = new Box("Camera", new Vector3f(), 0.7f,0.3f,0.3f);
		
		cameraNode.attachChild(main);
		
		Cylinder front = new Cylinder("CameraFront", 20, 20, 0.2f,0.2f, true);
		front.getLocalTranslation().x += main.xExtent + front.getHeight()/2f  - 0.01f;
		cameraNode.attachChild(front);			
		front.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Y);
		
		
		Cylinder top1 =  new Cylinder("CameraFront", 20, 20, main.xExtent/2f,0.3f, true);
		top1.getLocalTranslation().x += main.xExtent/2f;
		top1.getLocalTranslation().y += main.yExtent;
		cameraNode.attachChild(top1);	
		
		Cylinder top2 =  new Cylinder("CameraFront", 20, 20, main.xExtent/2f,0.3f, true);
		top2.getLocalTranslation().x -= main.xExtent/2f;
		top2.getLocalTranslation().y += main.yExtent;
		cameraNode.attachChild(top2);	
	//	front.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Y);
		
		
		
		cameraNode.setModelBound(new BoundingBox());
		cameraNode.updateModelBound();
		cameraNode.setIsCollidable(false);
		return cameraNode;
	}
}
