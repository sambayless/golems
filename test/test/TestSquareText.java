package test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;

import com.jme.image.Texture;
import com.jmex.awt.swingui.ImageGraphics;

public class TestSquareText {
    public static void main(String[] args) {
    	JFrame frame = new JFrame();
    	final BufferedImage image = buildSquareText("Test");
    	JLabel label = new JLabel()
    	{

			@Override
			protected void paintComponent(Graphics g) {
				g.drawImage(image,0,0,image.getWidth(),image.getHeight(),null);
			}
    		
    	};
    	frame.add(label,BorderLayout.CENTER);
    	frame.setVisible(true);
    }
    
    public static BufferedImage buildSquareText(Font font, String text, int textureWidth, int textureHeight,Color textColor)
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
		
		BufferedImage textImage = new BufferedImage(textureWidth,textureHeight, BufferedImage.TYPE_4BYTE_ABGR);//ImageGraphics.createInstance(textureWidth , textureHeight, 0);
		Graphics2D g =(Graphics2D) textImage.getGraphics();
		g.setFont(font);
		g.scale(scaleW, -scaleH);
		g.translate(0,-height);		
		g.setColor(textColor);
		
		//textImage.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
		
		for (int i = 0; i < lines.length;i++)
		{
			g.drawString(lines[i], 0,  (i)*lineHeight + lineAscent );
		}
			
    	   


        return textImage;
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
	public static BufferedImage buildSquareText(String text) {
		Font f = Font.decode("Sans-Serif").deriveFont(12).deriveFont(Font.PLAIN);
		return buildSquareText(f,text,256,256,Color.white);
	}
		

}
