package com.golemgame.functional.component;

import java.io.IOException;
import java.io.ObjectInputStream;

import com.jmex.physics.TranslationalJointAxis;

public class BSlideMotor extends BMotor {
	static final long serialVersionUID =1;
	private transient TranslationalJointAxis axis1;
	private transient TranslationalJointAxis axis2;
	

	private void initTransients()
	{
		axis1=null;
		axis2 =null;
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
	     in.defaultReadObject();
	     initTransients();	
	}
	
	public BSlideMotor() 
	{
		super();
		initTransients();
	}

	public TranslationalJointAxis getAxis1() {
		return axis1;
	}

	public void setAxis1(TranslationalJointAxis axis1) {
		this.axis1 = axis1;
	}

	public TranslationalJointAxis getAxis2() {
		return axis2;
	}

	public void setAxis2(TranslationalJointAxis axis2) {
		this.axis2 = axis2;
	}

	@Override
	public void apply(float time) {
	
		if (axis1 != null)
		{
			
			axis1.setAvailableAcceleration(15);
			axis1.setDesiredVelocity(-1 * state);//this should increase slower than acceleration, as a function of input
		}
		

	}

}
