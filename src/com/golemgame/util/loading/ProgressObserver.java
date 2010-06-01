package com.golemgame.util.loading;

/**
 * An interface for an observer interested in the progress of some work.
 * @author Sam
 *
 */
public interface ProgressObserver {
	
	public void setProgress(float progress);
	
	public void setWorking(boolean working);
	
	public boolean isWorking();
	
	public float getProgress();
}
