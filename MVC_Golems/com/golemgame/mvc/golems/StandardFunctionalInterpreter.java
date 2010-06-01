package com.golemgame.mvc.golems;

import com.golemgame.mvc.PropertyStore;

public class StandardFunctionalInterpreter extends PhysicalStructureInterpreter {
	
	//provides easy access to the most commonly used 3 wireport configuration
	//subclasses are not required to use all of these ports.
	
	public StandardFunctionalInterpreter(PropertyStore store) {
		super(store);
	}

	public PropertyStore getOutput()
	{
		return super.getOutput(0);
	}
	
/*	public void setOutput(Reference reference)
	{
		super.setOutput(0, reference);
	}
	*/
	public PropertyStore getInput()
	{
		return super.getInput(0);
	}
	
/*	public void setInput(Reference reference)
	{
		super.setInput(0, reference);
	}*/
	
	public PropertyStore getAuxInput()
	{
		return super.getInput(1);
	}
	
/*	public void setAuxInput(Reference reference)
	{
		super.setInput(1, reference);
	}*/
}
