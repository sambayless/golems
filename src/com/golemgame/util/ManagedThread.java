package com.golemgame.util;

public interface ManagedThread {
	/**
	 * Call to signal that the thread should terminate.
	 */
	public void shutdownThread();
}
