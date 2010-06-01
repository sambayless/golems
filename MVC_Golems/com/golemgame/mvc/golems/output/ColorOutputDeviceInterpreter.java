package com.golemgame.mvc.golems.output;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GolemsClassRepository;
import com.jme.renderer.ColorRGBA;

public class ColorOutputDeviceInterpreter extends OutputDeviceInterpreter{

	//note: Unusually, these keys are enumerated in the device interpreter, not here
	public static final String COLOR_POSITIVE = "device.color.positive";
	public static final String COLOR_NEGATIVE = "device.color.negative";
	public static final String COLOR_NEUTRAL = "device.color.neutral";
	

	public ColorOutputDeviceInterpreter() {
		this(new PropertyStore());
		
	}
	
	public ColorOutputDeviceInterpreter(PropertyStore store) {
		super(store);		
		getStore().setClassName(GolemsClassRepository.COLOR_OUTPUT_DEVICE_CLASS);
		super.setNumberOfInputs(1);
		super.setDeviceType(OutputType.COLOR);
	}
	
	public ColorRGBA getPositiveColor()
	{
		return getStore().getColor(COLOR_POSITIVE, new ColorRGBA( ColorRGBA.red));
	}
	public ColorRGBA getNeutralColor()
	{
		return getStore().getColor(COLOR_NEUTRAL, new ColorRGBA(1,1,1,0));
	}
	public ColorRGBA getNegativeColor()
	{
		return getStore().getColor(COLOR_NEGATIVE, new ColorRGBA( ColorRGBA.blue));
	}
	
	public void setPositiveColor(ColorRGBA color)
	{
		getStore().setProperty(COLOR_POSITIVE, new ColorRGBA( color));
	}
	public void setNeutralColor(ColorRGBA color)
	{
		getStore().setProperty(COLOR_NEUTRAL, new ColorRGBA( color));
	}
	public void setNegativeColor(ColorRGBA color)
	{
		getStore().setProperty(COLOR_NEGATIVE,  new ColorRGBA( color));
	}
}
