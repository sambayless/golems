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
package com.golemgame.structural.structures.ghost;

import java.util.Collection;

import com.golemgame.model.Model;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GhostInterpreter;
import com.golemgame.properties.Property;
import com.golemgame.structural.structures.Structure;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.action.information.SelectionInformation;
import com.golemgame.views.ViewManager;
import com.golemgame.views.Viewable;
import com.golemgame.views.Viewable.ViewMode;

/**
 * A ghost material is a non-phyiscal, non-collising material that is selectable by the user.
 * It can be used, for example, to choose a volume of space.
 * @author Sam
 *
 */
public abstract class GhostStructure extends Structure{
	private static final long serialVersionUID = 1L;
	
	private GhostInterpreter interpreter;


	public GhostInterpreter getInterpreter() {
		return interpreter;
	}

	public GhostStructure(PropertyStore store) {
		super(store);
		this.interpreter = new GhostInterpreter(store);
	}

	/**
	 * Return true if the given actionable is 'part' of this structure, for selection purposes.
	 * (Mainly, true if its one of the control points.)
	 * @param actionable
	 * @return
	 */
	public boolean isMember(Actionable actionable)
	{
		if(this.equals(actionable))
			return true;
		
		return false;
	}
	
	private ViewManager viewManager = new ViewManager();
	//private Collection<ViewMode> views = new HashSet<ViewMode>();
	
	public boolean addViewMode(ViewMode viewMode) {
		if(viewManager.addViewMode(viewMode)){
			refreshView();
			return true;
		}
		 return false;
	}

	public Collection<ViewMode> getViews() {
		return viewManager.getViews();
	}

	public boolean clearViews() {
		if(viewManager.clearViews()){
			refreshView();
			return true;
		}
		return false;
	}
	
	public void refreshView() {
		Collection<ViewMode> views = viewManager.getViews();
		 if (views.contains(Viewable.GHOST))
			{
			//	super.getAppearance().addEffect(ModelEffectPool.getInstance().getGhostEffect(), true);
			 	this.getAppearance().addEffect(	this.getStructuralAppearanceEffect(),false);
				setSelectable(true);
				this.getModel().setVisible(true);
			}else
			{
				setSelectable(false);
				this.getModel().setVisible(false);
			}
		 
	}
	
	
	
	@Override
	public void setSelectable(boolean selectable) {
		this.getModel().setCollidable(selectable);//its ok to do this here, because 
		super.setSelectable(selectable);
	}

	public boolean addViewModes(Collection<Viewable.ViewMode> viewModes) {
		if(viewManager.addViewModes(viewModes))
		{
			refreshView();
			return true;
		}
		return false;
	}
	
	public boolean removeViewMode(ViewMode viewMode) {
		if(viewManager.removeViewMode(viewMode)){
			refreshView();
			return true;
		}
		return false;
	}
	
	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		super.refresh();
		
		
	}

	public void refreshController() {

	}

	
	@Override
	public boolean isDeleted() {
		
		return this.getModel().isDeleted() || super.isDeleted();
	}


	
	protected Model[] getControlledModels() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Model getModel() {
		// TODO Auto-generated method stub
		return null;
	}



	


	@Override
	public Collection<Property> getPropertySet() {
		 Collection<Property> properties = super.getPropertySet();
		 
		
		 Property appearance = new Property(Property.PropertyType.APPEARANCE,this.interpreter.getAppearanceStore());		 
		 properties.add(appearance);
		 return properties;
	}

	@Override
	public Action<?> getAction(Type type) throws ActionTypeException {
		
		if (type == Action.SELECTINFO)
			return new SelectionInfo();
		
		return super.getAction(type);
	}


	protected class SelectionInfo extends SelectionInformation
	{

		@Override
		public Actionable getControlled() {
			return GhostStructure.this;
		}
		@Override
		public boolean isMultipleSelectable() {
			return false;
		}
		public boolean isSelectable() {
			return GhostStructure.this.isSelectable();
		}
		
	}
	
}
