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
