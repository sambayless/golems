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

import java.io.Serializable;

public interface ISetting<V> extends Serializable{
	public void addSettingsListener(SettingsListener<V> listener);
	
	/**
	 * 
	 * @param listener
	 * @param initiate Whether or not to immediately call the listener with the initial state of this setting (default: false).
	 */
	public void addSettingsListener(SettingsListener<V> listener, boolean initiate);
	
	/**
	 * Add a setting at this layer; if the InputLayer is blocked at or higher than this layer, this setting will not be reached
	 * @param listener
	 * @param layer
	 */
	public void addSettingsListener(SettingsListener<V> listener, int layer);
	
	/**
	 * Add a setting at this layer; if the InputLayer is blocked at or higher than this layer, this setting will not be reached
	 * @param listener
	 * @param layer
	 * @param initiate Whether or not to immediately call the listener with the initial state of this setting (default: false).
	 */
	public void addSettingsListener(SettingsListener<V> listener, int layer, boolean initiate);
	public void removeSettingsListener(SettingsListener<V> listener);
	public void notifyOfChange();
	
	/**
	 * A transient setting is one whose state is not saved between sessions.
	 * @return
	 */
	public boolean isTransient();
	
	/**
	 * Get the name of this setting.
	 * All settings should have a unique name, globally.
	 * Setting names are not case sensitive.
	 * @return
	 */
	public String getName();
	
	
	
	/**
	 * Get a string representation of the value of this key that can be parsed with loadFromString
	 * @return
	 */
	public String getStringRepresentation();
	
	public void loadFromString(String value)throws SettingLoadException;
	
}
