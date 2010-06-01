package com.golemgame.util;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;

public class OrientingNode extends Node {

	private static final long serialVersionUID = 1L;
	private boolean allowRoll=true;
	private boolean allowPitch=true;
	private boolean allowYaw=true;
	
	private boolean allLocked = false;
	
public boolean isAllLocked() {
		return allLocked;
	}

	public void setAllLocked(boolean allLocked) {
		this.allLocked = allLocked;
	}

	//	private final Quaternion temp = new Quaternion();
	private final float[] euler = new float[3];
/*	@Override
	public void updateWorldData(float time) {
		super.updateWorldData(time);
		
		//Quaternion rotation = temp.set(worldRotation);

		//rotation.mult(localRotation, worldRotation);
		
		
	}
*/
	
	
	public boolean isAllowRoll() {
		return allowRoll;
	}
	
	private void convertToPlane(Vector3f plane, Quaternion from)
	{
		Vector3f tX = new Vector3f(plane);
		from.multLocal(tX);
	//	float dist = tX.distanceSquared(plane);
		//if(dist>0.01f)
		{
		float angle = tX.angleBetween(plane) ;//% FastMath.HALF_PI;
	//	if(angle > 0.01f && angle <3f)
		
		Quaternion toPlane = new Quaternion().fromAngleNormalAxis(FastMath.HALF_PI - angle, tX.cross(plane));
		from.multLocal(toPlane.inverseLocal());
		}
	}

	@Override
	protected void updateWorldRotation() {
		super.updateWorldRotation();
		
		if(allLocked)
		{
			worldRotation.set(0,0,0,1);
			return;
		}
		
	/*	if(!allowYaw)
		{
			Vector3f tY = new Vector3f(Vector3f.UNIT_X);
			worldRotation.multLocal(tY);
			if(project(tY,Vector3f.UNIT_Y, tY))//get the component of the vector that is on the xy plain (to ignore pitch).
			{
				float angle = tY.angleBetween(Vector3f.UNIT_X);
				System.out.println(angle);
				Quaternion y = new Quaternion().fromAngleNormalAxis(tY.angleBetween(Vector3f.UNIT_X),Vector3f.UNIT_Y);
				y.inverseLocal();
				worldRotation.multLocal(y);
			}
			Vector3f tY = new Vector3f(Vector3f.UNIT_Y);
			worldRotation.multLocal(tY);
			Quaternion y = new Quaternion().fromAngleNormalAxis(tY.angleBetween(Vector3f.UNIT_Y),tY.cross(Vector3f.UNIT_Y));
			
			worldRotation.set(y);
		}
	//	System.out.println(this.getLocalRotation());
		if(allowPitch)
		{
			Vector3f tY = new Vector3f(Vector3f.UNIT_Y);
			worldRotation.multLocal(tY);
			//System.out.println(tY);
			if(project(tY,Vector3f.UNIT_X, tY))//get the component of the vector that is on the xy plain (to ignore pitch).
			{
				float angle = tY.angleBetween(Vector3f.UNIT_Y);
				//System.out.println(angle);
				Quaternion y = new Quaternion().fromAngleNormalAxis(tY.angleBetween(Vector3f.UNIT_Y),Vector3f.UNIT_X);
				y.inverseLocal();
				worldRotation.multLocal(y);
				
			//	worldRotation.normalize();
				
				tY = new Vector3f(Vector3f.UNIT_Y);
				worldRotation.multLocal(tY);
				project(tY,Vector3f.UNIT_X, tY);
				angle = tY.angleBetween(Vector3f.UNIT_Y);
				System.out.println(angle);
			}
			Vector3f tX = new Vector3f(Vector3f.UNIT_Y);
			worldRotation.multLocal(tX);
			Quaternion rollComponent = new Quaternion();
			rollComponent.fromAxes(tX.cross(Vector3f.UNIT_X),(tX.cross(Vector3f.UNIT_X)).cross(Vector3f.UNIT_X) , Vector3f.UNIT_Z);

			Vector3f tZ = new Vector3f(Vector3f.UNIT_Z);
			worldRotation.multLocal(tZ);
			tZ.normalizeLocal();
			//if(FastMath.abs(tZ.z)>0.0001f)
 			{
		//	tZ.normalizeLocal();
			float angle = tZ.angleBetween(Vector3f.UNIT_Z);
		//	angle %= (FastMath.HALF_PI);
		//	System.out.println(angle + " " + tZ);
			Quaternion z = new Quaternion().fromAngleNormalAxis(angle,tZ.cross(Vector3f.UNIT_Z));
			
			worldRotation.set(z);
 		//	}
		}
		
		if(!allowRoll)
		{
			//plan: find the component of rotation that is around the z axis, construct a quaternion for it, inverse it and multiply it into the main rotation (ie, subtract it)
			Vector3f tY = new Vector3f(Vector3f.UNIT_X);
			worldRotation.multLocal(tY);
			if(project(tY,Vector3f.UNIT_Z, tY))//get the component of the vector that is on the xy plain (to ignore pitch).
			{
				float angle = tY.angleBetween(Vector3f.UNIT_Y);
				//System.out.println(angle);
				Quaternion y = new Quaternion().fromAngleNormalAxis(tY.angleBetween(Vector3f.UNIT_X),Vector3f.UNIT_Z);
				y.inverseLocal();
				worldRotation.multLocal(y);
			}
		}
	
		//if(!allowRoll)
		{
		//	convertToPlane(Vector3f.UNIT_Z,worldRotation);
		//	convertToPlane(Vector3f.UNIT_X,worldRotation);
		//	convertToPlane(Vector3f.UNIT_Y,worldRotation);
			Vector3f tX = new Vector3f(Vector3f.UNIT_Y);
			worldRotation.multLocal(tX);
			Quaternion rollComponent = new Quaternion();
			rollComponent.fromAxes(tX.cross(Vector3f.UNIT_Z),(tX.cross(Vector3f.UNIT_Z)).cross(Vector3f.UNIT_Z) , Vector3f.UNIT_Z);
			//the quaternion for just the x component of this will be
			worldRotation.set(rollComponent);
 			
 			//if(FastMath.abs(tX.x)>0.0001f)
 			{
			Quaternion x = new Quaternion().fromAngleNormalAxis(tX.angleBetween(Vector3f.UNIT_X),tX.cross(Vector3f.UNIT_X));
		
			worldRotation.set(x);
 			}
		}
			Vector3f t = new Vector3f(Vector3f.UNIT_Z);
			worldRotation.multLocal(t);
			float error = t.angleBetween(Vector3f.UNIT_Z);
			
			Quaternion fix = new Quaternion().fromAngleNormalAxis(FastMath.HALF_PI- error,t.cross(Vector3f.UNIT_Z));
			//fix.inverseLocal();
			worldRotation.multLocal(fix);
			t = new Vector3f(Vector3f.UNIT_Z);
			worldRotation.multLocal(t);
			 error = t.angleBetween(Vector3f.UNIT_Z);
			 System.out.println(error);
			
		
			Vector3f t = new Vector3f(Vector3f.UNIT_Y);
			
			worldRotation.multLocal(t);
			
			//pitch is the amount of rotation of y around x...
			//so zero the z component, we dont care about it
			//t.x = 0;
			t.normalizeLocal();
			
			float error = t.angleBetween(Vector3f.UNIT_Y);
			float side = FastMath.sign( t.dot(Vector3f.UNIT_Z));
			//System.out.println(error*side);
			Quaternion fix = new Quaternion().fromAngleNormalAxis(-error*side,Vector3f.UNIT_Z);
			worldRotation.multLocal(fix);
			
			t = new Vector3f(Vector3f.UNIT_Y);
			
			worldRotation.multLocal(t);
			
			//pitch is the amount of rotation of y around x...
			//so zero the z component, we dont care about it
			t.x = 0; 
			t.normalizeLocal();
			
			 error = t.angleBetween(Vector3f.UNIT_Y);
				System.out.println(error*side);
		}
*/
	/*	worldRotation.toAngles(euler);
	//euler[0] %=FastMath.PI;
		System.out.print(Arrays.toString(euler));
		
		Quaternion yaw = new Quaternion();
		Quaternion pitch = new Quaternion();
		Quaternion roll = new Quaternion();
		
		pitch.fromAngleNormalAxis(euler[0], Vector3f.UNIT_X);
		yaw.fromAngleNormalAxis(euler[1], Vector3f.UNIT_Y);		
		roll.fromAngleNormalAxis(euler[2], Vector3f.UNIT_Z);
		//worldRotation.set(0f,0f,0f,1f);
		
		
	
	
	
		if(allowPitch)
			worldRotation.multLocal(pitch);
		if(allowYaw)
			worldRotation.multLocal(yaw);
		if(allowRoll)
			worldRotation.multLocal(roll);
		if(!allowPitch)
		{
			euler[0]=0;
		}
		if(!allowYaw)
		{
			euler[1]=0;
		}
		if(!allowRoll)// && FastMath.abs(euler[0])>FastMath.FLT_EPSILON)
		{
			//if(euler[0]!=0)
			{
				euler[2]=0;
			}
	
			//fix.inverseLocal();
			//worldRotation.multLocal(fix);
		
			
		}
		//worldRotation.fromAngles(euler);
		//worldRotation.set(roll).multLocal(yaw);
		worldRotation.toAngles(euler);
		System.out.print(Arrays.toString(euler));
		System.out.println();
	*/
	//	worldRotation.fromAngles(euler);
	}

