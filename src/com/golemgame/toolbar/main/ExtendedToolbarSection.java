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

import com.simplemonkey.IWidget;
import com.simplemonkey.layout.GridLayout;
import com.simplemonkey.tooltip.ToolTipWidget;
import com.simplemonkey.widgets.TextureContainer;

public class ExtendedToolbarSection extends TextureContainer{
	private AllignmentData allignmentData = new AllignmentData();
	private ExtensionLayout layout;
	public ExtendedToolbarSection() {
		super();
		this.setPermeable(false);
		this.setShrinkable(false);
		allignmentData.setAllignWidth(false);
		allignmentData.setAllignHeight(true);
		this.setLayoutData(allignmentData);
		layout = new ExtensionLayout(1,1);
		this.setLayoutManager(layout);
		
	}

	@Override
	public void layout() {
		//assume constant widget height
		if(this.getWidgets().size()>0)
		{
			IWidget widget = this.getWidgets().iterator().next();
			if(widget!=null)
			{
				float wHeight = widget.getHeight();
				int rows =(int) (this.getHeight()/wHeight);
				layout.setHeight(rows);
			}
		}
		super.layout();
	}

	public void addMainWidget(IWidget widget)
	{
		allignmentData.addAlligningWidget(widget);
	}

	public void setChildTooltipWidget(ToolTipWidget toolTipWidget) {
		for(IWidget w:this.getWidgets())
			w.setTooltipWidget(toolTipWidget);
	}
	
}
