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
