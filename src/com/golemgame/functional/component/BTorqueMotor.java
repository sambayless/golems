package com.golemgame.functional.component;

import com.jme.math.Vector3f;
import com.jmex.physics.DynamicPhysicsNode;

public class BTorqueMotor extends BMotor {
	static final long serialVersionUID =1;
	
	protected Vector3f maxForce;
	private Vector3f position;
	private transient DynamicPhysicsNode body;
	protected transient BMind mind;
	protected transient BSpace space;
	protected boolean prevState;
	private Vector3f tempForce = new Vector3f();
	private Vector3f angVel = new Vector3f();
	Vector3f linVel = new Vector3f();
	private Vector3f maxVelocity;
	//private float length;
	private Vector3f curForce = new Vector3f();
	//private Vector3f tPosition = new Vector3f();
	private float saturation;
	
	public BTorqueMotor(DynamicPhysicsNode wheel, BMind mind, Vector3f force,Vector3f position,Vector3f maxVelocity, float saturation) {
		super();
		

		this.body =wheel;
		this.mind = mind;
		this.position = position;
		this.maxForce = force;
		state = 0;
		prevState = false;
		this.maxVelocity = maxVelocity;
		this.saturation = saturation;
		
	}

	@Override
	public void apply(float time) {
		if (state > saturation)
			state = saturation;
		if (state <=0)
			return;
	
	//	state = saturation;
		maxForce.mult((state/saturation) ,curForce);
		runMotor(curForce);
	}
	protected void runMotor(Vector3f force)
	{
		//Need to implement checking direction - if velocity backwards, that should give full torque
		body.getAngularVelocity(angVel);
		
		//Shift angular values 
		float t = angVel.x;
		float tz = angVel.z;
		
		angVel.x = angVel.y;
		angVel.z = angVel.y;
		angVel.y = tz + t;		
		angVel.multLocal(position);	
		
		body.getLinearVelocity(linVel);		
		body.getWorldRotation().inverse().mult(linVel, linVel);
		
		//maxTorque - vel*maxTorque/maxVel
		linVel.addLocal(angVel);
		
		if (linVel.x > maxVelocity.x)
			linVel.x = maxVelocity.x;
		if (linVel.y > maxVelocity.y)
			linVel.y = maxVelocity.y;
		if (linVel.z > maxVelocity.z)
			linVel.z = maxVelocity.z;		
	
		if (linVel.x < 0)
			linVel.x = 0;
		if (linVel.y < 0)
			linVel.y = 0;
		if (linVel.z < 0)
			linVel.z = 0;	
		
		linVel.multLocal(force);
		
		if (maxVelocity.x > 0)
			linVel.x /= maxVelocity.x;
		if (maxVelocity.y > 0)
			linVel.y /= maxVelocity.y;
		if (maxVelocity.z > 0)
			linVel.z /= maxVelocity.z;
	
		force.subtract(linVel, tempForce);
	//	System.out.println(tempForce);		
		body.getWorldRotation().mult(tempForce,tempForce );//Transform to world coordinates
		body.addTorque(tempForce);//are the angles relative
	}
}
