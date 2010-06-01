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

public class LightnessMeter extends StatefullWidget<DefaultAppearance> {

	protected ColorData color;
	private Pixmap wheel;
	private ByteBuffer data ;
	private int width;
	private int height;
	private boolean needsRefresh = true;
	private static final int SNAP = 2;
	private ArrayList<ColorListener> listeners = new ArrayList<ColorListener>();
	

	
	public LightnessMeter(final int width,final int height) {
		super();
		this.setAppearance(new DefaultAppearance(this));
		this.width = 4;
		this.height = height;
		color = new ColorData();

		int length = this.width*this.height*3;
		data  = ByteBuffer.allocateDirect(length); 
		  data.order(ByteOrder.nativeOrder()); 
		
		//buildTexture();
		
		refresh();
		this.addMouseListener(new MouseAdapter()
		{

			@Override
			public void mouseDragged(MouseDraggedEvent mouseDraggedEvent) {
				int x = mouseDraggedEvent.getLocalX(LightnessMeter.this);
				int y= mouseDraggedEvent.getLocalY(LightnessMeter.this);
				if (Math.abs(y - getHeight()) <= SNAP)
					y = getHeight();
				if(Math.abs(y) <= SNAP)
					y =0;
				
				if (Math.abs(x - getWidth()) <= SNAP)
					x = getWidth();
				if(Math.abs(x) <= SNAP)
					x =0;
				
				ColorData color = getColor(x,y);
				LightnessMeter.this.color = color;
				selectColor(color);
				
			}	

			@Override
			public void mousePressed(MousePressedEvent mousePressedEvent) {
				int x = mousePressedEvent.getLocalX(LightnessMeter.this);
				int y= mousePressedEvent.getLocalY(LightnessMeter.this);
			
				ColorData color = getColor(x, y);
				LightnessMeter.this.color = color;
				selectColor(color);
				
			}
			
		});
		this.setMinSize(32,0);
	}
	private void selectColor (ColorData color)
	{
		for (ColorListener listener:listeners)
			listener.colorSelected(color);
	}
	
    @Override
	public Dimension getMinContentSize() {
		return this.getMinSize();
	}

	public void refresh()
	{
		needsRefresh = true;

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
		
	protected double[] hslTorgb(double h, double s, double l)
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
	
	protected byte[] getColorArray(int xPos, int yPos, int width,int height)
	{


		double lightness = ((double)(height -yPos))/((double)height);

		double[] rgbArray = hslTorgb(color.getHue(),color.getSaturation(),lightness);
			
			byte [] rgba = new byte[3];
			
			//rgba[3] = (byte) 255;
			
			rgba[0] = (byte)Math.round(rgbArray[0] * 255.0);
			rgba[1] = (byte)Math.round(rgbArray[1] * 255.0);
			rgba[2] = (byte)Math.round(rgbArray[2] * 255.0);
			
			return rgba;
			
	}

	@Override
	public void paintContent(Graphics g, IOpenGL gl) {
		gl.lineWidth(1f);
		if (needsRefresh)
		{
			needsRefresh = false;
			data.rewind();

			buildColorBuffer(data,width,height);

			
			ITexture texture = Binding.getInstance().getTexture(data, width, height, false);

			wheel = new Pixmap(texture);
		}
		g.setColor(1f,1f,1f,(float)color.getAlpha());
		g.drawFilledRectangle(0, 0, this.getWidth(), this.getHeight());
		g.drawScaledImage(wheel, 0, 0,this.getWidth(), this.getHeight());//force the wheel to be square
		
		{
			g.drawBevelRectangle(0, getPos() - 4, this.getWidth(), 8, Color.BLACK, Color.WHITE);
		}
	}
	

	protected ColorData getColor(int xPos, int yPos, int width,int height)
	{

		double lightness = ((double)(yPos))/((double)height);

		ColorData newColor = new ColorData();
		newColor.setHue( this.color.getHue());
		newColor.setSaturation(this.color.getSaturation());
		newColor.setLightness(lightness);
		newColor.setAlpha(this.color.getAlpha());
		double[] rgbArray = hslTorgb( newColor.getHue(),newColor.getSaturation(),lightness);
		
		int [] rgb = new int[3];
		
		rgb[0] = (int)Math.round(rgbArray[0] * 255.0);
		rgb[1] = (int)Math.round(rgbArray[1] * 255.0);
		rgb[2] = (int)Math.round(rgbArray[2] * 255.0);
		newColor.setR(rgb[0]);
		newColor.setG(rgb[1]);
		newColor.setB(rgb[2]);
	//	color.setRgb(  getRGB(rgbArray[0],rgbArray[1],rgbArray[2],1.0));
		return newColor;
	}

	protected int getPos()
	{
		return (int) Math.round( ( color.getLightness() * (double) this.getHeight()));
	}


	public ColorData getColor(int x, int y)
	{

		return getColor(x,y,this.getWidth(),this.getHeight());
	}
	
	/*public int getColorInt(int x, int y)
	{

		return getColorInt(x,y,this.getWidth(),this.getHeight());
	}
*/
	public void addColorListener(ColorListener listener)
	{
		listeners.add(listener);
	}
	
	public void removeColorListener(ColorListener listener)
	{
		listeners.remove(listener);
	}
	public ColorData getColor() {
		return color;
	}
	public void setColor(ColorData color) {
		this.color = color;
	}
	

}
