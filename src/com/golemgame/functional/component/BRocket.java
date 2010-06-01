package com.golemgame.functional.component;

import com.golemgame.structural.structures.RocketProperties;



public class BRocket extends BMotor {
	

	static final long serialVersionUID =1;
	
	public void setCallback(RocketCallback callback) {
		if(callback == null)
			this.callback = dummyCallback;
		else
			this.callback = callback;
	}

	public void setProperties(RocketProperties properties) {
		if(properties == null)
			this.properties = dummyProperties;
		else
			this.properties = properties;
	}

	public RocketCallback getCallback() {
		return callback;
	}

	public RocketProperties getProperties() {
		return properties;
	}


	
	
	private RocketCallback callback =dummyCallback;
	private RocketProperties properties = dummyProperties;
	
	public BRocket(RocketCallback callback,RocketProperties properties) {
		setCallback(callback);
		setProperties(properties);
	}
	
	
	public void apply(float time) 
	{
		if(this.state>1f)
			this.state = 1f;
		if(this.state<-1f)
			this.state = -1f;
		callback.applyForce(this.properties.getMaxForce() * this.state);
		this.state = 0;
	}


	public static interface RocketCallback
	{
		public void applyForce(float force);
	}
	
	private static final RocketCallback dummyCallback = new RocketCallback()
	{

		
		public void applyForce(float force) {

			
		}
		
	};
	
	private static final RocketProperties dummyProperties = new RocketProperties()
	{

		
		public float getMaxForce() {
	
			return 0;
		}

		
		public void setMaxForce(float maxAcceleration) {

			
		}
		
	};
}
