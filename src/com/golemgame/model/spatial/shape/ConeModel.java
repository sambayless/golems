package com.golemgame.model.spatial.shape;

import com.jme.bounding.BoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Cone;

public class ConeModel extends CylinderModel {
	private static final long serialVersionUID = 1L;
	public static final float INIT_SIZE = 1f;
	
	public ConeModel() {
		super();
	}

	public ConeModel(boolean registerSpatial) {
		super(registerSpatial);
	}

	protected Spatial buildSpatial() {
	//	Node node = new Node();
		Spatial	spatial = new Cone("cone",4,8, INIT_SIZE/2,INIT_SIZE);// new Box("built", new Vector3f(), INIT_SIZE/2,INIT_SIZE/2,INIT_SIZE/2);		 
		spatial.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, new Vector3f(1,0,0));
		//Spatial spatial= new Pyramid("pyramid", INIT_SIZE, INIT_SIZE);// new Box("box", new Vector3f(), INIT_SIZE/2f,INIT_SIZE/2f,INIT_SIZE/2f);		 
		spatial.setCullMode(SceneElement.CULL_ALWAYS);
		spatial.getLocalTranslation().zero();
		//spatial.getLocalRotation().loadIdentity();
		spatial.getLocalScale().set(1,1,1);
		spatial.setModelBound(new BoundingBox());
		spatial.updateModelBound();
		return spatial;
	}


}
