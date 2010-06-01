package com.golemgame.tool.action.information;

import java.util.Collection;
import java.util.List;

import com.golemgame.structural.group.StructureGroup;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.Actionable;

/**
 * 
 * @author Sam
 *
 */
public abstract class GroupInformation extends ActionInformation {


	@Override
	public Type getType() {
		return Action.GET_GROUP;
	}
	

	/**
	 * This gets information about connecting items
	 * @param includeStatics
	 * @return
	 */
	public abstract List<Actionable> getGroupMembers(boolean includeStatics);
	
	public abstract Collection<StructureGroup> getAssignedGroups();
	
	public abstract StructureGroup getAssignedGroup();
}
