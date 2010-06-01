package com.golemgame.tool;

import com.jme.math.Vector2f;

public interface ITool {
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
	
	public void deselect();
	
	public void showPrimaryEffect(boolean show);
//	public void createNewUndoPoint();
	
	public void focus();
	public void copy();
	public void delete();
	public void properties();
	public void xyPlane(boolean value);
	public void yzPlane(boolean value);
	public void xzPlane(boolean value);
}
