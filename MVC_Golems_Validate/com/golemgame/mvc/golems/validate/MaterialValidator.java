package com.golemgame.mvc.golems.validate;

import com.golemgame.mvc.golems.MaterialPropertiesInterpreter;
import com.golemgame.mvc.golems.MaterialPropertiesInterpreter.MaterialClass;
import com.golemgame.mvc.golems.validate.requirement.FloatBoundRequirement;

public class MaterialValidator extends Validator{
	public MaterialValidator() {
		super();
		super.requireData(MaterialPropertiesInterpreter.DENSITY, 1f);
		super.requireData(MaterialPropertiesInterpreter.MATERIAL, MaterialClass.DEFAULT);
		super.addRequirement(new FloatBoundRequirement(MaterialPropertiesInterpreter.MIN_DENSITY,MaterialPropertiesInterpreter.MAX_DENSITY, MaterialPropertiesInterpreter.DENSITY));
	}
}
