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
