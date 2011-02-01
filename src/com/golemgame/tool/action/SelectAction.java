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
package com.golemgame.tool.action;

import java.util.Set;

import com.golemgame.model.Model;


public abstract class SelectAction extends Action<SelectAction> {

	private Set<Model> ignoreCollisions = null;
	public Set<Model> getIgnoreCollisions() {
		return ignoreCollisions;
	}


	public void setIgnoreCollisions(Set<Model> ignoreCollisions) {
		this.ignoreCollisions = ignoreCollisions;
	}

	protected boolean select = true;
	public void setSelect(boolean select)
	{
		this.select = select;
	}
	

	@Override
	public Type getType() 
	{
		
		return Action.SELECT;
	}
	
	public SelectAction() {
		
	}

	public boolean isSelect() {
		return select;
	}

	@Override
	public SelectAction copy() {
		try{
			SelectAction copy = (SelectAction) this.clone();
			copy.select = select;
			copy.ignoreCollisions = this.ignoreCollisions;
			return copy;
		}catch(Exception e)
		{
			return null;
		}		
	}
	

	@Override
	public SelectAction merge(SelectAction mergeWith) throws ActionMergeException 
	{
		
	
			
			mergeWith.ignoreCollisions.retainAll(ignoreCollisions);
			return mergeWith;

	}
	
	@Override
	public String getDescription() {
		return "Select";
	}
}
