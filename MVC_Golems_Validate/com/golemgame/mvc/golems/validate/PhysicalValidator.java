package com.golemgame.mvc.golems.validate;

import com.golemgame.mvc.golems.GolemsClassRepository;
import com.golemgame.mvc.golems.PhysicalStructureInterpreter;
import com.golemgame.mvc.golems.validate.requirement.StoreValidationRequirement;


public class PhysicalValidator extends Validator {
	public static final float MIN_EXTENT = 0.01f;

	public PhysicalValidator() {
		super();
		super.addRequirement(new StoreValidationRequirement(PhysicalStructureInterpreter.MATERIAL_PROPERTIES,GolemsClassRepository.MATERIAL_PROPERTIES_CLASS, GolemsValidator.getInstance()));
	}
	
	
}
