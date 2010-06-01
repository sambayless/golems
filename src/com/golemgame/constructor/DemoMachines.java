package com.golemgame.constructor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import com.golemgame.states.GeneralSettings;


public class DemoMachines {
	
	private static final int demoVersion = 7;
	
	
	public static int getDemoVersion() {
		return demoVersion;
	}

	public static void ensureIsCopied() throws IOException
	{
		 if (getDemoVersion() != GeneralSettings.getInstance().getDemoVersion().getValue())
		 {//if the version has changed, clear the demos so they are forced to reset
			 GeneralSettings.getInstance().getDemoVersion().setValue(getDemoVersion());
			 ensureIsCopied(true);
		 }else
			 ensureIsCopied(false);
		
	}
	
	public static void ensureIsCopied(boolean forceCopy) throws IOException
	{
		 File file = new File(System.getProperty("user.home") + "/Machines/Demo/");
		 
		 if (file.exists() &! forceCopy)
			 return;
		 
		 file.mkdirs();

		 String[] list = new String[]{"Catapult","Human","Piston","Tachometer","Tower","Top","Tread","Head", "Helicopter"};
		 
		 for (String name:list)
		 {
				//URL machine = DemoMachines.class.getClassLoader().getResource("com/golemgame/data/app/demo/" + name + ".mchn");
				 
			 InputStream machineStream = DemoMachines.class.getClassLoader().getResourceAsStream("com/golemgame/data/app/demo/" + name + ".mchn");
			 
			 File dest = new File(file,name + ".mchn");
			 dest.delete();
			
			 copyInputStream(machineStream,dest);
				//copyFile(new File( machine.getFile()),new File(file,name + ".mchn"));
		 }	 
		 
	}

	
	public static void copyInputStream(InputStream source, File dest) throws IOException
	{
		
		 if(!dest.exists()) {
			  dest.createNewFile();
			 }
			 InputStream in = null;
			 OutputStream out = null;
			 try {
			  in = new BufferedInputStream(source,8192 );
			  out = new BufferedOutputStream( new FileOutputStream(dest),8192 );
			  int b = 0;
				  while( (b =in.read()) >= 0 )
				  {
					  out.write(b);
				  }
			 }
			 finally {
			  if(in != null) {
			   in.close();
			  }
			  if(out != null) {
			   out.close();
			  }
			 }
			
			 
	}
	
	public static void copyFile(File sourceFile, File destFile) throws IOException {
		 if(!destFile.exists()) {
		  destFile.createNewFile();
		 }
		 
		 FileChannel source = null;
		 FileChannel destination = null;
		 try {
		  source = new FileInputStream(sourceFile).getChannel();
		  destination = new FileOutputStream(destFile).getChannel();
		  destination.transferFrom(source, 0, source.size());
		 }
		 finally {
		  if(source != null) {
		   source.close();
		  }
		  if(destination != null) {
		   destination.close();
		  }
		}
	}


}
