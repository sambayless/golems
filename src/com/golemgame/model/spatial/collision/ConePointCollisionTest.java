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
import com.jme.scene.shape.Cone;

public class ConePointCollisionTest extends PointCollisionTest {


	
	public boolean containsPoint(Vector3f point,Spatial toTest) {
		//assume that the spatial is a cone
		Cone cone = (Cone) toTest;
		
		//tranform the point into the cone's coordinate system
		
        Vector3f testPoint = PointCollisionTest.transformPoint(point, toTest);
	
        return coneContainsPoint(testPoint, cone.getRadius(),cone.getHeight());
        
        
		
	}
	
	public static boolean coneContainsPoint(Vector3f testPoint, float radius, float height)
	{
        if (FastMath.abs(testPoint.z) > height/2f)
        	return false;
        
        //the radius at height h relative to the cone is (baseRadius/totalHeight) * height;
        float pointHeight = testPoint.z +height/2f;//0,0,0, is dead center in the cone - which is half way up.
        
        float localRadius = radius /height * pointHeight;
        return testPoint.x * testPoint.x + testPoint.y * testPoint.y <= localRadius*localRadius;
	}

}
