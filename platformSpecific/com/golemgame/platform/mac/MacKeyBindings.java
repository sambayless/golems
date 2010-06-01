package com.golemgame.platform.mac;

import com.golemgame.platform.DefaultKeyBindings;
import com.jme.input.KeyInput;

public class MacKeyBindings extends DefaultKeyBindings {

	@Override
	public int getMultipleSelectKey() {
		return KeyInput.KEY_LWIN;
	}
	
	@Override
	public int getUndoRedoSecondKey()
	{
		return KeyInput.KEY_LWIN;	
	}
}
