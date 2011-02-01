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
import org.fenggui.layout.RowLayout;

import com.golemgame.local.StringConstants;
import com.golemgame.mvc.golems.HydraulicInterpreter;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;




public class HydraulicsTab extends PropertyTabAdapter {
	

	private HydraulicInterpreter interpreter;
	
	private TextEditor maxSep;
	private TextEditor minSep;

	public HydraulicsTab() {
		super( StringConstants.get("TOOLBAR.HYDRAULIC","Hydraulic"));
	}

	
	@Override
	public void close(boolean cancel) {
		super.close(cancel);
		if(!cancel)
		{
			super.standardClosingBehaviour(maxSep, HydraulicInterpreter.JOINT_MAX_DISTANCE);
			super.standardClosingBehaviour(minSep, HydraulicInterpreter.JOINT_MIN_DISTANCE);
			Action<?> apply = super.apply();
			
			UndoManager.getInstance().addAction(apply);
		}
	}

	@Override
	public void open() {
		super.open();
			interpreter = new HydraulicInterpreter(super.getPrototype());
		
			interpreter.loadDefaults();
			super.initializePrototype();

			super.associateWithKey(maxSep, HydraulicInterpreter.JOINT_MAX_DISTANCE);
			
			super.associateWithKey(minSep, HydraulicInterpreter.JOINT_MIN_DISTANCE);
			
			
			super.standardOpeningBehaviour(maxSep, HydraulicInterpreter.JOINT_MAX_DISTANCE);
			super.standardOpeningBehaviour(minSep, HydraulicInterpreter.JOINT_MIN_DISTANCE);
	
	}

	@Override
	protected void buildGUI() {
		super.getTab().setLayoutManager(new RowLayout(false));
		
		Container minRow = FengGUI.createContainer(getTab());
		minRow.setLayoutManager(new RowLayout());
		FengGUI.createLabel(minRow, StringConstants.get("PROPERTIES.MOTOR.MIN_SEP","Minimum Separation of End Points"));
		minSep = FengGUI.createTextEditor(minRow);
		
		Container maxRow = FengGUI.createContainer(getTab());
		maxRow.setLayoutManager(new RowLayout());
		FengGUI.createLabel(maxRow,StringConstants.get("PROPERTIES.MOTOR.MAX_SEP","Maximum Separation of End Points"));
		maxSep = FengGUI.createTextEditor(maxRow);

	}


}
