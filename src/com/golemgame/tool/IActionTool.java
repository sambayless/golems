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
package com.golemgame.tool;

import java.util.Collection;

import com.golemgame.model.Model;
import com.golemgame.tool.action.Actionable;





public interface IActionTool extends ITool {

	public void select(Actionable toSelect, Model selectedModel, boolean primarySelection) throws FailedToSelectException;

	public Collection<Actionable> getSelectedActionables();

	/**
	 * Is anything currently selected.
	 * @return
	 */
	public boolean isSelected();
	
	public Actionable getPrimarySelection();

	public static class FailedToSelectException extends Exception
	{

		public FailedToSelectException() {
			super();
		
		}

		public FailedToSelectException(String message, Throwable cause) {
			super(message, cause);
			
		}

		public FailedToSelectException(String message) {
			super(message);
			
		}

		public FailedToSelectException(Throwable cause) {
			super(cause);
			
		}

		private static final long serialVersionUID = 1L;
		
	}

	public void deselect(Actionable actionable);
}
