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
package com.golemgame.model.quality.spatial;

import com.jme.renderer.Renderer;
import com.jme.scene.DistanceSwitchModel;
import com.jme.scene.SwitchNode;

/**
 * Allows a floor to be dynamically set on how low the index is allowed to go.
 * This is mainly intended to allow for in game quality settings.
 * @author Sam
 *
 */
public class FlooredDistanceSwitchModel extends DistanceSwitchModel {

	private static final int FRAME_SKIP = 3;
	private int floor = 0;
	
	public int getFloor() {
		return floor;
	}

	public void setFloor(int floor) {
		this.floor = floor;
	}

	@Override
	public int getSwitchChild() {
		int child = super.getSwitchChild();
		return Math.max(child, floor);
	}

	public FlooredDistanceSwitchModel() {
		super();
		
	}

	public FlooredDistanceSwitchModel(int numChildren) {
		super(numChildren);
		
	}
	
	private int count = FRAME_SKIP;
	@Override
	public void render(Renderer r, SwitchNode toSwitch) {
		if(count ++ >= FRAME_SKIP)
		{
			count = 0;
			super.render(r, toSwitch);
		}
		
	}
	
}
