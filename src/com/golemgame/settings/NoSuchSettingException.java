package com.golemgame.settings;

public class NoSuchSettingException extends Exception {

	private static final long serialVersionUID = 1L;

	public NoSuchSettingException() {
		super();
	
	}

	public NoSuchSettingException(String message, Throwable cause) {
		super(message, cause);
	
	}

	public NoSuchSettingException(String message) {
		super(message);
	
	}

	public NoSuchSettingException(Throwable cause) {
		super(cause);

	}

}
