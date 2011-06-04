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
