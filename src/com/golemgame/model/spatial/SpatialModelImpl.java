package com.golemgame.model.spatial;



public abstract class SpatialModelImpl extends SpatialModel {
	private static final long serialVersionUID = 1L;

	public SpatialModelImpl() {
		super();
	}

	public SpatialModelImpl(boolean registerSpatial) {
		super();
		if (registerSpatial)
			registerSpatial();
	}



}
