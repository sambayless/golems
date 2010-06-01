package com.jmex.buoyancy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.PhysicsUpdateCallback;
import com.jmex.physics.geometry.PhysicsSphere;
import com.jmex.physics.material.Material;

/**
 * <code>Buoyancy</code> applies realistic buoyant and friction forces to physics objects.
 * It is composed of a set of <code>FluidRegion</code>s, and <code>BuoyantObject</code>s. A <code>FluidRegion</code>
 * defines a volume containing a fluid (such as air or water) of uniform density and distribution. 
 * <code>BuoyantObject</code>s are composed of <code>DynamicPhysicsNode</code>s, <code>Spatial</code>s, 
 * and <code>PhysicsCollisionGeometry</code>s, and define a physical object which experience buoyant and friction forces.
 * 
 * Buoyancy can be used in two ways: either directly by calling <code>update()</code> before each physics step, or 
 * by adding it as a <code>PhysicsUpdateCallback</code> to the <code>PhysicsSpace</code>.
 * @author Sam Bayless
 *
 */
public class Buoyancy implements PhysicsUpdateCallback{
	


	/**
	 * The ratio of densities between region and object, below which buoyancy will be ignored.
	 */
	public static final float DEFAULT_THRESHOLD = IFluidRegion.DENSITY_AIR/IFluidRegion.DENSITY_STYROFOAM;
	
	/**
	 * The set of <code>FluidRegions</code> registered with this model.
	 */
	private ArrayList<IFluidRegion> regions = new ArrayList<IFluidRegion>();
	
	/**
	 * This is the region that objects are considered to be in if they are not in any other defined regions.
	 */
	private IFluidRegion defaultRegion = null;
	
	/**
	 * The set of <code>BuoyantObject</code> registered with this model.
	 */
	private ArrayList<BuoyantObject> objects = new ArrayList<BuoyantObject>();
	
	/**
	 * This is the minimum ratio of object density to the density of the fluid at which buoyancy forces will be applied.
	 */
	private float threshold = DEFAULT_THRESHOLD;
	private PhysicsSpace physics;
	
	private static final  float MAX_LINEAR_ACCELERATION = 200f;
	private static final float MAX_ANGULAR_ACCELERATION = 200f;

    private boolean enabled = true;

    /**
     * Instantiate a new <code>Buoyancy</code> model. This model will not take effect 
     * unless a call to <code>update()</code> is made before each physics step. One 
     * way to accomplish this is to add this object as a <code>PhysicsUpdateCallback</code> 
     * to the <code>PhysicsSpace</code>.
     * 
     * @param physics
     */
	public Buoyancy(PhysicsSpace physics)
	{
		this.physics = physics;

		
		defaultRegion = new FluidRegion(FluidRegion.AIR.getFluidDensity(),Float.MAX_VALUE,FluidRegion.AIR.getFluidViscosity())
		{//Create a new instance of a region that has the same characteristics as the publicly available Air region
            
            public boolean checkBounds(BuoyantObject object)
            {
                return true;
            }		    
		};
		
	}

	
	
	public void afterStep(PhysicsSpace space, float time) {
	
	}

	
	public void beforeStep(PhysicsSpace space, float time) {

		update(time);
		
	}



