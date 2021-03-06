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
package com.golemgame.states;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.golemgame.util.ManagedThread;
import com.golemgame.util.event.ListenerList;
import com.golemgame.util.loading.Loader;
import com.jmex.game.state.GameState;

/**
 * This game state serves only to ensure that the process gracefully exits on cleanup
 * @author Sam
 *
 */
public final class ShutdownManagerState extends GameState {
	private static final ShutdownManagerState instance = new ShutdownManagerState();
	private ArrayList<ManagedThread> threadList = new ArrayList<ManagedThread>();
	
	private ListenerList<Runnable> shutdownHooks = new ListenerList<Runnable>();
	
	public static ShutdownManagerState getInstance() {
		return instance;
	}

	private ShutdownManagerState() {
		super();
		this.setActive(false);
	}

	@Override
	public void cleanup() {
		try{
			
			Loader.getInstance().cancelAll();
			if(StateManager.getRecordingManager()!=null)
				StateManager.getRecordingManager().closeSession();
			if(StateManager.getAudioManager()!=null)
				StateManager.getAudioManager().close();
			StateManager.getThreadPool().shutdownNow();
			
		
			
			for (ManagedThread thread:threadList.toArray(new ManagedThread[threadList.size()]))
				{
					thread.shutdownThread();
				}	
		
	
			for (Runnable runnable:shutdownHooks)
				runnable.run();
		
			//this should happen last
			if (!StateManager.getThreadPool().awaitTermination(3,  TimeUnit.SECONDS))
			{//if the thread pool failed to finish in 3 seconds, forcefully shut down the program
				
			}
			System.exit(0);	//show down the program regardless of what the threadpool does	
		}catch (Exception e)
		{
			StateManager.logError(e);
		}
	}

	public void addManagedThread(ManagedThread thread)
	{
		threadList.add(thread);
	}
	public void removeManagedThread(ManagedThread thread)
	{
		threadList.remove(thread);
	}
	
	@Override
	public void render(float tpf) {

	}

	@Override
	public void update(float tpf) {		

	}

	@Override
	public void setActive(boolean active) {
		super.setActive(false);//refuse to activate
	}
	
	/**
	 * Attach a call back that will be called after all managed threads have been cleaned up.
	 * @param runnable
	 */
	public void addShutdownHook(Runnable runnable)
	{
		shutdownHooks.addListener(runnable);
	}
	
	public void removeShutdownHook(Runnable toRemove)
	{
		shutdownHooks.removeListener(toRemove);
	}

}
