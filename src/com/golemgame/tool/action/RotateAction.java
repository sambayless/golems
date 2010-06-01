package com.golemgame.tool.action;

import com.jme.math.Quaternion;

public abstract class RotateAction extends Action<RotateAction> {

	protected Quaternion rotation = new Quaternion();
	protected Quaternion oldRotation = new Quaternion(); 
	//protected Vector3f position = new Vector3f();
	//protected Vector3f oldPosition = new Vector3f();
	
	@Override
	public Type getType() {
		return Action.ROTATE;
	}

	//public abstract void setPosition(Vector3f position);

	public abstract void setRotation(Quaternion rotation);

	@Override
	public RotateAction copy() {

		try{
			RotateAction copy = (RotateAction) this.clone();
		//	copy.position = position.clone();
			//copy.oldPosition = oldPosition.clone();
			copy.rotation = new Quaternion(rotation);
			copy.oldRotation =  new Quaternion(oldRotation);
			return copy;
		}catch(Exception e)
		{
			return null;
		}		
	}


	@Override
	public RotateAction merge(RotateAction mergeWith) throws ActionMergeException {
	
			RotateAction merged = this;
			//merged.position = mergeWith.position;
			merged.rotation = mergeWith.rotation;
			return merged;

	}

	@Override
	public String getDescription() {
		return "Rotate";
	}
	
	
}
