package support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * To use this, export the GlassIcons FLA as a movie, using PNG as the file type.
 * 
 * @author Sam
 *
 */
public class PngRenamer {
	
	private static List<String> names = new ArrayList<String>();
	private static 	String ignore = "_IGNORE@";
	static{
		String base = "tools/";
		names.add(base+"SelectOne");
		names.add(ignore);
		names.add(base+"SelectTwo");
		names.add(base+"Wire");
		names.add(base+ignore);
		
		base = "joints/";
		names.add(base+"BallSocket");
		
		base = "tools/";
		names.add(base+"Selection");
		
		base = "polyhedrons/";
		names.add(base+"Cube");
		names.add(base+"Sphere");
		names.add(base+"Ellipse"); //10
		names.add(base+"Capsule");
		names.add(base+"Cone");
		names.add(base+"Pyramid");
		names.add(ignore);
		names.add(base+"Cylinder");
		
		base = "joints/";
		names.add(base+"Axle");
		names.add(ignore);
		names.add(base+"Hinge");
		
		base = "functionals/";
		names.add(base+"Slope");
		
		base = "joints/";
		names.add(base+"Hydraulic");//20
		
		base = "functionals/";
		names.add(base+"Battery");
		
		base = "special/";
		names.add(base+"Rocket");
		names.add(base+"Tire");
		
		base = "functionals/";
		names.add(base+"Push");
		names.add(base+"Distance");
		
		base = "solid/";
		names.add(base+"Solid_Blue");
		names.add(base+"Solid_Green");
		names.add(base+"Solid_Red");
		names.add(base+"Solid_Yellow");
		names.add(base+"Solid_Grey");
		
		base = "glass/";
		names.add(base+"Glass_Blue"); //30
		base = "solid/";
		names.add(base+"Solid_White");
		base = "glass/";
		names.add(base+"Glass_Green");
		names.add(base+"Glass_Red");
		names.add(base+"Glass_Yellow");
		names.add(base+"Glass_White");
		names.add(base+"Crystal");
		
	}
	
	private void renameAllFiles()
	{
		File base = new File("C:/Users/Sam/Convert/Png");
		File output = new File("C:/Users/Sam/Convert/Png/Output");
		base.mkdirs();
		output.mkdirs();
		int i = 0;
		for(File file:base.listFiles())
		{
			
			if (file.isDirectory()||file.isHidden() || file.getName().charAt(0)=='.')
				continue;
		

			renameFile(file, output,i);
			i++;
		}
		System.out.println("Done set");
	}
	
	public static void main(String[] args) {
	
		PngRenamer app = new PngRenamer();
		app.renameAllFiles();
	}
	
	private static void renameFile(File input, File output, int i)
	{
		String base = "";
		String extension=".png";
		if(names.size()<= i || names.get(i).contains(ignore))
			return;
		File outputFile = new File(output.getAbsolutePath()+ "/" + base + names.get(i) + extension);
		File parentOutput = outputFile.getParentFile();
		parentOutput.mkdirs();
		if(outputFile.exists())
			outputFile.delete();
		try{
			copyFile(input,outputFile);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	


	    public static void copyFile(File in, File out) 
	        throws IOException 
	    {
	    	
	    	//this method from http://www.rgagnon.com/javadetails/java-0064.html
	        FileChannel inChannel = new
	            FileInputStream(in).getChannel();
	        FileChannel outChannel = new
	            FileOutputStream(out).getChannel();
	        try {
	            inChannel.transferTo(0, inChannel.size(),
	                    outChannel);
	        } 
	        catch (IOException e) {
	            throw e;
	        }
	        finally {
	            if (inChannel != null) inChannel.close();
	            if (outChannel != null) outChannel.close();
	        }
	    }


	  
	
}
