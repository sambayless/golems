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
