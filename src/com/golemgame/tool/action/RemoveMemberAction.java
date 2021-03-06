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
package com.golemgame.tool.action;

import com.golemgame.mvc.Reference;

public class RemoveMemberAction extends Action<RemoveMemberAction> {
	public Reference member;
	
	public Reference getMember() {
		return member;
	}

	public void setMember(Reference member) {
		this.member = member;
	}

	@Override
	public String getDescription() {
		return "Remove Member";
	}

	@Override
	public com.golemgame.tool.action.Action.Type getType() {
		return Action.REMOVE_MEMBER;
	}
}
