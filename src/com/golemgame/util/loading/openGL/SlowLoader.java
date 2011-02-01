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
