package com.golemgame.util.loading;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;

import com.golemgame.states.StateManager;


/**
 * This is a special loadable that contains an ordered list of loadables.
 * If any loadable throws an unhandled exception, then no subsequent loadables in the list will be called.
 * @author Sam
 *
 */
public class LoadableSequence extends Loadable<Object> implements LoadingListener {

	public Queue<Loadable<?>> loadables = new LinkedList<Loadable<?>>();
	
	private int itemIndex = 0;
	private int totalItems = 1;
	
	public LoadableSequence(String name) {
		super(name);
		super.setWorking(false);
	}
	
	
	public LoadableSequence(String name, boolean working) {
		super(name,working);

	}
	
	public LoadableSequence() {
		super();
		super.setWorking(false);
	}
	
	public LoadableSequence(boolean working) {
		super(working);
		
	}

	public LoadableSequence(LoadingExceptionHandler handler, boolean working) {
		super(handler, working);
		
	}

	public LoadableSequence(LoadingExceptionHandler handler) {
		super(handler);
		super.setWorking(false);
	}

	
	public void load() throws Exception {
		totalItems= loadables.size();
		for (itemIndex = 0; itemIndex<totalItems;itemIndex++)
		{
			Loadable<?> loadable = loadables.poll();
			
			super.setWorking(loadable.isWorking());
			super.setProgress(((float)itemIndex)/((float)totalItems));
			StateManager.getLogger().log(Level.FINE,"Loading Sequence Item: " + loadable);
			loadable.tryLoad();
		}
		super.setProgress(1f);
	}

	public Queue<Loadable<?>> getLoadables() {
		return loadables;
	}

	public void queueLoadable(Loadable<?> loadable)
	{
		loadables.add(loadable);
		loadable.setLoadingListener(this);
	}

	
	public void setLoadableProgress(Loadable<?> loadable, float progress) {
		float totalProgress = ((float)itemIndex)/((float)totalItems);
		totalProgress += progress/(float)totalItems;
		super.setProgress(totalProgress);
	}

	
	public void setLoadableStatus(Loadable<?> loadable, boolean isWorking) {
		super.setWorking(isWorking);
		
	}
	
	
}
