package com.golemgame.tool.action.information;


import com.golemgame.model.Model;
import com.golemgame.tool.action.Action;
import com.jme.math.Vector3f;

public abstract class FocusInformation extends ActionInformation {

	
	@Override
	public Type getType() {
		return Action.FOCUS;
	}


	public abstract Vector3f getCenterVector();

	public abstract Model getCenterModel();
}
