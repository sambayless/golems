package com.golemgame.mvc.golems.input;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GolemsClassRepository;

public class SliderInputDeviceInterpreter extends InputDeviceInterpreter{
	//note: Unusually, these keys are enumerated in the device interpreter, not here
	public static final String INITIAL_VALUE = "device.initial";
	public static final String SHOW_ARROWS = "device.showArrows";
	public static final String IS_HORIZONTAL = "device.horizontal";
	
	public SliderInputDeviceInterpreter() {
		this(new PropertyStore());
		super.setNumberOfOutputs(1);
		super.setDeviceType(InputType.SLIDER);
		getStore().setClassName(GolemsClassRepository.SLIDER_INPUT_DEVICE_CLASS);
	}
	
	public SliderInputDeviceInterpreter(PropertyStore store) {
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
	

	public void setHorizontal(boolean horizontal) {
		 getStore().setProperty(IS_HORIZONTAL,horizontal);
	}

	public void setShowArrows(boolean showArrows) {
		
		 getStore().setProperty(SHOW_ARROWS,showArrows);
	}

	public boolean isHorizontal() {
		return getStore().getBoolean(IS_HORIZONTAL,false);
	}

	public boolean showArrows() {
		
		return getStore().getBoolean(SHOW_ARROWS,false);
	}
	
}
