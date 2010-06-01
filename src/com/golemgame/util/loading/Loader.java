package com.golemgame.util.loading;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

import com.golemgame.states.ProgressBarState;
import com.golemgame.states.StateManager;
import com.golemgame.util.pass.PassingGameStateManager;


/**
 * This class presents a unified system for loading/unloading objects, and for displaying information 
 * about this loading to the user.  
 * 
 * The loader might provide safegaurds for frozen loading items.
 * @author Sam
 *
 */
public class Loader implements LoadingListener{
	public static final Loader instance = new Loader();
	

	private final static int MS_TO_NS = 1000000;
	
	private BlockingQueue<FutureLoadable>  loadingQueue = new LinkedBlockingQueue<FutureLoadable> ();
	
	private OrderedExecutor executor;
	
	private LoadingExceptionHandler defaultHandler = LoadingExceptionHandlerAdapter.getInstance();

	//loader has its own progress bar instance.
	
	private ProgressBarState progressBar;
	
	private long delayTime = 0;
	
	private boolean showProgressBar = true;
	
	public long getDelayTime() {
		return delayTime;
	}

	/**
	 * Cancel all pending tasks.
	 */
	public void cancelAll()
	{
		try{
			
			loadingQueue.clear();
			
		}finally
		{
			
		}
	}
	
	/**
	 * Set the time to delay before displaying the progress bar (in ms)
	 * @param delayTime
	 */
	public void setDelayTime(long delayTime) {
		this.delayTime = delayTime;

	}

	public boolean isProgressBarVisible() {
		return showProgressBar;
	}

	public void setProgressBarVisible(boolean showProgressBar) {
		this.showProgressBar = showProgressBar;
	}

	public LoadingExceptionHandler getDefaultHandler() {
		return defaultHandler;
	}

	public void setDefaultHandler(LoadingExceptionHandler defaultHandler) {
		this.defaultHandler = defaultHandler;
	}

	public static Loader getInstance() {
		return instance;
	}

	private Loader() {
		super();

		progressBar = new ProgressBarState();
		PassingGameStateManager.getInstance().attachChild(progressBar);
		progressBar.setActive(false);
		progressBar.setWorking(false);
		progressBar.setProgress(0);


		executor = new OrderedExecutor();
		Thread executorThread = new Thread(executor,"Loading Thread");
		executorThread.setDaemon(true);
		executorThread.start();
		
	}
	
	
	public void setLoadableStatus(Loadable<?> loadable, boolean isWorking){
		progressBar.setWorking(isWorking);
	}
	
		
	public void setLoadableProgress(Loadable<?> loadable, float progress){
		progressBar.setProgress(progress);
	}
	
	/**
	 * Queue a loadable to be loaded when all previously queued items have loaded.
	 * Loadable.load() will be called, asynchrounously, at some point in the future. 
	 * It is guaranteed that items will be loaded in the order they are queued.
	 * @param loadable
	 */
	public synchronized Future<?> queueLoadable(Loadable<?> loadable)
	{
		FutureLoadable future = new FutureLoadable(loadable);

		loadingQueue.add(future);	
		
		loadable.setLoadingListener(this);
		return future;
	}
	
	/**
	 * Load the given loadable assyncrounously, at some point in the future.
	 * It is not part of the queue loading sequence; it may load in any order.
	 * A progress bar is not displayed.
	 * @param loadable
	 */
	public void loadAsynch(Loadable<?> loadable)
	{
			StateManager.getThreadPool().submit(loadable);
	}
	
	/**
	 * Load the given loadable assyncrounously, at some point in the future.
	 * It is not part of the queue loading sequence; it may load in any order.
	 * A progress bar is not displayed.
	 * @param loadable
	 */
	public void loadAsynch(final ConcurrentLoadable concurrent)
	{
		loadAsynch(new Loadable<Object>(true)
		{

			
			public void load() throws Exception {
				StateManager.getLogger().log(Level.FINE, "Begin Async Loading of concurrent component " + concurrent.toString());
				if (!concurrent.isLoaded())
					concurrent.waitForLoad();	
			}
			
		});
	}
	
	
	/**
	 * Convert a concurrent loadable into a sequential one; queue a sequential load that 
	 * simply waits until the given loadable has loaded.
	 * @param concurrent
	 */
	public void queueWaitForLoad(final ConcurrentLoadable concurrent)
	{
		queueLoadable(new Loadable<Object>(true)
		{

			
			public void load() throws Exception {
				if (!concurrent.isLoaded())
					concurrent.waitForLoad();	
			}
			
		});
	}
	
	private static class FutureLoadable extends FutureTask<Object>
	{

		private final Loadable<?> loadable;

		@SuppressWarnings("unchecked")
		public FutureLoadable(Loadable loadable) {
			super(loadable);
			this.loadable = loadable;
		}

		public Loadable<?> getLoadable() {
			return loadable;
		}
		
	}
	
	private class OrderedExecutor implements Runnable
	{

		
		public void run() {
			
			while(true)
			{
				long lastShowTime=0;
				FutureLoadable futureLoadable=null;
				Loadable<?> toLoad = null;
				while(toLoad == null) {
					 try {
						 futureLoadable= loadingQueue.take();
						 if (futureLoadable != null)
							 toLoad = futureLoadable.getLoadable();
					  } catch (InterruptedException ex) {
					    	 ex.printStackTrace();
					  }
				}
				long ellapsedTime = System.nanoTime() - lastShowTime;
				//if the ellapsed time is greater than the delay time, then start the delay time from 0 again.
				//otherwise, the progress bar should just continue to display.
				
				//display the progress bar
				if (ellapsedTime>=getDelayTime()*MS_TO_NS)
					progressBar.setDelayedAppearance(((float)getDelayTime())/1000f);//this doesnt quite have the intended effect - it should only be delayed for the first out of a group of loaders... works okay for a sequence.
				
				progressBar.setActive(isProgressBarVisible());
				progressBar.setWorking(toLoad.isWorking());
				progressBar.setProgress(0);
				try{
						futureLoadable.run();
						futureLoadable.get();//if there was an exception, this will throw it, even though we dont use the return values here.
				}catch(Exception e)
				{
					try{
					defaultHandler.handleException(e, toLoad);
					}catch(Exception ex)
					{
						ex.printStackTrace();
					}
					//futureLoadable.cancel(true);//this might be incorrect...
				}catch(Error e)
				{
					try{
						defaultHandler.handleException(new Exception(e), toLoad);
						}catch(Exception ex)
						{
							ex.printStackTrace();
						}
					throw e;
				}

				progressBar.setActive(false);
				lastShowTime = System.nanoTime();
			}
			
			
		}
		
	}
}
