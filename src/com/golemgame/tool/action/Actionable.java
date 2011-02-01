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

/**
 * This defines a common interface to facilitate context sensitive controls.
 * @author Sam
 *
 */
public interface Actionable 
{

	
	/**
	 * This function will attempt to retrieve an Action corresponding to the type parameter.
	 * The action may or may not be created as a new instance.
	 * If there is no action of the requested type, an ActionType exception will be thrown.
	 * @param type The type of action to retrieve
	 * @return An action that is an instance of the class corresponding to the type parameter
	 * @throws ActionTypeException this will be thrown if there is no action of the type requested
	 */
	public Action getAction(Action.Type type) throws ActionTypeException;
	
	
	
	
}
