package com.golemgame.model.spatial.collision;

import java.util.HashMap;
import java.util.Map;

import com.jme.intersection.CollisionData;
import com.jme.intersection.TriangleCollisionResults;
import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Capsule;
import com.jme.scene.shape.Cone;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.shape.Pyramid;
import com.jme.scene.shape.Sphere;

/**
 * Center Collision Results extends Triangle Collision Results by first testing if the center
 * of the shape to intersect (as defined by its bounding box) is inside of this geometry.
 * This test must be defined for each type of geometry to use with this test.
 * If the test fails, the resolution continues with triangle collision.
 * @author Sam
 *
 */
public class CentroidCollisionResults extends TriangleCollisionResults {

	private static final Map<Class<? extends Spatial>, PointCollisionTest> testMap = new HashMap<Class<? extends Spatial>, PointCollisionTest>();
	private static Vector3f _storeCenter = new Vector3f();
	@Override
	public void addCollision(Geometry s, Geometry t) {
		
		if(geoemtriesCollide(s,t))
		{
			 CollisionData data = new CollisionData(s, t);
		     addCollisionData(data);
			return;
		}
		
		//fall back on trimesh intersection.
		super.addCollision(s, t);
	
	}

	
	public static void addCollisionTest(Class<? extends Spatial> spatialClass, PointCollisionTest test)
	{
		testMap.put(spatialClass, test);
	}
	
	public static PointCollisionTest getCollisionTest(Spatial forSpatial)
	{
		return testMap.get(forSpatial.getClass());
	}
	
	public static boolean geoemtriesCollide(Geometry s, Geometry t)
	{
		if(!(isConvex(s) && isConvex(t)))
			return false;
		if (t.getWorldBound() != null)//since you are testing the bound's center against the center test, make sure that bound exists.
		{
			PointCollisionTest sourceTest =  testMap.get(s.getClass());
			if (sourceTest != null)
			{
				 Vector3f targetCentroid = t.getWorldBound().getCenter(_storeCenter);
				 if (sourceTest.containsPoint(targetCentroid,s))
				 {			
				     return true;
				 }
			}
		}
		
		if (s.getWorldBound() != null)//since you are testing the bound's center against the center test, make sure that bound exists.
		{
			PointCollisionTest targetTest =  testMap.get(t.getClass());
			if (targetTest != null)
			{
				 Vector3f sourceCentroid = s.getWorldBound().getCenter(_storeCenter);
				 if (targetTest.containsPoint(sourceCentroid,t))
				 {			
				     return true;
				 }
			}
		}
		return false;
	}
	
	private static boolean isConvex(Geometry s) {
		return testMap.containsKey(s.getClass());
	}

	static{
		testMap.put(Box.class, new BoxPointCollisionTest());
		testMap.put(Sphere.class, new SpherePointCollisionTest());
		testMap.put(Cylinder.class, new CylinderPointCollisionTest());
		testMap.put(Capsule.class, new CapsulePointCollisionTest());
		testMap.put(Cone.class, new ConePointCollisionTest());
		testMap.put(Pyramid.class, new PyramidPointCollisionTest());
	}
	
}
