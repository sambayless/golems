package com.golemgame.model.spatial.shape;

import com.golemgame.model.spatial.SpatialModelImpl;
import com.jme.bounding.BoundingBox;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Capsule;

public class CapsuleModel extends SpatialModelImpl{
	private static final long serialVersionUID = 1L;
	
	private Capsule spatial;
	
	public CapsuleModel(boolean register) {
		super(register);

		
		
	}
	
	
	protected Spatial buildSpatial()
	{
		spatial = new Capsule("capsule", 8, 8, 8, 0.5f,1f);	
		spatial.setCullMode(SceneElement.CULL_ALWAYS);
		spatial.setModelBound(new BoundingBox());
		spatial.updateModelBound();

		return spatial;
	}
	
	public void rebuild(float radius, float height)
	{
		spatial.setHeight(height);
		spatial.setRadius(radius);
		spatial.updateModelBound();
	}

}
