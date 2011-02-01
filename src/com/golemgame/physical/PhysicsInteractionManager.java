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
