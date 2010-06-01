package com.golemgame.tool.action;

import com.jme.math.Vector3f;

public abstract class CenterAction extends Action<CenterAction> {

	protected Vector3f center = new Vector3f();
	protected Vector3f oldTranslation = new Vector3f();
	
	@Override
	public String getDescription() {
		return "Center";
	}

	@Override
	public Type getType() {
		return Type.CENTER;
	}

	public abstract void setCenter(Vector3f centerOn);

	
}