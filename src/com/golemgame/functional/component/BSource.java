package com.golemgame.functional.component;

public abstract class BSource extends BComponent {
	static final long serialVersionUID =1;
	private boolean isUpdatable = false;
	private transient BMind mind;
	public void setMind(BMind mind) {
		this.mind = mind;
	}

	public BMind getMind() {
		return mind;
	}

	public BSource()
	{
		
	}
	
	public boolean isUpdatable()
	{
		return isUpdatable;
	}

	/**
	 * This is called at each step, before any signals are sent.
	 * @param time
	 */
	public abstract void updateSource(float time);
	
	protected void setUpdatable(boolean isUpdatable) {
		this.isUpdatable = isUpdatable;
	}

	

	
	
}
