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



import org.fenggui.Button;
import org.fenggui.Container;
import org.fenggui.Display;
import org.fenggui.FengGUI;
import org.fenggui.IWidget;
import org.fenggui.Label;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;

/**
 * This is a convenience class for creating standard dialogs.
 * @author Sam
 *
 */
public class MessageBox{

/*	private String title;
	private String message;*/
	

	private MessageBox() {
		super();
	}
	

	public static void showMessageBox(String title, String message,final Display display )
	{
		showMessageBox(title,message,display,Container.NORMAL_PRIORITY+1);
	}

	public static void showMessageBox(String title, String message,final Display display,final IWidget owner)
	{
		IWidget parent = owner;
		//find the root priority where this widget is attached to the display, it it is.
		while(parent.getParent()!= null)
			{
				parent = parent.getParent();
			}
		int priority =Display.NORMAL_PRIORITY;
		if(parent != null)
		{
		
			priority = display.getWidgetPriority(parent);
	
		}
		priority += 1;
		showMessageBox(title,message,display,priority);
	}

	public static void showMessageBox(String title, String message,final Display display, int priority)
	{
		final Window window = FengGUI.createDialog(display,title);
		display.removeWidget(window);
		

		display.addWidget(window,priority);
		Container container = window.getContentContainer();
		container.setLayoutManager(new BorderLayout());
		Label label = FengGUI.createLabel(container,message);

		if(label.getWidth()>300)
		{
			label.setLayoutData(BorderLayoutData.CENTER);
			label.getAppearance().getData().setMultiline(true);
			label.getAppearance().getData().setWordWarping(true);
			label.setMinSize(300, 75);
			label.getSize().setSize(label.getMinSize());
		}
	//	label.setSizeToMinSize();
		
		label.setShrinkable(false);
		container.updateMinSize();
		container.updateMinSize();
		container.setSizeToMinSize();
		
		Container buttonContainer = FengGUI.createContainer(container);
		buttonContainer.setLayoutManager(new BorderLayout());
		buttonContainer.setLayoutData(BorderLayoutData.SOUTH);
		Button okButton = FengGUI.createButton(buttonContainer);
		okButton.setLayoutData(BorderLayoutData.EAST);
		okButton.setText("OK");
		//for the moment, menu sizes wont be set correctly unless the game is reset completely.
		okButton.addButtonPressedListener(new IButtonPressedListener()
		{

			
			public void buttonPressed(ButtonPressedEvent e) {
				window.close();
				
			}

		
		});
		
		window.getContentContainer().updateMinSize();
		window.getContentContainer().setSizeToMinSize();
		window.pack();
		window.getPosition().setXY(display.getWidth()/2 - window.getWidth()/2, display.getHeight()/2 - window.getHeight()/2);
		
		display.addWidget(window);
		
		display.bringToFront(window);
		display.setFocusedWidget(window);
		
		
		if (window.getDisplayY() + window.getHeight()> window.getParent().getSize().getHeight())
			window.getPosition().setY(window.getParent().getSize().getHeight() - window.getHeight());

	}
	
	
}
