package com.golemgame.toolbar.main;


import java.util.List;

import com.simplemonkey.IWidget;
import com.simplemonkey.layout.LayoutManager;
import com.simplemonkey.util.Dimension;


public class MainLayout extends LayoutManager {
	
	private final int columns ;
	
	public MainLayout(int columns) {
		super();
		this.columns = columns;
	}

	@Override
	public Dimension computeMinSize(IWidget container, List<IWidget> content) {
		Dimension minSize = new Dimension(0,0);
	
		minSize.setWidth (calculateColumnHWidth(content, true));
		minSize.setHeight (calculateColumnHeight(content,true));
		return minSize;
	}

	private int calculateColumnHWidth(List<IWidget> content,boolean minimum)
	{
		//break the content down into the given number of rows, find the height of each one, report the largest
		int numberOfRows = (int)Math.ceil((float)content.size()/(float)columns);//cast to floats intentionally to avoid integer division
		
		int[] columnWidths = new int[numberOfRows];
		for(int i = 0; i<content.size();i++)
		{
			int row = i/columns;
			columnWidths[row]+= minimum? content.get(i).getMinSize().getHeight():content.get(i).getHeight();
			
		}
		int totalWidth = 0;
		for(int i = 0;i<numberOfRows;i++)
		{
			if(totalWidth<columnWidths[i])
				totalWidth = columnWidths[i];
		}
		return totalWidth;
	}
	
	
	private int calculateColumnHeight(List<IWidget> content,boolean minimum)
	{
		//break the content down into the given number of rows, find the height of each one, report the largest
		int[] columnHeights = new int[columns];
		for(int i = 0; i<content.size();i++)
		{
			int column = i%columns;
			columnHeights[column]+= minimum? content.get(i).getMinSize().getHeight():content.get(i).getHeight();
			
		}
		int totalHeight = 0;
		for(int i = 0;i<columns;i++)
		{
			if(totalHeight<columnHeights[i])
				totalHeight = columnHeights[i];
		}
		return totalHeight;
	}
	
	@Override
	public void doLayout(IWidget container, List<IWidget> content) {
		if(content.isEmpty())
			return;
		float currentRowHeight = container.getHeight();
		int numberOfRows = (int)Math.ceil((float)content.size()/(float)columns);//cast to floats intentionally to avoid integer division
		
		float rowHeight = container.getHeight()/numberOfRows;
		float columnWidth = container.getWidth()/columns;
		for(int i = 0; i<numberOfRows;i++)
		{
			int firstWidgetIndex = i*columns;
			//first get the row height
		/*
			for (int column = 0; column<columns;column++)
			{
				int currentWidgetIndex = firstWidgetIndex+column;
				if(currentWidgetIndex < content.size())
				{
					 IWidget widget = content.get(currentWidgetIndex);
					 if(rowHeight <widget.getHeight())
					 {
						 rowHeight = widget.getHeight();
					 }
				}
			}*/
			currentRowHeight -= rowHeight;
			
			int currentWidth = 0;
			 //line all the widgets up at that row height
			for (int column = 0; column<columns;column++)
			{
				int currentWidgetIndex = firstWidgetIndex+column;
				if(currentWidgetIndex < content.size())
				{
					 IWidget widget = content.get(currentWidgetIndex);
					 widget.setSize(new Dimension(columnWidth ,rowHeight));
					 widget.setPosition(currentWidth, currentRowHeight);
					 currentWidth += widget.getWidth();
				}
			}
		}

	}
}
