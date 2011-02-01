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
