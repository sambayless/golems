/*******************************************************************************
 * Copyright 2008, 2009, 2010 Sam Bayless.
 * 
 *     This file is part of Golems.
 * 
 *     Golems is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     Golems is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with Golems. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.jmex.buoyancy;

import com.jme.bounding.BoundingSphere;
import com.jme.math.FastMath;
import com.jme.math.Plane;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsCollisionGeometry;

/**
 * This is a <code>BoundingSphere</code> buoyant object. It is physically accurate for spheres, but can be used as an approximation for
 * similar, highly symmetric objects.
 * @author Sam Bayless
 *
 */
public class BuoyantSphere extends BoundedBuoyantObject<BoundingSphere> {
	
	/**
	 * This models low velocity energy loss due to waves. 
	 */
	public static final float MINIMUM_FRICTION_LOSS = 0.01f;
	
	public BuoyantSphere(Buoyancy parent, PhysicsCollisionGeometry collisionGeometry,
			Spatial spatial, DynamicPhysicsNode physicsNode, boolean useSpatialBound) {
		super(parent, collisionGeometry, spatial, physicsNode, new BoundingSphere(), useSpatialBound);
		
	}
	
	private static final Plane _plane = new Plane();
	private static final Vector3f _temp = new Vector3f();
	private static final Vector3f _temp3 = new Vector3f();
	@Override
	public float getVolumeAndCentroid(IFluidRegion region, Vector3f gravityUnit, Vector3f store) {
		BoundingSphere bound;
		
		try{
			bound = getCustomWorldBound();
		}catch(ClassCastException e)
		{
			throw new BoundClassException(e.getMessage());
		}
	
		Plane waterPlane = _plane;
		waterPlane.getNormal().set(gravityUnit);
		waterPlane.setConstant(region.getFluidHeight()); 
		float radius = bound.getRadius();
	
		Vector3f center = bound.getCenter(_temp3);
		
		//move the center down along gravity by the amount radius
		
		//intersect the line center, gravity unit with the plane
		Vector3f intersect = _temp;

		
		boolean result = intersectLineWithPlane(waterPlane, center, gravityUnit, true, intersect);
		if (!result)
			return 0;
		
		float height;//height here is relative to center, not to the end of the sphere. 
		intersect.subtractLocal(center);
		store.set(center);
		
		if ((height = intersect.length())>=radius)
		{
			
			
			if (intersect.dot(gravityUnit)>0)
				return 0;
			else 
				return super.getVolume();//the entire volume is submerged
		}
		
		height *= -FastMath.sign( intersect.dot(gravityUnit));//get the sign of the intersection relative to the opposite of gravity's vector
		   


		float totalVolume = 4f/3f * FastMath.PI * FastMath.pow(bound.radius, 3);
		
		float volumeBelowSurface =  volumeOfPartialSphere(radius, height,-radius);
	
	//	intersect.normalizeLocal();
		centroidOfPartialSphere(radius,-height, -radius,volumeBelowSurface,gravityUnit,store);//invert this to get the centroid below surface
		store.addLocal(center);
		
		float ratio = volumeBelowSurface/totalVolume;
		return super.getVolume()*ratio;
	}


