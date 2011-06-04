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
import com.golemgame.mvc.golems.BallAndSocketInterpreter;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;

public class BallSocketScaleTab extends ScaleTab {

	private BallAndSocketInterpreter interpreter;
	private TextEditor jointLength;
	private TextEditor jointRadius;
	private TextEditor ballRadius;
	private CheckBox<?> isUniversal;
	
	public BallSocketScaleTab() {
		super("Ball And Socket Scale");
	}

	@Override
	public void close(boolean cancel) {
		super.close(cancel);
		if(!cancel)
		{
			//boolean changed = false;
			standardClosingBehaviour(jointLength, BallAndSocketInterpreter.LEFT_JOINT_LENGTH, Format.Float);
			standardClosingBehaviour(jointRadius, BallAndSocketInterpreter.LEFT_JOINT_RADIUS, Format.Float);
			standardClosingBehaviour(ballRadius, BallAndSocketInterpreter.RIGHT_JOINT_RADIUS, Format.Float);
			standardClosingBehaviour(isUniversal, BallAndSocketInterpreter.IS_UNIVERSAL);
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
		isUniversal = FengGUI.createCheckBox(internal,"Unversal Joint (A universal joint constrains rotation on the 3rd axis)");
		
		
		Container scaleContainer = FengGUI.createContainer();
		scaleContainer.setLayoutData(BorderLayoutData.NORTH);
		scaleContainer.setLayoutManager(new RowLayout(false));
		
		
		Container position = FengGUI.createContainer(scaleContainer);
		
		position.setLayoutManager(new RowLayout());
		FengGUI.createLabel(position,"Joint Length").setExpandable(false);		
		jointLength = FengGUI.createTextEditor(position);

		FengGUI.createLabel(position,"Joint Radius").setExpandable(false);		
		jointRadius = FengGUI.createTextEditor(position);

		FengGUI.createLabel(position,"Ball Radius").setExpandable(false);		
		ballRadius = FengGUI.createTextEditor(position);

		
		getTab().setLayoutManager(new BorderLayout());
		internal.addWidget(scaleContainer);
		getTab().addWidget(internal);
	}
	
	@Override
	public void open() {
		super.open();
		interpreter = new BallAndSocketInterpreter(super.getPrototype());
		interpreter.loadDefaults();
		initializePrototype();
		super.associateWithKey(jointLength, BallAndSocketInterpreter.LEFT_JOINT_LENGTH);	
		super.associateWithKey(jointRadius, BallAndSocketInterpreter.LEFT_JOINT_RADIUS);	
		super.associateWithKey(ballRadius, BallAndSocketInterpreter.RIGHT_JOINT_RADIUS);
		super.associateWithKey(isUniversal, BallAndSocketInterpreter.IS_UNIVERSAL);
		super.standardOpeningBehaviour(jointLength, BallAndSocketInterpreter.LEFT_JOINT_LENGTH, Format.Float);
		super.standardOpeningBehaviour(jointRadius, BallAndSocketInterpreter.LEFT_JOINT_RADIUS, Format.Float);
		super.standardOpeningBehaviour(ballRadius, BallAndSocketInterpreter.RIGHT_JOINT_RADIUS, Format.Float);
		super.standardOpeningBehaviour(isUniversal, BallAndSocketInterpreter.IS_UNIVERSAL);
	}

}
