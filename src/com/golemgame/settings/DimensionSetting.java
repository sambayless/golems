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
package com.golemgame.settings;

import java.awt.Dimension;

public class DimensionSetting extends Setting<Dimension> {
	private static final long serialVersionUID = 1L;
	
	public static final String PREF_KEY_TYPE = "DIMENSION_SETTING";
	
	private Dimension value;

	protected DimensionSetting(String name,Dimension value) {
		super(name);
		this.value = value;
	}
	
	protected DimensionSetting(String name)
	{
		this(name,new Dimension(1,1));
	}

	public Dimension getValue() {
		return value;
	}

	public void setValue(Dimension value) 
	{	
		if (!this.value.equals( value))
		{
			Dimension oldValue = new Dimension(value);
			this.value.setSize(value);
			super.notifyOfChange(new SettingChangedEvent<Dimension>(this,value, oldValue));
		}	
	}

	@Override
	public String getStringRepresentation() {
		Dimension value = this.getValue();
		return String.valueOf((int)value.getWidth()) + VALUE_SEPARATOR + String.valueOf((int)value.getHeight());
	}

	@Override
	public void loadFromString(String value) throws SettingLoadException {
		try{
			int separator = value.indexOf(VALUE_SEPARATOR);
			if (separator >=0)
			{
				int width = Integer.parseInt(value.substring(0,separator));
				int height = Integer.parseInt(value.substring(separator+VALUE_SEPARATOR.length()));
				this.setValue(new Dimension (width,height));
			}else
				throw new SettingLoadException();
			
		}catch(RuntimeException e)
		{
			throw new SettingLoadException(e);
		}
			
	}

	@Override
	protected SettingChangedEvent<Dimension> generateChangedEvent() {
		return new SettingChangedEvent<Dimension>(this,value, value);
	}
	
	
}
