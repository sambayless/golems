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
