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
package com.golemgame.properties.fengGUI.optionsMenu;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.golemgame.properties.fengGUI.ITab;
import com.golemgame.util.loading.ConcurrentLoadable;

public class OptionsMenu implements ConcurrentLoadable {

	private boolean isLoaded = false;
	private VerticalTabbedWindow tabbedWindow;
	private OptionsMenu() {
		super();
		
		buildOptions();
	}
	
	public VerticalTabbedWindow getTabbedWindow() {
		return tabbedWindow;
	}

	private void buildOptions()
	{
		
		tabbedWindow = new VerticalTabbedWindow();
		tabbedWindow.setTitle("Game Options");
		
		
		ITab displayTab = new DisplayModeTab();
		tabbedWindow.addTab(displayTab);
		tabbedWindow.addTab(new MachineEnvironmentTab());
	}
	
	protected OptionsMenu(boolean loading)
	{
		super();
		if (!loading)
			buildOptions();
	}
	
	public static OptionsMenu createLoadableMenuState()
	{
		return new OptionsMenu(true);
	}


	
	public void load() throws Exception {
		
		loadingLock.lock();
		try {
			buildOptions();

			isLoaded = true;
			loadingCondition.signalAll();
		} finally {
			loadingLock.unlock();
		}
	}

	protected ReentrantLock loadingLock = new ReentrantLock();
	protected Condition loadingCondition = loadingLock.newCondition();
	
	public boolean isLoaded() {
		return isLoaded;
	}

	
	public void waitForLoad() {
		while(!isLoaded())
		{
			try{
				loadingCondition.await();
			}catch(InterruptedException e)
			{
			}
		}
	}
	
	
	
	
}
