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
