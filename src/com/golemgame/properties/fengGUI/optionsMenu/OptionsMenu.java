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
