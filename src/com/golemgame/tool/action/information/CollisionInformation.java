package com.golemgame.tool.action.information;

import com.golemgame.structural.collision.CollisionMember;
import com.golemgame.tool.action.Action;


public abstract class CollisionInformation  extends ActionInformation{
	
	@Override
	public Type getType() {
		return Action.COLLISION_INFO;
	}
	public abstract CollisionMember getCollisionMember();
}
