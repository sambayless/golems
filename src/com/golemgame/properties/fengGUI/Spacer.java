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
package com.golemgame.properties.fengGUI;

import org.fenggui.StandardWidget;
import org.fenggui.appearance.DefaultAppearance;
import org.fenggui.appearance.IAppearance;
import org.fenggui.binding.render.Graphics;
import org.fenggui.binding.render.IOpenGL;
import org.fenggui.util.Dimension;

public class Spacer extends StandardWidget {
	private DefaultAppearance appearance = new DefaultAppearance(this);
	public Spacer() {
		super();
		
	}

	public Spacer(int width, int height) {
		super();
		this.setMinSize(width, height);
	}
	
	@Override
	public IAppearance getAppearance() {
		return appearance;
	}

	@Override
	public Dimension getMinContentSize() {
		return this.getMinSize();
	}

	@Override
	public void paintContent(Graphics g, IOpenGL gl) {
		// TODO Auto-generated method stub
		
	}

}
