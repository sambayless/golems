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




/**
 * Collection of tools that can be reused
 * @author Sam
 *
 */

public class ToolPool 
{
	
	
	

	private  com.golemgame.tool.CameraTool cameraTool = null;
	private  MovementTool2 movementTool = null;
	private  RotationTool2 rotationTool = null;
	private  BoxSelectionTool boxSelectionTool = null;
	public  IActionTool getMovementTool()
	{

		return movementTool;
	}
	
	public  IActionTool getRotationTool()
	{
		if(rotationTool == null)
			rotationTool = new RotationTool2(); //? why neccesary?
		return rotationTool;
	}
	
	public  CameraTool getCameraTool()
	{

		return cameraTool;
	}

	
	public ToolPool() {
		 movementTool = new MovementTool2();
		 rotationTool = new RotationTool2();
		 boxSelectionTool = new BoxSelectionTool();
		 cameraTool = boxSelectionTool;//new com.golemgame.tool.CameraTool();
	}

	public IActionTool getDefaultTool()
	{
		return movementTool;
	}



	
}
