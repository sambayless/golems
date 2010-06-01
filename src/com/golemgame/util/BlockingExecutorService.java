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
