package com.golemgame.functional.component;


public class BDistanceSensor extends BSource 
{
	private float amount = 0;
	public BDistanceSensor() //add threshold requirements, etc
	{
		this.setUpdatable(false);
	}

	@Override
	public void updateSource(float time)
	{

	}
	
	@Override
	public float generateSignal(float time) {
		float signal = amount;
		amount = 0;
		state =0;	
		return signal;
	}
	
	public void detect(float distance)
	{
		amount = distance;
	}
}
