package com.golemgame.model.spatial.shape;

import com.golemgame.model.spatial.SpatialModelImpl;
import com.jme.bounding.BoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Cylinder;

public class CameraModel extends SpatialModelImpl{
	private static final long serialVersionUID = 1L;
	
	public CameraModel() {
		super();
		
	}

	public CameraModel(boolean registerSpatial) {
		super(registerSpatial);
	
	}

	protected Spatial buildSpatial() {
		Node cameraNode = new Node();
		
		Box main = new Box("Camera", new Vector3f(), 0.7f,0.4f,0.3f);
		
		cameraNode.attachChild(main);
		
		Cylinder front = new Cylinder("CameraFront", 6, 6, 0.2f,0.2f, true);
		front.getLocalTranslation().x += main.xExtent + front.getHeight()/2f - 0.01f;
		cameraNode.attachChild(front);			
		front.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Y);
		
		
		
		
		
		cameraNode.setModelBound(new BoundingBox());
		cameraNode.updateModelBound();
		cameraNode.setCullMode(SceneElement.CULL_ALWAYS);
		return cameraNode;
	}
}
