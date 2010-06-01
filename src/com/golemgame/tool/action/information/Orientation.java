package com.golemgame.tool.action.information;


import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

public class Orientation {

	private Vector3f direction;
	private Vector3f horizontal;
	private Vector3f vertical;


	public Vector3f getDirection() {
		return direction;
	}


	public Vector3f getHorizontal() {
		return horizontal;
	}


	public Vector3f getVertical() {
		return vertical;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
		+ ((direction == null) ? 0 : direction.hashCode());
		result = prime * result
		+ ((horizontal == null) ? 0 : horizontal.hashCode());
		result = prime * result
		+ ((vertical == null) ? 0 : vertical.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Orientation other = (Orientation) obj;
		if (direction == null) {
			if (other.direction != null)
				return false;
		} else if (!direction.equals(other.direction))
			return false;
		if (horizontal == null) {
			if (other.horizontal != null)
				return false;
		} else if (!horizontal.equals(other.horizontal))
			return false;
		if (vertical == null) {
			if (other.vertical != null)
				return false;
		} else if (!vertical.equals(other.vertical))
			return false;
		return true;
	}


	public Orientation(Vector3f direction, Vector3f horizontal)
	{
		this.direction = direction;
		this.horizontal = horizontal;
		this.vertical = direction.cross(horizontal);
	}

	public Orientation(Orientation toCopy)
	{
		this.direction = toCopy.direction.clone();
		this.horizontal= toCopy.horizontal.clone();
		this.vertical = toCopy.vertical.clone();
	}
	
	public void set(Orientation toCopy)
	{
		this.direction = toCopy.direction.clone();
		this.horizontal= toCopy.horizontal.clone();
		this.vertical = toCopy.vertical.clone();
	}
	
	public Orientation mult(Quaternion toMult)
	{
		return new Orientation(toMult.mult(direction),toMult.mult(horizontal));
	}
}