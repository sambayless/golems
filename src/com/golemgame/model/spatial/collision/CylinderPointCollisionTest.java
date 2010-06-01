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
