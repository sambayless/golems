package com.golemgame.constructor;

import java.util.concurrent.atomic.AtomicBoolean;



public class LoadingMonitor {
	private static final LoadingMonitor instance = new LoadingMonitor();
	
	//private static final long MS_TO_NS = 1000000 ;
	
	private Thread monitorThread;
	private final LoadingRunnable monitorRunnable;
	private final AtomicBoolean isStarted = new AtomicBoolean();

	private final LoadingMonitorListener dummyListener = new LoadingMonitorListener()
	{
		
		public void loadFailure() {
			System.err.println("Loading failure");
		}
	};
	
	private LoadingMonitorListener loadingListner = dummyListener;
	
	public static LoadingMonitor getInstance() {
		return instance;
	}
	
	

	public long getDelayTolerance() {
		return monitorRunnable.sleepPeriod;
	}
	
	public void setLoadingMonitorListener(LoadingMonitorListener loadingListner)
	{
		if(loadingListner == null)
			this.loadingListner = dummyListener;
		else
			this.loadingListner = loadingListner;
	}

	/**
	 * Set the maximum allowed delay in ms.
	 * @param delayTolerance
	 */
	public void setDelayTolerance(long delayTolerance) {
		//monitorRunnable.delayTolerance = delayTolerance * MS_TO_NS;
		monitorRunnable.sleepPeriod = delayTolerance;
	}

	private LoadingMonitor() {
		super();
		monitorRunnable = new LoadingRunnable();
		isStarted.set(false);
	}
	
	public void beginMonitoring()
	{
		if(isStarted.compareAndSet(false, true))
		{
			monitorThread = new Thread(monitorRunnable);
			monitorThread.setDaemon(true);
			monitorThread.setName("Loading monitor");
			monitorThread.start();
		}
	}

	public void endMonitoring()
	{
		monitorRunnable.finish();
	}
	
	public void update()
	{
		monitorRunnable.update();
	}
	
	public void loadingFailure()
	{
		loadingListner.loadFailure();
	}
	
	private class LoadingRunnable implements Runnable
	{
		private boolean keepAlive = true;
	//	private long lastUpdate = 0;
		
		private volatile boolean updated = false;
		
		/**
		 * The maximum allowed delay between updates before killing the loading process (in ms).
		 */
		//private long delayTolerance;
		
		/**
		 * Time to sleep between tests, in ms.
		 */
		private long sleepPeriod = 20000L;
		
		
		public void run() {
			
			while(keepAlive)
			{
				long startTime = System.currentTimeMillis();
				long remainingTime = sleepPeriod;
				
				while(remainingTime > 0 && keepAlive)
				{
					try{
						Thread.sleep(remainingTime);
						long ellapsedTime = System.currentTimeMillis() - startTime;
						remainingTime = sleepPeriod - ellapsedTime;
					}catch(InterruptedException  e)
					{
						
					}
				}
				Thread.yield();
		
				if(!keepAlive)
					break;
				
			//	long timeDifference = System.currentTimeMillis() - startTime;
			//	System.out.println(timeDifference);
				if ((!updated) && keepAlive)
				{
					loadingFailure();
					break;
				}
				
				updated = false;
				
				
			}
			
			
		}
		
		public void finish()
		{
			keepAlive = false;
		}
		
		public synchronized void update()
		{
			updated = true;
			//lastUpdate = Math.max(lastUpdate, System.nanoTime());
		}
		
	}
	
	

	public static interface LoadingMonitorListener
	{
		public void loadFailure();
	}
	
	
}