	/**
	 * Apply buoyancy to <code>BuoyantObject</code>s. This should be called before each physics step.
	 * Note: as an alternative, Buoyancy can be registered with the <code>PhysicsSpace</code> as a call-back.
	 * @param tpf The time that has passed since the previous call to update.
	 */
	public void update(float tpf)
	{	    
	    if (!enabled)
	        return;
		//Get gravity
	    Vector3f gravity = physics.getDirectionalGravity(_gravityStore);//retrieve gravity
	   // gravity.normalizeLocal();
		//For each object, find the region(s) it is in.
		for (BuoyantObject object:objects)
		{		 
			if (!object.isEnabled())
				continue;
			
	    	object.update();//update each object before applying buoyancy to it.

		    boolean fullySubmerged = false;//if an object is fully submerged in one region, it does not apply any other regions to it
		    int indexOfSignificantRegion = getSignificantRegion(object);
		    //The regions are sorted by density; denser regions are given priority for applying force.
		    //So, if an object is fully submerged in a particular region, no regions of lesser density will be considered.
		    //start searching from the first region that can apply significant force
		    for (int index = indexOfSignificantRegion; index<regions.size();index++)
		    {
		    	IFluidRegion region = regions.get( index );
		    	if(region == null || !region.isEnabled())
		    		continue;//skip this region if it is not enabled
		      //Apply buoyancy and friction if the object is in the fluid
		        fullySubmerged = applyRegion(object,region,gravity,tpf);
		        if (fullySubmerged)
		            break;		      
		    }
		    if (!fullySubmerged && defaultRegion != null && defaultRegion.isEnabled())
		    {//apply the default region if necessary
		    //	applyRegion(object, defaultRegion,gravity,tpf);		            
		    }		    
	
		}
	}
		/** Temporary vector for calculations (to avoid garbage collection)*/
		private final Vector3f _gravityStore = new Vector3f();
	  	/** Temporary vector for calculations (to avoid garbage collection)*/
	    private final Vector3f _forceStore = new Vector3f();
	    /** Temporary vector for calculations (to avoid garbage collection)*/
	    private final Vector3f _offsetStore = new Vector3f();
	    /** Temporary vector for calculations (to avoid garbage collection)*/
	    private final Vector3f _gravityTemp = new Vector3f();
	    
	    
	/**
	 * Test if this object is partially (or fully) submerged in this region. If not, return false.
	 * If it is, apply forces as appropriate.
	 * If the object is completely submerged (meaning no other regions can affect it) then return true.
	 * @param object
	 * @param region
	 * @param directionalGravity
	 * @return True if the object is fully submerged in this region, and no further regions should be applied to it. False if other regions should be checked against this object.
	 */
	    private boolean applyRegion(BuoyantObject object, IFluidRegion region, Vector3f gravity, float tpf)
	    {

	    	Vector3f gravityUnit = _gravityTemp.set( gravity).normalizeLocal();
	    	
	    	//Test if the object is in bounds
	    	if (!region.checkBounds(object))
	    		return false;
	    	
	    	float fluidDensity = region.getFluidDensity();
	    	float fluidHeight = region.getFluidHeight();
	    	//Test if the object is partially or fully submerged

	    	Vector3f offset = _offsetStore;
	    	offset.set(0,0,0);
	    	
	    	float volumeSubmerged = 0;
	    	float volumeOut = 0;
	    	float totalVolume = object.getVolume();
	    	
	    	//Determine the proportion of the object that is under water
	    	if (fluidHeight < Float.MAX_VALUE)//If fluid height is greater than max value, then the fluid extends infinitely upwards, so the object must be fully submerged if it is in bounds.
	    	{
	    		//Get the volume and the centroid of the object
	    		//don't pass gravityUnit here directly, to prevent it from being altered by objects
	    		 volumeSubmerged = object.getVolumeAndCentroid(region,gravityUnit, offset);//getCentroidAndVolume(region,object, physicsNode, gravityUnit, offset);
	    		 
		    	if (volumeSubmerged<=0f)
		    	{
		    		//object is out of the fluid, no force applied, continue checking other regions
		    		return false;
		    	}
				if (offset.lengthSquared()>0)
    			{
					offset.subtractLocal(object.getPhysicsNode().getWorldTranslation());
					object.getPhysicsNode().getWorldRotation().inverse().multLocal(offset);
    				// object.getPhysicsNode().worldToLocal(offset, offset);
    			}
	    	}else
	    	{
	    		volumeSubmerged = totalVolume;
	    		offset.set(object.getCollisionGeometry().getLocalTranslation());
	    	}
	    
	    	 
	    	
	    	volumeOut = totalVolume - volumeSubmerged;
	    	
	    	
	
	    	/*
	    	 * Don't apply any forces if the density of the fluid is 0.
	    	 */
	    	if (fluidDensity <= 0)
	    	{
	    		return volumeOut <= 0;
	    	}
	    	
	      	/*
	    	 * Set the buoyant force to equal the volume of the object under water, times the fluid density, times the magnitude of graivty, in the direction away from gravity
	    	 */
	    	Vector3f buoyantForce = _forceStore.set(gravity);
	    	buoyantForce.multLocal(-1*fluidDensity*volumeSubmerged);//this is the buoyant force if the object is fully submerged
	
	    	
	    	/*
	    	 * Apply friction to the object if the region has greater than zero viscosity
	    	 */
	    	if (region.getFluidViscosity() > 0)
	    	{//If there is 0 viscosity, ignore friction   		
	    		object.applyFriction(region,gravityUnit,tpf);
	    	}
	    	
	    	//TODO:
	    	/*debugNode.detachAllChildren();
	    	Box loc = new Box("", new Vector3f(), 0.5f,0.5f,0.5f);
	    	loc.getLocalTranslation().set(offset);
	    	loc.getLocalTranslation().addLocal(object.getPhysicsNode().getWorldTranslation());
	    	debugNode.attachChild(loc);
	    	loc.updateRenderState();*/
	    	
	    	//Apply buoyant force to the physics node, at the position of the CollisionGeometry
	    	applyLimitedForce(object,buoyantForce,offset);
	    	if (volumeOut > 0)
	    	{
	    		return false;
	    	}else
	    	{
	    		return true;                    
	    	}
	    }
	
