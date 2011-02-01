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

import com.golemgame.mvc.golems.AxleInterpreter;
import com.golemgame.mvc.golems.validate.requirement.FloatBoundRequirement;

public class AxleValidator  extends PhysicalValidator {

	public AxleValidator() {
		super();
		super.requireData(AxleInterpreter.LEFT_JOINT_LENGTH, 1f);
		super.requireData(AxleInterpreter.RIGHT_JOINT_LENGTH, 0.5f);
		super.requireData(AxleInterpreter.LEFT_JOINT_RADIUS, 0.5f);
		super.requireData(AxleInterpreter.RIGHT_JOINT_RADIUS, 0.125f);
		
		super.addRequirement( new FloatBoundRequirement(MIN_EXTENT, Float.POSITIVE_INFINITY,AxleInterpreter.LEFT_JOINT_LENGTH));
		super.addRequirement( new FloatBoundRequirement(MIN_EXTENT, Float.POSITIVE_INFINITY,AxleInterpreter.RIGHT_JOINT_LENGTH));
		super.addRequirement( new FloatBoundRequirement(MIN_EXTENT, Float.POSITIVE_INFINITY,AxleInterpreter.LEFT_JOINT_RADIUS));
		super.addRequirement( new FloatBoundRequirement(MIN_EXTENT, Float.POSITIVE_INFINITY,AxleInterpreter.RIGHT_JOINT_RADIUS));
	}


}
