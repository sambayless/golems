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
package com.golemgame.states;

import java.util.EventListener;

public interface StateListener extends EventListener {

	/**
	 * This indicates that the state is 'done' and preparing itself for deactivation;
	 * Control should be returned to the state that called it here, but this state is not yet ready to be deactivated.
	 * @param event
	 */
	public void finished(StateFinishedEvent event);	
	
	/**
	 * Called when the state de-activates (which may occur some time after a request for removal is called)
	 */
	public void deactivate(StateDeactivateEvent event);
}
