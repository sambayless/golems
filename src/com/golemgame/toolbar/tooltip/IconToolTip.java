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
package com.golemgame.toolbar.tooltip;


import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import com.jme.image.Texture;
import com.simplemonkey.Container;
import com.simplemonkey.IWidget;
import com.simplemonkey.layout.BorderLayout;
import com.simplemonkey.layout.BorderLayoutData;
import com.simplemonkey.layout.CenteringLayout;
import com.simplemonkey.layout.CenteringLayout.CenterStyle;
import com.simplemonkey.util.Dimension;
import com.simplemonkey.util.Spacing;
import com.simplemonkey.widgets.SolidColorTextureContainer;
import com.simplemonkey.widgets.TextWidget;
import com.simplemonkey.widgets.TextureContainer;
import com.simplemonkey.widgets.TextureWidget;

public class IconToolTip extends FadingToolTip {

	public static final int WORD_WRAP = 70;
	
	private static final Map<IWidget,ToolTipData> tooltipMap = new HashMap<IWidget, ToolTipData>();
	
	private TextureWidget icon ;
	private TextWidget text;
	private TextWidget title;
	private TextureContainer innerContainer;
	
	public IconToolTip() {
		super();
		super.removeAllWidgets();
		
		//build a new set of widgets for displaying content
		
		this.setLayoutManager(new BorderLayout());
		Color backColor = new Color(1,1,1,0.8f);
		buildFrame(backColor);
		
		innerContainer = new SolidColorTextureContainer(backColor);
		innerContainer.setLayoutData(BorderLayoutData.CENTER);
		innerContainer.setLayoutManager(new BorderLayout());
		
		innerContainer.getSpacingAppearance().setPadding(new Spacing(0,10,10,0));
		
		Container centerContainer = new Container();
		centerContainer.setLayoutManager(new BorderLayout());
		centerContainer.setLayoutData(BorderLayoutData.CENTER);
		
		this.addWidget(innerContainer);
		
		Container westContainer = new Container();
		westContainer.setLayoutData(BorderLayoutData.WEST);
	//	westContainer.setLayoutManager(new BorderLayout());
		westContainer.setLayoutManager(new CenteringLayout(CenterStyle.VERTICAL));
		westContainer.getSpacingAppearance().setPadding(new Spacing(0,10,10,0));
		westContainer.setShrinkable(true);
	//	westContainer.setMinSize(64,64);
	//	westContainer.setSizeToMinSize();
		
		innerContainer.addWidget(centerContainer);
		innerContainer.addWidget(westContainer);
		 icon = new TextureWidget();
		icon.setLayoutData(BorderLayoutData.WEST);
		
		icon.setExpandable(false);
		
		westContainer.addWidget(icon);
		
		title = new TextWidget();
		 title.setLayoutData(BorderLayoutData.NORTH);
		title.setFont( Font.decode("Sans-Serif").deriveFont(12).deriveFont(Font.BOLD));
	//	 title.getSpacingAppearance().setPadding(new Spacing(0,10,10,0));
		 
		title.setTextTextureApplyMode(Texture.AM_MODULATE);
		
		text = new TextWidget();
		text.setLayoutData(BorderLayoutData.CENTER);
		
		text.getSpacingAppearance().setPadding(new Spacing(0,10,10,0));
		text.setTextTextureApplyMode(Texture.AM_MODULATE);
	
		centerContainer.addWidget(text);
		centerContainer.addWidget(title);
		innerContainer.getSpatial().setZOrder(-1024,true);
	}

	private void buildFrame(Color color) {
		
	//	float size = 1;
		
		Container outerTop = new Container();
		outerTop.setLayoutManager(new BorderLayout());
		outerTop.setLayoutData(BorderLayoutData.NORTH);
		this.addWidget(outerTop);
		
		
		Container outerBottom = new Container();
		outerBottom.setLayoutManager(new BorderLayout());
		outerBottom.setLayoutData(BorderLayoutData.SOUTH);
		this.addWidget(outerBottom);
		
		ToolTipEdge left = new ToolTipEdge(true,color);
		left.setLayoutData(BorderLayoutData.WEST);
		//this.addWidget(left);
		
		ToolTipEdge right = new ToolTipEdge(true,color);
		right.setLayoutData(BorderLayoutData.EAST);
		//this.addWidget(right);
		
		ToolTipEdge top = new ToolTipEdge(false,color);
		top.setLayoutData(BorderLayoutData.CENTER);
		outerTop.addWidget(top);
		
		ToolTipEdge bottom = new ToolTipEdge(false,color);
		bottom.setLayoutData(BorderLayoutData.CENTER);
		outerBottom.addWidget(bottom);
		
		ToolTipCorner topLeft = new ToolTipCorner(0,color);
		topLeft.setLayoutData(BorderLayoutData.WEST);
	//	topLeft.setMinSize(size,size);
		outerTop.addWidget(topLeft);
		
		ToolTipCorner topRight = new ToolTipCorner(1,color);
		topRight.setLayoutData(BorderLayoutData.EAST);
		outerTop.addWidget(topRight);
	//	topRight.setMinSize(size,size);
		
		
		ToolTipCorner bottomLeft = new ToolTipCorner(2,color);
		bottomLeft.setLayoutData(BorderLayoutData.WEST);
		outerBottom.addWidget(bottomLeft);
		
		ToolTipCorner bottomRight = new ToolTipCorner(3,color);
		bottomRight.setLayoutData(BorderLayoutData.EAST);
		outerBottom.addWidget(bottomRight);
		outerTop.layout();
		outerBottom.layout();
		layout();
	}

