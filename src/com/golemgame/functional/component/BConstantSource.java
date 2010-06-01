package com.golemgame.functional.component;

public class BConstantSource extends BSource {

	
	private float value;
	
	
	
	public BConstantSource(float value) {
		super();
		this.value = value;
	}



	public BConstantSource() {
		super();
		value = 0;
	}



	@Override
	public void updateSource(float time) {
		this.state = value;

	}



	public float getValue() {
		return value;
	}



	public void setValue(float value) {
		this.value = value;
	}

}
