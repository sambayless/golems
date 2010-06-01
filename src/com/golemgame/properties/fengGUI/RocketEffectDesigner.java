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
