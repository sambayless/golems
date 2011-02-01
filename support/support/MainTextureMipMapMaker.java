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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;

import com.jme.util.geom.BufferUtils;



public class MainTextureMipMapMaker {
	private final static int mainSIZE = 64;//512;
	public static void main(String[] args) throws IOException {
		
		/*
		 * Note on use:
		 * The mipmaps are made from pseudo-56 and 28 pixel images.
		 * They are actually the original 64 width and 32 width ones, but first scaled down (in flash) by .875 percent (ie, 56/64)
		 * Then put those versions into their appropriate folders in the glassicons directory, and it will create the mipmaps for them correctly.
		 * only the 56/28 size images need mipmaps, the full size ones (used in tooltips) are not ever shrunk, so no mipmaps.
		 * 
		 */
		
		/*
		 * NOTE: It is important that none of the textures have TRANSPARENT background (background should be white or black).
		 * Sometimes flash conversion to SVG leaves the backgrounds transparent.
		 */
		
		
		
		//File destination = new File("C:/Users/Sam/GlassIcons/mipmaps/");
		File destination = new File("C:/Users/Sam/GlassIcons/pixmaptextures/mipmaps/");
		destination.mkdirs();
		
		List<File> sources = new ArrayList<File>();
		sources.add(new File("C:/Users/Sam/GlassIcons/pixmaptextures/export/size512x512"));
		sources.add(new File("C:/Users/Sam/GlassIcons/pixmaptextures/export/size256x256"));
		sources.add(new File("C:/Users/Sam/GlassIcons/pixmaptextures/export/size128x128"));
		sources.add(new File("C:/Users/Sam/GlassIcons/pixmaptextures/export/size64x64"));
		sources.add(new File("C:/Users/Sam/GlassIcons/pixmaptextures/export/size32x32"));
		sources.add(new File("C:/Users/Sam/GlassIcons/pixmaptextures/export/size16x16"));
		sources.add(new File("C:/Users/Sam/GlassIcons/pixmaptextures/export/size8x8"));
		sources.add(new File("C:/Users/Sam/GlassIcons/pixmaptextures/export/size4x4"));
		sources.add(new File("C:/Users/Sam/GlassIcons/pixmaptextures/export/size2x2"));
		sources.add(new File("C:/Users/Sam/GlassIcons/pixmaptextures/export/size1x1"));
		
	//	sources.add(new File("C:/Users/Sam/GlassIcons/56"));
	//	sources.add(new File("C:/Users/Sam/GlassIcons/28"));
		
		recursiveConvert(sources,destination);
	/*	String root = "C:/Users/Sam/GlassIcons/Mipmap/GlassBlue";
		File original = new File(root);
		File outputFolder = new File("C:/Users/Sam/GlassIcons/Mipmap/Generated/");
		outputFolder.mkdirs();
		File output = new File(outputFolder, original.getName() + ".png");
		
		output.createNewFile();
		OutputStream outputStream = new BufferedOutputStream(new FileOutputStream( output));
		
		ImageWriter pngWriter = ImageIO.getImageWritersBySuffix("png").next();
		pngWriter.setOutput(ImageIO.createImageOutputStream(outputStream));
		
		BufferedImage mipmap = generateMipmap(root);
		pngWriter.write(mipmap);*/
		System.out.println("Done");
	}
	
	
	public static void recursiveConvert(List<File> sourceFolders, File destinationFolder) throws IOException
	{
		destinationFolder.mkdirs();
		
		File root = sourceFolders.get(0);
		for (File file:root.listFiles())
		{
			if (file.isDirectory())
			{
				ArrayList<File> similarFolders = new ArrayList<File>();
				similarFolders.add(file);
				String name = file.getName();
				for (int i = 1;i<sourceFolders.size();i++)
				{
					//find the same folder in each of the other source folders
					File sourceFolder = sourceFolders.get(i);
					for (File subFile:sourceFolder.listFiles())
					{
						if (subFile.getName().equalsIgnoreCase(name))
						{
							similarFolders.add(subFile);
							break;
						}
					}
				
					
				}
				recursiveConvert(similarFolders, new File( destinationFolder,name));
			}else
			{
				String name = file.getName();
				System.out.println(file.getName());
				ArrayList<File> similarFiles = new ArrayList<File>();
				similarFiles.add(file);
				for (int i = 1;i<sourceFolders.size();i++)
				{
					//find the same folder in each of the other source folders
					File sourceFolder = sourceFolders.get(i);
					for (File subFiles:sourceFolder.listFiles())
					{
						if (subFiles.getName().equalsIgnoreCase(name))
						{
							similarFiles.add(subFiles);
						}
					}
			
					//moves the generate mipmaps line from here
				}
				generateMipmaps(similarFiles,new File(destinationFolder,name));
			}
		}
		
	}
	
	private static void generateMipmaps(ArrayList<File> files, File destinationFile) throws IOException {
	
		List<BufferedImage> images = new ArrayList<BufferedImage>();
		int num = mainSIZE;
		for (File file:files)
		{
			if (file == null)
			{
				BufferedImage image = new BufferedImage(num,num,BufferedImage.TYPE_4BYTE_ABGR);
				BufferedImage prevImage = images.get(images.size()-1);
				Graphics2D g = (Graphics2D) image.getGraphics();
				g.drawImage(prevImage, 0,0,num,num,null);
				images.add(image);
			}else
			{
				BufferedImage image = ImageIO.read(file);
				images.add(image);
			}
			num/=2;
		}
		while (num>0)
		{
			BufferedImage image = new BufferedImage(num,num,BufferedImage.TYPE_4BYTE_ABGR);
			BufferedImage prevImage = images.get(images.size()-1);
			Graphics2D g = (Graphics2D) image.getGraphics();
			g.drawImage(prevImage, 0,0,num,num,null);
			images.add(image);
			num/=2;
		}
	
		destinationFile.createNewFile();
		OutputStream outputStream = new BufferedOutputStream(new FileOutputStream( destinationFile));
		
		ImageWriter pngWriter = ImageIO.getImageWritersBySuffix("png").next();
		pngWriter.setOutput(ImageIO.createImageOutputStream(outputStream));
		
		BufferedImage mipmap = generateMipmap(images);
		pngWriter.write(mipmap);
	}


