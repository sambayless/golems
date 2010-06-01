package com.golemgame.states.physics;

public interface PhysicsMonitorListener {
	
	/**
	 * Called when a physics thread appears to change from unfrozen to frozen.
	 */
	public void frozen();
	
	
	/**
	 * Called when a physics thread appears to change from frozen to unfrozen.
	 */
	public void unfrozen();
	
	/**
	 * Called if the physics thread dies, for any reason other than an intentional shutdown.
	 */
	public void physicsDied(Throwable cause);
}
