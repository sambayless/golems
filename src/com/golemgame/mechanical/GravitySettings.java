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
package com.golemgame.mechanical;

import java.io.Serializable;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SustainedView;
import com.golemgame.mvc.golems.machineSettings.MachineSpaceSettingsInterpreter;
import com.jme.math.Vector3f;

public class GravitySettings  implements SustainedView, Serializable{

	private static final long serialVersionUID = 1L;
	private MachineSpaceSettingsInterpreter interpreter;

	
	public GravitySettings() {
		super();
	}
	
	public Vector3f getGravity()
	{
		return getDirectionOfGravity().mult(getMagnitude());
	}
	
	public Vector3f getDirectionOfGravity() {
		return interpreter.getGravityDirection();
	}
	public void setDirectionOfGravity(Vector3f directionOfGravity) {
	
		interpreter.getGravityDirection().set(directionOfGravity);
	

	}
	public float getMagnitude() {
		return interpreter.getGravityMagnitude();
	}
	public void setMagnitude(float magnitude) {
		
		interpreter.setGravityMagnitude(magnitude);

	}

	public void setStore(PropertyStore store) {
		interpreter = new  MachineSpaceSettingsInterpreter(store);
		
	}

	public void invertView(PropertyStore store) {

		
	}
	
	public void remove() {

	}
	public void refresh() {

	}

	public PropertyStore getStore() {
		return interpreter.getStore();
	}
	
	

	
	
}
