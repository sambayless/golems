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