	/**
	 * Get the index of the first region that can exert a significant force on this object
	 * @param object
	 * @return
	 */
	private int getSignificantRegion(BuoyantObject object)
	{
	    regionComparator.setFluidDensity( getThresholdFluidDensity(object));
	    
	    int pos = Collections.binarySearch(regions,regionComparator);
	    if (pos < 0)
	    {
	        pos *= -1;
	        pos --;
	    }
	    
	    return pos;
	}	
	
	/**
	 * Utility class for comparing regions.
	 */
	private FluidRegion regionComparator = new FluidRegion(0,0,0)
	{
		
		public boolean checkBounds(BuoyantObject object) {
			//Not used
			return false;
		}
	    
	};

	/**
	 * It is necessary to limit the amount of acceleration that any object can undergo; otherwise very light objects can behave erraticly.
	 * @param object
	 * @param force
	 * @param position
	 */
	public static void applyLimitedForce(BuoyantObject object, Vector3f force, Vector3f position)
	{
		float mass = object.getCollisionGeometry().getVolume() *object.getCollisionGeometry().getMaterial().getDensity();
    	//Limit the force being applied
		float magnitude = force.length();
		if (magnitude/mass>MAX_LINEAR_ACCELERATION)
		{
    		float factor = (mass*MAX_LINEAR_ACCELERATION)/magnitude;
    		force.multLocal(factor);
		}
    	object.getPhysicsNode().addForce(force, position);   
	}

	/**
	 * It is necessary to limit the amount of acceleration that any object can undergo; otherwise very light objects can behave erraticly.
	 * @param object
	 * @param torque
	 */
	public static void applyLimitedTorque(BuoyantObject object, Vector3f torque)
	{
		float mass =object.getCollisionGeometry().getVolume() *object.getCollisionGeometry().getMaterial().getDensity();
    	//Limit the torque being applied    
    	float magnitude = torque.length();
		if (magnitude/mass>MAX_ANGULAR_ACCELERATION)
		{
    		float factor = (mass*MAX_ANGULAR_ACCELERATION)/magnitude;
    		torque.multLocal(factor);
		}
    	object.getPhysicsNode().addTorque(torque);   
	}
	
	/**
	 * Takes a DynamicPhysicsNode, and adjusts the local translations appropriately such that their center of mass is at origin. 
	 * @param collisionGeometryMap
	 * @return The old center of mass.
	 */
	public static Vector3f zeroCenterOfMass(DynamicPhysicsNode toZero)
	{
		Vector3f cm = new Vector3f();
		Vector3f temp = new Vector3f();
		float totalMass = 0;
		for (Spatial spatial:toZero.getChildren())
		{
			if (spatial instanceof PhysicsCollisionGeometry)
			{
				PhysicsCollisionGeometry collision = (PhysicsCollisionGeometry) spatial;
				float volume = collision.getVolume();
				Material m = collision.getMaterial();
				if (m == null)
					m = collision.getPhysicsNode().getMaterial();
				float density = m.getDensity();
				float mass = volume*density;
				temp.set(collision.getLocalTranslation());
				temp.multLocal(mass);
				cm.addLocal(temp);
				totalMass += mass;
			}
		}
		cm.divideLocal(totalMass);//this is the center of mass
		for (Spatial spatial:toZero.getChildren())
		{
			spatial.getLocalTranslation().subtractLocal(cm);
		}
		//move the physics node so that the world translations of each object do not change
	
		temp.set(cm);
		toZero.getLocalRotation().inverse().multLocal(temp);
		toZero.getLocalTranslation().addLocal(temp);
		if (toZero instanceof DynamicPhysicsNode)
		{
			((DynamicPhysicsNode)toZero).setCenterOfMass(new Vector3f(0,0,0));//the center of mass is now zeroed
		}
		return cm;
		
	}
	
	public static final float calculateReynoldsNumber(IFluidRegion fluid, float velocity, float characteristicLength)
	{
		float reynolds = (fluid.getFluidDensity()/fluid.getFluidViscosity())*velocity*characteristicLength;
		return reynolds;
	}
	
