package com.golemgame.util;

public interface ControlledListener {
	
	/**
	 * Control whether this class is 'listening' to events (as defined by the class).
	 * This does not have to include all (or even any) events that the class subscribes to,
	 * but should ensure that the class is unresponsive to user input when set to false.
	 * @param listening
	 */
	public void setListening(boolean listening);
	
}
