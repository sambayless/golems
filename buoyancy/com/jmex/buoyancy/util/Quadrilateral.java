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
package com.jmex.buoyancy.util;

import java.nio.FloatBuffer;

import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.TriangleBatch;
import com.jme.util.geom.BufferUtils;

public class Quadrilateral extends TriMesh {

	private static final long serialVersionUID = 1L;

	public Quadrilateral(String name,Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3) {
		super(name);
	//	float max = 1f;
		
	//	if ((p0.distance(p1) > max) || (p0.distance(p2) > max) || (p0.distance(p3) > max)  )
	//		System.out.println(p0.distance(p1) + "\t" + p0.distance(p2) + "\t" + p0.distance(p3));
		init(p0,p1,p2,p3);
	}



	/**
	 * 
	 * <code>initialize</code> builds the data for the <code>Quad</code>
	 * object.
	 * 
	 * 
	 * @param width
	 *            the width of the <code>Quad</code>.
	 * @param height
	 *            the height of the <code>Quad</code>.
	 */
	public void init(Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3){
        TriangleBatch batch = getBatch(0);
		batch.setVertexCount(4);
		batch.setVertexBuffer(BufferUtils.createVector3Buffer(batch.getVertexCount()));
		batch.setNormalBuffer(BufferUtils.createVector3Buffer(batch.getVertexCount()));
        FloatBuffer tbuf = BufferUtils.createVector2Buffer(batch.getVertexCount());
        setTextureBuffer(0,tbuf);
	    batch.setTriangleQuantity(2);
	    batch.setIndexBuffer(BufferUtils.createIntBuffer(batch.getTriangleCount() * 3));

		batch.getVertexBuffer().put(p0.x).put(p0.y).put(p0.z);
		batch.getVertexBuffer().put(p1.x).put(p1.y).put(p1.z);
		batch.getVertexBuffer().put(p2.x).put(p2.y).put(p2.z);
		batch.getVertexBuffer().put(p3.x).put(p3.y).put(p3.z);

		batch.getNormalBuffer().put(0).put(0).put(1);
		batch.getNormalBuffer().put(0).put(0).put(1);
		batch.getNormalBuffer().put(0).put(0).put(1);
		batch.getNormalBuffer().put(0).put(0).put(1);

        
		tbuf.put(0).put(1);
        tbuf.put(0).put(0);
        tbuf.put(1).put(0);
        tbuf.put(1).put(1);

	    batch.getIndexBuffer().put(0);
	    batch.getIndexBuffer().put(1);
	    batch.getIndexBuffer().put(2);
	    batch.getIndexBuffer().put(0);
	    batch.getIndexBuffer().put(2);
	    batch.getIndexBuffer().put(3);
	}

	/**
	 * <code>getCenter</code> returns the center of the <code>Quad</code>.
	 * 
	 * @return Vector3f the center of the <code>Quad</code>.
	 */
	public Vector3f getCenter() {
		return worldTranslation;
	}
}
