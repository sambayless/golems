package com.golemgame.tool.action;

import com.jme.math.Vector3f;

public abstract class MoveAction extends Action<MoveAction> {
	
	protected Vector3f position = new Vector3f();
	protected Vector3f oldPosition = new Vector3f();
	
	/**
	 * Some subclasses may need this information
	 */
	protected Vector3f startingPosition = new Vector3f();
	
	public Vector3f getStartingPosition() {
		return startingPosition;
	}

	public void setStartingPosition(Vector3f startingPosition) {
		this.startingPosition = startingPosition;
	}

	@Override
	public Type getType() {
		return Action.MOVE;
	}
	
	public abstract void setPosition(Vector3f position);

	@Override
	public MoveAction copy() {
		try{
			MoveAction copy = (MoveAction) this.clone();
			copy.position = position.clone();
			copy.oldPosition = oldPosition.clone();
			return copy;
		}catch(Exception e)
		{
			return null;
		}		
	}

	@Override
	public MoveAction merge(MoveAction mergeWith) throws ActionMergeException 
	{
		
	
			MoveAction merged = this;
			merged.position = mergeWith.position;
			return merged;
		
	
	}
	@Override
	public String getDescription() {
		return "Move";
	}


}
