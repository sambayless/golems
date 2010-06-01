package com.golemgame.mvc.golems.validate;

import com.golemgame.mvc.DoubleType;
import com.golemgame.mvc.golems.BatteryInterpreter;
import com.golemgame.mvc.golems.GolemsClassRepository;
import com.golemgame.mvc.golems.MachineInterpreter;
import com.golemgame.mvc.golems.functions.FunctionSettingsInterpreter;
import com.golemgame.mvc.golems.functions.PolynomialFunctionInterpreter;
import com.golemgame.mvc.golems.validate.requirement.StoreValidationRequirement;

public class BatteryValidator extends PhysicalValidator {

	public BatteryValidator() {
		super();
			
		FunctionSettingsInterpreter batteryFunction = new FunctionSettingsInterpreter();
		batteryFunction.setMinX(0f);
		PolynomialFunctionInterpreter poly = new PolynomialFunctionInterpreter();
	
		batteryFunction.setPeriodic(true);		
		poly.setCoefficient(0, new DoubleType(1));
		poly.setCoefficient(1, new DoubleType(0));
		batteryFunction.setFunction(poly.getStore());
		super.requireData(BatteryInterpreter.FUNCTION_SETTINGS, batteryFunction.getStore());
		
		super.addRequirement(new StoreValidationRequirement(BatteryInterpreter.FUNCTION_SETTINGS,GolemsClassRepository.FUNCTION_SETTINGS_CLASS,GolemsValidator.getInstance()));

	}

}
