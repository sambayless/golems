package com.golemgame.properties.fengGUI;

import org.fenggui.Display;



public interface IFengGUIDisplayable {
	/**
	 * Display this component in the FengGUI display
	 * @param display
	 */
	public void display(Display display);
	public void display(Display display,int priority);
	public String getTitle();
	public String getDescription();
	public void setEnabled(boolean enabled);
	
	/**
	 * Add a listener to be notified the next time that this displayable is closed.
	 * The listener will be removed from the displayable after being called.
	 * @param listener
	 */
	public void addCloseListener(CloseListener listener);
	
	public void removeCloseListener(CloseListener listener);
	public Object setOwner(Object owner);
	public Object getOwner();
	/**
	 * Remove this component from the FengGUI display.
	 * Set the owner to null.
	 */
	public void close();
}
