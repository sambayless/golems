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

import com.golemgame.mvc.golems.HingeInterpreter;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;

public class HingeScaleTab extends ScaleTab {

	private HingeInterpreter interpreter;
	private TextEditor scale;
	private TextEditor leftAngle;
	private TextEditor rightAngle;
	
	public HingeScaleTab() {
		super("Hinge Scale");
	}

	@Override
	public void close(boolean cancel) {
		super.close(cancel);
		if(!cancel)
		{
			//boolean changed = false;
			standardClosingBehaviour(scale, HingeInterpreter.LEFT_JOINT_LENGTH, Format.Float);
			standardClosingBehaviour(leftAngle, HingeInterpreter.LEFT_JOINT_ANGLE, Format.Angle);
			standardClosingBehaviour(rightAngle, HingeInterpreter.RIGHT_JOINT_ANGLE, Format.Angle);

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
		
		position.setLayoutManager(new RowLayout());
		FengGUI.createLabel(position,"Size").setExpandable(false);		
		scale = FengGUI.createTextEditor(position);

		FengGUI.createLabel(position,"Left Angle").setExpandable(false);		
		leftAngle = FengGUI.createTextEditor(position);

		FengGUI.createLabel(position,"Right Angle").setExpandable(false);		
		rightAngle = FengGUI.createTextEditor(position);

		
		getTab().setLayoutManager(new BorderLayout());
		getTab().addWidget(internal);
	}
	
	@Override
	public void open() {
		super.open();
		interpreter = new HingeInterpreter(super.getPrototype());
		interpreter.loadDefaults();
		initializePrototype();
		super.associateWithKey(scale, HingeInterpreter.LEFT_JOINT_LENGTH);	
		super.associateWithKey(leftAngle, HingeInterpreter.LEFT_JOINT_ANGLE);	
		super.associateWithKey(rightAngle, HingeInterpreter.RIGHT_JOINT_ANGLE);
		super.standardOpeningBehaviour(scale, HingeInterpreter.LEFT_JOINT_LENGTH, Format.Float);
		super.standardOpeningBehaviour(leftAngle, HingeInterpreter.LEFT_JOINT_ANGLE, Format.Angle);
		super.standardOpeningBehaviour(rightAngle, HingeInterpreter.RIGHT_JOINT_ANGLE, Format.Angle);
		
	}

}
