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
package com.golemgame.model.texture;

import java.io.Serializable;

import com.golemgame.model.texture.TextureWrapper.TextureFormat;
import com.golemgame.mvc.golems.AppearanceInterpreter.ImageType;
import com.jme.system.DisplaySystem;

/**
 * A class used to identify loaded textures by their properties.
 * 
 * @author Sam
 *
 */
public class TextureTypeKey implements Serializable{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Identify the texture by the type of wrapping/mapping needed to display it properly. A particular image type may have multiple versions for different types.
	 * All image types support 'Plane'
	 * @author Sam
	 *
	 */
	public static enum TextureShape
	{
		Plane,Box,Sphere,Cylinder,Capsule,Cone;
	}
	
	private  ImageType image;
	private  TextureFormat format;
	private  boolean useMipmaps;
	private TextureShape shape;
	private String text = null;//most textures have no associated text.
	
	public void setText(String text) {
		this.text = text;
	}


	public String getText() {
		return text;
	}


	private int element = 0;
	



	public void setElementNumber(int element) {
		this.element = element;
	}


	public int getElementNumber() {
		return element;
	}

	
	public TextureShape getShape() {
		return shape;
	}


	private float width;
	private float height;
	
	/**
	 * 
	 * @param image
	 * @param width This is the requested width, as a fraction of the screen width. 
	 * @param height This is the requested height, as a fraction of the screen height.
	 * @param format
	 * @param useMipmaps
	 */
	public TextureTypeKey(ImageType image, float width, float height,  TextureFormat format, boolean useMipmaps) {
		this(image,width,height,format,useMipmaps,TextureShape.Plane);
	}
	
	public TextureTypeKey(ImageType image, int width, int height,  TextureFormat format, boolean useMipmaps) {
		this(image,width,height,format,useMipmaps,TextureShape.Plane);
	}
	
	public TextureTypeKey(ImageType image, float width, float height,  TextureFormat format, boolean useMipmaps, TextureShape requestedShape) {
		super();
		this.image = image;
		this.format = format;
		this.useMipmaps = useMipmaps;
		this.width = width;
		this.height = height;
		
		if(Images.getInstance().isShapeAvailable(image, requestedShape))
			this.shape = requestedShape;
		else
			this.shape = TextureShape.Plane;
	}
	
	public TextureTypeKey(ImageType image, int width, int height,  TextureFormat format, boolean useMipmaps, TextureShape requestedShape) {
		super();
		this.image = image;
		this.format = format;
		this.useMipmaps = useMipmaps;
		this.width = ((float) width)/(float)DisplaySystem.getDisplaySystem().getWidth();
		this.height = ((float) height)/(float)DisplaySystem.getDisplaySystem().getHeight();
		
		if(Images.getInstance().isShapeAvailable(image, requestedShape))
			this.shape = requestedShape;
		else
			this.shape = TextureShape.Plane;
	}
	public TextureTypeKey(ImageType image, float width, float height,  TextureFormat format, boolean useMipmaps, TextureShape requestedShape, int element) {
		super();
		this.element = element;
		this.image = image;
		this.format = format;
		this.useMipmaps = useMipmaps;
		this.width = ((float) width)/(float)DisplaySystem.getDisplaySystem().getWidth();
		this.height = ((float) height)/(float)DisplaySystem.getDisplaySystem().getHeight();
		
		if(Images.getInstance().isShapeAvailable(image, requestedShape))
			this.shape = requestedShape;
		else
			this.shape = TextureShape.Plane;
	}
	
	public TextureTypeKey(TextureTypeKey textureTypeKey) {
		this(textureTypeKey.image,textureTypeKey.width,textureTypeKey.height,textureTypeKey.format,textureTypeKey.useMipmaps,textureTypeKey.shape,textureTypeKey.element);
	}


	public int getTextureWidth()
	{
		return nearestPowerOfTwo((int)(DisplaySystem.getDisplaySystem().getWidth() * width));
	}
	
	public int getTextureHeight()
	{
		return nearestPowerOfTwo((int)(DisplaySystem.getDisplaySystem().getHeight() * height));
	}
	
	public static int nearestPowerOfTwo(int from)
	{
		
		int power = Integer.highestOneBit(from - 1);
		return 2 * power; 
		
		
	//	return Math.pow(2, Math.log(from)/Math.log(2));
	}
	
	public float getWidth() {
		return width;
	}
	public float getHeight() {
		return height;
	}
	public ImageType getImage() {
		return image;
	}
	public TextureFormat getFormat() {
		return format;
	}
	

	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + element;
		result = prime * result + ((format == null) ? 0 : format.hashCode());
		result = prime * result + Float.floatToIntBits(height);
		result = prime * result + ((image == null) ? 0 : image.hashCode());
		result = prime * result + ((shape == null) ? 0 : shape.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + (useMipmaps ? 1231 : 1237);
		result = prime * result + Float.floatToIntBits(width);
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TextureTypeKey other = (TextureTypeKey) obj;
		if (element != other.element)
			return false;
		if (format == null) {
			if (other.format != null)
				return false;
		} else if (!format.equals(other.format))
			return false;
		if (Float.floatToIntBits(height) != Float.floatToIntBits(other.height))
			return false;
		if (image == null) {
			if (other.image != null)
				return false;
		} else if (!image.equals(other.image))
			return false;
		if (shape == null) {
			if (other.shape != null)
				return false;
		} else if (!shape.equals(other.shape))
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		if (useMipmaps != other.useMipmaps)
			return false;
		if (Float.floatToIntBits(width) != Float.floatToIntBits(other.width))
			return false;
		return true;
	}


	/*
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((format == null) ? 0 : format.hashCode());
		result = prime * result + height;
		result = prime * result + ((image == null) ? 0 : image.hashCode());
		result = prime * result + (useMipmaps ? 1231 : 1237);
		result = prime * result + width;
		return result;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final TextureTypeKey other = (TextureTypeKey) obj;
		if (format == null) {
			if (other.format != null)
				return false;
		} else if (!format.equals(other.format))
			return false;
		if (height != other.height)
			return false;
		if (image == null) {
			if (other.image != null)
				return false;
		} else if (!image.equals(other.image))
			return false;
		if (useMipmaps != other.useMipmaps)
			return false;
		if (width != other.width)
			return false;
		return true;
	}*/
	public boolean useMipmaps() {
		return useMipmaps;
	}
	
	
	

	
	public void loadInitialize() {

	}

	
}
