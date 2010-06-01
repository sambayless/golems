package com.golemgame.mechanical;

import java.util.concurrent.CopyOnWriteArrayList;

import com.golemgame.structural.Interactor;
import com.golemgame.util.input.LayeredKeyInputListener;
import com.golemgame.util.input.LayeredMouseInputListener;


public class InteractionServer implements LayeredKeyInputListener,LayeredMouseInputListener{
	private CopyOnWriteArrayList<Interactor> listeners = new CopyOnWriteArrayList<Interactor> ();
	
	public void addInteractionListener(Interactor listener)
	{
		listeners.add(listener);
	}

	
	public boolean onKey(char character, int keyCode, boolean pressed) {
		for(Interactor listener:listeners)
		{
			listener.onKey(character, keyCode, pressed);
		}
		return true;
	}

	
	public boolean onButton(int button, boolean pressed, int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean onMove(int delta, int delta2, int newX, int newY) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean onWheel(int wheelDelta, int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}
	

}
