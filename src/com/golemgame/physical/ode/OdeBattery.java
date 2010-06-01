package com.golemgame.physical.ode;

import java.util.Map;

import com.golemgame.functional.FunctionSettings;
import com.golemgame.functional.component.BComponent;
import com.golemgame.functional.component.BFunctionSource;
import com.golemgame.functional.component.BMind;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.Reference;
import com.golemgame.mvc.golems.BatteryInterpreter;
import com.golemgame.mvc.golems.WirePortInterpreter;
import com.golemgame.mvc.golems.BatteryInterpreter.SwitchType;
import com.golemgame.mvc.golems.BatteryInterpreter.ThresholdType;
import com.golemgame.physical.ode.compile.OdePhysicsEnvironment;
import com.jmex.physics.PhysicsNode;

public class OdeBattery extends OdePhysicalStructure{
	
	
	private final BatteryInterpreter interpreter;
	
	public OdeBattery(PropertyStore store) {
		super(store);
		interpreter = new BatteryInterpreter(store);
	}
	
	public boolean isPropagating() {
		return false;
	}

	public void buildMind(BMind mind, Map<OdePhysicalStructure, PhysicsNode> physicsMap, Map<Reference,BComponent> wireMap, OdePhysicsEnvironment environment ) {
	
	//	final int switchKey = interpreter.getInteractionKey();
		FunctionSettings f = new FunctionSettings( interpreter.getFunctionStore());
		f.refresh();
		final BFunctionSource source = new BFunctionSource(f);
		
		//final KeyboardInteractionType interactionType = interpreter.getInteractionType();
		final SwitchType switchType = interpreter.getSwitchType();
		final ThresholdType thresholdType = interpreter.getThresholdType();
		final float threshold = interpreter.getThreshold();
		source.setSwitchType(switchType);
		source.setThreshold(threshold);
		source.setThresholdType(thresholdType);
		mind.addComponent(source.getSwitchInput());
		
		mind.addSource(source);
		mind.addComponent(source);
		
		WirePortInterpreter out = new WirePortInterpreter(interpreter.getOutput());
		WirePortInterpreter in = new WirePortInterpreter(interpreter.getInput());
		
		wireMap.put(out.getID(), source);
		wireMap.put(in.getID(), source.getSwitchInput());
		
		//Disabled behaviour
		/*if (interpreter.interactsWithUser())
		{
			final BConstantSource constantSource = new BConstantSource();
			mind.addSource(constantSource);
			mind.addComponent(constantSource);
			constantSource.attachOutput(source.getSwitchInput());
			constantSource.setValue(-1);
			source.getSwitchInput().modulateState(constantSource.getValue(), false);
			environment.getInteractionServer().addInteractionListener(new Interactor()
			{
				
				private boolean engaged = false;
				public boolean onKey(char character, int keyCode, boolean pressed) {
					if (keyCode == switchKey)
					{
				
						if (interactionType == KeyboardInteractionType.HeldDown )
						{
							engaged = pressed;
						}else
						{
							if (pressed)
								engaged = ! engaged;
						}
			
						if (engaged)
						{							
							constantSource.setValue(1);
						}
						else
						{
							constantSource.setValue(-1);
						}
						return true;
					}
					return false;
				}
				
			});
		}*/
	}

	
}
