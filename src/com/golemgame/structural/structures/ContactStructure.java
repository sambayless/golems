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
package com.golemgame.structural.structures;

import com.golemgame.functional.WirePort;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.ContactInterpreter;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;


public class ContactStructure extends SphereStructure {

	private static final long serialVersionUID = 1L;
	private WirePort output;
	private WirePort input;
	
	private ContactInterpreter interpreter;
	public ContactStructure(PropertyStore store) {
		super(store);
		interpreter = new ContactInterpreter(store);
		
		output = new WirePort(this,false);
		input = new WirePort(this,true);
	
		output.getModel().getLocalTranslation().y = -0.5f;
		output.getModel().updateWorldData();
		
		input.getModel().getLocalTranslation().y = 0.5f;
		input.getModel().updateWorldData();
		
		
		super.registerWirePort(input);	
	
		super.registerWirePort(output);
		
		getModel().addChild(output.getModel());
		getModel().addChild(input.getModel());
		
	}
	@Override
	public void refresh() {
		super.refresh();
		output.getModel().getLocalTranslation().y = -super.getInterpreter().getRadius();
		input.getModel().getLocalTranslation().y = super.getInterpreter().getRadius();
	}

	
	
}
