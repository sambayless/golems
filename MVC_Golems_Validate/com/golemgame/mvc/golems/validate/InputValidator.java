package com.golemgame.mvc.golems.validate;

import com.golemgame.mvc.golems.GolemsClassRepository;
import com.golemgame.mvc.golems.InputInterpreter;
import com.golemgame.mvc.golems.input.KeyboardInputDeviceInterpreter;
import com.golemgame.mvc.golems.validate.requirement.MulteClassStoreValidationRequirement;

public class InputValidator extends PhysicalValidator {
	public InputValidator() {
		super();
				
		KeyboardInputDeviceInterpreter inputDevice = new KeyboardInputDeviceInterpreter();
		
		super.requireData(InputInterpreter.INPUT_DEVICE, inputDevice.getStore());
		super.addRequirement(new MulteClassStoreValidationRequirement(InputInterpreter.INPUT_DEVICE, new String[]{GolemsClassRepository.KEY_INPUT_DEVICE_CLASS,GolemsClassRepository.TEXT_INPUT_DEVICE_CLASS,GolemsClassRepository.SLIDER_INPUT_DEVICE_CLASS},GolemsValidator.getInstance()));



	}

}
