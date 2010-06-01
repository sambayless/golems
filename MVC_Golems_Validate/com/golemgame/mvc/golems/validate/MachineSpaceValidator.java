package com.golemgame.mvc.golems.validate;

import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.golems.MachineSpaceInterpreter;
import com.golemgame.mvc.golems.validate.requirement.RecursiveValidationRequirement;

public class MachineSpaceValidator extends Validator {

	public MachineSpaceValidator() {
		super();
		super.requireData(MachineSpaceInterpreter.MACHINES,new CollectionType());	
		super.addRequirement(new RecursiveValidationRequirement(MachineSpaceInterpreter.MACHINES,GolemsValidator.getInstance()));

	}


}
