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
package com.golemgame.platform;

import com.golemgame.platform.linux.LinuxPlatformManager;
import com.golemgame.platform.mac.MacPlatformManager;
import com.golemgame.platform.unknown.UnknownPlatformManager;
import com.golemgame.platform.windows.WindowsPlatformManager;

public abstract class PlatformManager {
	private static  PlatformManager instance = null;

	public static synchronized PlatformManager get()
	{
		if (instance ==null)
		{
			//detect OS here

			String osName = System.getProperty("os.name");
			if(osName == null)
				osName = "";
			
			osName = osName.toUpperCase();
			
			if(osName.contains("WINDOWS"))
			{
				instance = new WindowsPlatformManager();
			}else if(osName.contains("LINUX") || osName.contains("UBUNTU") || osName.contains("DEBIAN")|| osName.contains("REDHAT")|| osName.contains("FEDORA")|| osName.contains("SUSE") )
			{
				instance = new LinuxPlatformManager();
			}else if (osName.contains("MAC") || osName.contains("APPLE"))
			{
				instance = new MacPlatformManager();
			}else{
				instance = new UnknownPlatformManager();
			}
			instance.init();
		}
	
		return instance;
	}

	public String[] getLaunchCommands() {
		return new String[]{};
	}
	
	public abstract DefaultKeyBindings getDefaultKeyBindings();
	
	public void init()
	{
	
	}
	
	public abstract String getNameForKey(int jmeKeyCode);
	
	public  String getJavaLauncher()
	{
		return "java";
	}
	
	
}
