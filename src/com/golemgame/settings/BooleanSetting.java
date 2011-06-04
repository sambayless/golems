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

public class BooleanSetting extends Setting<Boolean> {
	private static final long serialVersionUID = 1L;
	
	public static final String PREF_KEY_TYPE = "BOOLEAN_SETTING";
	
	private boolean value;
	
	public BooleanSetting(String name, boolean value) {
		super(name);
		this.value = value;
	}
	
	public BooleanSetting(String name) {
		this(name,false);
	}
	

	public boolean isValue() {
		return value;
	}

	public void setValue(boolean value) 
	{
		if (this.value != value)
		{

			this.value = value;
			super.notifyOfChange(new SettingChangedEvent<Boolean>(this,value, !value));
		}		
	}

	@Override
	public String getStringRepresentation() {
		return String.valueOf(this.isValue());
	}

	@Override
	public void loadFromString(String value) throws SettingLoadException {
		try{
			this.setValue(Boolean.parseBoolean(value));
		}catch(RuntimeException e)
		{
			throw new SettingLoadException(e);
		}
	}

	@Override
	protected SettingChangedEvent<Boolean> generateChangedEvent() {
		
		return new SettingChangedEvent<Boolean>(this,value, value);

	}
	
}
