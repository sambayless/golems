/*
 * Code courtesy of jME:
 * http://www.jmonkeyengine.com/wiki/doku.php?id=using_java2d_to_create_the_hud_texture
 */

package com.golemgame.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.animation.SpatialTransformer;
import com.jme.image.Image;

public abstract class PaintableImage extends Image {
	private static final long serialVersionUID = 1L;
	     private BufferedImage backImg;
	     private ByteBuffer scratch;
	     public PaintableImage(int width, int height, boolean hasAlpha) {
	          super();
	          try {
	               backImg = new BufferedImage(width, height, hasAlpha
	                             ? BufferedImage.TYPE_4BYTE_ABGR             
	                             : BufferedImage.TYPE_3BYTE_BGR);
	               setType(hasAlpha
	                   ? com.jme.image.Image.RGBA8888
	                   : com.jme.image.Image.RGB888);
	               setWidth(backImg.getWidth());
	               setHeight(backImg.getHeight());
	               scratch = ByteBuffer.allocateDirect(4 * backImg.getWidth()
	                    * backImg.getHeight());
	          } catch (IllegalArgumentException e) {
	              Logger.getLogger(SpatialTransformer.class.getName()).log(Level.WARNING,
	              "Problem creating buffered Image: " + e.getMessage());
	          }
	          refreshImage();
	     }
	     
	     public void refreshImage() {
	         Graphics2D g = backImg.createGraphics();
	         paint(g);
	         g.dispose();
	 
	         /* get the image data */
	         byte data[] = (byte[]) backImg.getRaster().getDataElements(0, 0,
	             backImg.getWidth(), backImg.getHeight(), null);
	         scratch.clear();
	         scratch.put(data, 0, data.length);
	         scratch.rewind();
	         setData(scratch);
	     }
	     
	     /**
	      * Implement this method so that it contains your drawing calls. The image
	      * state is preserved so if you want to clear the image to completely redraw
	      * it, you must provide the function yourself.
	      * 
	      * @param graphicsContext
	      *            Tha Graphics2D context in which the painting can be performed.
	      */
	     public abstract void paint(Graphics2D graphicsContext);
	 
	 
}
