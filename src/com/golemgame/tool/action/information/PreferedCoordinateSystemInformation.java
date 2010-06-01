package com.golemgame.tool.action.information;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

public abstract class PreferedCoordinateSystemInformation extends ActionInformation {

	@Override
	public com.golemgame.tool.action.Action.Type getType() {

		return COORDINATE_SYSTEM_INFO;
	}
	
	public abstract Vector3f getWorldZero();
	public abstract Quaternion getWorldRotation();

}
