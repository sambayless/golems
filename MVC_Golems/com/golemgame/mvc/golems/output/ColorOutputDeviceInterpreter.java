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
