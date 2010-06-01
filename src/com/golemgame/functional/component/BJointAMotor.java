package com.golemgame.functional.component;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.MotorPropertiesInterpreter;
import com.jme.math.FastMath;

public class BJointAMotor extends BMotor {
	private static final long serialVersionUID = 1;

	private MotorProperties motorProperties;
	private MotorCallback motorCallback;
	private PIDController pidController = new PIDController();
	private final BMotorFeedback motorFeedback = new BMotorFeedback();
	
	public BMotorFeedback getMotorFeedback() {
		return motorFeedback;
	}
	public MotorCallback getMotorCallback() {
		return motorCallback;
	}
	public void setMotorCallback(MotorCallback motorCallback) {
		if(motorCallback == null)
			this.motorCallback = dummyCallback;
		else
			this.motorCallback = motorCallback;
	}
	public MotorProperties getMotorProperties() {
		return motorProperties;
	}
	public void setMotorProperties(MotorProperties motorProperties) {
		if(motorProperties == null)
			this.motorProperties = dummyProperties;
		else
			this.motorProperties = motorProperties;
	}
	
	public void apply(float time) {
		
		switch (motorProperties.getInputType())
		{
			case POSITION:
				//set the acceleration to max, the desired velocity as needed to get closer to the desired position
				pidController.setKP(motorProperties.getPID_KP());
				pidController.setKI(motorProperties.getPID_KI());
				pidController.setKD(motorProperties.getPID_KD());
				
	
				float desiredPosition = this.motorProperties.getMaxPosition() - this.motorProperties.getMinPosition();
				desiredPosition*= (state + 1)/2f;
				desiredPosition += this.motorProperties.getMinPosition();
				
				if(motorProperties.doesPositionWrap())
				{
					while(desiredPosition < motorProperties.getMinPosition())
						desiredPosition += (motorProperties.getMaxPosition() - motorProperties.getMinPosition());
					
					while(desiredPosition > motorProperties.getMaxPosition())
						desiredPosition -= (motorProperties.getMaxPosition() - motorProperties.getMinPosition());
					
					/*if(desiredPosition <this.motorProperties.getMinPosition())
						desiredPosition %= this.motorProperties.getMinPosition();
					else if (desiredPosition> motorProperties.getMaxPosition())
						desiredPosition %= motorProperties.getMaxPosition();*/
				}else{
					if(desiredPosition <this.motorProperties.getMinPosition())
						desiredPosition = this.motorProperties.getMinPosition();
					else if (desiredPosition> motorProperties.getMaxPosition())
						desiredPosition = motorProperties.getMaxPosition();
				}
				
				float delta = desiredPosition - this.getPosition();
				if(motorProperties.doesPositionWrap()){
				
					float delta2 = desiredPosition - (this.getPosition() + FastMath.TWO_PI);
					if(FastMath.abs(delta) >FastMath.abs(delta2))
						delta = delta2;
				}
				float signal = pidController.getSignal(delta, time);//-1<signal<1
			//	System.out.println(state + "\t" + time + "\t" +  this.motorProperties.getMinPosition() + "\t" + this.getPosition() + "\t"  + desiredPosition + "\t" + delta + "\t" + signal);
	
				motorCallback.setAvailableAcceleration(motorProperties.getMaxAcceleration());
				motorCallback.setDesiredVelocity(signal * motorProperties.getMaxVelocity());
			
			//	motorCallback.setAvailableAcceleration(signal );
			//	motorCallback.setDesiredVelocity( FastMath.sign(signal)> 0?motorProperties.getMaxVelocity():-(motorProperties.getMaxVelocity()));
	
				//this.motorProperties.get
				
				
				break;
			case ACCELERATION:
				
				motorCallback.setAvailableAcceleration(FastMath.abs(state*motorProperties.getMaxAcceleration()));
				motorCallback.setDesiredVelocity( FastMath.sign(state)> 0?motorProperties.getMaxVelocity():-(motorProperties.getMaxVelocity()));
	
				break;
			default:
				motorCallback.setAvailableAcceleration(FastMath.abs(motorProperties.getMaxAcceleration()));
				motorCallback.setDesiredVelocity(state*motorProperties.getMaxVelocity());
				break;
		}
		
	
	}
	
	
	public float getRPS()
	{
		float v = motorCallback.getVelocity();
		
		return v;
	}
	
	public float getPosition()
	{
		return motorCallback.getPosition();
	}
	
	public boolean doesPositionWrap() {
		return false;
	}
	public float getMinPosition() {
		return 0;
	}
	public float getMaxPosition() {
		return 0;
	}
	
	
	private class PIDController
	{
		
		private float i = 0;
		  
