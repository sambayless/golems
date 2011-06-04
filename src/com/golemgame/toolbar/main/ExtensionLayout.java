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
