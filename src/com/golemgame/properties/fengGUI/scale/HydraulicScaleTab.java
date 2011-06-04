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
package com.golemgame.properties.fengGUI.scale;

import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.TextEditor;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;

import com.golemgame.mvc.golems.HydraulicInterpreter;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;

public class HydraulicScaleTab extends ScaleTab {

	private HydraulicInterpreter interpreter;
	private TextEditor maxDistance;
	private TextEditor minDistance;
	private TextEditor radius;
	private TextEditor distance;
	
	public HydraulicScaleTab() {
		super("Hydraulic Scale");
	}

	@Override
	public void close(boolean cancel) {
		super.close(cancel);
		if(!cancel)
		{
			//boolean changed = false;

			standardClosingBehaviour(radius, HydraulicInterpreter.JOINT_RADIUS, Format.Float);
			standardClosingBehaviour(distance, HydraulicInterpreter.JOINT_DISTANCE, Format.Float);
			standardClosingBehaviour(maxDistance, HydraulicInterpreter.JOINT_MAX_DISTANCE, Format.Float);
			standardClosingBehaviour(minDistance, HydraulicInterpreter.JOINT_MIN_DISTANCE, Format.Float);

				Action<?> apply = super.apply();
				
				UndoManager.getInstance().addAction(apply);
			
		}
	}
	
	@Override
	protected void buildGUI() {
		super.buildGUI();
	
		Container internal = FengGUI.createContainer();
		internal.setLayoutData(BorderLayoutData.NORTH);
		internal.setLayoutManager(new RowLayout(false));

		Container position = FengGUI.createContainer(internal);
		

		
		FengGUI.createLabel(position,"Radius").setExpandable(false);		
		radius = FengGUI.createTextEditor(position);

		FengGUI.createLabel(position,"Distance").setExpandable(false);		
		distance = FengGUI.createTextEditor(position);

		
		FengGUI.createLabel(position,"Min Distance").setExpandable(false);		
		minDistance = FengGUI.createTextEditor(position);

		FengGUI.createLabel(position,"Max Distance").setExpandable(false);		
		maxDistance = FengGUI.createTextEditor(position);
		
		getTab().setLayoutManager(new BorderLayout());
		getTab().addWidget(internal);
	}
	
	@Override
	public void open() {
		super.open();
		interpreter = new HydraulicInterpreter(super.getPrototype());
		interpreter.loadDefaults();
		initializePrototype();

		associateWithKey(radius, HydraulicInterpreter.JOINT_RADIUS);
		associateWithKey(distance, HydraulicInterpreter.JOINT_DISTANCE);
		associateWithKey(maxDistance, HydraulicInterpreter.JOINT_MAX_DISTANCE);
		associateWithKey(minDistance, HydraulicInterpreter.JOINT_MIN_DISTANCE);
		
		standardOpeningBehaviour(radius, HydraulicInterpreter.JOINT_RADIUS, Format.Float);
		standardOpeningBehaviour(distance, HydraulicInterpreter.JOINT_DISTANCE, Format.Float);
		standardOpeningBehaviour(maxDistance, HydraulicInterpreter.JOINT_MAX_DISTANCE, Format.Float);
		standardOpeningBehaviour(minDistance, HydraulicInterpreter.JOINT_MIN_DISTANCE, Format.Float);
	}

}
