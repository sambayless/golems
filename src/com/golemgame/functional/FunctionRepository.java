package com.golemgame.functional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SustainedView;
import com.golemgame.mvc.golems.FunctionSettingsRepositoryInterpreter;
import com.golemgame.mvc.golems.functions.FunctionSettingsInterpreter;

public class FunctionRepository implements SustainedView{
	private List<FunctionSettingsInterpreter> effects = new ArrayList<FunctionSettingsInterpreter>();
	
	private FunctionSettingsRepositoryInterpreter interpreter;

	public FunctionRepository(PropertyStore store) {
		super();
		interpreter = new FunctionSettingsRepositoryInterpreter(store);
		store.setSustainedView(this);
	}

	public void invertView(PropertyStore store) {
		
	}

	public void refresh() {
		effects.clear();
		for (DataType data:interpreter.getFunctions().getValues())
		{
			if (data.getType()!= DataType.Type.PROPERTIES)
				continue;			
			effects.add(new FunctionSettingsInterpreter((PropertyStore)data));
		}
		Collections.sort(effects);
	}

	public void remove() {
		
	}

	public PropertyStore getStore() {
		return interpreter.getStore();
	}
	
	public void addFunction(PropertyStore effect)
	{
		FunctionSettingsInterpreter tempInterp = new FunctionSettingsInterpreter(effect);
		//String name = tempInterp.getEffectName();
		//find any matching named effect and remove if from the repository
		int pos = Collections.binarySearch(effects, tempInterp);
		if (pos>=0)
		{
			interpreter.removeFunction(	effects.get(pos).getStore());
		}		
		interpreter.addFunction(effect);
		interpreter.refresh();
	}
	
	public void removeFunction(PropertyStore effect)
	{
		interpreter.removeFunction(effect);
		interpreter.refresh();
	}

	public List<FunctionSettingsInterpreter> getFunctions() {
		return effects;
	}
	

}
