/*******************************************************************************
 * Copyright 2008, 2009, 2010 Sam Bayless.
 * 
 *     This file is part of Golems.
 * 
 *     Golems is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     Golems is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with Golems. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
