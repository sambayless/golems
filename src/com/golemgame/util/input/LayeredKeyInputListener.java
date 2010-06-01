package com.golemgame.util.input;

public interface LayeredKeyInputListener {
	/**
	 * Return true to indicate that no lower priority layers should received this event.
	 * @param character
	 * @param keyCode
	 * @param pressed
	 * @return
	 */
	public boolean onKey(char character, int keyCode, boolean pressed);
}
