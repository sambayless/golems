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
import com.jme.scene.shape.Box;

public class BoxPointCollisionTest extends PointCollisionTest {


	
	public boolean containsPoint(Vector3f point,Spatial toTest) {
		//assume that the spatial is a box
		Box box = (Box) toTest;
		
		//tranform the point into the box's coordinate system
		
        Vector3f testPoint = PointCollisionTest.transformPoint(point, toTest);
		
		if (FastMath.abs(testPoint.x)<=  box.xExtent )
			if (FastMath.abs(testPoint.y)<=  box.yExtent )
				if (FastMath.abs(testPoint.z)<=  box.zExtent )
					return true;
		
		return false;
	}

}
