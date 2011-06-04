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
import com.jme.math.Vector3f;

public class GhostInterpreter extends StructureInterpreter{
	public GhostInterpreter(PropertyStore store) {
		super(store);
	
	}
	
	public GhostInterpreter() {
		this(new PropertyStore());		
	}

	@Override
	public Vector3f getLocalTranslation() {
		return super.getLocalTranslation(new Vector3f(1,0,0));
	}
	
	
}
