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

import com.jme.math.Vector3f;
import com.jme.scene.Spatial;

public abstract class PointCollisionTest {
	
	private static final Vector3f _storeVector = new Vector3f();
	
	/**
	 * Test if the given point is inside (or on the boundary) of this spatial.
	 * This test may assume that the spatial is a certain class (specified by subclasses),
	 * and behavior is unspecified if that condition is violated.
	 * @param point A position, in world coordinates.
	 * @return True if the specified point is inside (or on the boundary) of this spatial.
	 */
	public abstract boolean containsPoint(Vector3f point, Spatial toTest);
	
	/**
	 * Transform the given point into a new coordinate system. Store the result in a static vector.
	 * This transforms the translation, scale, AND rotation of the point.
	 * @param point
	 * @param coordinateSystem
	 * @return
	 */
	public static Vector3f transformPoint(Vector3f point, Spatial coordinateSystem)
	{
        Vector3f testPoint =  point.subtract( coordinateSystem.getWorldTranslation(), _storeVector);//.divideLocal(getWorldScale());
        coordinateSystem.getWorldRotation().inverse().mult(testPoint, testPoint);
		testPoint.divideLocal( coordinateSystem.getWorldScale());	
		return testPoint;
	}
}
