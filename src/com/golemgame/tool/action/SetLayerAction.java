package com.golemgame.tool.action;

import com.golemgame.mvc.Reference;

public abstract class SetLayerAction extends Action<SetLayerAction> {
	private Reference layer;

	public Reference getLayer() {
		return layer;
	}

	public void setLayer(Reference layer) {
		this.layer = layer;
	}

	@Override
	public String getDescription() {
		return "Change Layer";
	}

	@Override
	public com.golemgame.tool.action.Action.Type getType() {
		return Action.SET_LAYER;
	}

}
