package com.golemgame.tool.action;

public class ActionTypeException extends Exception {

	
	private static final long serialVersionUID = 1L;

	public ActionTypeException() {
		super("No matching action type");
	}

	public ActionTypeException(String message) {
		super(message);
	}


}
