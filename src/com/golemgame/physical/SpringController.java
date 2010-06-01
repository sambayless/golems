package com.golemgame.physical;

import com.jme.math.Vector3f;

public interface SpringController {
	public void setPosition(Vector3f position);
	public Vector3f getPosition(Vector3f store);
	public void setPosition(float x, float y);
	public void setTarget(PhysicsObject object);
}
