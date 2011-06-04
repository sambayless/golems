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
package com.golemgame.tool.control;

import java.io.Serializable;

import com.golemgame.model.Model;
import com.golemgame.mvc.PropertyState;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.SpatialInterpreter;
import com.jme.math.Vector3f;

public interface ControllableModel extends Serializable{
/*	*//**
	 * Notify the model that a period of adjustments has been completed, and the model should update itself, 
	 * and perform any collision checking, as needed.
	 */
	public void updateModel();
	
	public Model getParent();
	
	public Vector3f getWorldFromRelative(Vector3f localTranslation, Vector3f store);
	public Vector3f getRelativeFromWorld(Vector3f worldTranslation, Vector3f store);
	public void updateWorldData();
	public SpatialInterpreter getInterpreter();
	
	public PropertyStore getPropertyStore();
	
	public PropertyState getCurrentState();
}
