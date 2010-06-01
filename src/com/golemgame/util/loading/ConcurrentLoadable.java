package com.golemgame.util.loading;

public interface ConcurrentLoadable {
	
	/**
	 * Load this object. Note that this may take an indeterminate amount of time to complete.
	 * It should not be called from the GL thread.
	 */
	public void load() throws Exception;
	public boolean isLoaded();
	public void waitForLoad();
}
