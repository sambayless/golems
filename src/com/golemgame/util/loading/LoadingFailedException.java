package com.golemgame.util.loading;

public class LoadingFailedException extends Exception {

	private static final long serialVersionUID = 1L;


	public LoadingFailedException() {
		super();

	}


	public LoadingFailedException(String message, Throwable cause) {
		super(message, cause);
	
	}

	public LoadingFailedException(String message) {
		super(message);
	
	}


	public LoadingFailedException(Throwable cause) {
		super(cause);
	
	}

}
