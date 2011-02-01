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

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.FloatType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.HingeInterpreter;
import com.golemgame.mvc.golems.validate.requirement.DataTypeRequirement;
import com.golemgame.mvc.golems.validate.requirement.FloatBoundRequirement;
import com.golemgame.mvc.golems.validate.requirement.Requirement;
import com.jme.math.FastMath;

public class HingeValidator  extends PhysicalValidator {

	public HingeValidator() {
		super();
		super.requireData(HingeInterpreter.LEFT_JOINT_LENGTH, 1f);
		super.requireData(HingeInterpreter.RIGHT_JOINT_LENGTH, 1f);
		super.requireData(HingeInterpreter.LEFT_JOINT_ANGLE, 0f);
		super.requireData(HingeInterpreter.RIGHT_JOINT_ANGLE, 0f);
		
		
		super.addRequirement( new FloatBoundRequirement(MIN_EXTENT, Float.POSITIVE_INFINITY,HingeInterpreter.LEFT_JOINT_LENGTH));
		super.addRequirement( new FloatBoundRequirement(MIN_EXTENT, Float.POSITIVE_INFINITY,HingeInterpreter.RIGHT_JOINT_LENGTH));
		super.addRequirement( new DataTypeRequirement(DataType.Type.FLOAT, new FloatType(), HingeInterpreter.LEFT_JOINT_ANGLE));
		super.addRequirement( new DataTypeRequirement(DataType.Type.FLOAT, new FloatType(), HingeInterpreter.RIGHT_JOINT_ANGLE));
		super.addRequirement(new Requirement(){

			public void enfore(PropertyStore store) {		
				float leftAngle = FastMath.PI - store.getFloat(HingeInterpreter.LEFT_JOINT_ANGLE);
				float rightAngle = store.getFloat(HingeInterpreter.RIGHT_JOINT_ANGLE);
				if(Math.abs((leftAngle - rightAngle)%FastMath.TWO_PI) < FastMath.HALF_PI)
					store.setProperty(HingeInterpreter.RIGHT_JOINT_ANGLE,leftAngle + FastMath.HALF_PI);
			}

			public boolean test(PropertyStore store) {
				float leftAngle = store.getFloat(HingeInterpreter.LEFT_JOINT_ANGLE);
				float rightAngle = store.getFloat(HingeInterpreter.RIGHT_JOINT_ANGLE);
				if(Math.abs((leftAngle - rightAngle)%FastMath.TWO_PI) < FastMath.HALF_PI)
					return false;
				return true;
			}
			
		});
	}


}
