package com.golemgame.mvc.golems.validate;

import com.golemgame.mvc.golems.functions.CombinedFunctionInterpreter;
import com.golemgame.mvc.golems.validate.requirement.WeakStoreValidationRequirement;

public class CombinedFunctionValidator extends Validator {

	public CombinedFunctionValidator() {
		super();
		
		super.addRequirement(new WeakStoreValidationRequirement(CombinedFunctionInterpreter.FUNCTION1,GolemsValidator.getInstance()));
		super.addRequirement(new WeakStoreValidationRequirement(CombinedFunctionInterpreter.FUNCTION2,GolemsValidator.getInstance()));
		
	}

}
