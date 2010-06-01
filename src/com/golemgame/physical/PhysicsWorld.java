package com.golemgame.physical;

import java.util.Collection;

import com.golemgame.model.spatial.SpatialModel;

public interface PhysicsWorld {
	public Collection<PhysicsObject> getPhysicsObjects();
	public SpatialModel getSpatial();
	public PhysicsComponent getComponent(SpatialModel spatial);
	public void clear();
}
