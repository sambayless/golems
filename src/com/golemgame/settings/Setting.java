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

import com.golemgame.util.input.InputLayer;

public abstract class Setting<V> implements ISetting<V>{

	private static final long serialVersionUID = 1L;
	public static final String VALUE_SEPARATOR = ",";
	/*
 * When you subscribe a setting listener, you can (optionally) choose a layer level (default to max priority).
 * Settings are themselves registered at the highest possible priority, so they are always checked.
 * However, the settings maintain an internal layer buffer. Although this layer buffer does NOT do layering the way the other input layer does
 * it does check to see if the input layer is blocked at the assigned layer of the setting listener.
 * 
 * So: if the input layer is blocked at a particular layer, so are all settings listeners.
 * Note that settings have no mechanism for initiating the blocking of layers
 * 
 * As a result of this, individual states/tools/etc should no longer re-register the key listeners of the settings they are interested on set listening;
 * instead, those should all be registered statically at the start.
 * 
 */
	private transient SettingsEventDispatch dispatch = new SettingsEventDispatch();	
	
	private boolean transientSetting;
	private String name;
	
	
	public void addSettingsListener(SettingsListener<V> listener) {
		this.addSettingsListener(listener, InputLayer.MAX_PRIORITY);		
	}	
	
	
	public void addSettingsListener(SettingsListener<V> listener, int layer) {
		this.addSettingsListener(listener, layer, false);
	}	
	
	
	
	public void addSettingsListener(SettingsListener<V> listener,
			boolean initiate) {
		this.addSettingsListener(listener, InputLayer.MAX_PRIORITY,initiate);
		
	}

	
	public void addSettingsListener(SettingsListener<V> listener, int layer,
			boolean initiate) {
		dispatch.addSettingListener(listener,layer);
		
		if (initiate)
			listener.valueChanged(generateChangedEvent());
		
	}
	
	
	public void removeSettingsListener(SettingsListener<V> listener) {
		dispatch.removeSettingListener(listener);
	}
	
	protected void notifyOfChange(SettingChangedEvent<V> changeEvent) {
		dispatch.generateEvent(changeEvent);
	}


	protected abstract SettingChangedEvent<V> generateChangedEvent();
	
	
	
	public void notifyOfChange() {
		dispatch.generateEvent(generateChangedEvent());
	}

	protected Setting(String name)
	{
		this.name = name;
	}

	
	public String getName() {
		return name;
	}

	
	public boolean isTransient() {
		return transientSetting;
	}

	public void setIsTransient(boolean isTransient)
	{
		transientSetting = isTransient;
	}

	
	public String getStringRepresentation() {
		return "";
	}

	
	public void loadFromString(String value)throws SettingLoadException {
		
		
	}

	
	public String toString() {
		return getClass().getCanonicalName() + "." + this.getName();
	}

	
	
}
