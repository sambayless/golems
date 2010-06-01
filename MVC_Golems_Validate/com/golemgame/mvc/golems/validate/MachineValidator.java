package com.golemgame.mvc.golems.validate;

import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.golems.GolemsClassRepository;
import com.golemgame.mvc.golems.MachineInterpreter;
import com.golemgame.mvc.golems.validate.requirement.RecursiveValidationRequirement;
import com.golemgame.mvc.golems.validate.requirement.StoreValidationRequirement;

public class MachineValidator extends Validator {

	public MachineValidator() {
		super();
		super.requireData(MachineInterpreter.STRUCTURES,new CollectionType());	

		super.addRequirement(new RecursiveValidationRequirement(MachineInterpreter.STRUCTURES,GolemsValidator.getInstance()));
		super.addRequirement(new StoreValidationRequirement(MachineInterpreter.LAYER_REPOSITORY,GolemsClassRepository.LAYER_REPOSITORY_CLASS,GolemsValidator.getInstance()));

	}

}
