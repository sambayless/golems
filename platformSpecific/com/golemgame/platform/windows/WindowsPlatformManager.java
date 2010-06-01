package com.golemgame.platform.windows;

import org.fenggui.binding.render.Cursor;

import com.golemgame.platform.DefaultKeyBindings;
import com.golemgame.platform.PlatformManager;
import com.jme.input.KeyInput;

public class WindowsPlatformManager extends PlatformManager {

	private DefaultKeyBindings keySettings = new DefaultKeyBindings();

	@Override
	public void init() {
		Cursor.setShowCursor(true);
		System.out.println("Detected Windows");
		super.init();
	}
	@Override
	public DefaultKeyBindings getDefaultKeyBindings() {
		return keySettings;
	}
	@Override
	public String getNameForKey(int jmeKeyCode) {
		if (jmeKeyCode == KeyInput.KEY_LWIN || jmeKeyCode == KeyInput.KEY_RWIN )
		{
			return "Windows";
		}else
			return null;
	}
	@Override
	public String getJavaLauncher() {
		return "javaw";
	}
}
