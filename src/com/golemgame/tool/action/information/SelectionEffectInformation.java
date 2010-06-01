package com.golemgame.tool.action.information;

import com.golemgame.tool.action.Action;

public abstract class SelectionEffectInformation extends ActionInformation {
	@Override
	public Type getType() {
		return Action.SELECT_EFFECT_INFO;
	}

	public abstract boolean usesStandardEffects();
}
