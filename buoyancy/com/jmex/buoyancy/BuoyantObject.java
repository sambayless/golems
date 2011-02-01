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

import com.jme.math.FastMath;
import com.jme.math.Plane;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsCollisionGeometry;



/**
 * <code>BuoyantObject</code> defines a physical object that is affected buoyant forces, and fluid friction. 
 * A <code>BuoyantObject</code> is composed of a combinations of elements, including a <code>Spatial</code>,
 * a <code>DynamicPhysicsNode</code> and a <code>PhysicsCollisionGeometry</code>. The Buoyant force is calculated for the
 * <code>PhysicsCollisionGeometry</code>. This means that a single <code>DynamicPhysicsNode</code> can be composed of multiple
 * <code>PhysicsCollisionGeometry</code>s, some of which are part of <code>BuoyantObject</code>s, and some of which are not. 
 * This allows for both efficiency (by not applying buoyancy to components that will not reasonably make use of it) and for 
 * versatility: multiple <code>BuoyantObject</code>s can be combined together to form unique and unusual shapes.
 * The Spatial is used to leverage <code>BoundingVolume</code> bounds checking, and should be as close as possible a match for the 
 * <code>PhysicsCollisionGeometry</code>.
 * It is up to the implementation to decide how physically accurate this representation is (including the volume of the object,
 * and the amount of friction applied to it).
 * @author Sam Bayless
 *
 */
public abstract class BuoyantObject
{
    private Spatial spatial;
    private PhysicsCollisionGeometry collisionGeometry;
    private DynamicPhysicsNode physicsNode;
    private Buoyancy buoyancy;
    
    private boolean enabled;
    
    public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public DynamicPhysicsNode getPhysicsNode()
    {
        return physicsNode;
    }

    /**
     * A buoyant object is a combination of two representations of a physical object. 
     * It MUST be composed of both a PhysicsCollisionGeometry, and a corresponding Spatial. The spatial should be as similar as possible to the
     * PhysicsCollisionGeometry in size and shape, and must be attached to the same PhysicsNode as the collision geometry.
     * Additionally, the spatial must have a VolumeBounds (Axis Aligned Bounding Box or Sphere).
     * @param collisionGeometry
     * @param spatial
     */
    public BuoyantObject(Buoyancy parent, PhysicsCollisionGeometry collisionGeometry, Spatial spatial, DynamicPhysicsNode physicsNode) {
        super();
        this.buoyancy = parent;
        this.collisionGeometry = collisionGeometry;
        this.spatial = spatial;
        this.physicsNode = physicsNode;
        this.enabled = true;
    }
    
    /**
     * Update properties of the buoyant object. This should be called at each step, before buoyancy is applied. 
     */
    public abstract void update();
    


    public float getVolume()
    {
        return this.getCollisionGeometry().getVolume();
    }



    public Spatial getSpatial()
    {
        return spatial;
    }

    public PhysicsCollisionGeometry getCollisionGeometry()
    {
        return collisionGeometry;
    }

    public abstract void applyFriction(IFluidRegion region,Vector3f gravityUnit, float tpf);
  

	/**
	 * Calculate the volume and centroid of the part of this object under water.
	 * Note: It is up to the implementation to decide how accurate these measures should be;
	 * in particular, some implementations may consider only the bounding volume in their calculations.
	 * @param region
	 * @param gravityUnit
	 * @param store
	 * @return The percentage of the volume that is under water, or 0 if no part of this object is underwater
	 */
	public abstract float getVolumeAndCentroid(IFluidRegion region, Vector3f gravityUnit, Vector3f store);

    /**
     * Test if a vector (in world coordinates) is under water
     * @param region
     * @param vector
     * @param gravityUnit
     * @return Less than 0 if under water
     */
    protected static float isBelowWater(IFluidRegion region, Vector3f vector, Vector3f gravityUnit)
    {	
		float fluidHeight = region.getFluidHeight();
		float gravityDistance = -gravityUnit.dot(vector);
		//gravity distance is the height of the object, relative to the direction of gravity
    	return gravityDistance  - fluidHeight;
    }
    
    protected static boolean intersectLineWithPlane(Plane p, Vector3f origin,  Vector3f direction, boolean allowIntersectionBehindOrigin, Vector3f store)
    {
        float denominator = p.getNormal().dot(direction);

        if (denominator > -FastMath.FLT_EPSILON && denominator < FastMath.FLT_EPSILON)
            return false; // coplanar

        float numerator = -(p.getNormal().dot(origin) + p.getConstant());
        float ratio = numerator / denominator;

        if (!allowIntersectionBehindOrigin && (ratio < FastMath.FLT_EPSILON))
           return false; // intersects behind origin

        store.set(direction).multLocal(ratio).addLocal(origin);

        return true;
    }
   
	/** Temporary vector for calculations (to avoid garbage collection)*/
    private static final Vector3f _tempIntersect = new Vector3f();
    protected static boolean intersectPairWithPlane(Plane p, Vector3f p1, Vector3f p2, Vector3f store, boolean allowIntersectionOutsidePoints)
    {
    	_tempIntersect.set(p2).subtractLocal(p1);
    	_tempIntersect.normalizeLocal();
    	Vector3f direction = _tempIntersect;
    	Vector3f origin = p1;
        float denominator = p.getNormal().dot(direction);

        if (denominator > -FastMath.FLT_EPSILON && denominator < FastMath.FLT_EPSILON)
            return false; // coplanar

        float numerator = -(p.getNormal().dot(origin) + p.getConstant());
        float ratio = numerator / denominator;

        if (Float.isInfinite(ratio))
        	return false;
        
      	//if (ratio < FastMath.FLT_EPSILON)
    	//	return false; // intersects behind p1
     
        store.set(direction).multLocal(ratio);
        store.addLocal(origin);
        if (!allowIntersectionOutsidePoints)
        {
        	float length  = store.dot(p.getNormal());
        	float p1Length = p.getNormal().dot(p1);
        	float p2Length = p.getNormal().dot(p2);
        	if (length >( p1Length > p2Length ? p1Length:p2Length ))
        			return false;
        	else if (length <( p1Length < p2Length ? p1Length:p2Length ))
        			return false;
        }
        
        return true;
    }

	public Buoyancy getBuoyancy() {
		return buoyancy;
	}
}

