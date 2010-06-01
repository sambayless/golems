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
