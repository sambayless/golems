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
package com.golemgame.properties.fengGUI.mainmenu;

import org.fenggui.Label;
import org.fenggui.composite.Window;
import org.fenggui.layout.RowLayout;

public class MainWindow extends Window {

	public MainWindow() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MainWindow(boolean closeBtn, boolean maximizeBtn,
			boolean minimizeBtn, boolean autoClose) {
		super(closeBtn, maximizeBtn, minimizeBtn, autoClose);
		// TODO Auto-generated constructor stub
	}

	public MainWindow(boolean closeBtn, boolean maximizeBtn, boolean minimizeBtn) {
		super(closeBtn, maximizeBtn, minimizeBtn);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void build(boolean closeBtn, boolean maximizeBtn,
			boolean minimizeBtn) {
		super.build(closeBtn, maximizeBtn, minimizeBtn);
		super.titleBar.setVisible(false);
	}





}
