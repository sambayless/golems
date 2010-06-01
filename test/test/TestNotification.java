package test;

import com.golemgame.constructor.ErrorNotification;

public class TestNotification {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String message = "An unexpected error has caused the program to crash:\nThe program failed to load.";
		
		String title = "Unexpected Error";
		ErrorNotification.getInstance().generateExternalErrorMessage(message, title);
		
		Runtime.getRuntime().exit(0);
	}

}
