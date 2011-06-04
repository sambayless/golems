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

import org.fenggui.CheckBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.TextEditor;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;

import com.golemgame.mvc.golems.AxleInterpreter;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;

public class AxleScaleTab extends ScaleTab {

	private AxleInterpreter interpreter;
	private TextEditor scaleLeft;
	private TextEditor scaleRight;
	private TextEditor leftLength;
	private TextEditor rightLength;
	private CheckBox<?> isBearing;
	//private TextEditor bearingLength;
	//private TextEditor bearingRadius;
	
	public AxleScaleTab() {
		super("Axle Scale");
	}

	@Override
	public void close(boolean cancel) {
		super.close(cancel);
		if(!cancel)
		{
			//boolean changed = false;
			standardClosingBehaviour(scaleLeft, AxleInterpreter.LEFT_JOINT_RADIUS, Format.Float);
			standardClosingBehaviour(scaleRight, AxleInterpreter.RIGHT_JOINT_RADIUS, Format.Float);
			standardClosingBehaviour(leftLength, AxleInterpreter.LEFT_JOINT_LENGTH, Format.Float);
			standardClosingBehaviour(rightLength, AxleInterpreter.RIGHT_JOINT_LENGTH, Format.Float);
			standardClosingBehaviour(isBearing, AxleInterpreter.IS_BEARING);

				Action<?> apply = super.apply();
				
				UndoManager.getInstance().addAction(apply);
			
		}
	}
	
	@Override
	protected void buildGUI() {
		super.buildGUI();
		Container internal = FengGUI.createContainer();
		internal.setLayoutManager(new RowLayout(false));
		internal.setLayoutData(BorderLayoutData.NORTH);
		isBearing = FengGUI.createCheckBox(internal,"Ball Bearing");
		
		Container scaleContainer = FengGUI.createContainer();
		scaleContainer.setLayoutData(BorderLayoutData.NORTH);
		scaleContainer.setLayoutManager(new RowLayout(false));
		

		
		Container position = FengGUI.createContainer(scaleContainer);
		
		position.setLayoutManager(new RowLayout());
		FengGUI.createLabel(position,"Left Radius").setExpandable(false);		
		scaleLeft = FengGUI.createTextEditor(position);
		
		FengGUI.createLabel(position,"Right Radius").setExpandable(false);		
		scaleRight = FengGUI.createTextEditor(position);
		
		FengGUI.createLabel(position,"Left Length").setExpandable(false);		
		leftLength = FengGUI.createTextEditor(position);

		FengGUI.createLabel(position,"Right Length").setExpandable(false);		
		rightLength = FengGUI.createTextEditor(position);

		
		
		getTab().setLayoutManager(new BorderLayout());
		internal.addWidget(scaleContainer);
		getTab().addWidget(internal);
	}
	
	@Override
	public void open() {
		super.open();
		interpreter = new AxleInterpreter(super.getPrototype());
		interpreter.loadDefaults();
		initializePrototype();
		associateWithKey(scaleLeft, AxleInterpreter.LEFT_JOINT_RADIUS);
		associateWithKey(scaleRight, AxleInterpreter.RIGHT_JOINT_RADIUS);
		associateWithKey(leftLength, AxleInterpreter.LEFT_JOINT_LENGTH);
		associateWithKey(rightLength, AxleInterpreter.RIGHT_JOINT_LENGTH);
		standardOpeningBehaviour(scaleLeft, AxleInterpreter.LEFT_JOINT_RADIUS, Format.Float);
		standardOpeningBehaviour(scaleRight, AxleInterpreter.RIGHT_JOINT_RADIUS, Format.Float);
		standardOpeningBehaviour(leftLength, AxleInterpreter.LEFT_JOINT_LENGTH, Format.Float);
		standardOpeningBehaviour(rightLength, AxleInterpreter.RIGHT_JOINT_LENGTH, Format.Float);
		
		associateWithKey(isBearing, AxleInterpreter.IS_BEARING);
		standardOpeningBehaviour(isBearing, AxleInterpreter.IS_BEARING);
		
	}

}
