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
package com.golemgame.mvc.golems;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import com.golemgame.mvc.BinaryType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.DataType.Type;

public class ImageInterpreter extends StoreInterpreter {
	public static final String IMAGE_CLASS = "image";
	public static final String IMAGE_DATA = "image.data";
	public static final String IMAGE_TYPE = "image.type";
	
	
	public static enum ImageCompression
	{
		None("bmp"),PNG("png"),JPG("jpeg"),BMP("bmp"),GIF("gif"), CHOOSE("Choose");
		private final String name;

		private ImageCompression(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
		
	}
	
	private static final ImageCompression[] choices = new ImageCompression[]{ImageCompression.JPG,ImageCompression.PNG};
	
	public ImageInterpreter() {
		this(new PropertyStore());
	}

	public ImageInterpreter(PropertyStore store) {
		super(store);
		getStore().setClassName(IMAGE_CLASS);
	}
		
	public void setImage(BufferedImage image, ImageCompression compression)
	{
		//write the iamge into a binary buffer using the chosen compression/image type scheme:
			if (image== null)
			{				
				return;
			}
			
			try {
				if(compression == ImageCompression.CHOOSE)
				{
					byte[]  smallestOutput = null;
					int smallest = Integer.MAX_VALUE;
					ImageCompression smallestC = ImageCompression.None;
					for(ImageCompression c:choices)
					{

						ByteArrayOutputStream output = new ByteArrayOutputStream();
					

						ImageOutputStream imageOut;
						imageOut = ImageIO.createImageOutputStream(output);
						ImageIO.write(image, c.getName(), imageOut); 
					
						
						if(	output.size()<smallest)
						{
							byte[] byteOut = output.toByteArray();
							smallest = byteOut.length;
							smallestOutput = byteOut;
							smallestC = c;
						}
					}
					BinaryType binary = new BinaryType(ByteBuffer.wrap(smallestOutput));
					
					getStore().setProperty(IMAGE_TYPE, smallestC);
					getStore().setProperty(IMAGE_DATA, binary);
				}else
				{
					BinaryType binary ;

					ByteArrayOutputStream output = new ByteArrayOutputStream();

					ImageOutputStream imageOut;
					imageOut = ImageIO.createImageOutputStream(output);
					ImageIO.write(image, compression.getName(), imageOut); 
					binary = new BinaryType(ByteBuffer.wrap(output.toByteArray()));
					
					getStore().setProperty(IMAGE_TYPE, compression);
					getStore().setProperty(IMAGE_DATA, binary);
				}
			
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
	
		//getStore().refresh();//probably too expensive
		
	}
	
	public BufferedImage getImage()
	{
		ImageCompression compression = getStore().getEnum(IMAGE_TYPE, ImageCompression.None);
		DataType binaryData = getStore().getProperty(IMAGE_DATA);
		if(binaryData == null || binaryData.getType()!=Type.BINARY)
			return null;
		BinaryType binary = (BinaryType) binaryData;
		ByteArrayInputStream input = new ByteArrayInputStream(binary.getArray());
		try {
			return ImageIO.read(input);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		//return new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
	}

}
