package com.golemgame.util.input;


public interface LayeredMouseInputListener {
	
	/**
	 * Return true to indicate that no lower priority layers should received this event.
	 * @param button
	 * @param pressed
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean onButton(int button, boolean pressed, int x, int y) ;
	
	/**
	 * *Return true to indicate that no lower priority layers should received this event.
	 * @param xDelta
	 * @param yDelta
	 * @param newX
	 * @param newY
	 * @return
	 */
	public boolean onMove(int xDelta, int yDelta, int newX, int newY);

	/**
	 * Return true to indicate that no lower priority layers should received this event.
	 * @param wheelDelta
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean onWheel(int wheelDelta, int x, int y) ;

}
