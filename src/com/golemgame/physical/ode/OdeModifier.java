package com.golemgame.physical.ode;

import java.util.Map;

import com.golemgame.functional.FunctionSettings;
import com.golemgame.functional.component.BComponent;
import com.golemgame.functional.component.BDifferentiator;
import com.golemgame.functional.component.BFunctionApplier;
import com.golemgame.functional.component.BMind;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.Reference;
import com.golemgame.mvc.golems.ModifierInterpreter;
import com.golemgame.mvc.golems.WirePortInterpreter;
import com.golemgame.mvc.golems.functions.FunctionSettingsInterpreter.FunctionType;
import com.golemgame.physical.ode.compile.OdePhysicsEnvironment;
import com.jmex.physics.PhysicsNode;

public class OdeModifier extends OdePhysicalStructure{
	
	
	private final ModifierInterpreter interpreter;
	
	public OdeModifier(PropertyStore store) {
		super(store);
		interpreter = new ModifierInterpreter(store);
	}
	public boolean isPropagating() {
		return false;
	}
	public void buildMind(BMind mind, Map<OdePhysicalStructure, PhysicsNode> physicsMap, Map<Reference,BComponent> wireMap, OdePhysicsEnvironment environment ) {
		BComponent source;

		WirePortInterpreter input = new WirePortInterpreter(interpreter.getAuxInput());
		FunctionSettings fun = new FunctionSettings(interpreter.getFunctionStore());
		fun.refresh();
		FunctionType fType = fun.getFunctionType();
		if (fType == FunctionType.Function || fType ==null)
		{
			source = new BFunctionApplier(fun);
			mind.addComponent(((BFunctionApplier )source).getSwitchInput());
			((BFunctionApplier )source).setThresholdType(interpreter.getThresholdType());
			wireMap.put(input.getID(),((BFunctionApplier )source).getSwitchInput());
			((BFunctionApplier )source).setSwitchType(interpreter.getSwitchType());
			((BFunctionApplier )source).setThreshold(interpreter.getThreshold());
			
		}else 
		{
			source =new BDifferentiator (fType == FunctionType.AntiDifferentiate);
			mind.addComponent(((BDifferentiator )source).getSwitchInput());
		//	((BDifferentiator )source).setThresholdInverted(interpreter.isThresholdInverted());
			wireMap.put(input.getID(),((BDifferentiator )source).getSwitchInput());
		}
		
		mind.addComponent(source);

		
		WirePortInterpreter out = new WirePortInterpreter(interpreter.getOutput());
		WirePortInterpreter in = new WirePortInterpreter(interpreter.getInput());
		
		wireMap.put(out.getID(), source);
		wireMap.put(in.getID(), source);
		
		
		
	}

	
}
