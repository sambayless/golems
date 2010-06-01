package com.golemgame.states;



import com.golemgame.util.ControlledListener;
import com.jme.scene.Node;

public interface Controllable extends ControlledListener {


	public Node getRootNode();
	public Node getHUD();
	/**
	 * Initiate this state for displaying. 
	 */
	public void init();
	
	/**
	 * Remove this state from displaying
	 */
	public void remove();
	
	public void addListener(StateListener listener);
	public void removeListener(StateListener listener);

}
