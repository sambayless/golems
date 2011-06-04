/*******************************************************************************
 * Copyright 2008, 2009, 2010 Sam Bayless.
 * 
 *     This file is part of Golems.
 * 
 *     Golems is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     Golems is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with Golems. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
