package com.golemgame.platform;

import com.jme.input.KeyInput;

/**
 * Implementations of this class will provide platform specific default key bindings.
 * @author Sam
 *
 */
public class DefaultKeyBindings {
	public int getMultipleSelectKey()
	{
		return KeyInput.KEY_LCONTROL;
	}
	
	public int getUndoRedoSecondKey()
	{
		return KeyInput.KEY_LCONTROL;	
	}
}
