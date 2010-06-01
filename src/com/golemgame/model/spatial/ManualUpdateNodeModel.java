package com.golemgame.model.spatial;

import com.jme.scene.Spatial;

public class ManualUpdateNodeModel extends NodeModel {

	private static final long serialVersionUID = 1L;


	@Override
	protected Spatial buildSpatial() {
		return new LockedUpdateNode();
	}

	
	public void manualUpdate(float time)
	{
		((LockedUpdateNode)super.getNode()).manualUpdate(time, true);
		
	}

}
