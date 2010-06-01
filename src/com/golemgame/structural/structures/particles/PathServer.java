package com.golemgame.structural.structures.particles;

import com.jme.math.Vector3f;

/**
 * This class returns the next available position for a particular particle on a given path.
 * Each particle should have its own path server.
 * @author Sam
 *
 */
public class PathServer {
	private final ParticlePath path;
	private final Vector3f initialPosition = new Vector3f();
	public Vector3f getInitialPosition() {
		return initialPosition;
	}
	public PathServer(ParticlePath path) {
		super();
		this.path = path;
	}
	
	private int pos = 0;
	/**
	 * Return the next position in the particle's path
	 * @return
	 * @throws EndOfPathException when the path is closed and the last position is reached.
	 */
	public Vector3f getNextPosition(Vector3f store) throws EndOfPathException
	{
		Vector3f ret = path.getPathPosition(pos);
		if (path.getNumberOfPathPositions() > pos)
		{
			pos ++;
		}
		return store.set(ret).addLocal(initialPosition);
	}
	public ParticlePath getPath() {
		return path;
	}
	public void setInitialPosition(Vector3f setTo)
	{
		this.initialPosition.set(setTo);
	}
}
