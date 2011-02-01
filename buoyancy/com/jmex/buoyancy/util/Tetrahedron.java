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

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.TriangleBatch;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;

/**
 * A Tetrahedron TriMesh.
 * @author Sam Bayless
 *
 */
public class Tetrahedron extends TriMesh {
	private static final long serialVersionUID = 1L;
	private Vector3f[] vertices;
	private Vector3f center;
	
	/**
	 * Get the vertices that define this Tetrahedron. 
	 * Note: Adjusting these will not alter the TriMesh.
	 * @return
	 */
	public Vector3f[] getVertices() {
		return vertices;
	}

	/**
	 * Get the center of the Tetrahedron (in local coordinates).
	 * Note: Adjusting this will not alter the TriMesh.
	 * @return The center, in local coordinates, of the Tetrahedron
	 */
	public Vector3f getCenter() {
		return center;
	}

	/**
	 * Construct a Tetrahedron connecting the given vertices. 
	 * @param name The name of the scene element. This is required for identification and comparision purposes.
	 * @param vertex1 A vertex in the Tetrahedron.
	 * @param vertex2 A vertex in the Tetrahedron.
	 * @param vertex3 A vertex in the Tetrahedron.
	 * @param vertex4 A vertex in the Tetrahedron.
	 */
	public Tetrahedron(String name, Vector3f vertex1, Vector3f vertex2, Vector3f vertex3, Vector3f vertex4) {
		this(name, new Vector3f[]{vertex1,vertex2,vertex3,vertex4});
	}
	
	/**
	 * Construct a Tetrahedron connecting the given vertices.
	 * @param vertex1 A vertex in the Tetrahedron.
	 * @param vertex2 A vertex in the Tetrahedron.
	 * @param vertex3 A vertex in the Tetrahedron.
	 * @param vertex4 A vertex in the Tetrahedron.
	 */
	public Tetrahedron(Vector3f vertex1, Vector3f vertex2, Vector3f vertex3, Vector3f vertex4) 
	{
		this("temp",  new Vector3f[]{vertex1,vertex2,vertex3,vertex4},false);
	}
	
	/**
	 * Construct a Tetrahedron connecting the given vertices.
	 * @param vertex1 A vertex in the Tetrahedron.
	 * @param vertex2 A vertex in the Tetrahedron.
	 * @param vertex3 A vertex in the Tetrahedron.
	 * @param vertex4 A vertex in the Tetrahedron.
	 * @param centerVertices Whether or not to adjust the provided vertices so that the center of this TriMesh is at 0,0,0 (in local coordinates).
	 */
	public Tetrahedron(Vector3f vertex1, Vector3f vertex2, Vector3f vertex3, Vector3f vertex4, boolean centerVertices) 
	{
		this("temp",  new Vector3f[]{vertex1,vertex2,vertex3,vertex4},centerVertices);
	}
	
	public Tetrahedron(String name, Vector3f vertex1, Vector3f vertex2, Vector3f vertex3, Vector3f vertex4,boolean centerVertices) 
	{
		this("temp",  new Vector3f[]{vertex1,vertex2,vertex3,vertex4},centerVertices);
	}
	
	/**
	 * Construct a Tetrahedron connecting the given vertices. Note: vertices must contain exactly 4 elements.
	 * @param vertices A list of the 4 vertices of the Tetrahedron.
	 */
	public Tetrahedron (Vector3f[] vertices)
	{
		this ("temp",vertices,false);
	}
	
	/**
	 * Construct a Tetrahedron connecting the given vertices. Note: vertices must contain exactly 4 elements.
	 * @param vertices A list of the 4 vertices of the Tetrahedron.
	 * @param centerVertices Whether or not to adjust the provided vertices so that the center of this TriMesh is at 0,0,0 (in local coordinates).
	 */
	public Tetrahedron (Vector3f[] vertices, boolean centerVertices)
	{
		this ("temp",vertices,centerVertices);
	}
	
	/**
	 * 
	 * @param name The name of the scene element. This is required for identification and comparision purposes.
	 * @param vertices A list of the 4 vertices of the Tetrahedron.
	 * @param centerVertices Whether or not to adjust the provided vertices so that the center of this TriMesh is at 0,0,0 (in local coordinates).
	 */
	public Tetrahedron(String name, Vector3f[] vertices, boolean centerVertices)
	{
		super(name);
		this.vertices = vertices;
		if (centerVertices)
		{
			adjustToCenter();
			setVertexData();
			setNormalData();
			setTextureData();
			setIndexData();
		}else
		{
			calculateCenter();
			setVertexData();
			setNormalData();
			setTextureData();
			setIndexData();
		}
	}
	
	/**
	 * Construct a Tetrahedron connecting the given vertices. Note: vertices must contain exactly 4 elements.
	 * @param name The name of the scene element. This is required for identification and comparision purposes.
	 * @param vertices A list of the 4 vertices of the Tetrahedron.
	 */
	public Tetrahedron(String name, Vector3f[] vertices) {
		this(name,vertices, false);

	}
	
	private void adjustToCenter()
	{
		calculateCenter();//calculate the center point of these vertices
		for (Vector3f vertex:vertices)
			vertex.subtractLocal(center);//move the vertices so the center will be at 0
		
		center.zero();
	}
	
	private void calculateCenter()
	{
        center = new Vector3f();
        //average the 4 points to get the center
        for (Vector3f vertex:vertices)
        {
        	center.addLocal(vertex);
        }
        center.divideLocal(vertices.length);
	}
	
