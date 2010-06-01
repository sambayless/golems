package com.golemgame.platform.unknown;

import org.fenggui.binding.render.Cursor;

import com.golemgame.platform.DefaultKeyBindings;
import com.golemgame.platform.PlatformManager;
import com.jme.input.KeyInput;

public class UnknownPlatformManager extends PlatformManager {
	private DefaultKeyBindings keySettings = new DefaultKeyBindings();

	@Override
	public DefaultKeyBindings getDefaultKeyBindings() {
		return keySettings;
	}

	@Override
	public void init() {
		Cursor.setShowCursor(false);
		System.out.println("Detected Unknown Platform/OS");
		super.init();
	}

	@Override
	public String getNameForKey(int jmeKeyCode) {
		if (jmeKeyCode == KeyInput.KEY_LWIN || jmeKeyCode == KeyInput.KEY_RWIN )
		{
			return "Meta";
		}else
			return null;
	}
	
}