	/**
	 * 
	 * @param images Images ordered from largest to smallest sizes
	 * @return
	 */
	public static BufferedImage generateMipmap(List<BufferedImage> images)
	{
		BufferedImage main = images.get(0);
		int width = main.getWidth();// + (horizontal?  (int) Math.ceil(((float) main.getHeight())/2f):0);
		int height = main.getHeight();//+ (horizontal? 0:  (int) Math.ceil(((float) main.getHeight())/2f));
		
		int t = main.getHeight();
		while(t>0)
		{
			t/=2;
			int size = t*t;
			size/= width;
			height += size;
		}
		height += 1;
		
		BufferedImage mipmap = new BufferedImage(width,height, BufferedImage.TYPE_4BYTE_ABGR);
	
		ByteBuffer mipmapBuffer = ByteBuffer.allocateDirect(width*height*4 -4);
		mipmapBuffer.order(ByteOrder.nativeOrder());
		
		//Graphics2D g =  (Graphics2D) mipmap.getGraphics();
		
		//g.drawImage(main, 0, 0, null);
	
		int curY = 0;//horizontal? 0: main.getHeight();
		int curX = 0;//horizontal? main.getWidth():0;
		for(int i = 0;i<images.size();i++)
		{ //lay out the remaining images below this one, horizontally
			BufferedImage image = images.get(i);
			BufferedImage inverted = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_4BYTE_ABGR);
			
		/*	//invert the image
			AffineTransform invert = AffineTransform.getScaleInstance(1, -1);
			
			invert.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));
			g.transform(invert);*/
			
	           int imageWidth = image.getWidth(null);
               int[] tmpData = new int[imageWidth];
               int row = 0;
            
               for(int y=image.getHeight(null)-1; y>=0; y--) {
            	   image.getRGB(0, (true ? row++ : y), imageWidth, 1, tmpData, 0, imageWidth);
            	   inverted.setRGB(0, y, imageWidth, 1, tmpData, 0, imageWidth);
               }
			image = inverted;
			
			ByteBuffer imageBuffer = getImageBuffer(image,true);
			imageBuffer.rewind();
			
			mipmapBuffer.put(imageBuffer);
		}
		
		mipmapBuffer.rewind();
		byte[] data = new byte[mipmapBuffer.capacity()];
		mipmapBuffer.get(data);
/*		byte[] temp = new byte[data.length];
		for (int i = 0;i<data.length;i++)
		{
			temp[temp.length-i-1] = data[i];
		}
		data = temp;*/
		mipmap.getRaster().setDataElements(0, 0, width, (data.length/width)/4, data);
		
	/*	for(int i = 0;i<mipmapBuffer.capacity();i+=4)
		{
		//The byte data is interleaved in a single byte array in the order A, B, G, R from lower to higher byte addresses within each pixel.
			int a =mipmapBuffer.get();
			int b =  mipmapBuffer.get();
			int g =mipmapBuffer.get();
			int r = mipmapBuffer.get();
			System.out.println(a + "\t" + r + "\t" + g + "\t" + b);
		a = 0;
			r = 8;
			b = 0;
			g = 8;
			
			
		//	mipmap.getRaster().setDataElements(curX, curY, width, height, mipmapBuffer)
			
		//	int p = a + b<<8 + (g<<16) + (r<<24) ;//+ (a<<24); 
			
			int pos = i/4;
			int x = pos%width;
			int y = pos/width;
			
			mipmap.setRGB(x, y, p);
		}*/
		return mipmap;
	}
	
	private static ByteBuffer getImageBuffer(BufferedImage tex, boolean hasAlpha)
	{
		
	     // Get a pointer to the image memory
	     ByteBuffer scratch = BufferUtils.createByteBuffer(4 * tex.getWidth() * tex.getHeight());
	     scratch.order(ByteOrder.nativeOrder());
	     byte data[] = (byte[]) tex.getRaster().getDataElements(0, 0,
	             tex.getWidth(), tex.getHeight(), null);
	     scratch.clear();
	     scratch.put(data);
	     scratch.flip();
	  
	     return scratch;
	}

	public static BufferedImage generateMipmap(String string) throws IOException {

		int num = mainSIZE;
		List<File> files = new ArrayList<File>();
		while(num>0)
		{
			
			File file = new File(string + num + ".png");
			if (file.exists())
				files.add(file);
			else 
				files.add(null);
			num/= 2;
		}
		List<BufferedImage> images = new ArrayList<BufferedImage>();
		num = mainSIZE;
		for (File file:files)
		{
			if (file == null)
			{
				BufferedImage image = new BufferedImage(num,num,BufferedImage.TYPE_4BYTE_ABGR);
				BufferedImage prevImage = images.get(images.size()-1);
				Graphics2D g = (Graphics2D) image.getGraphics();
				g.drawImage(prevImage, 0,0,num,num,null);
				images.add(image);
			}else
			{
				BufferedImage image = ImageIO.read(file);
				images.add(image);
			}
			num/=2;
		}
		return generateMipmap(images);
	}
	
}
