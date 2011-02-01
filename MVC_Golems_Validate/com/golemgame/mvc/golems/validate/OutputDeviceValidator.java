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

import com.golemgame.mvc.golems.output.OutputDeviceInterpreter;

public class OutputDeviceValidator extends Validator {

	public OutputDeviceValidator() {
		super();
		super.requireData(OutputDeviceInterpreter.INSTRUMENT_WINDOWED, true);
		super.requireData(OutputDeviceInterpreter.INSTRUMENT_LOCKED, false);
		super.requireData(OutputDeviceInterpreter.INSTRUMENT_HEIGHT, 170.0/768.0);
		super.requireData(OutputDeviceInterpreter.INSTRUMENT_WIDTH, 250.0/1024.0);
		super.requireData(OutputDeviceInterpreter.INSTRUMENT_X, 0);
		super.requireData(OutputDeviceInterpreter.INSTRUMENT_Y, 0);

	}


	
	
	
}
