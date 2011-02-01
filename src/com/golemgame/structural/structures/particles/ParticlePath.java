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
package com.golemgame.structural.structures.particles;

import java.util.ArrayList;

import com.jme.math.Vector3f;

/**
 * A set of path points for particles to follow.
 * @author Sam
 *
 */
public class ParticlePath implements Comparable<ParticlePath>{
	

	
	private final int maximumFollowers;
	
	private int followers = 0;
	
	public int getFollowers() {
		return followers;
	}

	/**
	 * This is the initial launching direction of the path.
	 * You can use this vector to manipulate the path so that it can be used for nearby launching directions.
	 */
	private final Vector3f initialDirection;
	
	private float age = 0;
	
	private boolean closed = false;
	
	public Vector3f getInitialDirection() {
		return initialDirection;
	}

	/**
	 * 
	 * @param initialDirection
	 * @param maximumFollowers < 0 for unlimited.
	 */
	public ParticlePath(Vector3f initialDirection, int maximumFollowers) {
		super();
		
		this.initialDirection = initialDirection;
		this.maximumFollowers = maximumFollowers;
	}
	
	public int getMaximumFollowers() {
		return maximumFollowers;
	}

	/**
	 * Close the path - indicating that no more positions will be added to it.
	 */
	public void closePath()
	{
		closed = true;
	}

	public boolean isClosed() {
		return closed;
	}

	private ArrayList<Vector3f> path = new ArrayList<Vector3f>(600);
	
	/**
	 * Add the given vector to the end of the path.
	 * @param position
	 */
	public void addToPath(Vector3f position)
	{
		path.add(position);
	}
	
	/**
	 * Return a path server that can keep track of and return the next path for the particle (if that path exists); 
	 * @return
	 */
	public PathServer getPathServer()
	{
		return new PathServer(this);
	}
	
	/**
	 * Return the ith path position if it exists, or Vector3f.ZERO if it doesnt.
	 * @param pathNum
	 * @return
	 */
	public Vector3f getPathPosition(int i)
	{
		if (i<path.size())
			return path.get(i);
		return Vector3f.ZERO;
	}
	
	public int getNumberOfPathPositions()
	{
		return path.size();
	}

	public int compareTo(ParticlePath o) {
		
		return (int)Math.signum(this.age - o.age);
	}
	
	public float getAge()
	{
		return  age;
	}
	
	public void update(float tpf)
	{
		this.age+= tpf;
	}
	
	public void addFollower()
	{
		this.followers++;
	}
	public boolean isUnfilled()
	{
		return this.followers<this.maximumFollowers;
	}

	public void removeFollower() {
		this.followers--;
	}
}
