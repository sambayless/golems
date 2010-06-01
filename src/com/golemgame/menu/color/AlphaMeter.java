package com.golemgame.menu.color;

public class AlphaMeter extends LightnessMeter {

	public AlphaMeter(int width, int height) {
		super(width, height);

	}



	@Override
	protected ColorData getColor(int xPos, int yPos, int width, int height) {
		double alpha = ((double)(yPos))/((double)height);

		ColorData newColor = new ColorData();
		newColor.setHue( this.color.getHue());
		newColor.setSaturation(this.color.getSaturation());
		newColor.setLightness(this.color.getLightness());
		newColor.setAlpha(alpha);
		
		newColor.copyHSLtoRGB();
		
		
		return newColor;
	}



	@Override
	protected byte[] getColorArray(int xPos, int yPos, int width,int height)
	{

			byte [] rgba = new byte[3];
			
			//rgba[3] = (byte) 255;
			
			rgba[0] = (byte)this.color.getR();
			rgba[1] = (byte)this.color.getG();
			rgba[2] = (byte)this.color.getB();
			
			return rgba;
	}



	@Override
	protected int getPos() {
		return (int) Math.round( ( color.getAlpha() * (double) this.getHeight()));
	}

	
	
}
