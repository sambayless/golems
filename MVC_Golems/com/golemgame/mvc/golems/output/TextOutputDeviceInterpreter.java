package com.golemgame.mvc.golems.output;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GolemsClassRepository;

public class TextOutputDeviceInterpreter extends OutputDeviceInterpreter{

	//note: Unusually, these keys are enumerated in the device interpreter, not here


	public TextOutputDeviceInterpreter() {
		this(new PropertyStore());

	}
	
	public TextOutputDeviceInterpreter(PropertyStore store) {
		super(store);	
		store.setClassName(GolemsClassRepository.TEXT_OUTPUT_DEVICE_CLASS);
		super.setNumberOfInputs(1);
		super.setDeviceType(OutputType.TEXT);
	}
	
}
