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
package com.golemgame.structural;

import java.io.Serializable;
import java.util.Collection;

import com.golemgame.functional.LocalWireManager;
import com.golemgame.functional.WirePort;
import com.golemgame.mechanical.StructuralMachine;
import com.golemgame.model.Model;
import com.golemgame.model.effect.Appearance;
import com.golemgame.model.effect.ModelEffect;
import com.golemgame.mvc.Reference;
import com.golemgame.mvc.SustainedView;
import com.golemgame.tool.action.Actionable;
import com.golemgame.views.Viewable;


public interface Structural extends Serializable,Viewable,SustainedView,Actionable{
	public boolean isPhysical();
	public boolean isMindful();
	//public Physical[] getPhysical();
	

	public Actionable getActionable();
	public Model getModel();
	
	public Collection<LocalWireManager> getWireManagers();
	
	/**
	 * If the permanent appearance of the structure is being changed, do so through the appearance object
	 * @return
	 */
	public Appearance getAppearance();
	
	public StructuralAppearanceEffect getStructuralAppearanceEffect();
	/**
	 * Transient effects - like selection - can be added directly here
	 * @param effect
	 */
	public void addModelEffect(ModelEffect effect);
	
	public Reference getLayerReference();
	
	//public void setMachine(StructuralMachine machine);
	public StructuralMachine getMachine();

	public Collection<WirePort> getWirePorts();
	
	public Reference getID();
}
