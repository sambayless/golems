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
package com.golemgame.structural.collision;

import java.io.Serializable;



public interface CollisionListener extends Serializable{

	/**
	 * To be called any time the collision member's group changes.
	 * @param oldGroup
	 * @param newGroup
	 */
	//public void updateGroup(CollisionGroup oldGroup, CollisionGroup newGroup);

	public void notifyDelete();
	
	public void notifyUndelete();

}

/*
There will now be two separate collision systems: one for constructor groups, and one for physical groups
They will be completely separate, and just happen to be called at the same times.
For example: floors will only implement the physical collision system, while functional and structural components will be in their own separate subsystems of the grouping system
*/