	public void applyFriction(IFluidRegion region, Vector3f gravityUnit, float tpf) {
		BoundingSphere bound;
		try{
			bound = getCustomWorldBound();
		}catch(ClassCastException e)
		{
			throw new BoundClassException(e.getMessage());
		}
		
		Plane waterPlane = _plane;
		waterPlane.getNormal().set(gravityUnit);
		waterPlane.setConstant(region.getFluidHeight()); 
		float radius = bound.getRadius();
	
		Vector3f center = bound.getCenter(_temp3);
		
		//move the center down along gravity by the amount radius
		
		//intersect the line center, gravity unit with the plane
		Vector3f intersect = _temp;

		
		boolean result = intersectLineWithPlane(waterPlane, center, gravityUnit, true, intersect);
		if (!result)
			return;
		
		float height;//height here is relative to center, not to the end of the sphere. 
		intersect.subtractLocal(center);

		
		if ((height = intersect.length())>=radius)
		{
			if (intersect.dot(gravityUnit)>0)
				return;
			else
				height = radius;
		}
		
		height *= -FastMath.sign( intersect.dot(gravityUnit));//get the sign of the intersection relative to the opposite of gravity's vector
		   
		float submergedRadius = radius;
		if (height < 0)
			submergedRadius = FastMath.sqrt(radius*radius - height * height);
		
		applyLinearFriction(region,submergedRadius,bound, tpf);
		applyRotationalFriction(region,bound,tpf);
	}
	

	  
    private static final Vector3f _frictionStore = new Vector3f();
    private static final float coefficient = 0.6f;//this coefficient of drag for a sphere from: http://www.grc.nasa.gov/WWW/K-12/airplane/shaped.html
	
    private void applyLinearFriction(IFluidRegion region, float submergedRadius, BoundingSphere bound, float tpf)
    {
    	Vector3f linearFriction;
    	
    	linearFriction = getPhysicsNode().getLinearVelocity(_frictionStore);
    	float velocity = linearFriction.length();
    	linearFriction.normalizeLocal();
    	applyLinearFrictionToVector(velocity, linearFriction, region, submergedRadius, bound, tpf);
    }
    
    
    private void applyLinearFrictionToVector(float velocity, Vector3f unitVelocity, IFluidRegion region, float radius, BoundingSphere bound, float tpf)
    {
    	/*
    	 * In Subsection 1.1, we mentioned that the drag force, FD, depends on the velocity of the sphere, v, 
    	 * the diameter of the sphere, D, the density of the fluid, rho, and the viscosity of the fluid, mu.
    	 * 
    	 * The powerful technique of dimensional analysis shows that these five variables can be combined into two 
    	 * variables without any loss of ability to describe the drag force. These two variables, are the drag 
    	 * coefficient, CD = FD/(1/2 rho v^2 A), and the Reynolds number, defined by Re = rho v D/mu, 
    	 * where A is the cross-sectional area of the sphere.
    	 * http://www.ma.iup.edu/projects/CalcDEMma/drag/drag4.html
    	 */
    	Vector3f linearFriction;
    	
    	linearFriction = _frictionStore.set(unitVelocity);
    
	    	if ( Buoyancy.calculateReynoldsNumber(region,velocity,2f*bound.getRadius() ) > 10f)
	    	{
	        	//If the reynolds number is high, then the following formula is appropriate
	    		//See:http://en.wikipedia.org/wiki/Fluid_friction
	    		
	        	float area = radius * radius * FastMath.PI;//this is slightly incorrect if the sphere is not fully submerged
	        	

	        	float friction = velocity*velocity;
	        	friction *= -coefficient * area * region.getFluidDensity() /2f;
	        	
	        	linearFriction.multLocal(friction);
	        	
	        	Buoyancy.applyLimitedForce(this,linearFriction,this.getCollisionGeometry().getLocalTranslation());
	
	    		
	    	}else
	    	{//otherwise, apply viscous drag
	    		
	        	//float area = radius * radius * FastMath.PI;//this is slightly incorrect if the sphere is not fully submerged
	        	

	        	float friction = velocity* region.getFluidViscosity() + FastMath.sign(velocity)* MINIMUM_FRICTION_LOSS;
	    		//if (FastMath.abs(friction) <   MINIMUM_FRICTION_LOSS)
	    		//	friction = FastMath.sign(friction) *  MINIMUM_FRICTION_LOSS;
	        	friction *= -6f * FastMath.PI* radius ;
	        	//This formula is stokes drag for spheres 
	        	//http://en.wikipedia.org/wiki/Drag_(physics)
	        	
	        	linearFriction.multLocal(friction);
	         
	        	Buoyancy.applyLimitedForce(this,linearFriction,this.getCollisionGeometry().getLocalTranslation());

	    	}
	    	
    }
    
