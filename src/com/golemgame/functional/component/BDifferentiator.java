package com.golemgame.functional.component;



public class BDifferentiator extends BComponent {
	private static final long serialVersionUID =1;
	private float previousState = 0;
	private boolean antidifferentiate;
	private final BAuxInput switchInput = new BAuxInput();
	private float threshold = 0;
	private boolean open = false;
	private boolean thresholdInverted;
	
	public BDifferentiator(boolean antidifferentiate) {
		super();
		this.antidifferentiate = antidifferentiate;
	}


	public BDifferentiator() {
		this(false);
	}


	@Override
	public float generateSignal(float time) 
	{
		if(!open)
		{
			if (!antidifferentiate)
			{
				float signal =((state - previousState)/time);
				previousState = state;
				state = 0;
				return signal;
			}else
			{
				float signal =((state - previousState)*time);
				previousState = state;
				state = 0;
				return signal;
			}
		}
		state = 0;
		return 0;
	}
	
	protected class BAuxInput extends BComponent
	{
		private static final long serialVersionUID =1;
		@Override
		public float generateSignal(float time) 
		{

				if(state>threshold)
					setOpen(!thresholdInverted);
				else
					setOpen(thresholdInverted);
			
			return 0;
		}
		
	}
	public float getThreshold() {
		return threshold;
	}
	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}
	
	public BAuxInput getSwitchInput() {
		return switchInput;
	}
	
	/**
	 * Set whether the function applier is 'closed' (allows signals through) 
	 * or 'open' (outputs only 0).
	 * @param b
	 */
	public void setOpen(boolean open) {
		this.open = open;
	}


	public boolean isThresholdInverted() {
		return thresholdInverted;
	}


	public void setThresholdInverted(boolean thresholdInverted) {
		this.thresholdInverted = thresholdInverted;
	}

}
