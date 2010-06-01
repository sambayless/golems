package com.golemgame.functional.component;


public abstract class BMotor extends BComponent {

	//protected float activation=0;
	static final long serialVersionUID =1;

	protected BMotor()
	{
	
	}
	
	/**
	 * Called by space
	 *
	 */
	public abstract void apply(float time);	
	
	@Override
	public float generateSignal(float time) {
	//	activation = state;//save state for later use
		apply(time);
		state = 0;
		return 0;
		//Motors do not send signals
		//super.update(time, activeReactorSet);
		//System.out.println(activation);
	}



}
