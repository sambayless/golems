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

/**
 * This is a setting to signal that some instruction has been given, ie, delete. It is not associated with any persisting value.
 * For example, an action setting could be "Take Screen Shot" or "Open Menu."
 * @author Sam
 *
 */
public class ActionSetting extends Setting<Object> {
	
	private static final long serialVersionUID = 1L;
	
	public ActionSetting() {
		super("");
	}

	public void initiateAction()
	{
		super.notifyOfChange();
	}

	@Override
	protected SettingChangedEvent<Object> generateChangedEvent() {
		return new SettingChangedEvent<Object>(this, null, null);
	}
	
	
}
