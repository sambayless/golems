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
package com.golemgame.properties.fengGUI.optionsMenu;


import org.fenggui.Button;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.IContainer;
import org.fenggui.decorator.background.GradientBackground;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;
import org.fenggui.util.Color;

import com.golemgame.properties.fengGUI.HorizontalTabbedWindow;
import com.golemgame.properties.fengGUI.ITab;


public class VerticalTabbedWindow extends HorizontalTabbedWindow{

	
	
	public VerticalTabbedWindow() {
		super();
		Container tabRowContainer = FengGUI.createContainer((IContainer)tabRow.getParent());
		tabRowContainer.setLayoutManager(new BorderLayout());
		tabRowContainer.setLayoutData(BorderLayoutData.WEST);
		((IContainer)tabRow.getParent()).removeWidget(tabRow);
		tabRowContainer.addWidget(tabRow);
		 tabRow.setLayoutData(BorderLayoutData.NORTH);
		 tabRow.setLayoutManager(new RowLayout(false));
	
		 Color leftColor = new Color(211, 211, 211, 90);
		 
		 tabRowContainer.getAppearance().add(new GradientBackground(leftColor, Color.DARK_GRAY, Color.DARK_GRAY,leftColor));
		 tabRowContainer.getParent().layout();
		 tabRow.layout();
	}


	
	protected void insertTabButton(ITab tab) {
		Button button = FengGUI.createButton(tabRow);
		final ITab fTab = tab;
		button.setText(tab.getTitle());
		button.addButtonPressedListener(new IButtonPressedListener()
		{

			
			public void buttonPressed(ButtonPressedEvent e) {
				displayTab(fTab);
				
			}
			
		});

		tabRow.layout();
	}

	
}
