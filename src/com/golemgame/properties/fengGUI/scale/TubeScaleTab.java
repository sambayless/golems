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

import com.golemgame.mvc.golems.TubeInterpreter;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;

public class TubeScaleTab extends ScaleTab {

	private TubeInterpreter interpreter;
	private TextEditor height;
	private TextEditor innerRadius;
	private TextEditor radius;
	private TextEditor arc;
	
	public TubeScaleTab() {
		super("Tube Scale");
	}

	@Override
	public void close(boolean cancel) {
		super.close(cancel);
		if(!cancel)
		{
			//boolean changed = false;
			standardClosingBehaviour(height, TubeInterpreter.CYL_HEIGHT, Format.Float);
			standardClosingBehaviour(radius, TubeInterpreter.CYL_RADIUS, Format.Float);
			standardClosingBehaviour(innerRadius, TubeInterpreter.RADIUS_INNER, Format.Float);
			standardClosingBehaviour(arc, TubeInterpreter.ARC,Format.Angle);
			
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
		FengGUI.createLabel(positionBig,"Tube: ").setExpandable(false);
		
		Container position = FengGUI.createContainer(positionBig);
		
		position.setLayoutManager(new RowLayout());
		FengGUI.createLabel(position,"Height").setExpandable(false);		
		height = FengGUI.createTextEditor(position);
		FengGUI.createLabel(position,"Radius").setExpandable(false);
		radius = FengGUI.createTextEditor(position);

		FengGUI.createLabel(position,"Inner Radius").setExpandable(false);
		innerRadius = FengGUI.createTextEditor(position);

		FengGUI.createLabel(position,"Arc").setExpandable(false);
		arc = FengGUI.createTextEditor(position);
		
		getTab().setLayoutManager(new BorderLayout());
		getTab().addWidget(internal);
	}
	
	@Override
	public void open() {
		super.open();
		interpreter = new TubeInterpreter(super.getPrototype());
		interpreter.loadDefaults();
		initializePrototype();
		super.associateWithKey(radius, TubeInterpreter.CYL_RADIUS);
		super.associateWithKey(height, TubeInterpreter.CYL_HEIGHT);
		super.associateWithKey(innerRadius, TubeInterpreter.RADIUS_INNER);
		super.associateWithKey(arc, TubeInterpreter.ARC);
		super.standardOpeningBehaviour(arc, TubeInterpreter.ARC,Format.Angle);
		super.standardOpeningBehaviour(radius, TubeInterpreter.CYL_RADIUS,Format.Float);
		super.standardOpeningBehaviour(height, TubeInterpreter.CYL_HEIGHT);
		super.standardOpeningBehaviour(innerRadius, TubeInterpreter.RADIUS_INNER,Format.Float);
	}

}
