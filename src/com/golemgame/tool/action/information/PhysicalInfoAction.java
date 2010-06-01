package com.golemgame.tool.action.information;

import com.golemgame.structural.structures.PhysicalStructure;
import com.golemgame.tool.action.Action;


public abstract class PhysicalInfoAction extends ActionInformation {

	@Override
	public com.golemgame.tool.action.Action.Type getType() {
		return Action.PHYSICAL_INFO;
	}	
	
	public abstract PhysicalStructure getPhysicalStructure();
	
}
