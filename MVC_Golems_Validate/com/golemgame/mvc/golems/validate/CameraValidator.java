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
package com.golemgame.mvc.golems.validate;

import com.golemgame.mvc.golems.CameraInterpreter;
import com.golemgame.mvc.golems.validate.requirement.FloatBoundRequirement;

public class CameraValidator extends PhysicalValidator {

	public CameraValidator() {
		super();
		super.requireData(CameraInterpreter.VIEW_THRESHOLD,  1f);
		super.addRequirement(new FloatBoundRequirement(-1f,1f,CameraInterpreter.VIEW_THRESHOLD));
	}


	
	
	
}
