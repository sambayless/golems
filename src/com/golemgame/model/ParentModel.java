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
package com.golemgame.model;

import java.util.Collection;

import com.jme.math.Vector3f;

public interface ParentModel extends Model{
	
	public void addChild(Model child) throws ModelTypeException;
	public void detachChild(Model child) throws ModelTypeException;
	public void detachAllChildren();
	/**
	 * Centers this node on the coordinates provided, maintaining the world coordinates of all children
	 * @param center Coordinate to center on (in world coordinates)
	 */
	public void recenter(Vector3f centerOn);
	public Collection<Model> getChildren() ;
	
	public boolean isUpdateLocked();
	public void setUpdateLocked(boolean updateLocked);
}
