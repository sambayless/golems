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
