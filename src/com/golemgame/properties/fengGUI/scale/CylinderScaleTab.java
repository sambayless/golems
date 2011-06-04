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

import com.golemgame.mvc.golems.CylinderInterpreter;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;

public class CylinderScaleTab extends ScaleTab {

	private CylinderInterpreter interpreter;
	private TextEditor height;

	private TextEditor radius;
	
	public CylinderScaleTab() {
		super("Cylinder Scale");
	}
	public CylinderScaleTab(String name) {
		super(name);
	}
	@Override
	public void close(boolean cancel) {
		super.close(cancel);
		if(!cancel)
		{
			//boolean changed = false;
			standardClosingBehaviour(height, CylinderInterpreter.CYL_HEIGHT, Format.Float);
			standardClosingBehaviour(radius, CylinderInterpreter.CYL_RADIUS, Format.Float);

			
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
		
		Container positionBig = FengGUI.createContainer(internal);
		positionBig.setLayoutManager(new RowLayout());
		FengGUI.createLabel(positionBig,"Cylinder: ").setExpandable(false);
		
		Container position = FengGUI.createContainer(positionBig);
		
		position.setLayoutManager(new RowLayout());
		FengGUI.createLabel(position,"Height").setExpandable(false);		
		height = FengGUI.createTextEditor(position);
		FengGUI.createLabel(position,"Radius").setExpandable(false);
		radius = FengGUI.createTextEditor(position);

		
		getTab().setLayoutManager(new BorderLayout());
		getTab().addWidget(internal);
	}
	
	@Override
	public void open() {
		super.open();
		interpreter = new CylinderInterpreter(super.getPrototype());
		interpreter.loadDefaults();
		initializePrototype();
		super.associateWithKey(radius, CylinderInterpreter.CYL_RADIUS);
		super.associateWithKey(height, CylinderInterpreter.CYL_HEIGHT);
		super.standardOpeningBehaviour(radius, CylinderInterpreter.CYL_RADIUS,Format.Float);
		super.standardOpeningBehaviour(height, CylinderInterpreter.CYL_HEIGHT);

	}

}
