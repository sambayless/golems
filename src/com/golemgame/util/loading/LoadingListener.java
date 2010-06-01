package com.golemgame.util.loading;

public interface LoadingListener {
	/**
	 * Sets whether the status of a loadable is 'working' - that is, if it has no known end time.
	 * @param working
	 */
	public void setLoadableStatus(Loadable<?> loadable, boolean isWorking);

	
	public void setLoadableProgress(Loadable<?> loadable, float progress);

}
