package com.golemgame.toolbar;

import java.util.List;

import com.simplemonkey.IWidget;
import com.simplemonkey.layout.GridLayout;

public class VerticalSquareGridLayout extends GridLayout {

	public VerticalSquareGridLayout(int rows, int columns) {
		super(rows, columns);
	}

	public VerticalSquareGridLayout() {
		super(1,1);
	}

	@Override
	public void doLayout(IWidget container, List<IWidget> content) {

	 	
	 	//container.getSize().setHeight(Math.min(container.getSpacingAppearance().getContentHeight(), (rows-1) * container.getSpacingAppearance().getContentWidth()/2));

		super.doLayout(container, content);
		
		
		
	}

}
