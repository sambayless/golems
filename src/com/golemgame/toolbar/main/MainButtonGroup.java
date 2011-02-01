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






import com.golemgame.toolbar.ButtonGroup;
import com.jme.image.Texture;



public class MainButtonGroup extends ButtonGroup {
	private static final long serialVersionUID = 1L;
	private Texture active = null;
	private Texture inactive = null;
	private Texture hover = null;
	
	public MainButtonGroup(float width)
	{
		super();
		super.setWidth(width);
		//maxWidth = width;
		super.setLayoutManager(new MainLayout(2));
		super.setExpandable(true);
		super.setShrinkable(true);
	}
	
	public MainButtonGroup(float width, Texture inactive, Texture hover,Texture active) {
		this(width);
		this.active = active;
		this.inactive = inactive;
		this.hover = hover;
	}
	

	
	public Texture getActive() {
		return active;
	}

	public void setActive(Texture active) {
		this.active = active;
	}

	public Texture getInactive() {
		return inactive;
	}

	public void setInactive(Texture inactive) {
		this.inactive = inactive;
	}

	public Texture getHover() {
		return hover;
	}

	public void setHover(Texture hover) {
		this.hover = hover;
	}

	
}
