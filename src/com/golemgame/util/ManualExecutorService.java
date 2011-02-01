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

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class ManualExecutorService extends AbstractExecutorService {

	protected final Queue<Runnable> executionQueue;
	private boolean shutdown = false;
	
	public ManualExecutorService(Queue<Runnable> executionQueue) {
		super();
		this.executionQueue = executionQueue;
	}

	public ManualExecutorService() {
		this(new ConcurrentLinkedQueue<Runnable>());
		
	}

	public boolean awaitTermination(long timeout, TimeUnit unit)
			throws InterruptedException {
		throw new UnsupportedOperationException();
		
		//return false;
	}

	public boolean isShutdown() {
		return shutdown;
	}

	public boolean isTerminated() {
		return false;
	}

	public void shutdown() {
		shutdown = true;
		executionQueue.clear();
	}

	public List<Runnable> shutdownNow() {
		shutdown = true;
		List<Runnable> list = new ArrayList<Runnable>(executionQueue);
		executionQueue.clear();
		shutdown();
		return list;
		//return null;
	}

	public void execute(Runnable command) {
		executionQueue.add(command);
	}

	public boolean hasTask()
	{
		return !executionQueue.isEmpty();
	}
	
	public void manualExecuteAll()
	{
		Runnable r = null;
		while((r=executionQueue.poll())!=null)
		{
			r.run();
		}
	}
	
	/**
	 * Manually execute all items until there are none left, or the specified amount of time has elapsed.
	 * @param maxTimeMS
	 */
	public void manualExecuteUntil(long maxTimeMS)
	{
		long time = System.nanoTime();
		long maxtime = maxTimeMS*1000000;
		Runnable r = null;
		while((r=executionQueue.poll())!=null)
		{
			r.run();
			if((System.nanoTime()-time)>=maxtime)
				break;
		}
	}
	
	
	public void manualExecute()
	{
		Runnable r = executionQueue.poll();
		if(r!=null)
		{
			r.run();
		}
	}
	
}
