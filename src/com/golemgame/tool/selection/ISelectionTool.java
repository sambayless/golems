package com.golemgame.tool.selection;

import com.jme.math.Vector2f;

public interface ISelectionTool {
	/**
	 * Return true to attempt standard selection behaviour. Return false to skip selection behaviour
	 * @param button
	 * @param pressed
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean mouseButton(int button, boolean pressed, int x, int y);
	
	public void mouseMovementAction(Vector2f mousePos, boolean left, boolean right);
	
	public void  scrollMove(int wheelDelta, int x, int y);


	public SelectionResponder getResponder();
}
