package com.golemgame.model.spatial;

import com.golemgame.util.OrientingNode;
import com.jme.scene.Spatial;

public class OrientingNodeModel extends NodeModel {


	private static final long serialVersionUID = 1L;
	private OrientingNode orientingNode;
	
	@Override
	protected Spatial buildSpatial() {
		orientingNode = new OrientingNode();
		return orientingNode;
	}
	public void lockRollPitchYaw(boolean roll, boolean pitch, boolean yaw)
	{
		this.orientingNode.setAllowPitch(!pitch);
		this.orientingNode.setAllowRoll(!roll);
		this.orientingNode.setAllowYaw(!yaw);
	}
	public void setOrientationLocked(boolean lock)
	{
		this.orientingNode.setAllLocked(lock);
	}
}
