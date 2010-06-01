package com.golemgame.toolbar.main;

import com.jme.image.Texture;
import com.simplemonkey.util.Dimension;
import com.simplemonkey.widgets.ToggleButton;
import com.simplemonkey.widgets.ToggleListener;

public class MainToggleButton extends ToggleButton {

	private Texture onIcon;
	private Texture offIcon;
	private int iconUnit;
	
	public MainToggleButton(String name, Texture activeTexture,
			Texture inactiveTexture, Texture hoverTexture, int unit) 
	{
		this(name,activeTexture,inactiveTexture,hoverTexture,unit, null,null,-1);
	}
	
	public MainToggleButton(String name, Texture activeTexture,
			Texture inactiveTexture, Texture hoverTexture, int unit, final Texture onIcon,final Texture offIcon,final int iconUnit) {
		super(name, activeTexture, inactiveTexture, hoverTexture, unit);
		super.setSize(64,64);
		super.setMinSize(32,32);
		this.setShrinkable(true);
		this.setExpandable(true);
		this.onIcon = onIcon;
		this.offIcon = offIcon;
		this.iconUnit = iconUnit;
	
		super.addToggleListener(new ToggleListener(){

			public void toggle(boolean value) {
				if(onIcon !=null && offIcon != null)
				{
					if(value)
					{
						setTexture(offIcon,iconUnit);
					}else{
						setTexture(onIcon,iconUnit);
					}
				}
			}
			
		});
		
		if(onIcon !=null && offIcon != null)
		{
			if(this.isValue())
			{
				setTexture(offIcon,iconUnit);
			}else{
				setTexture(onIcon,iconUnit);
			}
		}
	}



	@Override
	public void setSize(Dimension s) {
		float minSize = Math.min(s.getWidth(), s.getHeight());
		Dimension size = new Dimension(minSize,minSize);
		
		super.setSize(size);
	}

}
