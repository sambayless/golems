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

import java.util.concurrent.CopyOnWriteArrayList;

import org.fenggui.StatefullWidget;
import org.fenggui.appearance.DefaultAppearance;
import org.fenggui.binding.render.Graphics;
import org.fenggui.binding.render.IOpenGL;
import org.fenggui.util.Color;
import org.fenggui.util.Dimension;

import com.jme.renderer.ColorRGBA;

public class ColorPatch extends StatefullWidget<DefaultAppearance> {

	private Color color;
	
	private CopyOnWriteArrayList<WidgetAlteredListener> alteredListeners = new CopyOnWriteArrayList<WidgetAlteredListener>();
	
	float minAlpha = 0.4f;
	
	public void addAlteredListener(WidgetAlteredListener listener)
	{
		alteredListeners.add(listener);
	}
	
	public void removeAlteredListener(WidgetAlteredListener listener)
	{
		alteredListeners.remove(listener);
	}
	
	public float getMinAlpha() {
		return minAlpha;
	}

	public void setMinAlpha(float minAlpha) {
		this.minAlpha = minAlpha;
	}

	public ColorPatch() {
		super();
		this.setAppearance(new DefaultAppearance(this));
		color = Color.WHITE;
		this.setMinSize(16,16);
	}

	@Override
	public Dimension getMinContentSize() {
		return this.getMinSize();
	}
	private static Color c = new Color(1f,1f,1f,1f);
	@Override
	public void paintContent(Graphics g, IOpenGL gl) {
		if(this.getWidth()>0 && this.getHeight()>0)
		{
			c.setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() > minAlpha? color.getAlpha():minAlpha);
			g.setColor(c);
		
			g.drawFilledRectangle(0, 0, this.getWidth(),this.getHeight());
		}
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		if(!this.color.equals(color))
		{
			this.color = color;
			for(WidgetAlteredListener l:alteredListeners)
				l.widgetAltered(this);
		}
	}
	
	public void setColor(ColorRGBA color) {
		setColor( new Color(color.r,color.g,color.b,color.a));
	}


	public ColorRGBA getColorRGBA()
	{
		return new ColorRGBA(color.getRed(),color.getGreen(), color.getBlue(),color.getAlpha());
	}
	
}
