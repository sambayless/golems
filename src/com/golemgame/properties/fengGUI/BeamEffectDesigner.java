package com.golemgame.properties.fengGUI;

import org.fenggui.CheckBox;
import org.fenggui.FengGUI;

import com.golemgame.mvc.golems.BeamInterpreter;
import com.golemgame.mvc.golems.RocketPropellantInterpreter;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;

public class BeamEffectDesigner extends PropertyTabAdapter{

	public BeamEffectDesigner() {
		super("Beam");

	}

	private BeamInterpreter interpreter;
	
	private CheckBox<?> enableEffects;
	
	
	

	@Override
	protected void buildGUI() {
		 enableEffects =	FengGUI.createCheckBox(getTab(), "Enable Effects");
	}

	@Override
	public void close(boolean cancel) {
		if(!cancel)
		{
			super.standardClosingBehaviour(enableEffects, BeamInterpreter.EFFECTS_ENABLED);

			Action<?> apply = super.apply();
			
			UndoManager.getInstance().addAction(apply);
		}
		super.close(cancel);
	}

	@Override
	public void open() {
		this.interpreter = new BeamInterpreter(super.getPrototype());
		
		super.associateWithKey(enableEffects, BeamInterpreter.EFFECTS_ENABLED);
		super.standardOpeningBehaviour(enableEffects, BeamInterpreter.EFFECTS_ENABLED);
		
	}

	
	
}
