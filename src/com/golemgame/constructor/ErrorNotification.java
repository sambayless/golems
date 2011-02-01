/*******************************************************************************
 * Copyright 2008, 2009, 2010 Sam Bayless.
 * 
 *     This file is part of Golems.
 * 
 *     Golems is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     Golems is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with Golems. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
