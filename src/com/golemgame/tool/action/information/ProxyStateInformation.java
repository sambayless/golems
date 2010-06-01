package com.golemgame.tool.action.information;

import com.golemgame.mvc.PropertyState;

public abstract class ProxyStateInformation extends ActionInformation {

	@Override
	public String getDescription() {
		return "Property State Change";
	}

	@Override
	public com.golemgame.tool.action.Action.Type getType() {
		return Type.PROXY_PROPERTY_STATE;
	}

	public abstract PropertyState getCurrentState();

	
}
