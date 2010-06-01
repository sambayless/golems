package com.golemgame.functional.component;


/**
 * The switch is an intermediate component. It contains a secondary component - the switch input.
 * When the signal to the switch input is greater than threshold, then the switch is 'on'.
 * @author Sam
 *
 */
public class BSwitch extends BComponent{
	private static final long serialVersionUID =1;
	private final BAuxInput switchInput = new BAuxInput();
	private boolean on = false;
	private float threshold = 0;
	@Override
	public float generateSignal(float time) 
	{		
		float signal = 0;
		if (on)
			signal= state;
		state = 0;
		
		return signal;
	}
	
	protected class BAuxInput extends BComponent
	{
		private static final long serialVersionUID =1;
		@Override
		public float generateSignal(float time) 
		{
			on = state>threshold;
			state = 0;
			return 0;
		}
		
	}

	public BAuxInput getSwitchInput() {
		return switchInput;
	}
}
