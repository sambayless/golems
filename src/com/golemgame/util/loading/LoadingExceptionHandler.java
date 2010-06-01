package com.golemgame.util.loading;

public interface LoadingExceptionHandler {
	public void handleException(Exception e, Loadable<?> l) throws Exception;
}
