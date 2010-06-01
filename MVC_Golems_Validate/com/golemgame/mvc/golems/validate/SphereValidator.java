package com.golemgame.mvc.golems.validate;

import com.golemgame.mvc.golems.SphereInterpreter;
import com.golemgame.mvc.golems.validate.requirement.Vector3MinimumRequirement;
import com.jme.math.Vector3f;

public class SphereValidator extends PhysicalValidator {


	public SphereValidator() {
		super();
		super.requireData(SphereInterpreter.RADII, new Vector3f(0.5f,0.5f,0.5f));
		super.requireData(SphereInterpreter.ELLIPSOID, false);
		
		super.addRequirement( new Vector3MinimumRequirement(MIN_EXTENT,MIN_EXTENT,MIN_EXTENT,SphereInterpreter.RADII));
		
	}


}
