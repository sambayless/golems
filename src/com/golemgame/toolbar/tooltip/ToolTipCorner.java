package com.golemgame.toolbar.tooltip;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import com.golemgame.util.PaintableImage;
import com.jme.image.Texture;
import com.jme.renderer.ColorRGBA;
import com.simplemonkey.util.Dimension;
import com.simplemonkey.widgets.TextureWidget;

public class ToolTipCorner extends TextureWidget {

	public ToolTipCorner(final int position, final Color color) {
		super();
		final int size = 5;
		this.setSize(5,5);
		this.setShrinkable(true);
		super.setMinSize(new Dimension(size,size));
		this.setMinContentSize(new Dimension(size,size));
		PaintableImage image = new PaintableImage(size, size, true)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics2D g) {
				g.setColor(color);
				
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			//	Arc2D.Double corner;
				
				//corner = new Arc2D.Double(0,0,5,5,0, Math.PI/2.0,Arc2D.PIE);
				
			//	g.fill(corner);
				if (position == 0)					
					g.fillArc(0, -size,size*2, size*2,180,90);
				else if (position == 1)					
					g.fillArc(-size, -size, size*2, size*2,270, 90);
				else if (position == 2)					
					g.fillArc(0, 0, size*2, size*2,90, 90);
				else if (position == 3)					
					g.fillArc(-size, 0, size*2, size*2,0, 90);
				
				//g.fillRect(0, 0, size,size);
				
	/*			g.setColor(Color.red);
				g.setBackground(Color.blue);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
			
					g.fillRect(0, 0,5 , 5);*/
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
