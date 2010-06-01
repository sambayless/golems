package com.golemgame.functional.component;



public class BThreshold extends BComponent {
	static final long serialVersionUID =1;
	private float threshold;
	BThreshold(float threshold)
	{
		this.threshold = threshold;
		
	}

	@Override
	public float generateSignal(float time) {
		float amount;
		if (state>=threshold)
			amount = 1;
		else
			amount = 0;
		
			
		state =0;	
		return amount;
	}
}
