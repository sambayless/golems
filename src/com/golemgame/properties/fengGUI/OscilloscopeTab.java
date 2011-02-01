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

import org.fenggui.CheckBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.TextEditor;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;

import com.golemgame.local.StringConstants;
import com.golemgame.mvc.golems.OscilloscopeInterpreter;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;

public class OscilloscopeTab extends PropertyTabAdapter {
	private TextEditor name;
	private OscilloscopeInterpreter interpreter;
	private Container innerControls;
	public OscilloscopeTab() {
		super(StringConstants.get("TOOLBAR.OSCILLOSCOPE","Oscilloscope"));
		
	}

	@Override
	protected void buildGUI() {
		getTab().setLayoutManager(new BorderLayout());
		Container upperControls = FengGUI.createContainer(getTab());
		upperControls.setLayoutManager(new RowLayout(false));
		upperControls.setLayoutData(BorderLayoutData.NORTH);
		Container nameContainer = FengGUI.createContainer(upperControls);
		nameContainer.setLayoutManager(new RowLayout());
		Label lblName = FengGUI.createLabel(nameContainer,"Name: ");
		lblName.setExpandable(false);
		name = FengGUI.createTextEditor(nameContainer);
		
		
		innerControls = FengGUI.createContainer(getTab());
		innerControls.setLayoutData(BorderLayoutData.SOUTH);
		innerControls.setLayoutManager(new BorderLayout());
	}

	@Override
	public void close(boolean cancel) {

		super.close(cancel);
		if(!cancel)
		{
			super.standardClosingBehaviour(name, OscilloscopeInterpreter.OSCILLOSCOPE_NAME,Format.String);
			
		
			Action<?> apply = super.apply();
			
			UndoManager.getInstance().addAction(apply);
		}
	}

	@Override
	public boolean embed(ITab tab) {
		if(tab instanceof OutputDeviceTab)
		{
			tab.getTab().setLayoutData(BorderLayoutData.NORTH);
			innerControls.addWidget(tab.getTab());
			super.getEmbeddedTabs().add(tab);
		
			getTab().layout();
			
			return true;
		}
		return super.embed(tab);
	}

	@Override
	public void open() {

		super.open();
		this.interpreter = new OscilloscopeInterpreter(getPrototype());
		interpreter.loadDefaults();
		initializePrototype();
		super.associateWithKey(name, OscilloscopeInterpreter.OSCILLOSCOPE_NAME);

	
		super.standardOpeningBehaviour(name, OscilloscopeInterpreter.OSCILLOSCOPE_NAME,Format.String);
		
	}

}
