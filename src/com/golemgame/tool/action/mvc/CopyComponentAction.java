package com.golemgame.tool.action.mvc;

import java.util.ArrayList;
import java.util.Collection;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.tool.action.Action;

public abstract class CopyComponentAction extends Action<CopyComponentAction> {
	private Collection< PropertyStore> components = new ArrayList<PropertyStore>();
	
	public void addComponent(PropertyStore component) {
		components.add(component);
	}

	public CopyComponentAction() {
		super();
		
	}



	@Override
	public String getDescription() {
		return "Copy a component";
	}

	public Collection<PropertyStore> getComponents() {
		return components;
	}
	
	public abstract Collection<PropertyStore> getCopiedComponents();
		
	
	@Override
	public com.golemgame.tool.action.Action.Type getType() {
		return Type.COPY_COMPONENT;
	}

	


	
}
