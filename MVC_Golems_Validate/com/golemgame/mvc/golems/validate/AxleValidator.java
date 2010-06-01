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