	/**
	 * 
	 * <code>setIndexData</code> sets the indices into the list of vertices,
	 * defining all triangles that constitute the tetrahedron.
	 *  
	 */
	private void setIndexData() {
		Vector3f[][] sides = new Vector3f[4][];
        sides[0] = new Vector3f[]{vertices[0], vertices[1],vertices[2]};
        sides[1] = new Vector3f[]{vertices[0], vertices[1],vertices[3]};
        sides[2] = new Vector3f[]{vertices[0], vertices[2],vertices[3]};
        sides[3] = new Vector3f[]{vertices[1], vertices[2],vertices[3]};
		
        IntBuffer indices = BufferUtils.createIntBuffer(12);
	    for (int i = 0; i <sides.length;i++)
	    {
	    	indices.put(i*3+2).put(i*3+1).put(i*3);
	    }

	    indices.rewind();
        TriangleBatch batch = getBatch(0);
        batch.setIndexBuffer(indices);
	}
	
	
	/**
	 * 
	 * <code>setTextureData</code> sets the points that define the texture of
	 * the box. It's a one-to-one ratio, where each plane of the tetrahedron has 
	 * a copy of the texture.
	 *  
	 */
	private void setTextureData() {
		Vector3f[][] sides = new Vector3f[4][];
        sides[0] = new Vector3f[]{vertices[0], vertices[1],vertices[2]};
        sides[1] = new Vector3f[]{vertices[0], vertices[1],vertices[3]};
        sides[2] = new Vector3f[]{vertices[0], vertices[2],vertices[3]};
        sides[3] = new Vector3f[]{vertices[1], vertices[2],vertices[3]};
        
        TriangleBatch batch = getBatch(0);
	    if (batch.getTextureBuffers().get(0) == null) {
		    batch.getTextureBuffers().set(0,BufferUtils.createVector2Buffer(16));
		    FloatBuffer tex = batch.getTextureBuffers().get(0);
	
			for (int i = 0; i < 4; i++) {
			
			    tex.put(1).put(0);
				tex.put(0).put(0);
				tex.put(0).put(1);
				tex.put(1).put(1);
			}
	    }
	}
	
	/**
	 * 
	 * <code>setNormalData</code> defines the normals of each face of the
	 * tetrahedron.
	 *  
	 */
	private void setNormalData() {
 
		
		FloatBuffer norms = BufferUtils.createVector3Buffer(12);
        
		//for each side, to get the normal of that side, average its vertices, subtract the center from that average, and normalize
		Vector3f[][] sides = new Vector3f[4][];
        sides[0] = new Vector3f[]{vertices[0], vertices[1],vertices[2]};
        sides[1] = new Vector3f[]{vertices[0], vertices[1],vertices[3]};
        sides[2] = new Vector3f[]{vertices[0], vertices[2],vertices[3]};
        sides[3] = new Vector3f[]{vertices[1], vertices[2],vertices[3]};
		
        for (int i = 0; i < sides.length;i++)
        {
        	Vector3f centerOfSide = new Vector3f();
        	for (Vector3f vertex:sides[i])
        	{
        		centerOfSide.addLocal(vertex);
        	}
        	centerOfSide.divideLocal(sides[i].length);
        	centerOfSide.subtractLocal(center);//this is now the normal of this side
        	centerOfSide.normalizeLocal();
        	
        	
        	for (int e = 0;e <sides[i].length;e++)
        	{//Put these normals into the buffer
        		norms.put(centerOfSide.x).put(centerOfSide.y).put(centerOfSide.z);
        	}
        }

        norms.rewind();
        TriangleBatch batch = getBatch(0);
        batch.setNormalBuffer(norms);
	}
	
	private void setVertexData() 
	{
        FloatBuffer verts = BufferUtils.createVector3Buffer(12);

		//side 1
	    verts.put(vertices[0].x).put(vertices[0].y).put(vertices[0].z);
	    verts.put(vertices[1].x).put(vertices[1].y).put(vertices[1].z);
	    verts.put(vertices[2].x).put(vertices[2].y).put(vertices[2].z);

		//side 2
	    verts.put(vertices[0].x).put(vertices[0].y).put(vertices[0].z);
	    verts.put(vertices[1].x).put(vertices[1].y).put(vertices[1].z);
	    verts.put(vertices[3].x).put(vertices[3].y).put(vertices[3].z);

		//side 3
	    verts.put(vertices[0].x).put(vertices[0].y).put(vertices[0].z);
	    verts.put(vertices[2].x).put(vertices[2].y).put(vertices[2].z);
	    verts.put(vertices[3].x).put(vertices[3].y).put(vertices[3].z);

		//side 4
	    verts.put(vertices[1].x).put(vertices[1].y).put(vertices[1].z);
	    verts.put(vertices[2].x).put(vertices[2].y).put(vertices[2].z);
	    verts.put(vertices[3].x).put(vertices[3].y).put(vertices[3].z);
	    
        verts.rewind();
        TriangleBatch batch = getBatch(0);
        batch.setVertexBuffer(verts); 
	}
	
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(vertices[0], "vertex0", Vector3f.ZERO);
        capsule.write(vertices[1], "vertex1", Vector3f.ZERO);
        capsule.write(vertices[2], "vertex2", Vector3f.ZERO);
        capsule.write(vertices[3], "vertex3", Vector3f.ZERO);
         capsule.write(center, "center", Vector3f.ZERO);

    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        vertices[0].set((Vector3f) capsule.readSavable("vertex0", Vector3f.ZERO.clone()));
        vertices[1].set((Vector3f) capsule.readSavable("vertex1", Vector3f.ZERO.clone()));
        vertices[2].set((Vector3f) capsule.readSavable("vertex2", Vector3f.ZERO.clone()));
        vertices[3].set((Vector3f) capsule.readSavable("vertex3", Vector3f.ZERO.clone()));

        center.set((Vector3f) capsule.readSavable("center", Vector3f.ZERO.clone()));
    }
	
}
