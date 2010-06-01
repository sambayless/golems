package com.golemgame.model.spatial.shape;

import com.golemgame.model.spatial.SpatialModelImpl;
import com.jme.bounding.BoundingBox;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Pyramid;

public class PyramidModel extends SpatialModelImpl{
	private static final long serialVersionUID = 1L;
	public static final float INIT_SIZE = 1f;
	
		
	public PyramidModel() {
		super();
	}


	public PyramidModel(boolean registerSpatial) {
		super(registerSpatial);
	}


	protected Spatial buildSpatial()
	{
		Spatial spatial= new Pyramid("pyramid", INIT_SIZE, INIT_SIZE);// new Box("box", new Vector3f(), INIT_SIZE/2f,INIT_SIZE/2f,INIT_SIZE/2f);		 

		spatial.setModelBound(new BoundingBox());
		spatial.updateModelBound();
		return spatial;
	}
}
