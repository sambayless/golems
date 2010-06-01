package com.golemgame.states;

import java.util.EventListener;

public interface StateListener extends EventListener {

	/**
	 * This indicates that the state is 'done' and preparing itself for deactivation;
	 * Control should be returned to the state that called it here, but this state is not yet ready to be deactivated.
	 * @param event
	 */
	public void finished(StateFinishedEvent event);	
	
	/**
	 * Called when the state de-activates (which may occur some time after a request for removal is called)
	 */
	public void deactivate(StateDeactivateEvent event);
}
