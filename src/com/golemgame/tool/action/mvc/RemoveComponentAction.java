package com.golemgame.tool.action.mvc;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.tool.action.Action;

public class RemoveComponentAction extends Action<RemoveComponentAction> {

	private PropertyStore component;
	
	public void setComponent(PropertyStore component) {
		this.component = component;
	}

	public RemoveComponentAction(PropertyStore component) {
		super();
		this.component = component;
	}

	public RemoveComponentAction() {
		super();
	
	}

	@Override
	public String getDescription() {
		return "Delete/Remove a component";
	}

	public PropertyStore getComponent() {
		return component;
	}

	@Override
	public com.golemgame.tool.action.Action.Type getType() {
		return Type.REMOVE_COMPONENT;
	}

	@Override
	public RemoveComponentAction copy() {
		return new RemoveComponentAction(new PropertyStore(component));
	}


	
}
