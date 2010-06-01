package com.golemgame.launch;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.JOptionPane;

import com.golemgame.launch.settings.ShowSettings;
import com.golemgame.platform.PlatformManager;
import com.golemgame.states.MemoryManager;

public class Launcher {

	private static final long MIN_SAFE_RUN_TIME = 2000;

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException, IOException {
		
		//pass all args onto Golems, unless you start with 's' or '-s' or '-settings'
	//	JOptionPane.showMessageDialog(null, Arrays.toString(args));
		if(args.length > 0 && (args[0].equalsIgnoreCase("s") || args[0].equalsIgnoreCase("-s")|| args[0].equalsIgnoreCase("-settings") || args[0].equalsIgnoreCase("settings")) )
		{
			String[] newArgs = new String[args.length-1];
			System.arraycopy(args, 1, newArgs, 0, newArgs.length);
			args = newArgs;
			
			showSettings();
		}
		int tries = 0;
		while(tries<2)
		{
			tries++;
			try{
			
				
				//attempt to locate the jvm and run a new process from there. If it cant be done, then rely on the OS and hope the java command is recognized.
				//finally, if none of those work, launch using this JVM.
				
				try{
					try{
					String javaHome = System.getProperty("java.home") ;
				
						File javaDir = new File(javaHome);
				
					
						//look for a bin dir
						if(javaDir.exists())
						{
							File[] files =javaDir.listFiles();
							for(File d:files)
							{
								if(d.exists() && d.getName().equalsIgnoreCase("bin"))
								{
									File bin = d;
									
									for(File j:bin.listFiles())
									{
									
										if(j.exists())
										{
											String jName = j.getName().toLowerCase();
										
											if(jName.equals("java.exe") || jName.equals("java"))
											{
												if(j.isFile())
												{
													
													runJava(j,args);
													return;
													
													
												}
											}
										}
									}
									break;
								}
							}
							
						}
						
						throw new IOException("Can't find jvm folder");
					}catch(JavaSettingsException e)
					{
						throw e;
					}catch(Exception e)
					{
						throw new JavaLaunchException("Defaulting to OS java launcher. Reason:" + e.getMessage());
					}
					
				}catch(JavaLaunchException e)
				{
					e.printStackTrace();
					runJava(null,args);
					return;
				}
				
				
			}catch(JavaSettingsException e)
			{
				//show memory settings
			
				if(tries<2)
				{
					if(!showSettings(e))
					{
						System.exit(-1);
						return;//break;
					}
				}else
				{
					System.exit(-1);
					return;
				}
			
			}catch(Exception e)
			{
				e.printStackTrace();
				System.exit(-1);
				return;
			}
		}
		
	}

	private static boolean showSettings(JavaSettingsException e) throws IOException, InterruptedException {
		return ShowSettings.showSettings("Golems failed to load, adjusting memory or display settings may resolve the issue.");
		
	}

	private static boolean showSettings() throws InterruptedException, IOException {
		return  ShowSettings.showSettings();
	}

	private static void runJava(File j, String[] args) throws Exception,JavaSettingsException{
		//launch a new java process
		// Launcher.class.getClassLoader().getResource("");

		URL pathURL = (Launcher.class.getProtectionDomain().getCodeSource().getLocation());
		//URL pathURL =  Launcher.class.getClassLoader().getResource(Launcher.class.getName());
		//System.out.println(pathURL);
		
		// as per http://weblogs.java.net/blog/kohsuke/archive/2007/04/how_to_convert.html
		File launcherJar;
		try {
			launcherJar = new File(pathURL.toURI());
		} catch(URISyntaxException e) {
				launcherJar = new File(pathURL.getPath());
		}


		 File golemsDir = launcherJar.getParentFile();
			if(!golemsDir.exists())
				throw new IOException("Golems root doesnt exist (" + golemsDir + ")" );
		//	System.out.println(golemsDir);
	//	boolean tryAgain = true;
		ArrayList<String> cmds = new ArrayList<String>();
		//do{
			cmds.clear();
		//	tryAgain = false;
		
		
				if(j !=null){
					cmds.add(j.getPath());
				}else{
					cmds.add("java");//let the OS figure this out
				}
				//int memory = GeneralSettings.getInstance().getMaxMemory().getValue();
				
				//we could also test whether the display settings make sense here, and whether Golems crashed last time.
				
				cmds.addAll(Arrays.asList(PlatformManager.get().getLaunchCommands() ));
				
				long heap = MemoryManager.getHeapMemory();
				long direct = MemoryManager.getDirectMemory();
				long physics = MemoryManager.getPhysicsMemory();//not used here
				
				//add vm args
				File libPath = new File(golemsDir,"lib");
				File nativePath = new File(libPath,"native");
				if(!nativePath.exists())
					throw new IOException("Native path doesnt exist (" + nativePath + ")" );
				
				cmds.add("-XX:MaxDirectMemorySize=" + String.valueOf(direct) +"m");
			
				cmds.add("-Xms32m");
				cmds.add("-Xmx" + String.valueOf(heap) + "m");
			
				
				cmds.add("-Djava.library.path=" +nativePath.toString());
				
				
				cmds.add("-jar");
				
				cmds.add("golems.jar");
				
				cmds.addAll(Arrays.asList(args));
				
				
				Iterator<String> it = cmds.iterator();
				while(it.hasNext())
				{
					String n = it.next();
					if(n==null || n.length()==0 || n.trim().length()==0)
						it.remove();
				}
				
				//System.out.println(cmds);
				
				
				ProcessBuilder p = new ProcessBuilder(cmds.toArray(new String[0]) );
				
				p.redirectErrorStream(true);
				p.directory(golemsDir);
				
				long startTime = System.currentTimeMillis();
				
	
				Process process = p.start();
				int data = 0;
				try{
					while((data = process.getInputStream().read()) >= 0 )
					{
						System.out.write(data);
					}
				}catch(Exception e)
				{					
				}
				int result = process.waitFor();
				
				if(System.currentTimeMillis()-startTime < MIN_SAFE_RUN_TIME)
				{
					System.out.println("Failed to launch");
					throw new JavaSettingsException("Golems failed to launch");
				}
				System.out.println("Finished");
				System.exit(result);
				return;
			
			
		//}while(tryAgain);
	}

}
