package com.golemgame.settings;

import java.util.EventObject;

public class SettingChangedEvent<V> extends EventObject {
	private static final long serialVersionUID = 1;
	private ISetting setting;
	private V oldValue;
	private V newValue;



	public SettingChangedEvent(ISetting setting, V newValue,V oldValue) {
		super(setting);
		this.setting = setting;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}



	public ISetting getSetting() {
		return setting;
	}



	public V getOldValue() {
		return oldValue;
	}



	public V getNewValue() {
		return newValue;
	}
}
