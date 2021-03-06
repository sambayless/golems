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
package com.golemgame.mvc.golems;

import java.util.Collection;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;

public class GearInterpreter extends PhysicalStructureInterpreter {
	
	/**
	 * This is the height from the edge of the cylinder to the top of the center of the gear tooth.
	 * The height of the gear tooth at the edges is slightly more: this height+deltaH, as calculated in GearStructure.
	 */
	public static final String TOOTH_HEIGHT = "Tooth.Height";
	public static final String TOOTH_WIDTH = "Tooth.Width";
	/**
	 * This is TWICE the angle of incidence of each angled edge against the side of the tooth (measured from the top of the tooth)
	 */
	public static final String TOOTH_ANGLE = "Tooth.Angle";
	public static final String TOOTH_NUMBER = "Tooth.Number";
	public GearInterpreter(PropertyStore store) {
		super(store);
		store.setClassName(GolemsClassRepository.GEAR_CLASS);
	}
	
	public GearInterpreter() {
		this(new PropertyStore());		
	}

	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(TOOTH_NUMBER);		
		keys.add(TOOTH_ANGLE);		
		keys.add(TOOTH_WIDTH);		
		keys.add(TOOTH_HEIGHT);		
	
		return super.enumerateKeys(keys);
	}

	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(TOOTH_NUMBER))
			return defaultInt;
		if(key.equals(TOOTH_ANGLE))
			return defaultFloat;
		if(key.equals(TOOTH_WIDTH))
			return defaultFloat;
		if(key.equals(TOOTH_HEIGHT))
			return defaultFloat;

		return super.getDefaultValue(key);
	}
	
	public float getToothHeight()
	{
		return getStore().getFloat(TOOTH_HEIGHT,0.1f);
	}
	public void setToothHeight(float height)
	{
		getStore().setProperty(TOOTH_HEIGHT,height);
	}
	
	public float getToothWidth()
	{
		return getStore().getFloat(TOOTH_WIDTH,0.1f);
	}
	public void setToothWidth(float width)
	{
		getStore().setProperty(TOOTH_WIDTH,width);
	}
	
	public float getToothAngle()
	{
		return getStore().getFloat(TOOTH_ANGLE,0.1f);
	}
	public void setToothAngle(float angle)
	{
		getStore().setProperty(TOOTH_ANGLE,angle);
	}
	
	public int getNumberOfTeeth()
	{
		return getStore().getInt(TOOTH_NUMBER,6);
	}
	public void setNumberOfTeeth(int numberOfTeeth)
	{
		getStore().setProperty(TOOTH_NUMBER,numberOfTeeth);
	}
	
}