	/**
	 * Project a vector onto a plane (define by the normal Normal)
	 * Inputs are assumed to be normalized. If the normal equals the vector (or very nearly does), return false, and set store to zero.
	 * @param vector
	 * @param normal
	 * @param store The (normalized) projection
	 * @return
	 */
	private boolean project(Vector3f vector, Vector3f normal, Vector3f store) {
		//take the zero vector, project it into the plane, and normalize
		//A || B = B x (AxB / |B|) / |B| projection of a vector A onto a plane with normal B. NOTE: division happens AFTER crossing
		//See: http://www.euclideanspace.com/maths/geometry/elements/plane/lineOnPlane/index.htm
		//assuming normalized inputs, we can skip some steps...
		
		if(normal.distanceSquared(vector)<FastMath.FLT_EPSILON)
		{
			store.zero();
			return false;
		}
		else
		{
				store.set(vector);
				store.crossLocal(normal);
				normal.cross(store,store);
				if(store.lengthSquared()<=FastMath.FLT_EPSILON)
				{
					store.zero();
					return false;
				}
				store.normalizeLocal();
				return true;
		}
	//	worldZeroVector =(orientation.getDirection().cross( worldZeroVector));//.cross(orientation.getDirection()));
	//	worldZeroVector.set( orientation.getDirection().cross(worldZeroVector.cross(orientation.getDirection()))).normalizeLocal();

	}

	public void setAllowRoll(boolean allowRoll) {
		this.allowRoll = allowRoll;
	}

	public boolean isAllowPitch() {
		return allowPitch;
	}

	public void setAllowPitch(boolean allowPitch) {
		this.allowPitch = allowPitch;
	}

	public boolean isAllowYaw() {
		return allowYaw;
	}

	public void setAllowYaw(boolean allowYaw) {
		this.allowYaw = allowYaw;
	}
	
	
}
