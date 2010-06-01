package com.golemgame.tool.action.mvc;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.tool.action.Action;

public class AddWireAction extends Action<AddComponentAction> {

	private PropertyStore component;
	
	public void setComponent(PropertyStore component) {
		this.component = component;
	}

	public AddWireAction(PropertyStore component) {
		super();
		this.component = component;
	}

	public AddWireAction() {
		super();
	
	}

	@Override
	public String getDescription() {
		return "Create/Add a component";
	}

	public PropertyStore getComponent() {
		return component;
	}

	@Override
	public com.golemgame.tool.action.Action.Type getType() {
		return Type.ADD_WIRE;
	}

	@Override
	public AddComponentAction copy() {
		return new AddComponentAction(new PropertyStore(component));
	}


	
}
