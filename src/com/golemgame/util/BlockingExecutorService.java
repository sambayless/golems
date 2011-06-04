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
package com.golemgame.util;

import java.util.concurrent.LinkedBlockingQueue;

public class BlockingExecutorService extends ManualExecutorService{

	public BlockingExecutorService() {
		super(new LinkedBlockingQueue<Runnable>());
		
	}

	@Override
	public void manualExecuteAll()
	{
		Runnable r = null;
		try {
			while(!isShutdown() && (r=((LinkedBlockingQueue<Runnable>)executionQueue).take())!=null)
			{
				r.run();
			}
		} catch (InterruptedException e) {
			
		}
	}
	
	@Override
	public void manualExecuteUntil(long maxTimeMS)
	{
		long time = System.nanoTime();
		long maxtime = maxTimeMS*1000000;
		Runnable r = null;
		try {
			while(!isShutdown() && (r=((LinkedBlockingQueue<Runnable>)executionQueue).take())!=null)
			{
				r.run();
				if((System.nanoTime()-time)>=maxtime)
					break;
			}
		} catch (InterruptedException e) {

		}
	}
	
	@Override
	public void manualExecute()
	{
		if(isShutdown())
			return;
		
		Runnable r;
		try {
			r = ((LinkedBlockingQueue<Runnable>)executionQueue).take();	
			if(r!=null)
			{
				r.run();
			}		
		} catch (InterruptedException e) {

		}
	}

	@Override
	public void shutdown() {
		super.shutdown();
		((LinkedBlockingQueue<Runnable>)executionQueue).add(new Runnable(){

			public void run() {
				//dummy entry to wake up blocking queues.
				
			}});
	}
	
	
}
