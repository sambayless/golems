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
package com.golemgame.instrumentation.fenggui;

import org.fenggui.StatefullWidget;
import org.fenggui.appearance.DefaultAppearance;
import org.fenggui.appearance.LabelAppearance;
import org.fenggui.binding.render.Graphics;
import org.fenggui.binding.render.IOpenGL;
import org.fenggui.binding.render.ImageFont;
import org.fenggui.util.Color;
import org.fenggui.util.Dimension;

import com.golemgame.instrumentation.fenggui.Graph.GraphStyle;

public class ColorDisplay extends StatefullWidget<DefaultAppearance>{
	private Color disabledColor = new Color(0.3f,0.3f,0.3f,.7f);
	
	private Color colorPos = new Color(0.0f,0.0f,1f,1f);
	public Color getColorPos() {
		return colorPos;
	}

	public void setColorPos(Color colorPos) {
		this.colorPos = colorPos;
	}

	public Color getColorZero() {
		return colorZero;
	}

	public void setColorZero(Color colorZero) {
		this.colorZero = colorZero;
	}

	public Color getColorNeg() {
		return colorNeg;
	}

	public void setColorNeg(Color colorNeg) {
		this.colorNeg = colorNeg;
	}

	private Color colorZero = new Color(1f,1f,1f,0.0f);
	private Color colorNeg = new Color(1f,0f,0f,1f);
	
	private float val = 0;
	
	public float getVal() {
		return val;
	}

	public void setVal(float val) {
		this.val = val;
	}

	public ColorDisplay() {
		super();
		this.setAppearance(new DefaultAppearance(this));
		this.setMinSize(1,1);
	}

	public static final Dimension MIN_SIZE = new Dimension(1,1);
	private Color curColor = new Color(0.0f,0.0f,0f,0f);
	@Override
	public void paintContent(Graphics g, IOpenGL gl) {
		float pVal = val;
		if(pVal>1f)
		{
			pVal = 1f;
		}else if (pVal<-1f)
		{
			pVal = -1f;
		}
		
		if(pVal>=0f)
		{
			curColor.setColor(colorPos.getRed()*pVal + colorZero.getRed()*(1f-pVal),
					colorPos.getGreen()*pVal + colorZero.getGreen()*(1f-pVal),
					colorPos.getBlue()*pVal + colorZero.getBlue()*(1f-pVal), 
					colorPos.getAlpha()*pVal + colorZero.getAlpha()*(1f-pVal));
		}else
		{
			pVal = -pVal;
			curColor.setColor(colorNeg.getRed()*pVal + colorZero.getRed()*(1f-pVal),
					colorNeg.getGreen()*pVal + colorZero.getGreen()*(1f-pVal),
					colorNeg.getBlue()*pVal + colorZero.getBlue()*(1f-pVal), 
					colorNeg.getAlpha()*pVal + colorZero.getAlpha()*(1f-pVal));
		}
		g.setColor(curColor);
		g.drawFilledRectangle(getX(), getY(), getWidth(), getHeight());
		
		/*
		 * Draw a dark screen over this
		 */
		if(!super.isEnabled())
		{
			
			g.setColor(disabledColor);
			g.drawFilledRectangle(0, 0, this.getWidth(), getHeight());
		}
	}

	@Override
	public Dimension getMinContentSize() {
		return MIN_SIZE;
	}
	
}
