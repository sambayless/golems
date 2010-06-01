package com.golemgame.toolbar.main;

import java.util.List;

import com.simplemonkey.IWidget;
import com.simplemonkey.layout.ILayoutData;
import com.simplemonkey.layout.LayoutManager;
import com.simplemonkey.util.Dimension;

public class AlligningLayout extends LayoutManager {
	private Dimension minSize = new Dimension(0,0);
	@Override
	public Dimension computeMinSize(IWidget container, List<IWidget> content) {
		float width = 0;
		float height = 0;
		for(IWidget c:content)
		{
			if(c.isVisible())
			{
				if(c.getWidth()>width)
					width = c.getWidth();
				if(c.getHeight()>height)
					height = c.getHeight();
				
			}
		}
		minSize.setSize(width, height);
		return minSize;
	}

	@Override
	public void doLayout(IWidget container, List<IWidget> content) {
		
		for(IWidget c:content)
		{
			if(!c.isVisible())
				continue;
			
			ILayoutData layout = c.getLayoutData();
			if(!(layout instanceof AllignmentData ))
					continue;
			
			AllignmentData allign = (AllignmentData)layout;
			
			
			//set the x and y pos to the top left element of this object, set the height to the bottommost
			float minX = Float.POSITIVE_INFINITY;
			float minY = Float.POSITIVE_INFINITY;
			
			float maxX = Float.NEGATIVE_INFINITY;
			float maxY = Float.NEGATIVE_INFINITY;
			
			for(IWidget w:allign.getWidgets())
			{
				if(w.getWorldX() < minX)
					minX = w.getWorldX();
				if(w.getWorldY()<minY)
					minY = w.getWorldY();
				
				float mX = w.getWorldX() + w.getWidth();
				float mY = w.getWorldY() + w.getHeight();
				if(mX>maxX)
					maxX = mX;
				if(mY > maxY)
					maxY = mY;
					
			}
			
			if(allign.isAllignWidth())
			{
				c.getSize().setWidth(maxX-minX);
			}
			if(allign.isAllignHeight())
			{
				c.getSize().setHeight(maxY-minY);
			}
			
			c.setPosition(minX, minY);
			
			this.computeMinSize(container, content);
			
		}
		
		
	}
	
}
