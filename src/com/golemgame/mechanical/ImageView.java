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
package com.golemgame.mechanical;

import java.awt.image.BufferedImage;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SustainedView;
import com.golemgame.mvc.golems.ImageInterpreter;
import com.golemgame.mvc.golems.ImageInterpreter.ImageCompression;

public class ImageView implements SustainedView {

	//private static final BufferedImage defaultImage = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
	private BufferedImage image;
	private ImageInterpreter interpreter;
	
	public ImageView(PropertyStore store)
	{
		interpreter = new ImageInterpreter(store);
		store.setSustainedView(this);
	}
	
	public void refresh() {
		
		image = interpreter.getImage();
	

	}

	public PropertyStore getStore() {
		return interpreter.getStore();
	}

	public void remove() {
		image = null;
	}
	
	public BufferedImage getImage()
	{
		return image;
	}
	
	public void setImage(BufferedImage image,ImageCompression compression) 
	{
		interpreter.setImage(image, compression);
	}
	public void setImage(BufferedImage image) 
	{
		interpreter.setImage(image, ImageCompression.CHOOSE);
	}
	

}
