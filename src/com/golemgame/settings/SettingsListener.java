package com.golemgame.settings;

import java.util.EventListener;

public interface SettingsListener<V> extends EventListener {
	
	public void valueChanged(SettingChangedEvent<V> e);
}
