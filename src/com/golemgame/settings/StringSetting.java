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


public class StringSetting extends Setting<String> {
	private static final long serialVersionUID = 1L;
	
	public static final String PREF_KEY_TYPE = "STRING_SETTING";
	
	private String value;
	
	protected StringSetting(String name,String value) {
		super(name);
		this.value = value;
	}
	
	protected StringSetting(String name)
	{
		this(name,"");
	}

	/**
	 * Get the value of this setting. For performance reasons, this returns the internal instance of the Vector3f. 
	 * This instance should be considered immutable - changing its value will cause this setting to behave incorrectly.
	 * @return
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set this Vector3f to match the provided value.
	 * @param value
	 */
	public void setValue(String value) 
	{
		if(value == null)
			value = "";
		if (!this.value.equals( value))
		{
			String oldValue = this.value;
			this.value = value.intern();
			super.notifyOfChange(new SettingChangedEvent<String>(this,value, oldValue));
		}		
	}

	@Override
	public String getStringRepresentation() {
		return value;
	}

	@Override
	public void loadFromString(String value) throws SettingLoadException {
		this.value = value.intern();
	}

	@Override
	protected SettingChangedEvent<String> generateChangedEvent() {

		return new SettingChangedEvent<String>(this,value, value);
	}
	

}
