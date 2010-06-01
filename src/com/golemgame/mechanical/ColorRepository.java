package com.golemgame.mechanical;

import java.util.Collection;

import com.golemgame.mvc.ColorType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SustainedView;
import com.golemgame.mvc.golems.ColorRepositoryInterpreter;

public class ColorRepository implements SustainedView {


	private ColorRepositoryInterpreter interpreter;

	public ColorRepository(PropertyStore store) {
		super();
		interpreter = new ColorRepositoryInterpreter(store);
		store.setSustainedView(this);
	}

	public void invertView(PropertyStore store) {
		
	}

	public void refresh() {

	}

	public void remove() {
		
	}

	public PropertyStore getStore() {
		return interpreter.getStore();
	}
	
	public void addColor(ColorType color)
	{
		interpreter.addColor(color);
	}
	
	public void removeFunction(int num)
	{
		interpreter.getColors().removeElement(num);
	
	}

	public Collection<DataType> getColors() {
		return interpreter.getColors().getValues();
	}
}
