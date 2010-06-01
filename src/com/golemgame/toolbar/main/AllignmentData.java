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
