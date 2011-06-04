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
package com.golemgame.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.jme.image.Texture;
import com.jmex.awt.swingui.ImageGraphics;

/**
 * A utility class to build textures containing arbitrary text, rendered with Java2D.
 * @author Sam
 *
 */
public class TextTextureBuilder {
	private static String[] toLines(String text)
	{
		ArrayList<String> lines = new ArrayList<String>();
		String cur = text;
	
		int linePos = 0;
		while ((linePos = cur.indexOf("\n"))>=0)
		{
			lines.add(cur.substring(0, linePos));
			if (linePos < cur.length())
				cur = cur.substring(linePos +1);
			else
				break;
		}
		if (linePos < cur.length())
			lines.add(cur.substring(linePos+1));
		
		return lines.toArray(new String[lines.size()]);
		
	}
		
	/**
	 * Constructs a texture displaying the provided text. The text will be broken down into lines, and rendered onto a texture. 
	 * The height and width of the texture will be placed into the provided Dimension (size).
	 * @param text The text to build the texture around
	 * @param storeSize A Dimension instance to place the height and width of the text into.
	 * @return A texture containing text. 
	 */
	public static Texture buildTextTexture(String text, Font font, Color textColor, Dimension storeSize)
	{
		String[] lines = toLines(text);
		ImageGraphics testSample = ImageGraphics.createInstance(1, 1, 0);//create a dummy graphics context to get font metrics properties
		testSample.setFont(font);
		FontMetrics metrics = testSample.getFontMetrics();

		int lineWidth = 0; 
		for (String line:lines)
		{//Get the longest width of any one line
			int curWidth = metrics.stringWidth(line);
			if (curWidth>lineWidth)
				lineWidth = curWidth;
		}

		int lineHeight = metrics.getHeight();		
		int lineAscent = metrics.getAscent();
		int width = lineWidth ;
		int height = (lines.length-1) * lineHeight + lineAscent + metrics.getDescent();
		testSample.dispose();	
		storeSize.width = width;
		storeSize.height = height;
		ImageGraphics textImage = ImageGraphics.createInstance(width , height, 0);
		textImage.setFont(font);
		textImage.scale(1, -1);
		textImage.translate(0,-height);		
		textImage.setColor(textColor);
		
		//textImage.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
		
		for (int i = 0; i < lines.length;i++)
		{
			textImage.drawString(lines[i], 0,  (i)*lineHeight + lineAscent );
		}
			
    	textImage.update();      

        Texture textTexture = new Texture(); 
        textTexture.setApply(Texture.AM_MODULATE);
     //   textTexture.setBlendColor(new ColorRGBA(1, 1, 1, 1));     
        textTexture.setFilter(Texture.MM_LINEAR);
        textTexture.setImage(textImage.getImage());
        textTexture.setMipmapState(Texture.FM_LINEAR);

        return textTexture;
	}
	
	public static Texture buildTextTexture(String text,Font requestedFont,  Color textColor, int height,Dimension storeSize)
	{
		Font font = getFontForSize(height, requestedFont);
		return buildTextTexture(text,font,textColor,  storeSize);
	}
	
	public static Texture buildTextTexture(String text, Color textColor, Dimension storeSize)
	{
		return buildTextTexture(text, Font.decode("Sans-Serif").deriveFont(12).deriveFont(Font.PLAIN) ,textColor,storeSize);
	}
	public static Dimension getTextSize(Font font, String text)
	{
		
		BufferedImage temp = new BufferedImage(1,1,BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = temp.createGraphics();

		FontMetrics metrics = g.getFontMetrics(font);

		int textHeight =(int) metrics.getAscent();
		int textWidth = metrics.stringWidth(text);
		
		return new Dimension(textWidth,textHeight);

	}
	
	public static Font getFontForSize(int height, Font font)
	{
		
		//determine which font size will be the closest size within this restriction.
		BufferedImage temp = new BufferedImage(1,1,BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = temp.createGraphics();

		FontMetrics metrics = g.getFontMetrics(font);

		Font chooseFont = font;
	
		
		int textHeight =(int) metrics.getAscent() - metrics.getDescent();
	

		if (textHeight>height)
		{
	
			chooseFont = font.deriveFont(1f);
			metrics = g.getFontMetrics(chooseFont);
	
			textHeight =(int) metrics.getAscent()- metrics.getDescent();
		}

		while( textHeight <= height)
		{
			
			Font testFont = chooseFont.deriveFont((float) chooseFont.getSize()+1);
			metrics = g.getFontMetrics(chooseFont);
		
			textHeight =(int) metrics.getAscent()- metrics.getDescent();
			
			if (  textHeight <= height)
				chooseFont = testFont;
			
		}
		
		return chooseFont;
	}
}
