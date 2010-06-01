package com.golemgame.mvc.golems.input;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GolemsClassRepository;

public class KeyboardInputDeviceInterpreter extends InputDeviceInterpreter{
	
	//note: Unusually, these keys are enumerated in the device interpreter, not here
	public static final String KEY = "device.key";
	public static final String KEY_EVENT = "device.event";
	public static final String OUTPUT_NEGATIVE = "output.negative";
	
	public enum KeyEventType
	{
		HeldDown(), Toggle();
	}
	
	public KeyboardInputDeviceInterpreter() {
		this(new PropertyStore());
		super.setNumberOfOutputs(1);
		super.setDeviceType(InputType.KEYBOARD);
		getStore().setClassName(GolemsClassRepository.KEY_INPUT_DEVICE_CLASS);
	}
	
	public KeyboardInputDeviceInterpreter(PropertyStore store) {
		super(store);		
	}
	
	public boolean outputNegative()
	{
		return getStore().getBoolean(OUTPUT_NEGATIVE,true);
	}
	
	public void setOutputNegative(boolean zero)
	{
		getStore().setProperty(OUTPUT_NEGATIVE, zero);		
	}
	
	
	public int getKeyCode()
	{
		return getStore().getInt(KEY, 'A');
	}
	
	public void setKeyCode(int keyCode)
	{
		getStore().setProperty(KEY, keyCode);		
	}
	
	public KeyEventType getInteractionType()
	{
		return getStore().getEnum(KEY_EVENT, KeyEventType.HeldDown);
	}
	
	public void setInteractionType(KeyEventType key)
	{
		getStore().setProperty(KEY_EVENT, key);
	}
}
