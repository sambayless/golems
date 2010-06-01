package com.golemgame.physical;


public interface CollisionRequirements {
	public boolean meetsRequirements(PhysicsComponent geom, float distance);
}
