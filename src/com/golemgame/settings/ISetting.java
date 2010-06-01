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
