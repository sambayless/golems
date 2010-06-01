package com.golemgame.mvc.golems.validate;

import com.golemgame.mvc.golems.functions.FunctionSettingsInterpreter;
import com.golemgame.mvc.golems.validate.requirement.WeakStoreValidationRequirement;

public class FunctionSettingsValidator extends Validator {

	public FunctionSettingsValidator() {
		super();
		
		super.addRequirement(new WeakStoreValidationRequirement(FunctionSettingsInterpreter.FUNCTION,GolemsValidator.getInstance()));
		
	}

}
