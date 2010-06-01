package com.golemgame.functional.component;



public class BSpring extends BJointAMotor{

	private float springContant;
	
	private final boolean torqueSpring;
		
	public BSpring(float springContant, boolean torqueSpring) {
		super();
		this.springContant = springContant;
		this.torqueSpring = torqueSpring;
	}

	@Override
	public void apply(float time) {
		//ignore normal motor procedures, including any connected sources
		
	
		
		
		float position = super.getMotorCallback().getPosition();
		
		float deltaX = (position-getMotorProperties().getMinPosition())-(getMotorProperties().getMaxPosition()-getMotorProperties().getMinPosition())/2f ;
		//distance from the middle of min and max
		//System.out.println(deltaX + "\t" + position + "\t" + getMotorProperties().getMaxPosition() + "\t" + getMotorProperties().getMinPosition() );
	//	super.getMotorCallback().setDesiredVelocity(0);
	//	super.getMotorCallback().setAvailableAcceleration(-1f);
		
		if (!torqueSpring)
		{
			float force = -deltaX*springContant;
	
			super.getMotorCallback().applyForce(force);
			
			float friction = -super.getMotorCallback().getVelocity() * getMotorProperties().getSpringFriction();
			
			super.getMotorCallback().applyForce(friction);
		}
		else
		{
			float mod_springContant = springContant;
			
		//	if (mod_springContant/(getMotorProperties().getMass()*getMotorProperties().getMass())>100000f)
		//		mod_springContant= (getMotorProperties().getMass()*getMotorProperties().getMass())*100000f;
			
			//if velocity is in the direction of the torque, cut it in half
			float springTorque = -deltaX*mod_springContant;
			
			
			
		//	if(mod_springContant/ getMotorProperties().getMass() > 1000f)
			//	mod_springContant = getMotorProperties().getMass()*1000f;
			
			super.getMotorCallback().applyTorque(springTorque, time);
			float smallerMass = Math.min(getMotorProperties().getMass1(), getMotorProperties().getMass2());
		//	float friction = -super.getMotorCallback().getVelocity() * getMotorProperties().getSpringFriction();
			float mod_frictContant =  getMotorProperties().getSpringFriction();
			if (mod_frictContant/smallerMass>100f)
				mod_frictContant= smallerMass*100f;
			
		//	super.getMotorCallback().applyTorque(friction);
			float friction = -super.getMotorCallback().getVelocity() *mod_frictContant*(smallerMass);
		//	
			super.getMotorCallback().applyTorque(friction,time);
		}
		
		super.apply(time);
	}


}
