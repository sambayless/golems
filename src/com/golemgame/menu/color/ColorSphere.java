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
package com.golemgame.menu.color;

import java.awt.image.BufferedImage;

import org.fenggui.Container;
import org.fenggui.binding.render.Binding;
import org.fenggui.binding.render.Graphics;
import org.fenggui.binding.render.IOpenGL;
import org.fenggui.binding.render.ITexture;
import org.fenggui.binding.render.Pixmap;


public class ColorSphere extends Container {


	private Pixmap wheel;
	public ColorSphere(int resolution) {
		super();

		BufferedImage colorWheel = buildColorImage(resolution);
		ITexture backTex = Binding.getInstance().getTexture(colorWheel);
		wheel = new Pixmap(backTex);
		
	}


	private BufferedImage buildColorImage(int width)
	{
		BufferedImage colorWheel = new BufferedImage(width,width,BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x<width;x++)
		{
			for (int y = 0; y < width; y++)
			{
				int rgb = getColor(x,y,width);
			
				colorWheel.setRGB(x, y, rgb);
			}
		}
		return colorWheel;
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


	@Override
	public void paintContent(Graphics g, IOpenGL gl) {
		gl.lineWidth(1f);
		super.paintContent(g, gl);
		int size = Math.min(this.getWidth(), this.getHeight());
		g.drawScaledImage(wheel, 0, 0,size, size);//force the wheel to be square

	}
	
	public int getColor(int x, int y)
	{
		int size = Math.min(this.getWidth(), this.getHeight());
		return getColor(x,y,size);
	}

}
