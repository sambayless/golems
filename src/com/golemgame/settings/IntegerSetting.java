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

public class IntegerSetting extends Setting<Integer> {
	private static final long serialVersionUID = 1L;
	
	public static final String PREF_KEY_TYPE = "INT_SETTING";
	
	private int value;
	
	public IntegerSetting(String name, int value) {
		super(name);
		this.value = value;
	}
	
	public IntegerSetting(String name) {
		this(name,0);
	}
	

	public int getValue() {
		return value;
	}

	public void setValue(int value) 
	{
		if (this.value != value)
		{
			int oldValue = this.value;
			this.value = value;
			super.notifyOfChange(new SettingChangedEvent<Integer>(this,value, oldValue));
		}		
	}

	@Override
	public String getStringRepresentation() {
		return String.valueOf(this.getValue());
	}

	@Override
	public void loadFromString(String value) throws SettingLoadException {
		try{
			this.setValue(Integer.parseInt(value));
		}catch(RuntimeException e)
		{
			throw new SettingLoadException(e);
		}
	}

	@Override
	protected SettingChangedEvent<Integer> generateChangedEvent() {
		
		return new SettingChangedEvent<Integer>(this,value, value);

	}
	
}
