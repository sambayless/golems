package com.golemgame.mvc.golems;

import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.PropertyStore;


public interface FunctionalInterpreter  {

	public static final String OUTPUTS = "outputs";
	public static final String INPUTS = "inputs";

	
	
	public CollectionType getInputs();
	
	public CollectionType getOutputs();
	
	//public void setOutput(int outputNumber, PropertyStore store);
	
	public PropertyStore getOutput(int outputNumber);

	//public void setInput(int inputNumber, PropertyStore store);
	
	public PropertyStore getInput(int inputNumber);

}
