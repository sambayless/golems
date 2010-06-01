package com.golemgame.tool.action.information;

import com.golemgame.tool.action.Action;

public abstract class StaticMaterialInformation extends ActionInformation {
	
	@Override
	public Type getType() {
		return Action.STATIC_INFO;
	}

	public abstract boolean isStatic();
	
}
