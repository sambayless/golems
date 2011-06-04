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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

public class MultifaceTexture {
    private static final Random RANDOM = new Random();
    private static final Font FONT = new Font("Arial Unicode MS", Font.PLAIN,
            30);
    private static Color[] colors = new Color[] { Color.red, Color.green, Color.blue,
            Color.yellow, Color.white, Color.orange };
	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		int width = 512;
		  final BufferedImage bi = new BufferedImage(width, width*8,
	                BufferedImage.TYPE_INT_ARGB);
	        Graphics2D bg = (Graphics2D) bi.getGraphics();
	        bg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                RenderingHints.VALUE_ANTIALIAS_ON);
	        bg.setFont(FONT);
	        for (int i = 0; i < 6; i++) {
	            bg.setColor(colors[i]);
	            bg.fillRect(0, i * width, width, (i + 1) * width);
	            bg.setColor(Color.black);
	            bg.drawString("" + i, 28, width * i + 38);
	        }
	        bg.dispose();
	        ImageIO.write(bi, "png",  ImageIO.createImageOutputStream(new FileOutputStream("faceTexture.png")));
	       
	}

}
