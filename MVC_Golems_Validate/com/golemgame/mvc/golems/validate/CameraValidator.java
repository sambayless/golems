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
