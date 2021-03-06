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


import com.golemgame.tool.action.Action;
import com.jme.math.Vector3f;

public abstract class MoveRestrictedInformation extends ActionInformation {

	@Override
	public Type getType() {
		return Action.MOVE_RESTRICTED;
	}

	/**
	 * Get the closest restricted position to the supplied position. 
	 * @param from The position to be restricted. Implementation note: This Vector3f may be destroyed in the process.
	 * @return The closest restricted position
	 */
	public abstract Vector3f getRestrictedPosition(Vector3f from);
	
}
