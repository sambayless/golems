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

public class PyramidPointCollisionTest extends PointCollisionTest {


	
	/**
	 * Note: this class assumes that the pyramid was created with a 1 by 1 base, and height 1. 
	 * (Its scale, however, can be anything).
	 */
	public boolean containsPoint(Vector3f point,Spatial toTest) {
		//assume that the spatial is a cone
		//Pyramid pyramid = (Pyramid) toTest;
		
		//tranform the point into the cone's coordinate system
		
        Vector3f testPoint = PointCollisionTest.transformPoint(point, toTest);
	
        //update this to give the correct width and height if the pyramid class is updated to store that information.
        
        return pyramidContainsPoint(testPoint,1,1);     
        
		
	}
	
	public static boolean pyramidContainsPoint(Vector3f testPoint, float width, float height)
	{
        if (FastMath.abs(testPoint.y) > height/2f)
        	return false;
        
        //the radius at height h relative to the pyramid is (baseRadius/totalHeight) * height;
        float pointHeight = height/2f  -  testPoint.y ;//0,0,0, is dead center in the pyramid - which is half way up.
        
        float localWidth = width/height * pointHeight /2f;
        return (Math.abs(testPoint.x) <= localWidth && Math.abs(testPoint.z) <= localWidth);
        
	}
}
