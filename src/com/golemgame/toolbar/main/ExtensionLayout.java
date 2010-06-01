package com.golemgame.toolbar.main;

import java.util.List;

import com.simplemonkey.IWidget;
import com.simplemonkey.layout.GridLayout;

public class ExtensionLayout extends GridLayout {
	private int height = 1;
	public ExtensionLayout(int rows, int columns) {
		super(rows, columns);
		super.columnsFirst=true;
		super.splitUpAdditionalSpace=false;
	}
	public ExtensionLayout() {
		this(1, 1);
	}
	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getColumns() {
		return columns;
	}

	public void setColumns(int columns) {
		this.columns = columns;
	}

	public void setHeight(int height)
	{
		this.height = height;
		
	}
	@Override
	public void doLayout(IWidget container, List<IWidget> content) {
		if(height>0){
			int columns = content.size()/height + (content.size()%height >0? 1:0);
			this.setColumns(columns);
			this.setRows(height);
		}
		
		super.doLayout(container, content);
	}
	
}
