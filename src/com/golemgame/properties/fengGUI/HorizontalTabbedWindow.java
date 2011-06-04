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



import java.util.HashMap;
import java.util.Map;

import org.fenggui.Button;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.composite.Window;
import org.fenggui.decorator.background.Background;
import org.fenggui.decorator.background.PlainBackground;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.IWindowClosedListener;
import org.fenggui.event.IWindowResizedListener;
import org.fenggui.event.WindowClosedEvent;
import org.fenggui.event.WindowResizedEvent;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;
import org.fenggui.util.Color;
import org.fenggui.util.Dimension;
import org.fenggui.util.Spacing;

import com.golemgame.local.StringConstants;
import com.jme.system.DisplaySystem;

public class HorizontalTabbedWindow extends TabbedWindow {

	protected Container tabRow;
	protected Container mainArea;
	protected Container bottomArea;

	static final Dimension MIN_TAB_SIZE = new Dimension(350,200);
	
	public HorizontalTabbedWindow() {
		super();
		buildGUI();
	}
	
	private Button getTabButton(ITab tab)
	{
		return buttonMap.get(tab);
	}
	private Background selectedBackground;
	private Background unselectedBackground;
	protected void setCurrentButton(ITab tab)
	{
		Button curButton = getTabButton(tab);
		Button oldButton = getTabButton(super.getCurrentTab());
		if(oldButton!=null)
		{
			oldButton.getAppearance().remove(selectedBackground);
			oldButton.getAppearance().add(unselectedBackground);
		}
		if(curButton!=null)
		{
			curButton.getAppearance().remove(unselectedBackground);
			curButton.getAppearance().add(selectedBackground);
		}
	}

	public void displayTab(ITab tab) {
		setCurrentButton(tab);
		mainArea.removeAllWidgets();
		mainArea.addWidget(tab.getTab());
		//mainArea.pack();
		mainArea.updateMinSize();
		window.updateMinSize();
		
		if (window.getMinSize().getWidth()>window.getSize().getWidth())
		{
			window.getSize().setWidth(window.getMinSize().getWidth());
		}
		if (window.getMinSize().getHeight()>window.getSize().getHeight())
		{
			window.getSize().setHeight(window.getMinSize().getHeight());
		}
		
		if (window.getSize().getWidth() < MIN_TAB_SIZE.getWidth())
			window.getSize().setWidth( MIN_TAB_SIZE.getWidth());
		
		if (window.getSize().getHeight() < MIN_TAB_SIZE.getHeight())
			window.getSize().setHeight( MIN_TAB_SIZE.getHeight());
		
		window.layout();
		if (window.getParent() != null)
		{
			
			Spacing border =  window.getAppearance().getBorder();
			//.getTop() + window.getAppearance().getBorder().getTop() + window.getAppearance().getContentHeight();
			Spacing padding = window.getAppearance().getPadding();
			
			Spacing margin = window.getAppearance().getMargin();
			
			int top = window.getDisplayY() + window.getAppearance().getContentHeight()+border.getBottomPlusTop() + padding.getBottomPlusTop() +margin.getBottom();
			
			
			if (top> window.getParent().getSize().getHeight())
			{
				window.getPosition().setY(window.getParent().getSize().getHeight() - top);
			}
		}
		super.displayTab(tab);
		//if (window.getDisplayY()<0)
	//		window.getPosition().setY(0);
	}

	protected Map<ITab,Button> buttonMap = new HashMap<ITab, Button>();
	
	protected void insertTabButton(ITab tab)
	{
		Button button = FengGUI.createButton(tabRow);
		buttonMap.put(tab, button);
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
	
	private void buildGUI()
	{
		final int width = DisplaySystem.getDisplaySystem().getWidth();
		final int height = DisplaySystem.getDisplaySystem().getHeight();

		selectedBackground = new PlainBackground(Color.WHITE);
		unselectedBackground = new PlainBackground(Color.TRANSPARENT);
		
	        // create the main container
	       // Window window = FengGUI.createWindow(display, false, false, false, true);
	       // window = new Window();
			
	      	window = createWindow();
	      	
	        window.setTitle(getTitle());
	        window.setSize(width/2, height/4);
	       // window.removeWidget(window.getTitleBar());
	        
	        // center the window on the screen
	        window.setX(width/2 - window.getWidth()/2);
	        window.setY(height/2 - window.getHeight()/2);
	        window.getContentContainer().setLayoutManager(new BorderLayout());
	        
	        mainArea = FengGUI.createContainer(window.getContentContainer());
	        tabRow = FengGUI.createContainer(window.getContentContainer());
	        mainArea.setLayoutData(BorderLayoutData.CENTER);
	        tabRow.setLayoutData(BorderLayoutData.NORTH);
	        
	        bottomArea = FengGUI.createContainer(window.getContentContainer());
	        bottomArea.setLayoutData(BorderLayoutData.SOUTH);
	        bottomArea.setLayoutManager(new BorderLayout());
	        
	        tabRow.setLayoutManager(new RowLayout());
	        mainArea.setLayoutManager(new RowLayout());
	        
	        window.addWindowResizedListener(new IWindowResizedListener()
	        {

				
				public void windowResized(WindowResizedEvent windowResizedEvent) {
					if (window.getDisplayY() + window.getHeight() > DisplaySystem.getDisplaySystem().getHeight())
					{
						window.getSize().setHeight(DisplaySystem.getDisplaySystem().getHeight()-window.getDisplayY());
						window.layout();
					}
					
				}

	        	
	        });
			  
	        window.addWindowClosedListener(new IWindowClosedListener()
	        {

				
				public void windowClosed(
						WindowClosedEvent windowClosedEvent) {
					close();
				}
	        	
	        });
	        
	        Container buttonRow = FengGUI.createContainer(bottomArea);
	        buttonRow.setLayoutData(BorderLayoutData.EAST);
	        
	        Button okButton = FengGUI.createButton(buttonRow,StringConstants.get("MAIN_MENU.OK" ,"OK"));
	        Button cancelButton = FengGUI.createButton(buttonRow,StringConstants.get("MAIN_MENU.CANCEL" ,"Cancel"));
	        
	        okButton.addButtonPressedListener(new IButtonPressedListener()
	        {

				
				public void buttonPressed(ButtonPressedEvent e) {
					cancel = false;
					close();
					
				}
	        	
	        });
	        
	        cancelButton.addButtonPressedListener(new IButtonPressedListener()
	        {

				
				public void buttonPressed(ButtonPressedEvent e) {
					cancel = true;
					close();
					
				}
	        	
	        });
	}

	protected Window createWindow() {
		return FengGUI.createWindow(true, false, false, true);
	}

}
