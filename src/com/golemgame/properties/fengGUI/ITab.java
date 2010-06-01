package com.golemgame.properties.fengGUI;

import org.fenggui.Container;

public interface ITab {
	
	public String getTitle();
	
	public Container getTab();
	
	public void close(boolean cancel);

	public void open();

	/**
	 * Attempt to embed a tab within this tab.
	 * @param tab
	 * @return True if the embedding succeeded, false otherwise.
	 */
	public boolean embed(ITab tab);
}
