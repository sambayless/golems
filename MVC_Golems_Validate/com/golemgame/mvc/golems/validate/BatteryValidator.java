/*******************************************************************************
 * Copyright 2008, 2009, 2010 Sam Bayless.
 * 
 *     This file is part of Golems.
 * 
 *     Golems is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     Golems is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with Golems. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
