package com.golemgame.tool.action.information;

import com.golemgame.model.Model;

public abstract class ModelInformation extends ActionInformation {

	@Override
	public Type getType() {
		
		return Type.MODEL;
	}
	
	public abstract Model getCollisionModel();

}
