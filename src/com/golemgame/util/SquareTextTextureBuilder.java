package com.golemgame.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import java.util.ArrayList;

import com.jme.image.Texture;
import com.jmex.awt.swingui.ImageGraphics;

public class SquareTextTextureBuilder{
	/**
	 * text uses a constant width font only.
	 * Build a text that has a constant character width/height, regardless of the input text.
	 * @param font
	 * @param text
	 * @param charHeight
	 * @param interCharWidth
	 * @param interLineHeight
	 * @param textureWidth
	 * @param textureHeight
	 * @param textColor
	 * @return
	 */
	public static Texture buildSquareText(Font font, String text, int charHeight, int interCharWidth, int interLineHeight, int textureWidth, int textureHeight,Color textColor)
	{
		
		int totalLineHeight = charHeight+interLineHeight;
		int maxLines = textureHeight/totalLineHeight;//only print up to this many lines.

		
		font = TextTextureBuilder.getFontForSize(charHeight, font);
		ImageGraphics testSample = ImageGraphics.createInstance(1, 1, 0);//create a dummy graphics context to get font metrics properties
		testSample.setFont(font);
		FontMetrics metrics = testSample.getFontMetrics();
		
		int totalCharWidth =metrics.getWidths()[(int)'W']+ interCharWidth;//get width of 'W'
		int maxCharsPerLine = textureWidth/totalCharWidth;
		
		String[] lines = toLines(text,maxLines,maxCharsPerLine);

		/*int lineWidth = 0; 
		for (String line:lines)
		{//Get the longest width of any one line
			int curWidth = metrics.stringWidth(line);
			if (curWidth>lineWidth)
				lineWidth = curWidth;
		}*/

		int lineHeight = interCharWidth + charHeight;// metrics.getHeight();		
		int lineAscent = charHeight;//metrics.getAscent();
		//int width = lineWidth ;
	//	int height = (lines.length-1) * lineHeight + lineAscent + metrics.getDescent();
		testSample.dispose();
		
	
		
		ImageGraphics textImage = ImageGraphics.createInstance(textureWidth , textureHeight, 0);
		textImage.setFont(font);
		textImage.scale(1, -1);
		textImage.translate(0,-textureHeight);	//upside down	
		textImage.setColor(textColor);
		
	//	textImage.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
		
		for (int i = 0; i < lines.length;i++)
		{
			textImage.drawString(lines[i], 0,  (i)*lineHeight + lineAscent );
		}
			
    	textImage.update();      

        Texture textTexture = new Texture(); 
        textTexture.setApply(Texture.AM_DECAL);
     //   textTexture.setBlendColor(new ColorRGBA(1, 1, 1, 1));     
        textTexture.setFilter(Texture.MM_LINEAR);
        textTexture.setImage(textImage.getImage());
        textTexture.setMipmapState(Texture.FM_LINEAR);

        return textTexture;
	}
	
	/**
	 * Build a text texutre to fill the given texture size, centered.
	 * @param font
	 * @param text
	 * @param textureWidth
	 * @param textureHeight
	 * @param textColor
	 * @return
	 */
	public static Texture buildSquareText(Font font, String text, int textureWidth, int textureHeight,Color textColor)
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
	
		float scaleW = ((float)textureWidth)/((float)width);
		float scaleH = ((float)textureHeight)/((float)height);
		
		ImageGraphics textImage = ImageGraphics.createInstance(textureWidth , textureHeight, 0);
		textImage.setColor(Color.black); //new Color(0,0,0,0f));
		textImage.fillRect(0, 0, textureWidth, textureHeight);
		textImage.setFont(font);
		textImage.scale(scaleW, -scaleH);
		textImage.translate(0,-height);		
		textImage.setColor(textColor);
		
		textImage.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
		
		for (int i = 0; i < lines.length;i++)
		{
			textImage.drawString(lines[i], 0,  (i)*lineHeight + lineAscent );
		}
			
    	textImage.update();      

        Texture textTexture = new Texture(); 
        textTexture.setApply(Texture.AM_DECAL);
     //   textTexture.setBlendColor(new ColorRGBA(1, 1, 1, 1));     
        textTexture.setFilter(Texture.MM_LINEAR);
        textTexture.setImage(textImage.getImage());
        textTexture.setMipmapState(Texture.FM_LINEAR);

        return textTexture;
	}
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
		
	private static String[] toLines(String text, int maxLines, int maxCharsPerLine)
	{
		ArrayList<String> lines = new ArrayList<String>();
		String cur = text;
	
		int linePos = 0;
		while ((linePos = cur.indexOf("\n"))>=0)
		{
			lines.add(cur.substring(0, Math.min(linePos, maxCharsPerLine)));//cur off the line if it is more than max chars per line
			if(lines.size()>=maxLines)
				break;//stop processing here
			
			if (linePos < cur.length())
			{
				cur = cur.substring(linePos +1);
			}
			else
				break;
		}
		if (linePos < cur.length())
			lines.add(cur.substring(linePos+1));
		
		return lines.toArray(new String[lines.size()]);
		
	}
	public static Texture buildSquareText(String text) {
		Font f = Font.decode("Sans-Serif").deriveFont(12).deriveFont(Font.PLAIN);
		return buildSquareText(f,text,256,256,Color.white);
	}
		

	
}
