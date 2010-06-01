package com.golemgame.model.spatial;

import com.jme.scene.Node;

public class LockedUpdateNode extends Node{

	private static final long serialVersionUID = 1L;

	@Override
	public void updateGeometricState(float time, boolean initiator) {
	
	}
	
	public void manualUpdate(float time, boolean initiator)
	{
		super.updateGeometricState(time, initiator);
	}
}
