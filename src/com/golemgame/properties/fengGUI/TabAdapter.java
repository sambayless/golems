package com.golemgame.properties.fengGUI;

import java.util.ArrayList;
import java.util.Collection;

import org.fenggui.Container;
import org.fenggui.FengGUI;

public class TabAdapter implements ITab {
	
	private String title;
	private Container tab;
	private Collection<ITab> embeddedTabs = new ArrayList<ITab>();
	
	public TabAdapter(String title) {
		super();
		this.title = title;
		tab = FengGUI.createContainer();
		buildGUI();
	}
	
	

	public boolean embed(ITab tab) {
		return false;
	}	

	public Collection<ITab> getEmbeddedTabs() {
		return embeddedTabs;
	}

	protected void buildGUI()
	{
		
	};
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	
	public void close(boolean cancel) {
		for(ITab tab:embeddedTabs)
			tab.close(cancel);
	}

	
	public Container getTab() {
		return tab;
	}

	
	public String getTitle() {

		return title;
	}

	
	public void open() {
		for(ITab tab:embeddedTabs)
			tab.open();
		
	}
}
