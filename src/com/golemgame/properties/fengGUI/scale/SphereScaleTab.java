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

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.golems.SphereInterpreter;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;

public class SphereScaleTab extends ScaleTab {

	private SphereInterpreter interpreter;
	private TextEditor radius;

	public SphereScaleTab() {
		super("Sphere Scale");
	}

	@Override
	public void close(boolean cancel) {
		super.close(cancel);
		if(!cancel)
		{
			//boolean changed = false;
			standardClosingBehaviourMultiplex(radius, SphereInterpreter.RADII,0, DataType.Type.VECTOR3, Format.Float);

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
		FengGUI.createLabel(position,"Radius").setExpandable(false);		
		radius = FengGUI.createTextEditor(position);

		
		getTab().setLayoutManager(new BorderLayout());
		getTab().addWidget(internal);
	}
	
	@Override
	public void open() {
		super.open();
		interpreter = new SphereInterpreter(super.getPrototype());
		interpreter.loadDefaults();
		initializePrototype();
		super.associateWithKey(radius, SphereInterpreter.RADII);
	
		super.standardOpeningBehaviour(radius, SphereInterpreter.RADII, Format.Float,0,DataType.Type.VECTOR3);
	
	}

}
