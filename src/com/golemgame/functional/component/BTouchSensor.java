package com.golemgame.functional.component;


public class BTouchSensor extends BSource {

	public BTouchSensor(
			) {
		super();

	}

	@Override
	public void updateSource(float time) {
		
		state = collisionStatus;
		collisionStatus = 0;
		
	}
	private float collisionStatus =0;
	public void notifyOfCollision()
	{
		collisionStatus = 1;
	}

}
