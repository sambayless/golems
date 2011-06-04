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

import com.golemgame.model.Model;
import com.golemgame.tool.action.Action.Type;


/**
 * This class allows for a set of models to be attached.
 * It also allows for one model and one actionable to be specified as the primary 
 * actionable, which will handle unsupported actions.
 * Rotation and movement actions can be performed on the whole action node.
 * @author Sam
 *
 */
public class PrimaryActionNode extends ActionNode {


	
	private Actionable primary;
	private Model primaryModel;
	

	public void setPrimaryModel(Model primaryModel)
	{
		addModel(primaryModel);
		this.primaryModel = primaryModel;
		primary = primaryModel.getActionable();
	}
	

	/**
	 * Remove each model, return it to its previous parent with preserved world vectors
	 */
	public void detachModels()
	{
		super.detachModels();
		this.primary = null;
		this.primaryModel = null;
	}
	
	@Override
	public Action<?> getAction(Type type) throws ActionTypeException {
		try{
			return super.getAction(type);
		}	catch(ActionTypeException e)
		{
			if(primary!=null)
			{
				return primary.getAction(type);
			}else
				throw new ActionTypeException("No Primary");
		}
	
	}
}
