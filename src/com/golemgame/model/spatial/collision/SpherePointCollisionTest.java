package com.golemgame.model.spatial.collision;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Sphere;

public class SpherePointCollisionTest extends PointCollisionTest {

	private static final Vector3f _storeVector = new Vector3f();
	
	public boolean containsPoint(Vector3f point,Spatial toTest) {
		//assume that the spatial is a sphere
		Sphere sphere = (Sphere) toTest;
		
		//if, as is common, the sphere has all of its scales the same, then we can do a simpler test
		if( (FastMath.abs (sphere.getWorldScale().x - sphere.getWorldScale().y)< FastMath.FLT_EPSILON) &&  (FastMath.abs (sphere.getWorldScale().x - sphere.getWorldScale().z)< FastMath.FLT_EPSILON))
		{
			//only need to translate the point over and adjust for scale(dont care about rotation)
			Vector3f testPoint = point.subtract(toTest.getWorldTranslation(),_storeVector);			
			testPoint.divideLocal(sphere.getWorldScale());				
			
			return sphereContainsPoint(testPoint,sphere.radius);
		}else
		{//in this case, it is neccesary to transform the point into the local coordinate system
			//tranform the point into the spheres coordinate system
			//and then divide its position by the sphere's world scale.
			  Vector3f testPoint = PointCollisionTest.transformPoint(point, toTest);
			
			return sphereContainsPoint(testPoint,sphere.radius);
		}

		
	
	}
	/**
	 *  The point must be transformed into local coordinates first (using PointCollisionTest.transformPoints)
	 * @param testPoint
	 * @param radius
	 * @return
	 */
	public static boolean sphereContainsPoint(Vector3f testPoint, float radius)
	{
		return testPoint.lengthSquared() <= radius*radius;
	}

}
