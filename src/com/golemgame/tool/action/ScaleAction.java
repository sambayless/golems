package com.golemgame.tool.action;

import com.jme.math.Vector3f;

public abstract class ScaleAction extends Action<ScaleAction> {

	protected Vector3f newScale = new Vector3f();
	protected Vector3f oldScale = new Vector3f();
	
	public abstract void setScale(Vector3f scale);


	@Override
	public ScaleAction copy() {
		try{
			ScaleAction copy = (ScaleAction) this.clone();
			copy.newScale = newScale.clone();
			copy.oldScale = oldScale.clone();
			return copy;
		}catch(Exception e)
		{
			return null;
		}		
	}
	
	@Override
	public String getDescription() {
		return "Scale";
	}

	@Override
	public Type getType() {
		return Type.SCALE;
	}

}