	@Override
	public void forceShowToolTip(IWidget owner, String toolTip, float x, float y) {
		setTooltipData(owner,toolTip);
		super.forceShowToolTip(owner, toolTip, x, y);
	}
	
	private void setTooltipData(IWidget owner, String tooltip)
	{
		ToolTipData data = tooltipMap.get(owner);
		
		if (data == null)
		{
			icon.removeTexture(0);
			icon.removeTexture(1);
			icon.setVisible(false);
			icon.setSize(0,0);
			icon.setMinSize(new Dimension(0,0));
			icon.updateMinSize();
			icon.getParent().updateMinSize();
			
			title.setText(tooltip);
			title.updateMinSize();
			title.setSizeToMinSize();
			
			text.setText(" ");
			text.setVisible(false);
			text.setMinSize(0, 0);
			text.setSizeToMinSize();
			
			text.getParent().updateMinSize();
			text.getParent().layout();
			
		}else
		{
			if (!data.hasIcon())
			{
				icon.removeTexture(1);
				icon.setVisible(false);
				icon.setSize(0,0);
				icon.updateMinSize();
				icon.getParent().updateMinSize();
			}else
			{
				icon.setVisible(true);
				icon.setTexture(data.getIcon(),1);
				icon.setSize(data.getIcon().getImage().getWidth(),data.getIcon().getImage().getHeight());
				icon.getParent().layout();
			}
			
			if (!data.hasBackground())
			{
				icon.removeTexture(0);		
			}else
			{		
				icon.setTexture(data.getBackground(),0);
				if(data.hasIcon())
					icon.setSize(data.getIcon().getImage().getWidth(),data.getIcon().getImage().getHeight());
				else
					icon.setSize(data.getBackground().getImage().getWidth(),data.getBackground().getImage().getHeight());
			}
			
			icon.setShrinkable(false);
			
			if (data.hasTitle())
			{
		
				title.setText(data.getTitle());//for some reason, if I dont do this there are sometimes weird graphical problems with the title text...
				title.setVisible(true);
				title.setSizeToMinSize();
			}else
			{
				title.setText("");
				title.setMinSize(0,0);
				title.setVisible(false);
				title.setSizeToMinSize();
			}
			
			if (data.hasMessage())
			{
				text.setVisible(true);
				text.setText(data.getMessage());
				text.setSizeToMinSize();
			}else
			{
				text.setText(" ");
				text.setVisible(false);		
				text.setMinSize(0, 0);
				text.setSizeToMinSize();
			}
			
		}
		text.setShrinkable(true);
		text.layout();
		text.getParent().updateMinSize();
		text.getParent().getParent().updateMinSize();
		text.getParent().layout();
		text.getParent().getParent().layout();
	
		icon.getParent().layout();
		updateMinSize();
		setSizeToMinSize();
		layout();
	}

	public static void setTooltipData(IWidget widget, ToolTipData data)
	{
		tooltipMap.put(widget, data);
	}
	
	public static void setDescription(IWidget widget, String description)
	{
		ToolTipData data = tooltipMap.get(widget);
		if (data == null)
		{
			data = new ToolTipData(widget.getToolTip());
			setTooltipData(widget,data);
		}
			
			data.setMessage(description);
	}
	
/*	public static void setDescription(IWidget widget, Description description)
	{
		ToolTipData data = tooltipMap.get(widget);
		if (data == null)
		{
			data = new ToolTipData(widget.getToolTip());
			setTooltipData(widget,data);
		}
			
			data.setDescription(description);
	}*/
	
	
}
