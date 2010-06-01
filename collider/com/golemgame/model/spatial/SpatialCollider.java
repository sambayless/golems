package com.golemgame.model.spatial;

import com.jmex.physics.jme.collision.NotTestableException;
import com.jmex.physics.jme.collision.ODECollider;

public class SpatialCollider {
	
	
	private static ODECollider collider = null;
	
	public static void setCollider(ODECollider c) throws NotTestableException
	{
		if (collider != null)
			throw new NotTestableException("Collider not instantiated");
		
		collider = c;
	}
	
	public static ODECollider getCollider()
	{
		return collider;
	}
}
