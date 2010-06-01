package com.golemgame.util.input;


public class FengGUILayerAdapter implements LayeredKeyInputListener,
		LayeredMouseInputListener {

	private FengJMEListener fengGUIListener;
	
	public FengGUILayerAdapter(FengJMEListener fengGUIListener) {
		super();
		this.fengGUIListener = fengGUIListener;
	}
	
	
	public boolean onKey(char character, int keyCode, boolean pressed) {
		fengGUIListener.onKey(character, keyCode, pressed);
		return fengGUIListener.wasKeyHandled();
	}

	
	public boolean onButton(int button, boolean pressed, int x, int y) {
		fengGUIListener.onButton(button, pressed, x, y);
	//	System.out.println(fengGUIListener.wasMouseHandled());
		return fengGUIListener.wasMouseHandled();
	}

	
	public boolean onMove(int xDelta, int yDelta, int newX, int newY) {
		fengGUIListener.onMove(xDelta, yDelta, newX, newY);
		return fengGUIListener.wasMouseHandled();
	}

	
	public boolean onWheel(int wheelDelta, int x, int y) {
		fengGUIListener.onWheel((int)Math.signum(wheelDelta), x, y);//only pass a single rotation to the listener
		return fengGUIListener.wasMouseHandled();
	}



}
