package com.golemgame.util.loading;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentLoadableAdapter implements ConcurrentLoadable {

	private boolean loaded = false;
	protected ReentrantLock loadingLock = new ReentrantLock();
	protected Condition loadingCondition = loadingLock.newCondition();
	
	public boolean isLoaded() {
		return loaded;
	}

	
	public void waitForLoad() {
		while(!isLoaded())
		{
			try{
				loadingCondition.await();
			}catch(InterruptedException e)
			{
			}
		}
	}

	
	public void load() throws Exception {
		
		loadingLock.lock();
		try {

			loaded = true;
			loadingCondition.signalAll();
		} finally {
			loadingLock.unlock();
		}
	}

}
