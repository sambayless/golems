package com.golemgame.tool;

import com.golemgame.model.Model;
import com.golemgame.tool.action.Actionable;

public interface SelectionListener {
	/**
	 * Return true to prevent normal selection behaviour.
	 * @param actionable
	 * @param model
	 * @return
	 */
	public boolean select(Actionable actionable, Model model);
}
