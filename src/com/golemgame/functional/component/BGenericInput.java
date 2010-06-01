package com.golemgame.functional.component;

public abstract class BGenericInput extends BComponent {

	@Override
	public float generateSignal(float time) {
		if(state>1f)
			state =1f;
		else if(state<-1f)
			state = -1f;
		float signal = state;
		state = 0f;
		return signalRecieved(signal,time);
	}
	
	protected abstract float signalRecieved(float signal, float time);

}