    private static final Vector3f _tempLinearFriction = new Vector3f();
	private static final Vector3f _tempRotation = new Vector3f();
	private static final Vector3f _tempCenter = new Vector3f();
	private void applyRotationalFriction(IFluidRegion region, BoundingSphere bound, float tpf)
	{//This won't be correct for spheres not at origin. Approach this the same way as for the box
		Vector3f relativeCenter = bound.getCenter(_tempCenter).subtractLocal(getPhysicsNode().getWorldTranslation());
		if (relativeCenter.lengthSquared() < FastMath.FLT_EPSILON)
		{//if the sphere is nearly at origin, then apply a torque to it directly
			float radius = bound.getRadius();
			Vector3f omega = this.getPhysicsNode().getAngularVelocity(_tempRotation);
			
			omega.multLocal(-8*FastMath.PI * region.getFluidViscosity() *FastMath.pow(radius,3));
			this.getPhysicsNode().addTorque(omega);
		}else
		{
	    	Vector3f omega = this.getPhysicsNode().getAngularVelocity(_frictionStore);
	    	//for each component of angular velocity...
	    	for (int axisOfRotation = 0; axisOfRotation < 3; axisOfRotation++)
	    	{
	    		Vector3f omegaAxis = axis[axisOfRotation];
	    		for (Vector3f linearAxis:axis)
	    		{
	    			if (linearAxis == omegaAxis)
	    				continue;
	    			
	    			float linearVelocity =  relativeCenter.dot(linearAxis) * omega.dot( omegaAxis);
	    			Vector3f linearDirection=null;
	    			for (Vector3f direction:axis)
	    			{
	    				if (direction != omegaAxis && direction != linearAxis)
	    				{
	    					linearDirection = _tempLinearFriction.set(direction);
	    					break;
	    				}
	    			}
	    			
	    			applyLinearFrictionToVector(linearVelocity, linearDirection, region,bound.getRadius(), bound, tpf);
	    	
	    		}

	    		
	    	}
	    	


		}
		
	}
	private static final Vector3f[] axis = new Vector3f[]{Vector3f.UNIT_X, Vector3f.UNIT_Y, Vector3f.UNIT_Z};
	/**
	 * Get the centroid of a partial sphere
	 * @param radius
	 * @param height
	 * @param gravityUnit The unit axis along which height is measured
	 * @param store
	 * @return
	 */
	private Vector3f centroidOfPartialSphere(float radius, float top, float bottom, float volumeOfPartialSphere, Vector3f gravityUnit, Vector3f store)
	{
		//The centroid (relative to the center of the sphere)
		float centroidAlongAxis = 0;

		centroidAlongAxis += top*top*radius*radius/2f - FastMath.pow(top, 4)/4f;
		centroidAlongAxis -= bottom*bottom*radius*radius/2f - FastMath.pow(bottom, 4)/4f;
		
		centroidAlongAxis *= FastMath.PI/volumeOfPartialSphere;


		return store.set(gravityUnit).multLocal(-centroidAlongAxis);//this is negative because it goes in the opposite direction of gravity
	}
	
	/**
	 * Get the volume of a partial sphere
	 * @param radius Radius of the sphere
	 * @param height The height of a partial sphere. Top and bottom are the heighest and lowest points of the partial sphere to get the volume of;
	 * they must be -radius<=bottom<=top<=radius. A full sphere has bottom set to -radius and top set to radius.
	 * @return
	 */
	private float volumeOfPartialSphere(float radius, float top, float bottom)
	{
		float volume =0;

	
		volume += (radius*radius*top - FastMath.pow(top, 3)/3f);

		volume -= (radius*radius*bottom - FastMath.pow(bottom, 3)/3f);
		
		return FastMath.PI*volume;
	}

	
	
}
