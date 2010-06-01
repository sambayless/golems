package com.golemgame.physical;

import java.util.Collection;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

public interface PhysicsObject {
	public Collection<PhysicsComponent> getComponents();
	public Vector3f getLocalTranslation(Vector3f store);
	public Quaternion getLocalRotation(Quaternion store);

	public boolean isStatic();
	public Vector3f getWorldTranslation(Vector3f positionStore);
}
