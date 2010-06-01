package com.golemgame.constructor;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import com.golemgame.platform.PlatformManager;
import com.golemgame.states.StateManager;

public class ErrorNotification {
	private static final ErrorNotification instance = new ErrorNotification();

	private ErrorNotification() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void generateExternalErrorMessage(String message, String title)
	{
		try {
			
			String[] cmds = new String[5];
			int c = 0;
			cmds[c++] = PlatformManager.get().getJavaLauncher();
		
			cmds[c++] = "-jar";
		
			File notificationFile;
			URL notificationURL =  getClass().getClassLoader().getResource("notification.jar");
			try {
				notificationFile = new File(notificationURL.toURI());
			} catch(URISyntaxException e) {
				notificationFile = new File(notificationURL.getPath());
			}
			
			cmds[c++] =  (notificationFile.getPath());
			
			cmds[c++]= message;
			
			cmds[c++] = title;
			
			Runtime.getRuntime().exec(cmds);
		} catch (Exception e) {
			StateManager.logError(e);
		}
	}

	public static ErrorNotification getInstance() {
		return instance;
	}
	
}
