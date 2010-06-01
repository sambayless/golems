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
