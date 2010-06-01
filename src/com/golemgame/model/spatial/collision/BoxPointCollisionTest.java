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
