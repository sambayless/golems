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
