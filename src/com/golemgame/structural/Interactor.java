package com.golemgame.structural;

/**
 * An interface for machine elements that observe user input, such as key strokes.
 * This is intentionally separate from an InputLayer listener,
 * and may later listen to other user events as well.
 * @author Sam
 *
 */
public interface Interactor {
	public boolean onKey(char character, int keyCode, boolean pressed);
/*	public boolean onButton(int button, boolean pressed, int x, int y);
	public boolean onMove(int delta, int delta2, int newX, int newY); 
	public boolean onWheel(int wheelDelta, int x, int y);*/
}