	/**
	 * Calculate the least dense fluid that could exert a significant amount of force of this object (as defined by the threshold buoyancy).
	 * @param object
	 * @return
	 */
	public float getThresholdFluidDensity(BuoyantObject object)
	{
	    float density = object.getCollisionGeometry().getMaterial().getDensity();
	    return density*threshold;
	}
	
	/**
	 * The cut off in terms of the percentage of density of the physics nodes relative to the density of the medium at which buoyancy is considered negligible, and ignored.
	 * This allows for very dense objects to be ignored.
	 * @param threshold A value between 0 and 1. Set threshold to 1 to disregard threshold.
	 */
	public void setThresholdBuoyancy(float threshold)
	{
		this.threshold = threshold;
	}	
	
	/**
	 * Add a buoyancy region. Regions are locations in space in which a uniform fluid exists. 
	 * For example, a region could be a lake, the atmosphere, or a bucket of water.
	 * @param region
	 */
	public void addRegion(IFluidRegion region)
	{
	    regions.add(region);
	    Collections.sort(regions);
	}
	
	public void removeRegion(IFluidRegion region)
	{
	    regions.remove( region );
	}
	
	/**
	 * Add a <code>BuoyantObject</code> to apply buoyant forces and fluid frictions to.
	 * @param object
	 */
	public void addBuoyantObject(BuoyantObject object)
	{
	    objects.add( object );
	}
	
	public void removeBuoyantObject(BuoyantObject object)
	{
	    objects.remove( object );
	}
	
	/**
	 * Get a list of all <code>BuoyantObject</code>s registered with this Buoyancy class.
	 * @return
	 */
	public ArrayList<BuoyantObject> getBuoyantObjects()
	{
	    return objects;
	}
	
	

	/**
	 * Add a PhysicsNodes to those that will have buoyancy applied to them.
	 * You must also supply a map connecting collision geometries to the Spatials they represent, as these Spatials are used to calculate the bounds of the collision geometry
	 * It is recommended that each Spatial in that map be bounded by a BoundingBox (as they are significantly more accurate than BoundingSpheres for these purposes).
	 * Any spatial without a BoundingVolume will have a BoundingBox applied to it by this method.
	 * BoundingCapsules will not work properly.
	 * @param physicsNode A physics node. Note: the children of this physics node will have their translations adjusted such that their center of mass is at 0,0,0; 
	 * the physics node will have its local translation adjusted in the opposite direction, such that the world translations of each child remains the same.
	 * @param collisionMap This is the same type of map that is created by a call to PhysicsNode.generatePhysicsGeometry()
	 */
	public void addPhysicsNode(DynamicPhysicsNode physicsNode, Map<Spatial, PhysicsCollisionGeometry> collisionMap)
	{		
		int count = 0;
		zeroCenterOfMass(physicsNode);
		for (Spatial spatial:collisionMap.keySet())
		{//Find the collision geometries of this physics node
			PhysicsCollisionGeometry collision = collisionMap.get(spatial);
			count ++;
			if ((spatial.getWorldBound() == null))
			{//ensure that all bounds are world bounds
				spatial.updateWorldVectors();
				spatial.setModelBound(new BoundingBox());
				spatial.updateModelBound();
			}
			
			BuoyantObject object;
			if(collision instanceof PhysicsSphere)
				object = new BuoyantSphere(this,collision,spatial, physicsNode,false);
			else 
				object = new BuoyantBox(this, collision,spatial, physicsNode,false);
			

			addBuoyantObject(object);
		}
	}

	public void removePhysicsNode(DynamicPhysicsNode physicsNode)
	{
		Iterator<BuoyantObject> iterator = objects.iterator();
		while (iterator.hasNext())
		{
			if (iterator.next().getPhysicsNode().equals(physicsNode))
			{
				iterator.remove();
			}
		}
	}

	/**
	 * Remove all BuoyantObjects
	 */
	public void clear()
	{
		objects.clear();
	}

    public IFluidRegion getDefaultRegion()
    {
        return defaultRegion;
    }

    public void setDefaultRegion( IFluidRegion defaultRegion )
    {
        this.defaultRegion = defaultRegion;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Set whether or not to apply buoyancy forces.
     * @param enabled
     */
    public void setEnabled( boolean enabled )
    {
        if (!this.enabled && enabled)
        {
            //update each object's properties, because they may not have been updated recently
            for (BuoyantObject object:objects)
            {
                object.update();
            }
        }
        this.enabled = enabled;
    }



}  

