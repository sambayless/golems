package com.golemgame.util.input;

public abstract class LayeredMouseInputAdapter implements
		LayeredMouseInputListener {

	
	public boolean onButton(int button, boolean pressed, int x, int y) {
	
		return false;
	}

	
	public boolean onMove(int delta, int delta2, int newX, int newY) {
	
		return false;
	}

	
	public boolean onWheel(int wheelDelta, int x, int y) {
		
		return false;
	}
}
