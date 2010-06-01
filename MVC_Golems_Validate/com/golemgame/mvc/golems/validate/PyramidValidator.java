package com.golemgame.mvc.golems.validate;

import com.golemgame.mvc.golems.PyramidInterpreter;
import com.golemgame.mvc.golems.validate.requirement.Vector3MinimumRequirement;
import com.jme.math.Vector3f;

public class PyramidValidator extends PhysicalValidator {


	public PyramidValidator() {
		super();
		super.requireData(PyramidInterpreter.PYRAMID_SCALE,new Vector3f(0.5f,0.5f,0.5f));
		
		super.addRequirement( new Vector3MinimumRequirement(MIN_EXTENT,MIN_EXTENT,MIN_EXTENT,PyramidInterpreter.PYRAMID_SCALE));
		
	}

}
