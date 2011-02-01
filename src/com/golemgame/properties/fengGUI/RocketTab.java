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

import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.TextEditor;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;

import com.golemgame.local.StringConstants;
import com.golemgame.mvc.golems.RocketInterpreter;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;


public class RocketTab extends PropertyTabAdapter {

	private RocketInterpreter interpreter;
	
	private TextEditor accel;
	

	public RocketTab() {
		super(StringConstants.get("TOOLBAR.ROCKET","Rocket"));
	}

	
	protected void buildGUI() {
		
		getTab().setLayoutManager(new BorderLayout());
		
		Container accelContainer = FengGUI.createContainer(getTab());
		accelContainer.setLayoutData(BorderLayoutData.NORTH);
		
		accelContainer.setLayoutManager(new RowLayout());
		FengGUI.createLabel(accelContainer,StringConstants.get("PROPERTIES.ROCKET.MAX_FORCE","Maximum Force (Newtons):"));
		accel = FengGUI.createTextEditor(accelContainer);
		
		
		
	}

	
	public void close(boolean cancel) {
		if(!cancel)
		{
			super.standardClosingBehaviour(accel, RocketInterpreter.MAX_ACCELERATION);

			Action<?> apply = super.apply();
			
			UndoManager.getInstance().addAction(apply);
			
		}
	}

	
	public void open() {
		interpreter = new RocketInterpreter(super.getPrototype());
		this.interpreter.loadDefaults();
		super.setComparePoint();
		super.associateWithKey(accel, RocketInterpreter.MAX_ACCELERATION);
		super.standardOpeningBehaviour(accel, RocketInterpreter.MAX_ACCELERATION);
		
	}
	
	
	
	
	
}
