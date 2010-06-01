package com.golemgame.mvc.golems.input;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GolemsClassRepository;

public class TextInputDeviceInterpreter extends InputDeviceInterpreter{
	//note: Unusually, these keys are enumerated in the device interpreter, not here
	public static final String INITIAL_VALUE = "device.initial";
	
	public TextInputDeviceInterpreter() {
		this(new PropertyStore());
		super.setNumberOfOutputs(1);
		super.setDeviceType(InputType.TEXT);
		getStore().setClassName(GolemsClassRepository.TEXT_INPUT_DEVICE_CLASS);
	}
	
	public TextInputDeviceInterpreter(PropertyStore store) {
		super(store);		
	}
	
	public float getInitialValue()
	{
		return getStore().getFloat(INITIAL_VALUE,0f);
	}
	
	public void setInitialValue(float keyCode)
	{
		getStore().setProperty(INITIAL_VALUE,keyCode);		
	}
	
}
