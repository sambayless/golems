package com.golemgame.util.loading;

import java.util.concurrent.Callable;
import java.util.logging.Level;

import com.golemgame.states.StateManager;



public abstract class Loadable<E> implements Callable<E>,ProgressObserver{

	private volatile boolean working = false;
	private volatile float progress = 0;
	private LoadingListener loadingListener = null;
	private LoadingExceptionHandler handler =null;
	private E returnValue = null;
	private String name;
	
	public Loadable(String name) {
		super();
		this.name = name;
		this.handler = LoadingExceptionHandlerAdapter.getInstance();
		this.working = false;
	}

	
	public Loadable() {
		this("");
	}

	public Loadable(LoadingExceptionHandler handler,
			boolean working) {
		super();
		this.handler = handler;
		this.working = working;
	}

	public Loadable(String name,boolean working) {
		super();
		this.working = working;
		this.handler = LoadingExceptionHandlerAdapter.getInstance();
	}
	
	public Loadable(boolean working) {
		this("",working);
	}

	public Loadable(LoadingExceptionHandler handler) {
		super();
		this.handler = handler;
	}

	public final void setHandler(LoadingExceptionHandler handler) {
		this.handler = handler;
	}
	public final LoadingExceptionHandler getHandler() {
		return handler;
	}

	public final void setLoadingListener(LoadingListener loader)
	{
		this.loadingListener = loader;
	}
	
	public LoadingListener getLoadingListener() {
		return loadingListener;
	}

	
	public final E call() throws Exception {
		tryLoad();
		return returnValue;
	}
	
	protected final void setReturnValue(E value)
	{
		this.returnValue = value;
	}
	
	public final void tryLoad() throws Exception
	{
		try{
			StateManager.getLogger().log(Level.FINE, "Begin  " + this.toString());

			load();
			StateManager.getLogger().log(Level.FINE, "Finish  " + this.toString());

		}catch(Exception e)
		{
			if (handler != null)
				handler.handleException(e, this);
			else
				throw e;
		}
	}
	
	/**
	 * Load the loadable, and skip this loadables assigned exception handler.
	 * @throws Exception
	 */
	public abstract void load() throws Exception;

	/**
	 * Sets whether the status of this loadable is 'working' - that is, if it has no known end time.
	 * @param working
	 */
	public final void setWorking(boolean working)
	{
		this.working = working;
		if (loadingListener != null)
			loadingListener.setLoadableStatus(this, working);
	}
	
	public final boolean isWorking()
	{
		return working;
	}
	
	public final void setProgress(float progress)
	{
		if (loadingListener != null)
			loadingListener.setLoadableProgress(this, progress);
	}
	
	public final float getProgress()
	{
		return progress;
	}


	
	public String toString() {
		return name;
	}
	
	
}
