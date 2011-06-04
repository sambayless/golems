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
package com.golemgame.tool.action.mvc;

import java.util.ArrayList;
import java.util.Collection;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.tool.action.Action;

public abstract class CopyComponentAction extends Action<CopyComponentAction> {
	private Collection< PropertyStore> components = new ArrayList<PropertyStore>();
	
	public void addComponent(PropertyStore component) {
		components.add(component);
	}

	public CopyComponentAction() {
		super();
		
	}



	@Override
	public String getDescription() {
		return "Copy a component";
	}

	public Collection<PropertyStore> getComponents() {
		return components;
	}
	
	public abstract Collection<PropertyStore> getCopiedComponents();
		
	
	@Override
	public com.golemgame.tool.action.Action.Type getType() {
		return Type.COPY_COMPONENT;
	}

	


	
}
