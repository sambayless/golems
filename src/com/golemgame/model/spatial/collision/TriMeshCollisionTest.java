package com.golemgame.model.spatial.collision;

import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.TriangleBatch;

/**
 * Collide a TriMesh against a model with efficient bounds, if possible.
 * @author Sam
 *
 */
public class TriMeshCollisionTest {
	
	private static Vector3f[] verts = new Vector3f[3];

	public static boolean hasCollision(TriMesh spatial1, TriMesh spatial2)
	{
		PointCollisionTest pointTest = CentroidCollisionResults.getCollisionTest(spatial1);
		TriMesh trimesh = spatial2;
		TriMesh testSpatial = spatial1;
		if (pointTest == null)
		{
			pointTest = CentroidCollisionResults.getCollisionTest(spatial2);
			trimesh = spatial1;
			testSpatial = spatial2;
		}
		
		if (pointTest == null)
				return ((TriMesh) spatial1).hasTriangleCollision((TriMesh)spatial2);
		
		//it MIGHT be possible for this to fail to see collisions in extreme situations
		//regardless... it doesnt seem to be any faster than doing it normally
	      TriangleBatch a;
	        for (int x = 0; x < trimesh.getBatchCount(); x++) {
	            a = trimesh.getBatch(x);
	            if (a == null || !a.isEnabled()) continue;
	            	
	    		for (int i = 0; i < a.getTriangleCount(); i++) {
	    			{    			
	    				a.getTriangle(i, verts);
	    				for (Vector3f vert:verts)
	    				{
	    					trimesh.localToWorld(vert, vert);
	    					if (pointTest.containsPoint(vert, testSpatial))
	    						return true;
	    				}
	    							
	    			}
	    		}
	        }
	        
	        return false;
	}
}
