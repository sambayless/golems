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
package com.golemgame.model.spatial;

import com.golemgame.util.OrientingNode;
import com.jme.scene.Spatial;

public class OrientingNodeModel extends NodeModel {


	private static final long serialVersionUID = 1L;
	private OrientingNode orientingNode;
	
	@Override
	protected Spatial buildSpatial() {
		orientingNode = new OrientingNode();
		return orientingNode;
	}
	public void lockRollPitchYaw(boolean roll, boolean pitch, boolean yaw)
	{
		this.orientingNode.setAllowPitch(!pitch);
		this.orientingNode.setAllowRoll(!roll);
		this.orientingNode.setAllowYaw(!yaw);
	}
	public void setOrientationLocked(boolean lock)
	{
		this.orientingNode.setAllLocked(lock);
	}
}
