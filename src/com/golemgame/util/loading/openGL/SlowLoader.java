package com.golemgame.util.loading.openGL;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;

import com.golemgame.states.StateManager;
import com.jme.util.GameTaskQueueManager;

/**
 * This is a class for managing the loading of slow items in the opengl queue. It delays loading items in this queue as needed, to 
 * prevent the open gl thread from freezing.
 * @author Sam
 *
 */
public class SlowLoader {
	
	private static final SlowLoader instance = new SlowLoader();
	
	public static SlowLoader getInstance() {
		return instance;
	}


	private BlockingQueue<FutureTask<?>>  loadingQueue = new LinkedBlockingQueue<FutureTask<?>> ();

	private SlowLoader() {
		super();
		Thread thread = new Thread(new OpenGLExecutor());
		
		thread.setDaemon(true);
		thread.start();
	}


	public synchronized <E> Future<E> queue(Callable<E> callable)
	{
		FutureTask<E> future = new FutureTask<E>(callable);

		loadingQueue.add(future);	

		return future;
	}
	
	
	private class OpenGLExecutor implements Runnable
	{

	
		public void run() {
			
			while(true)
			{
			
				FutureTask<?> futureLoadable=null;
		
				while(futureLoadable == null) {
					 try {
						 futureLoadable= loadingQueue.take();

					  } catch (InterruptedException ex) {
					    	 ex.printStackTrace();
					  }
				}
				
				final FutureTask<?> toCall = futureLoadable;
				try{
					GameTaskQueueManager.getManager().update(new Callable<Object>()
							{

								
								public Object call() throws Exception {
									toCall.run();
									return null;
								}
						
							}).get();//only schedule again once the current one is done.
				}catch(Exception e)
				{
					StateManager.logError(e);
					//futureLoadable.cancel(true);//this might be incorrect...
				}
				try{
					if(StateManager.getGame().getSettings().getFramerate()> 0)
						Thread.sleep(2000/StateManager.getGame().getSettings().getFramerate());//sleep for two frames
					else
						Thread.sleep(200);
				}catch(InterruptedException e)
				{
					//this is ok... the exact timing is not important..
				}
				//System.out.println("finished sleeping");
			}
			
			
		}
		
	}
}
