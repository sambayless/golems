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

import java.util.ArrayList;

import com.simplemonkey.IWidget;
import com.simplemonkey.layout.ILayoutData;

public class AllignmentData implements ILayoutData {
	private final ArrayList<IWidget> widgets = new ArrayList<IWidget>();
	private boolean allignWidth = true;
	public boolean isAllignWidth() {
		return allignWidth;
	}
	public void setAllignWidth(boolean allignWidth) {
		this.allignWidth = allignWidth;
	}
	public boolean isAllignHeight() {
		return allignHeight;
	}
	public void setAllignHeight(boolean allignHeight) {
		this.allignHeight = allignHeight;
	}
	private boolean allignHeight = true;
	
	public ArrayList<IWidget> getWidgets() {
		return widgets;
	}
	public void addAlligningWidget(IWidget widget)
	{
		widgets.add(widget);
	}
	
}
