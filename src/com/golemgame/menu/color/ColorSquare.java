package com.golemgame.menu.color;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import org.fenggui.StatefullWidget;
import org.fenggui.appearance.DefaultAppearance;
import org.fenggui.binding.render.Binding;
import org.fenggui.binding.render.Graphics;
import org.fenggui.binding.render.IOpenGL;
import org.fenggui.binding.render.ITexture;
import org.fenggui.binding.render.Pixmap;
import org.fenggui.event.mouse.MouseAdapter;
import org.fenggui.event.mouse.MouseDraggedEvent;
import org.fenggui.event.mouse.MousePressedEvent;
import org.fenggui.util.Color;
import org.fenggui.util.Dimension;

public class ColorSquare extends StatefullWidget<DefaultAppearance> {
	private ByteBuffer data ;
	
	private ColorData color;

	private Pixmap wheel;

	private int width;
	private int height;
	
	
	
	private boolean needsRefresh = true;
	private ArrayList<ColorListener> listeners = new ArrayList<ColorListener>();
	
	public ColorSquare(final int width, final int height) {
		super();
		this.setAppearance(new DefaultAppearance(this));
		color = new ColorData();
		color.setLightness(0.5f);
		this.width = width;
		this.height = height;

	//	colorWheel  =ImageConverter.createGlCompatibleAwtImage(wid,size);
		int length = width*height*3;
		data  = ByteBuffer.allocateDirect(length); 
		  data.order(ByteOrder.nativeOrder()); 
		//buildColorBuffer(data, width,height);
		//colorWheel = new BufferedImage(ImageConverter.COLOR_MODEL,)
		refresh();
		this.setMinSize(32,32);
		this.addMouseListener(new MouseAdapter()
		{

			@Override
			public void mouseDragged(MouseDraggedEvent mouseDraggedEvent) {
				int x = mouseDraggedEvent.getLocalX(ColorSquare.this);
				int y= mouseDraggedEvent.getLocalY(ColorSquare.this);
				
				ColorData color = getColor(x,y);
				ColorSquare.this.color = color;
				selectColor(color);
				
			}	

			@Override
			public void mousePressed(MousePressedEvent mousePressedEvent) {
				int x = mousePressedEvent.getLocalX(ColorSquare.this);
				int y= mousePressedEvent.getLocalY(ColorSquare.this);
		
				ColorData color = getColor(x,y);
				ColorSquare.this.color = color;
				selectColor(color);
			}
			
		});
		
	}
	
	private void selectColor (ColorData color)
	{
		for (ColorListener listener:listeners)
			listener.colorSelected(color);
	}

	public ColorData getColor() {
		return color;
	}

	public void setColor(ColorData color) {
		this.color = color;
	}

	@Override
	public Dimension getMinContentSize() {
		return this.getMinSize();
	}


	
	public void refresh()
	{
		needsRefresh =true;
	}

	private void buildColorBuffer(ByteBuffer imageBuffer, int width,int height)
	{
   		for (int y = 0; y < height; y++)
		{
			for ( int x = 0; x<width;x++)
			{
				byte[] rgb = getColorArray(x,y,width,height);
				imageBuffer.put(rgb);
			}
		}
        imageBuffer.flip();
	}

	

		
	private double[] hslTorgb(double h, double s, double l)
	{
		double q;
		if (l<0.5)
			q = l*(1+s);
		else
			q = l + s - (l*s);
		double p = 2*l-q;
		double hk = h;
		double[] tC = new double[3];
		tC[0] = hk + 1.0/3.0;
		tC[1] = hk;
		tC[2] = hk - (1.0/3.0);
		if (tC[0]<0)
			tC[0]+=1.0;
		else if (tC[0]>1.0)
			tC[0]-=1.0;
		if (tC[1]<0)
			tC[1]+=1.0;
		else if (tC[1]>1.0)
			tC[1]-=1.0;
		if (tC[2]<0)
			tC[2]+=1.0;
		else if (tC[2]>1.0)
			tC[2]-=1.0;
		
		for (int i = 0; i<3;i++)
		{
			double c = tC[i];
			if (c < 1.0/6.0)
			{
				tC[i] = p + (q-p)*6*c;
			}else if (c<0.5)
			{
				tC[i] = q;
			}else if (c < 2.0/3.0)
			{
				tC[i] = p + (q-p)*6*(2.0/3.0-c);
			}else
				tC[i] = p;
		}
		return tC;
	}
	
	private byte[] getColorArray(int xPos, int yPos, int width,int height)
	{


			double saturation = ((double)(height-yPos))/((double)height);
			double hue = ((double) xPos)/((double)width);
			
			double[] rgbArray = hslTorgb(hue,saturation,color.getLightness());
			
			byte [] rgba = new byte[3];
			
			//rgba[3] = (byte) 255;
			
			rgba[0] = (byte)Math.round(rgbArray[0] * 255.0);
			rgba[1] = (byte)Math.round(rgbArray[1] * 255.0);
			rgba[2] = (byte)Math.round(rgbArray[2] * 255.0);
			
			return rgba;
			
	}
	
	
	private ColorData getColor(int xPos, int yPos, int width,int height)
	{

		double saturation = ((double)(yPos))/((double)height);
		double hue = ((double) xPos)/((double)width);
		ColorData newColor = new ColorData();
		newColor.setHue( hue);
		newColor.setSaturation(saturation);
		newColor.setLightness(color.getLightness());
		newColor.setAlpha(this.color.getAlpha());
		double[] rgbArray = hslTorgb(hue,saturation,color.getLightness());

		int [] rgb = new int[3];
		
		//rgba[3] = (byte) 255;
		
		rgb[0] = (int)Math.round(rgbArray[0] * 255.0);
		rgb[1] = (int)Math.round(rgbArray[1] * 255.0);
		rgb[2] = (int)Math.round(rgbArray[2] * 255.0);
		newColor.setR(rgb[0]);
		newColor.setG(rgb[1]);
		newColor.setB(rgb[2]);
		//color.setRgb(  getRGB(rgbArray[0],rgbArray[1],rgbArray[2],1.0));
		return newColor;
	}

	private int[] getPos()
	{
		int[] pos = new int[2];
		
		pos [0] =(int)Math.round( ( color.getHue() * (double) this.getWidth()));
		pos [1] =(int)Math.round( ( color.getSaturation() * (double) this.getHeight()));
		return pos;
	}


	
	@Override
	public void paintContent(Graphics g, IOpenGL gl) {
		if(this.getHeight()<= 0 || this.getWidth()<= 0)
			return;

		if (needsRefresh)
		{
			needsRefresh = false;
			data.rewind();

			buildColorBuffer(data,width,height);
		
			
			ITexture texture = Binding.getInstance().getTexture(data, width, height, false);
			
			wheel = new Pixmap(texture);
		}
		
	
		gl.lineWidth(1f);
		g.setColor(1f,1f,1f,(float)color.getAlpha());
		g.drawFilledRectangle(0, 0, this.getWidth(), this.getHeight());
		g.drawScaledImage(wheel, 0, 0,this.getWidth(), this.getHeight());//force the wheel to be square
		int[] pos = getPos();

			g.drawBevelRectangle(pos[0]-4, pos[1] - 4, 8, 8, Color.BLACK, Color.WHITE);
		
		
	}
	
	public ColorData getColor(int x, int y)
	{
		
		return getColor(x,y,getWidth(),getHeight());
	}
	

	public void addColorListener(ColorListener listener)
	{
		listeners.add(listener);
	}
	
	public void removeColorListener(ColorListener listener)
	{
		listeners.remove(listener);
	}
	

}
