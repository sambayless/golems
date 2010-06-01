package com.golemgame.model.spatial.shape;

import com.golemgame.model.spatial.SpatialModelImpl;
import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Sphere;

public class DomeModel extends SpatialModelImpl {

	private static final long serialVersionUID = 1L;
	public static final float INIT_SIZE = 1f;


	
	public DomeModel() {
		super();

	}



	public DomeModel(boolean registerSpatial) {
		super(registerSpatial);

	}



	protected Spatial buildSpatial()
	{
		Spatial spatial = new Sphere("Faux Dome", new Vector3f(), 8,8,INIT_SIZE/2f); //Dome("dome", new Vector3f(), 8, 8, INIT_SIZE/2,true);
		spatial.setCullMode(SceneElement.CULL_ALWAYS);
		spatial.setModelBound(new BoundingBox());
		spatial.updateModelBound();

		return spatial;
	}
	


}
