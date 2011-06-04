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
package com.golemgame.instrumentation;

import org.fenggui.Button;
import org.fenggui.FengGUI;
import org.fenggui.TextEditor;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.IWindowChangedListener;
import org.fenggui.event.key.IKeyListener;
import org.fenggui.event.key.Key;
import org.fenggui.event.key.KeyPressedEvent;
import org.fenggui.event.key.KeyReleasedEvent;
import org.fenggui.event.key.KeyTypedEvent;
import org.fenggui.event.mouse.MouseAdapter;
import org.fenggui.event.mouse.MouseDoubleClickedEvent;

public class TextBoxController extends SingleAxisController {
	private TextEditor val;
	private float curVal = 0;
	private boolean locked = false;
	private boolean userPositioned = false;
	public boolean isUserPositioned() {
		return userPositioned;
	}

	public void setUserPositioned(boolean userPositioned) {
		this.userPositioned = userPositioned;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean isLocked() {
		return locked;
	}
	public TextBoxController() {
		super();
		super.setTypeName("Text");
		window.setSize(200, 50);
		
		val = FengGUI.createTextEditor();
		val.setText("0");
		val.addMouseListener(new MouseAdapter(){

			@Override
			public void mouseDoubleClicked(MouseDoubleClickedEvent mouseDoubleClickedEvent) {
				window.setWindowed(!window.isWindowed());
			}
			
		});
		window.addWindowChangedListener(new IWindowChangedListener(){

			public void userChanged() {
				userPositioned = true;
			}
			
		});
		final Button enterVal = FengGUI.createButton("Set");
		enterVal.setShrinkable(true);
		enterVal.setExpandable(false);
		super.controlContainer.addWidget(val);
		super.controlContainer.addWidget(enterVal);
	
		super.registerListener(new AxisControlListener()
		{
			public void valueSet(float value) {
				if(curVal!=value)
				{
					curVal = value;
					val.setText(String.valueOf(value));
				}
			}			
		});
		
		enterVal.addButtonPressedListener(new IButtonPressedListener(){

			public void buttonPressed(ButtonPressedEvent e) {
				try{
					float v = Float.valueOf(val.getText());
					TextBoxController.this.setCurrentValue(v);
				}catch(NumberFormatException  e1)
				{
					//fine
					val.setText(String.valueOf(curVal));
				}
			}			
		});
		
		val.addKeyListener(new IKeyListener()
		{
			public void keyPressed(KeyPressedEvent keyPressedEvent) {
				if(keyPressedEvent.getKeyClass() == Key.ENTER)
				{
					try{
						float v = Float.valueOf(val.getText());
						TextBoxController.this.setCurrentValue(v);
					}catch(NumberFormatException  e1)
					{
						//fine
						val.setText(String.valueOf(curVal));
					}
				}
			}

			public void keyReleased(KeyReleasedEvent keyReleasedEvent) {
			
			}

			public void keyTyped(KeyTypedEvent keyTypedEvent) {
				
			}
			
		});
		
	}
	
	public boolean isWindowed() {
		return window.isWindowed();
	}
	
}
