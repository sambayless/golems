package com.golemgame.tool.action.mvc;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.tool.action.Action;

public class MergeComponentAction extends Action<MergeComponentAction> {

	private PropertyStore component;
	
	public void setComponent(PropertyStore component) {
		this.component = component;
	}

	public MergeComponentAction(PropertyStore component) {
		super();
		this.component = component;
	}

	public MergeComponentAction() {
		super();
	
	}

	@Override
	public String getDescription() {
		return "Merge a component";
	}

	public PropertyStore getComponent() {
		return component;
	}

	@Override
	public com.golemgame.tool.action.Action.Type getType() {
		return Type.MERGE_COMPONENT;
	}

	@Override
	public MergeComponentAction copy() {
		return new MergeComponentAction(new PropertyStore(component));
	}


	
}
