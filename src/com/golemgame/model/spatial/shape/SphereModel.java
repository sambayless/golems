package com.golemgame.model.spatial.shape;

import com.golemgame.model.spatial.SpatialModelImpl;
import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Sphere;

public class SphereModel extends SpatialModelImpl{
	private static final long serialVersionUID = 1L;
	public static final float INIT_SIZE = 1f;

	public SphereModel() {
		super();
	}

	public SphereModel(boolean registerSpatial) {
		super(registerSpatial);
	}

	protected Spatial buildSpatial()
	{
		Spatial spatial = new Sphere("sphere", new Vector3f(), 8, 8, INIT_SIZE/2);		
		spatial.setCullMode(SceneElement.CULL_ALWAYS);
		spatial.setModelBound(new BoundingBox());
		spatial.updateModelBound();
		return spatial;
	}
	
	
	public boolean isShareable() {
		return true;
	}

	
	public SphereModel makeSharedModel() {
		SphereModel sharedBox = new SphereModel();
	
		return sharedBox;
	}


}
