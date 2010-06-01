package com.golemgame.util.loading;

import java.util.logging.Level;

import com.golemgame.states.StateManager;


public class LoadingExceptionHandlerAdapter implements LoadingExceptionHandler {
	private static final LoadingExceptionHandlerAdapter instance = new LoadingExceptionHandlerAdapter();
	public static LoadingExceptionHandlerAdapter getInstance() {
		return instance;
	}
	
	public void handleException(Exception e, Loadable<?> l) throws Exception {
		StateManager.getLogger().log(Level.FINE, "Error on loading: " + e + "\t" + l.toString()); 

		throw e;

	}

}
