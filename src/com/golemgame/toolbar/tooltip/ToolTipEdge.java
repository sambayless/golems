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
