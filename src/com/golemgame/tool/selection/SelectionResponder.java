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
package com.golemgame.tool.selection;

import java.util.Collection;

import com.golemgame.tool.action.Actionable;

/**
 * Tools will provide an implementation of this.
 * @author Sam
 *
 */
public interface SelectionResponder {
	public void remove();
	
	/**
	 * This can be called at any time; the selection need not have been changed.
	 * 
	 * @param currentSelection
	 * @param removedFromSelection guaranteed to hold any elements that have been removed from the selection since the last time this was called.
	 */
	public void updateSelection(Collection<Actionable> currentSelection,Collection<Actionable> removedFromSelection);
	
	/**
	 * May be called even if the primary selection has not changed. 
	 * @param newPrimarySelction May be null
	 * @param oldPrimarySelection May be null
	 */
	public void updatePrimarySelection(Actionable newPrimarySelction, Actionable oldPrimarySelection);
	

}
