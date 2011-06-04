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

import java.awt.Color;

import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.ObservableLabelWidget;
import org.fenggui.composite.Window;
import org.fenggui.decorator.background.PlainBackground;
import org.fenggui.event.IWindowChangedListener;
import org.fenggui.event.mouse.MouseAdapter;
import org.fenggui.event.mouse.MouseDoubleClickedEvent;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.util.Alignment;

import com.golemgame.instrumentation.fenggui.EmbeddedWindow;
import com.jme.scene.Node;

public class TextOutputInstrument implements Instrument{
	protected EmbeddedWindow window;
	protected Container controlContainer;
	private boolean visible = false;
	private String name = "Text";
	private String typeName = "Text";
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
		window.setResizable(!locked);
		window.setMovable(!locked);
		window.getCloseButton().setEnabled(!locked);
		window.setPinable(!locked);
	}

	public boolean isLocked() {
		return locked;
	}
	
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public void setWindowed(boolean windowed) {
		window.setWindowed(windowed);
	}

	private ObservableLabelWidget display;
	public TextOutputInstrument() {
		super();
		window = new EmbeddedWindow(true, false, false, true);
		FengGUI.getTheme().setUp(window);
		window.setTitle("Text");
		window.setSize(50, 100);
		//window.getContentContainer().setLayoutManager(new BorderLayout());
		window.addWindowChangedListener(new IWindowChangedListener(){

			public void userChanged() {
				userPositioned = true;
			}
			
		});
		controlContainer = new Container();
	
		window.getContentContainer().addWidget(controlContainer);
		controlContainer.setLayoutData(BorderLayoutData.CENTER);
		display = new ObservableLabelWidget();
		display.getAppearance().add(new PlainBackground(new org.fenggui.util.Color(1f,1f,1f,0.7f)));
		controlContainer.addWidget(display);
		//window.setVisible(false);
		window.layout();
		display.getAppearance().setAlignment(Alignment.MIDDLE);
		display.addMouseListener(new MouseAdapter(){

			@Override
			public void mouseDoubleClicked(MouseDoubleClickedEvent mouseDoubleClickedEvent) {
				if(!locked)
					window.setWindowed(!window.isWindowed());
			}
			
		});
//		setWindowed(false);
	}
	
	

	public boolean isWindowed() {
		return window.isWindowed();
	}
	public void setName(String name)
	{
		this.name = name;
		if(name.equalsIgnoreCase("(Unnamed)"))
			window.setTitle(typeName);
		else if(name.toUpperCase().contains(typeName.toUpperCase()))
		{
			window.setTitle(name);
		}else
			window.setTitle(name + " (" + typeName + ")");
	//	window.layout();
	}

	public void attachSpatial(Node attachTo) {
		
	}


	public void attach(InstrumentationLayer instrumentLayer) {
	
	}

	public String getName() {
		return name;
	}
	public Window getInstrumentInterface() {

		return window;
	}

	public void update(float tpf) {
		
	} 
	

	public void setVisible(boolean visible) {
		if(this.visible!=visible)
		{
			this.visible = visible;
			window.setVisible(visible);
		}
	
	}
	
	public void setCurrentValue(float value)
	{
		display.setText(String.valueOf(Math.round(value*100.0)/100.0));
	}	
	


}
