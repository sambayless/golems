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
