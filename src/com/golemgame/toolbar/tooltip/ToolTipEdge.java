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
package com.golemgame.toolbar.tooltip;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import com.golemgame.util.PaintableImage;
import com.jme.image.Texture;
import com.jme.renderer.ColorRGBA;
import com.simplemonkey.util.Dimension;
import com.simplemonkey.widgets.TextureWidget;

public class ToolTipEdge extends TextureWidget {

	public ToolTipEdge(final boolean horizontal, final Color color) {
		super();
		super.setSize(1,1);
		super.setMinSize(new Dimension(1,1));
		super.setExpandable(true);
		PaintableImage image = new PaintableImage(1, 1, true)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics2D g) {
				g.setColor(color);
			
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
			
					g.fillRect(0, 0, 1 , 1);
			
			}
			
		};
		
        Texture texture = new Texture(); 
        texture.setApply(Texture.AM_MODULATE);
        texture.setBlendColor(new ColorRGBA(1, 1, 1, 1));
    
        texture.setFilter(Texture.MM_NONE);
        texture.setImage(image);
        texture.setMipmapState(Texture.FM_NEAREST);

		super.setTexture(texture);
	}


}
