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
