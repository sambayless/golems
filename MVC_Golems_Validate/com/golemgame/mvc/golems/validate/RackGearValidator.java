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

import com.golemgame.mvc.golems.GearInterpreter;
import com.jme.math.FastMath;

public class RackGearValidator extends BoxValidator {

	public RackGearValidator() {
		super();
		super.requireData(GearInterpreter.TOOTH_NUMBER,6);
		super.requireData(GearInterpreter.TOOTH_ANGLE,FastMath.HALF_PI/3f);
		super.requireData(GearInterpreter.TOOTH_HEIGHT, 0.1f);
		super.requireData(GearInterpreter.TOOTH_WIDTH, 0.1f);

	}

}
