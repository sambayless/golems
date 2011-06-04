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

import org.fenggui.CheckBox;
import org.fenggui.FengGUI;

import com.golemgame.local.StringConstants;
import com.golemgame.mvc.golems.RocketPropellantInterpreter;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;

public class RocketEffectDesigner extends PropertyTabAdapter{

	public RocketEffectDesigner() {
		super(StringConstants.get("PROPERTIES.ROCKET.PROPELLANT","Propellant"));

	}

	private RocketPropellantInterpreter interpreter;
	
	private CheckBox<?> enableEffects;
	
	
	

	@Override
	protected void buildGUI() {
		 enableEffects =	FengGUI.createCheckBox(getTab(), StringConstants.get("PROPERTIES.ROCKET.ENABLE","Enable Effects"));
	}

	@Override
	public void close(boolean cancel) {
		if(!cancel)
		{
			super.standardClosingBehaviour(enableEffects, RocketPropellantInterpreter.EFFECTS_ENABLED);

			Action<?> apply = super.apply();
			
			UndoManager.getInstance().addAction(apply);
		}
		super.close(cancel);
	}

	@Override
	public void open() {
		this.interpreter = new RocketPropellantInterpreter(super.getPrototype());
		
		super.associateWithKey(enableEffects, RocketPropellantInterpreter.EFFECTS_ENABLED);
		super.standardOpeningBehaviour(enableEffects, RocketPropellantInterpreter.EFFECTS_ENABLED);
		
	}

	
	
}
