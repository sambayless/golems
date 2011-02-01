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

import java.util.concurrent.CopyOnWriteArrayList;

import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.composite.Window;
import org.fenggui.layout.BorderLayoutData;

import com.golemgame.instrumentation.fenggui.EmbeddedWindow;
import com.jme.scene.Node;

public abstract class SingleAxisController implements Instrument{
	protected EmbeddedWindow window;
	protected Container controlContainer;
	private boolean visible = false;
	private String name = "Control";
	private String typeName = "Control";
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	private final CopyOnWriteArrayList<AxisControlListener> listeners = new CopyOnWriteArrayList<AxisControlListener>();
	public void setWindowed(boolean windowed) {
		window.setWindowed(windowed);
	}
	public SingleAxisController() {
		super();
		window = new EmbeddedWindow(true, false, false, true);
		FengGUI.getTheme().setUp(window);
		window.setTitle("Control");
		window.setSize(50, 200);
		//window.getContentContainer().setLayoutManager(new BorderLayout());
		
		controlContainer = new Container();
	
		window.getContentContainer().addWidget(controlContainer);
		controlContainer.setLayoutData(BorderLayoutData.CENTER);
		//window.setVisible(false);
		window.layout();
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
		for(AxisControlListener l:listeners)
			l.valueSet(value);
	}	
	
	public void registerListener(AxisControlListener l)
	{
		this.listeners.add(l);
	}
	
	public void removeListener(AxisControlListener l)
	{
		this.listeners.remove(l);
	}
}
