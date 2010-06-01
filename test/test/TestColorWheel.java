package test;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class TestColorWheel extends JFrame {

	public TestColorWheel() throws HeadlessException {
		super();
		ColorCanvas colorCanvas = new ColorCanvas();
		this.add(colorCanvas);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		
		JFrame frame = new TestColorWheel();
		frame.setSize(400, 400);
		frame.setVisible(true);
		
	}
}

class ColorCanvas extends Canvas
{
	
	
	public ColorCanvas() {
		super();
		Graphics2D graphics = (Graphics2D) colorWheel.getGraphics();
		for (int x = 0; x<100;x++)
		{
			for (int y = 0; y < 100; y++)
			{
				int rgb = getColor(x,y,100);
			
				colorWheel.setRGB(x, y, rgb);
			}
		}
		
	}
	
	private int getRGB(double r, double g, double b, double a)
	{
	     return getRGB((int)(r*255+0.5), (int)(g*255+0.5), (int)(b*255+0.5), (int)(a*255+0.5));
	}
	
	private int getRGB(int r, int g, int b, int a)
	{
	         return ((a & 0xFF) << 24) |
	                 ((r & 0xFF) << 16) |
	                 ((g & 0xFF) << 8)  |
	                 ((b & 0xFF) << 0);
	}
		
	private double[] hsvTorgb(double h, double s, double v)
	{

		double hOver60 = h/(Math.PI/3.0) ;//(60.0);
		int hi =((int)Math.floor(hOver60))  % 6;
		double f= hOver60 - Math.floor(hOver60);
		
		double p = v * (1.0-s);
		double q = v*(1.0-f*s);
		double t = v*(1.0-(1.0-f)*s);
		
		switch(hi)
		{
			case 0:
				return new double[]{v,t,p};
			case 1:
				return new double[]{q,v,p};
			case 2:
				return new double[]{p,v,t};
			case 3:
				return new double[]{p,q,v};
			case 4:
				return new double[]{t,p,v};
			case 5:
				return new double[]{v,p,q};
			
		}
		//not reachable
		return null;
		
	}

	private int getColor(int xPos, int yPos, int width)
	{
		double w2 = width/2.0;
		double w3 = w2/2.0;
		
		double x = xPos - w2;
		double y = width - yPos - w2;
		double sv = Math.sqrt(Math.pow(x, 2)+Math.pow(y,2));
		double hue=Math.atan2(x,y);
		if (hue < 0)
			hue += Math.PI*2.0;

		double s = sv<w3?sv/w3*1.0:1.0;
		double v = sv>w3?Math.max(0.0,1.0-((sv-w3)/(w2-w3))*1.0):1.0;

		double[] rgbArray = hsvTorgb(hue,s,v);
		return getRGB(rgbArray[0],rgbArray[1],rgbArray[2],1.0);
	}
	
	BufferedImage colorWheel = new BufferedImage(100,100,BufferedImage.TYPE_INT_ARGB);

	@Override
	public void paint(Graphics g) {
		Graphics2D graphics = (Graphics2D) g;
		graphics.drawImage(colorWheel,0,0,null);

		g.dispose();
	}
	
}
