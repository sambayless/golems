package com.golemgame.mvc.golems.validate;

import com.golemgame.mvc.golems.input.InputDeviceInterpreter;

public class InputDeviceValidator extends Validator {

	public InputDeviceValidator() {
		super();
		super.requireData(InputDeviceInterpreter.INSTRUMENT_WINDOWED, true);
		super.requireData(InputDeviceInterpreter.INSTRUMENT_LOCKED, false);
		super.requireData(InputDeviceInterpreter.INSTRUMENT_HEIGHT, 170.0/768.0);
		super.requireData(InputDeviceInterpreter.INSTRUMENT_WIDTH, 250.0/1024.0);
		super.requireData(InputDeviceInterpreter.INSTRUMENT_X, 0);
		super.requireData(InputDeviceInterpreter.INSTRUMENT_Y, 0);

	}

}
