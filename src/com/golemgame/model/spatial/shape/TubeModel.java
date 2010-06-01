package com.golemgame.model.spatial.shape;

import com.golemgame.model.spatial.SpatialModelImpl;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.CollisionTreeManager;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.shape.SemiTube;
import com.jme.scene.shape.Tube;

public class TubeModel extends SpatialModelImpl{
	private static final long serialVersionUID = 1L;
	public static final float RADIUS = 0.5f;
	public static final float HEIGHT = 1f;
	private SemiTube tube;
	private float arc = 1f;
	private float innerRadius;

	public TubeModel() {
		super();
	
	}

	public TubeModel(boolean registerSpatial) {
		super(registerSpatial);
		
	}
/*
	public void setInnerRadius(float radius)
	{
		tube.setInnerRadius(radius);
	}
	*/
	protected Spatial buildSpatial() {
		this.innerRadius = RADIUS/2f;
		this.arc = FastMath.TWO_PI;
		tube = new SemiTube("cone", RADIUS,RADIUS/2f, HEIGHT,4,32,arc);
		tube.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Z);
	
		tube.setCullMode(SceneElement.CULL_ALWAYS);

		tube.setModelBound(new BoundingBox());
		tube.updateModelBound();
		return tube;
	}

	public void setParameters(float innerRadius, float arc) {
		
		boolean changed = false;
		if(this.arc != arc)
			changed = true;
		if(this.innerRadius != innerRadius)
			changed = true;
		
		if(!changed)
			return;
		
		this.innerRadius = innerRadius;
		this.arc = arc;
		
		tube.updateGeometry(RADIUS, innerRadius,HEIGHT, tube.getAxisSamples(), tube.getRadialSamples(),  arc);
		tube.updateModelBound();
		
		//tube.updateGeometricState(0,true);
		 CollisionTreeManager.getInstance().updateCollisionTree(tube);//this is critically important for colliision detection; otherwise some tube shape changes can result in section so of the tube that do not collide properly.
		 
	}
}
