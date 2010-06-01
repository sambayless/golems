package com.jmex.buoyancy;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import com.jme.bounding.BoundingVolume;
import com.jme.scene.Geometry;
import com.jme.scene.Spatial;
import com.jme.scene.batch.GeomBatch;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsCollisionGeometry;

/**
 * Defines a <code>BuoyantObject</code> that has the buoyancy characteristics of a <code>BoundingVolume</code>.
 * @author Sam Bayless
 *
 * @param <E>
 */
public abstract class BoundedBuoyantObject<E extends BoundingVolume> extends BuoyantObject {

	private boolean useSpatialBound = false;
	private ArrayList<BoundingVolume> batchBounds = null;
	private ArrayList<BoundingVolume> worldBatchBounds = null;
	private E worldBound = null;

	protected static class BoundClassException extends ClassCastException
		{
			private static final long serialVersionUID = 1L;
			public BoundClassException() {
				super();
			}

			public BoundClassException(String message) {
			
			}
		}

	public void applyCustomBound() {
			Geometry geom = (Geometry)this.getSpatial();	
			
			batchBounds = new ArrayList<BoundingVolume>(); 
			worldBatchBounds= new ArrayList<BoundingVolume>(); 
			for (int batchNum = 0; batchNum<geom.getBatchCount(); batchNum++)
			{
				GeomBatch batch = geom.getBatch(batchNum);
				FloatBuffer points = batch.getVertexBuffer();
				try{
				BoundingVolume bound = worldBound.getClass().newInstance();
				batchBounds.add(bound);
				worldBatchBounds.add(worldBound.getClass().newInstance());
				bound.computeFromPoints(points);
				}catch(InstantiationException e)
				{
					throw new BoundClassException(e.getMessage());
				}catch(IllegalAccessException e)
				{
					throw new BoundClassException(e.getMessage());
				}
			}    
	}
	
	@SuppressWarnings("unchecked")
	public E getCustomWorldBound() throws BoundClassException {
		if (!useSpatialBound)
			return worldBound;
		else
		{
			//try{
				//this.getSpatial().getWorldBound().getClass().isInstance(worldBound);
			//	if (worldBound.getClass().isInstance(this.getSpatial().getWorldBound()))
			//	E bound = worldBound.getClass().cast(this.getSpatial().getWorldBound());
				//this is not safe, but it is a precondition of this class that this succeed.
				return (E) this.getSpatial().getWorldBound();
			//	else
			//		throw new BoundClassException();
			//}catch(ClassCastException e)
			//{
			//	throw new BoundClassException();
			//}
		}
	}

	@Override
	public void update() {
	  	getPhysicsNode().updateWorldVectors();
    	getCollisionGeometry().updateWorldVectors();
    	getSpatial().updateWorldVectors();
    	
		if (!useSpatialBound)
			updateCustomWorldBound();
		
	}

	@SuppressWarnings("unchecked")
	public void updateCustomWorldBound() {
		
	
		for (int i =0; i < batchBounds.size(); i++)
		{
			BoundingVolume bound = batchBounds.get(i);
	        BoundingVolume localWorldBound = bound.transform(this.getSpatial().getWorldRotation(), this.getSpatial().getWorldTranslation(),
	        		this.getSpatial().getWorldScale(), worldBatchBounds.get(i));
	        if (i == 0)
	        {//this is not safe, but it is a precondition of instantiating this class that this work
	        	worldBound =(E)localWorldBound.clone(worldBound);
	
	        }else
	        {
	        	  worldBound.mergeLocal(localWorldBound);
	        }
	     
		}
	}

	public BoundedBuoyantObject(Buoyancy parent,
			PhysicsCollisionGeometry collisionGeometry, Spatial spatial,
			DynamicPhysicsNode physicsNode, E worldbound, boolean useSpatialBound) {
		super(parent, collisionGeometry, spatial, physicsNode);
		this.useSpatialBound = useSpatialBound;
	
		this.worldBound = worldbound;
		if (!useSpatialBound)
		{
			applyCustomBound();
			updateCustomWorldBound();
		}
	}

	protected boolean useSpatialBound() {
		return useSpatialBound;
	}

}