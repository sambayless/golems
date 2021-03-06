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
import org.fenggui.layout.GridLayout;

import com.golemgame.mvc.golems.MotorPropertiesInterpreter;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;

public class PIDControlsTab extends PropertyTabAdapter{

	private MotorPropertiesInterpreter interpreter;
	private TextEditor pid_P;
	private TextEditor pid_I;	
	private TextEditor pid_D;	
	
	public PIDControlsTab() {
		super("PID");
	}

	@Override
	protected void buildGUI() {
		
		getTab().setLayoutManager(new BorderLayout());
		
		Container pidContainer = FengGUI.createContainer(getTab());
		pidContainer.setLayoutData(BorderLayoutData.NORTH);
		pidContainer.setLayoutManager(new GridLayout(3,2));
	/*	FengGUI.createLabel(toothContainer, "Number of Teeth");
		toothNumber = FengGUI.createTextEditor();*/
		
		FengGUI.createLabel(pidContainer, "K_p");
		pid_P = FengGUI.createTextEditor(pidContainer);
		
		FengGUI.createLabel(pidContainer, "K_i");
		pid_I = FengGUI.createTextEditor(pidContainer);
		
		FengGUI.createLabel(pidContainer, "K_d");
		pid_D = FengGUI.createTextEditor(pidContainer);
		
		
	}

	@Override
	public void close(boolean cancel) {
		if(!cancel)
		{
			standardClosingBehaviour(pid_P, MotorPropertiesInterpreter.PID_KP,Format.Float);
			standardClosingBehaviour(pid_I, MotorPropertiesInterpreter.PID_KI,Format.Float);
			standardClosingBehaviour(pid_D, MotorPropertiesInterpreter.PID_KD,Format.Float);

			Action<?> apply = super.apply();
			
			UndoManager.getInstance().addAction(apply);
		}
	}



	@Override
	public void open() {
		this.interpreter = new MotorPropertiesInterpreter(getPrototype());
		this.interpreter.loadDefaults();
		initializePrototype();
		
		super.associateWithKey(pid_P, MotorPropertiesInterpreter.PID_KP);
		super.associateWithKey(pid_I, MotorPropertiesInterpreter.PID_KI);
		super.associateWithKey(pid_D, MotorPropertiesInterpreter.PID_KD);
		
		super.standardOpeningBehaviour(pid_P, MotorPropertiesInterpreter.PID_KP);
		super.standardOpeningBehaviour(pid_I, MotorPropertiesInterpreter.PID_KI);
		super.standardOpeningBehaviour(pid_D, MotorPropertiesInterpreter.PID_KD);	
	}
}
