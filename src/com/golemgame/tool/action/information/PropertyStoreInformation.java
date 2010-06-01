package com.golemgame.tool.action.information;

import com.golemgame.mvc.PropertyStore;

public abstract class PropertyStoreInformation extends ActionInformation {

	@Override
	public com.golemgame.tool.action.Action.Type getType() {
		
		return Type.PROPERTY_STORE;
	}

	public abstract PropertyStore getStore();
	
}
