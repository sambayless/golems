package support;

import java.io.File;
import java.io.IOException;

public class SVGExporter {
//-e, --export-png=
	
	private static final String INKSCAPE = "C:\\Program Files\\Inkscape\\Inkscape.exe";

	public static void main(String[] args) throws IOException, InterruptedException {
		
		int width = 512;
		int height = 512;
		
		File svgSource = new File("C:/Users/Sam/GlassIcons/pixmaptextures/vector");
		
		File destination = new File("C:/Users/Sam/GlassIcons/pixmaptextures/export");
		destination.mkdirs();
		
		for(File svgFile:svgSource.listFiles())
		{
			System.out.println("exporting... " + svgFile.getName());
			export(svgFile,destination,width,height);
			
		
			
		}
		System.out.println("Done");
	}

	private static void export(File svgFile, File destination, int width,
			int height) throws IOException, InterruptedException {
		for (int w = width, h = height; w>0 && h>0;w /=2, h/=2)
		{
			String outname = svgFile.getName().substring(0, svgFile.getName().lastIndexOf(".")) + ".png";
			File outputDir = new File(destination,"\\size" + w+ "x" + h);
			outputDir.mkdirs();
			String outputFilename = outputDir.getAbsolutePath()+"\\" + outname;
			String option1 = "--export-png=" + outputFilename;
			
			
			
			String[] cmdArray = new String[]{INKSCAPE,option1,"--export-width=" + w,"--export-height=" +h,svgFile.getAbsolutePath()};
			Process p = Runtime.getRuntime().exec(cmdArray);
			p.waitFor();
		}
	}
	
}
