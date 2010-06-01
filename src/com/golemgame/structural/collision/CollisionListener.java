package com.golemgame.structural.collision;

import java.io.Serializable;



public interface CollisionListener extends Serializable{

	/**
	 * To be called any time the collision member's group changes.
	 * @param oldGroup
	 * @param newGroup
	 */
	//public void updateGroup(CollisionGroup oldGroup, CollisionGroup newGroup);

	public void notifyDelete();
	
	public void notifyUndelete();

}

/*
There will now be two separate collision systems: one for constructor groups, and one for physical groups
They will be completely separate, and just happen to be called at the same times.
For example: floors will only implement the physical collision system, while functional and structural components will be in their own separate subsystems of the grouping system
*/