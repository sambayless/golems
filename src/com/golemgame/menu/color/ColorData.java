package com.golemgame.menu.color;

public class ColorData {
	//private int rgb;
	private int r;
	private int g;
	private int b;
	private double hue;
	private double saturation;
	private double lightness;
	private double alpha = 1;
	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public ColorData() {
		super();
	}

/*	public int getRgb() {
		return rgb;
	}
	public void setRgb(int rgb) {
		this.rgb = rgb;
	}
*/


	public int getR() {
		return r;
	}

	public void setR(int r) {
		this.r = r;
	}

	public int getG() {
		return g;
	}

	public void setG(int g) {
		this.g = g;
	}

	public int getB() {
		return b;
	}

	public void setB(int b) {
		this.b = b;
	}

	public double getHue() {
		return hue;
	}

	public void setHue(double hue) {
		this.hue = hue;
	}

	public double getSaturation() {
		return saturation;
	}

	public void setSaturation(double saturation) {
		this.saturation = saturation;
	}

	public double getLightness() {
		return lightness;
	}

	public void setLightness(double lightness) {
		this.lightness = lightness;
	}
	
	
	/**
	 * Convert and copy the HSL values of this data into RGB
	 */
	public void copyHSLtoRGB()
	{
		double[] rgbArray = hslTorgb(hue,saturation,lightness);

		int [] rgb = new int[3];

		rgb[0] = (int)Math.round(rgbArray[0] * 255.0);
		rgb[1] = (int)Math.round(rgbArray[1] * 255.0);
		rgb[2] = (int)Math.round(rgbArray[2] * 255.0);
		setR(rgb[0]);
		setG(rgb[1]);
		setB(rgb[2]);
	}
	
	public void copyRGBtoHSL()
	{
		double r = ((double)this.getR())/255.0;
		double g = ((double)this.getG())/255.0;
		double b = ((double)this.getB())/255.0;
		
		
		double max =Math.max( Math.max(r, g),b);
		double min =Math.min( Math.min(r, g),b);
		double h=0;
		
		if (max == min)
		{
			h = 0;
		}else if (max == r)
		{
			h = ((double)(g-b))/((double)(max-min)*6.0);

		}else if (max == g)
		{
			h = ((double)(b-r))/((double)(max-min)*6.0) + 1.0/3.0;
		}else if (max == b)
		{
			h = ((double)(r-g))/((double)(max-min)*6.0) + 2.0/3.0;
		}
		
		if (h>1.0)
			h -= 1.0;
		else if (h < 0)
			h+= 1.0;
		
		double l = (max+min)/2.0;
		
		double s=0;
		if (max == min)
		{
			s= 0;
		}else if (l<=0.5)
		{
			s = (max-min)/(max+min);

		}else if (l>0.5)
		{
			s = (max-min)/(2-(max+min));
		}
		
		this.setHue(h);
		this.setSaturation(s);
		this.setLightness(l);
	}
	
	public int getRGB(int r, int g, int b, int a)
	{
	         return ((a & 0xFF) << 24) |
	                 ((r & 0xFF) << 16) |
	                 ((g & 0xFF) << 8)  |
	                 ((b & 0xFF) << 0);
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
	
}
