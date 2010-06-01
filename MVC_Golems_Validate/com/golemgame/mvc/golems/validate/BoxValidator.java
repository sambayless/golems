package com.golemgame.mvc.golems.validate;

import com.golemgame.mvc.golems.BoxInterpreter;
import com.golemgame.mvc.golems.validate.requirement.Vector3MinimumRequirement;
import com.jme.math.Vector3f;

public class BoxValidator extends PhysicalValidator {

	public BoxValidator() {
		super();
		super.requireData(BoxInterpreter.BOX_EXTENT,new Vector3f(0.5f,0.5f,0.5f));
		
		super.addRequirement( new Vector3MinimumRequirement(MIN_EXTENT,MIN_EXTENT,MIN_EXTENT,BoxInterpreter.BOX_EXTENT));
		
	}


}
