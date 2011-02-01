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



import java.util.concurrent.Callable;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.fenggui.Button;
import org.fenggui.Container;
import org.fenggui.Display;
import org.fenggui.FengGUI;
import org.fenggui.IWidget;
import org.fenggui.Label;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.IWindowClosedListener;
import org.fenggui.event.WindowClosedEvent;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;
import org.fenggui.util.Alignment;

import com.golemgame.states.StateManager;
import com.jme.util.GameTaskQueueManager;

/**
 * This is a convenience class for creating standard dialogs.
 * @author Sam
 *
 */
public class ConfirmationBox{

/*	private String title;
	private String message;*/
	

	private ConfirmationBox() {
		super();
	}
	

	public static boolean showBlockingConfirmBox(final String title,final String message,final Display display )
	{
		if(StateManager.getGame().inGLThread())
		{
			StateManager.getLogger().severe("Cannot launch blocking confirm from the GL thread.");
			return false;
		}
		//showConfirmBox(title,message,display,Container.NORMAL_PRIORITY+1);
		final Lock blockLock = new ReentrantLock();
		final Condition confirmed = blockLock.newCondition();
		blockLock.lock();
		try{
			
			final boolean[] confirm = new boolean[]{false,false};//the first element of confirm is just
			//to ensure that an answer was given. The second is the value of that answer.
			
			GameTaskQueueManager.getManager().update(new Callable<Object>(){
	
				public Object call() throws Exception {
					showConfirmBox(title,message,display,display,new ConfirmationCallback(){
	
						public void call(boolean c) {
							blockLock.lock();
							try{
								confirm[0]=true;
								confirm[1] = c;
								confirmed.signal();
							}finally{
								blockLock.unlock();
							}
						}
						
					});
					return null;
				}
				
			});
			
			while(!confirm[0])
				confirmed.await();
			
			return confirm[1];
			
		} catch (InterruptedException e) {
			return false;
		}finally{
			blockLock.unlock();
		}
	
	}

	public static void showConfirmBox(String title, String message,final Display display,final IWidget owner,final ConfirmationCallback confirm)
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
		showConfirmBox(title,message,display,priority,confirm);
	}

	public static void showConfirmBox(String title, String message,final Display display, int priority,final ConfirmationCallback confirm)
	{
		final Window window = FengGUI.createDialog(display,title);
		display.removeWidget(window);
		

		display.addWidget(window,priority);
		Container container = FengGUI.createContainer(window.getContentContainer());
		window.getContentContainer().setLayoutManager(new BorderLayout());
		container.setLayoutData(BorderLayoutData.NORTH);
		container.setLayoutManager(new BorderLayout());
		Label label = FengGUI.createLabel(container,message);

		//if(label.getWidth()>300)
		{
			label.setLayoutData(BorderLayoutData.NORTH);
			label.getAppearance().getData().setMultiline(true);
			label.getAppearance().getData().setWordWarping(true);
			label.getAppearance().setAlignment(Alignment.MIDDLE);
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
		
		Container bContainer = FengGUI.createContainer(buttonContainer);
		bContainer.setLayoutData(BorderLayoutData.EAST);
		bContainer.setLayoutManager(new RowLayout());
		
		Button okButton = FengGUI.createButton(bContainer);
	
		okButton.setText("OK");
	
		//for the moment, menu sizes wont be set correctly unless the game is reset completely.
		okButton.addButtonPressedListener(new IButtonPressedListener()
		{			
			public void buttonPressed(ButtonPressedEvent e) {
				window.close();		
				confirm.call(true);
			}		
		});
		
		Button cancelButton = FengGUI.createButton(bContainer);
		
		cancelButton.setText("Cancel");
		//for the moment, menu sizes wont be set correctly unless the game is reset completely.
		cancelButton.addButtonPressedListener(new IButtonPressedListener()
		{			
			public void buttonPressed(ButtonPressedEvent e) {
				window.close();		
				confirm.call(false);
			}		
		});
		window.addWindowClosedListener(new IWindowClosedListener(){

			public void windowClosed(WindowClosedEvent windowClosedEvent) {
				confirm.call(false);
			}
			
		});
		window.getContentContainer().updateMinSize();
		window.getContentContainer().setSizeToMinSize();
		window.layout();
		window.pack();
		container.layout();
		container.pack();
		window.getPosition().setXY(display.getWidth()/2 - window.getWidth()/2, display.getHeight()/2 - window.getHeight()/2);
		
		display.addWidget(window);
		
		display.bringToFront(window);
		display.setFocusedWidget(window);
		
		
		if (window.getDisplayY() + window.getHeight()> window.getParent().getSize().getHeight())
			window.getPosition().setY(window.getParent().getSize().getHeight() - window.getHeight());
		window.pack();
		container.layout();
		container.pack();
		window.layout();
	}
	
	public static interface ConfirmationCallback
	{
		public void call(boolean confirmed);
	}
	
}
