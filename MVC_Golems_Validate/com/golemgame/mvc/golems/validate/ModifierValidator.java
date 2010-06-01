package com.golemgame.mvc.golems.validate;

import com.golemgame.mvc.DoubleType;
import com.golemgame.mvc.golems.BatteryInterpreter;
import com.golemgame.mvc.golems.GolemsClassRepository;
import com.golemgame.mvc.golems.ModifierInterpreter;
import com.golemgame.mvc.golems.functions.FunctionSettingsInterpreter;
import com.golemgame.mvc.golems.functions.PolynomialFunctionInterpreter;
import com.golemgame.mvc.golems.validate.requirement.StoreValidationRequirement;

public class ModifierValidator extends PhysicalValidator{

	public ModifierValidator() {
		super();
	
		FunctionSettingsInterpreter modifierFunction = new FunctionSettingsInterpreter();
		modifierFunction.setMinX(-1f);
		PolynomialFunctionInterpreter poly = new PolynomialFunctionInterpreter();
		 poly.setCoefficient(0, new DoubleType(0));
		 poly.setCoefficient(1, new DoubleType(1));
		 modifierFunction.setFunction(poly.getStore());
		super.requireData(ModifierInterpreter.FUNCTION_SETTINGS, modifierFunction.getStore());
		super.addRequirement(new StoreValidationRequirement(BatteryInterpreter.FUNCTION_SETTINGS,GolemsClassRepository.FUNCTION_SETTINGS_CLASS,GolemsValidator.getInstance()));

	}

}
