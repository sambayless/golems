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
package com.golemgame.model.spatial.collision;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Cylinder;

public class CylinderPointCollisionTest extends PointCollisionTest {


	
	public boolean containsPoint(Vector3f point, Spatial toTest) {
		//assume that the spatial is a cylinder
		Cylinder cyl = (Cylinder) toTest;
		
		//tranform the point into the box's coordinate system
		
		Vector3f testPoint = PointCollisionTest.transformPoint(point, toTest);
	
		return cylinderContainsPoint(testPoint, cyl.getRadius(),cyl.getHeight());
	}
	
	/**
	 * The point must be transformed into local coordinates first (using PointCollisionTest.transformPoints)
	 * @param point
	 * @param radius
	 * @param height
	 * @return
	 */
	public static boolean cylinderContainsPoint(Vector3f testPoint, float radius, float height)
	{

		if (FastMath.abs(testPoint.z) > height/2f)
			return false;
			
		if ((testPoint.y * testPoint.y) + (testPoint.x * testPoint.x)> radius*radius)
			return false;
		
		
		
		return true;
	}

}
