package com.golemgame.properties.fengGUI;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * A tab that contains other tabs, each laid out as a set of tabs.
 * @author Sam
 *
 */
public abstract class TabbedTab extends ImageTabAdapter {

	public TabbedTab(String title) {
		super(title);
		
	}
	private final ArrayList<ITab> tabs = new ArrayList<ITab>();
	
	public ArrayList<ITab> getTabs() {
		return tabs;
	}
	/**
	 * Keep a list of all tabs that have been opened since this tab was most recently opened.
	 * Only those tabs will get close signals (As others may not have been initiated). 
	 * Only tabs that have not been opened since the most recent window display will be given open signals.
	 */
	private Set<ITab> openedTabs = new HashSet<ITab>();
	private ITab currentTab = dummyTab;
	public ITab getCurrentTab() {
		return currentTab;
	}

	
	private static final ITab dummyTab = new TabAdapter("Dummy")
	{
	};
	
	public void addTab(ITab tab) {
		tabs.add(tab);
		insertTabButton(tab);
		if (tabs.size() == 1)
		{
			displayTab(tab);
		}
	}
	
	protected abstract void insertTabButton(ITab tab);

	public void displayTab(ITab tab)
	{
		if(tab==null)
			this.currentTab = dummyTab;
		else
		{
			this.currentTab = tab;
			if (openedTabs.add(tab))
			{
				this.currentTab.open();
			}
		}
		

	}
	
	@Override
	public void close(boolean cancel) {
		for(ITab tab:openedTabs)
		{
			tab.close(cancel);
		}
		openedTabs.clear();
		
	}

	@Override
	public void open() {
		this.openedTabs.clear();
		super.open();
		displayTab(currentTab);
	}
	
}
