package com.golemgame.physical;

import java.util.ArrayList;

import com.golemgame.model.ModelIntersectionData;
import com.golemgame.model.spatial.SpatialModel;
import com.golemgame.states.StateManager;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.system.DisplaySystem;

public abstract class PhysicsInteractionManager {
	protected CollisionRequirements req = new CollisionRequirements()
	{
		public boolean meetsRequirements(PhysicsComponent geom,
				float distance) {
			return true;
		}		
	};
	public CollisionRequirements getCollisionDetectionRequirements() {
		return req;
	}
	private final PhysicsWorld world;
	public PhysicsInteractionManager(PhysicsWorld world) {
		super();
		this.world = world;
	}

	public void attachController(SpringController controller)
	{
		
	}
	
	public synchronized void setCollisionDetectionRequirements(CollisionRequirements req)
	{
		this.req = req;
	}
	
	/**
	 * Return this closest dynamic physics node, from the camera, that intersects the ray leaving the screen at location
	 * @param pickAt
	 * @return
	 */
	public PhysicsComponent pickDynamicPhysics(Vector2f location)
	{
		Ray pickRay = new Ray();
		DisplaySystem.getDisplaySystem().getPickRay(location, StateManager.IS_AWT_MOUSE, pickRay);
		///PickResults res = new TrianglePickResults();
	//	res.setCheckDistance(true);
		ArrayList<ModelIntersectionData> results = new ArrayList<ModelIntersectionData>();
		this.world.getSpatial().intersectRay(pickRay, results, true);
		for(ModelIntersectionData d:results)
		{
			PhysicsComponent comp = world.getComponent((SpatialModel)d.getModel());
			if(comp==null && d.getModel()!=null)
			{
				comp = world.getComponent((SpatialModel)d.getModel().getParent());
			}
			if(comp!=null)
			{
				return comp;
			}
		}
		return null;
	}

}