		public float getKP() {
			return kP;
		}

		public void setKP(float kp) {
			kP = kp;
		}

		public float getKI() {
			return kI;
		}

		public void setKI(float ki) {
			kI = ki;
		}

		public float getKD() {
			return kD;
		}

		public void setKD(float kd) {
			kD = kd;
		}
		private float kP = 0.1f;
		private float kI = 0.0002f;		
		private float kD = 0.001f;	
		private float prevDistance;
		
		private float getSignal(float displacement, float time)
		{
			if(time == 0)
				time = 0.01f;
			float p = kP * displacement;
			
			if (FastMath.abs( displacement) > 0.01f)
				i += kI * displacement * time;
			else
				i = 0;
			
			if (FastMath.abs(i) > 1f)
				i = FastMath.sign(i);//dont let i become greater than the maximum signal
			
			float d = kD * (displacement - prevDistance)/time;
			
			prevDistance = displacement;
			float output = (p + i + d);
			if (FastMath.abs(output) > 1f)
				output = FastMath.sign(output);
			return output;
		}
	}
	

	protected class BMotorFeedback extends BSource
	{
		private static final long serialVersionUID =1;
		private float oldVelocity = 0;
		public BMotorFeedback() {
			super();
			//this.setUpdatable(false);
		}

		
		public void updateSource(float time) {
			
		//	System.out.println("");
		}
		
		public float generateSignal(float time) {
			
			float signal;
			
			switch (motorProperties.getOutputType())
			{
				case POSITION:
						signal = (getPosition()- motorProperties.getMinPosition())/(motorProperties.getMaxPosition()- motorProperties.getMinPosition())*2f-1f;	
					
						break;
				case ACCELERATION:
				{
					float v = getRPS();
					
					signal = (v-oldVelocity)/time;
					
					oldVelocity = v;
					break;//possibly this break shouldn't be here?
				}
				default:
					signal = getRPS();
			}
			
			return signal;
				
		}
		
	}

	public static interface MotorCallback
	{
		public void setAvailableAcceleration(float amount);
		public void setDesiredVelocity(float amount);
		public float getVelocity();
		public float getPosition();
		
		public void applyForce(float force);
		public void applyTorque(float torque, float time);
		
	}

	private static final MotorProperties dummyProperties = new MotorProperties()
	{
		
	
		public void remove() {

		}
		
	

		public float getPID_KD() {
			
			return 0;
		}



		public float getPID_KI() {
		
			return 0;
		}



		public float getPID_KP() {
		
			return 0;
		}



		public void setPID_KD(float d) {
		
			
		}



		public void setPID_KI(float i) {
		
			
		}



		public void setPID_KP(float p) {
			
			
		}



		public void refresh() {
		
			
		}


		public PropertyStore getStore() {
			
			return null;
		}


		public float getMaxAcceleration() {
		
			return 0;
		}

		
		public float getMaxVelocity() {
			
			return 0;
		}



		
		public boolean doesPositionWrap() {
			
			return false;
		}

		
		public float getMaxPosition() {
		
			return 0;
		}

		
		public float getMinPosition() {
		
			return 0;
		}

		
		public MotorPropertiesInterpreter.IOType getInputType() {
		
			return null;
		}

		
		public MotorPropertiesInterpreter.IOType getOutputType() {
		
			return null;
		}

		
		public void setInputType(MotorPropertiesInterpreter.IOType inputType) {
	
		}

		
		public void setOutputType(MotorPropertiesInterpreter.IOType outputType) {
	
		}

		
		public void setMaxAcceleration(float scale) {
	
			
		}

		
		public void setMaxVelocity(float scale) {
		
			
		}

		
		public float getSpringConstant() {
		
			return 0;
		}

		
		public boolean isSpring() {
	
			return false;
		}

		
		public void setSpring(boolean isSpring) {
		
			
		}

		
		public void setSpringConstant(float spring) {
	
			
		}

		
		public float getSpringFriction() {
		
			return 0;
		}

		
		public float getMass1() {

			return 0;
		}



		public float getMass2() {
		
			return 0;
		}



		public void setMass1(float mass) {
	
			
		}



		public void setMass2(float mass) {
			
			
		}



		public void setSpringFriction(float spring) {
	
			
		}


		
	};
	
	private static final MotorCallback dummyCallback = new MotorCallback()
	{

		
		public void setAvailableAcceleration(float amount) {

			
		}

		
		public void setDesiredVelocity(float amount) {

		}

		
		public float getVelocity() {
			return 0;
		}

		
		public float getPosition() {
			
			return 0;
		}

		
		public void applyForce(float force) {
	
		}

		
		public void applyTorque(float torque, float time) {
	
		}
		
	};
}
