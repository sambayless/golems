package com.golemgame.model.spatial.shape;

import com.golemgame.model.spatial.NodeModel;
import com.jme.bounding.BoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.state.RenderState;

public class CapsuleCombinedFacade extends NodeModel{

	private static final long serialVersionUID = 1L;
	private final UncappedCylinderFacade cyl;
	private final DomeFacade left;
	private final DomeFacade right;
	
	public CapsuleCombinedFacade() {
		this(false);
		
	}
	public CapsuleCombinedFacade(boolean register) {
		super();
		
		left = new DomeFacade(register);
		
		right = new DomeFacade(register);
		left.invertTexture(true);//to make gradient textures work nicely.
		cyl = new UncappedCylinderFacade(register);
		
		left.getLocalRotation().fromAngleNormalAxis(-FastMath.HALF_PI, Vector3f.UNIT_Z);
		right.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Z);
		left.getLocalRotation().multLocal(new Quaternion().fromAngleNormalAxis(FastMath.PI, Vector3f.UNIT_Y));
		super.addChild(left);
		super.addChild(right);
		super.addChild(cyl);
		
		//if you dont include this than in certain circumstances the bounding box is incorrect and parts
		//of the capsule occasionally disappear.
		super.getSpatial().setModelBound(new BoundingBox());
		super.getSpatial().updateModelBound();
		super.getSpatial().updateGeometricState(0, true);
	}
	
	@Override
	public void applyRenderState(RenderState state, int position) {
		if(position == 0)
		{
			
			
				cyl.applyRenderState(state,0);
			
			
		}else if (position == 1)
		{
		
				left.applyRenderState(state, 0);			
			
		}else if (position == 2)
		{
			right.applyRenderState(state, 0);
		}
		this.getSpatial().updateRenderState();
	}

	public int getNumberOfPositions()
	{
		return 3;
	}

	
	public UncappedCylinderFacade getCyl() {
		return cyl;
	}
	public DomeFacade getLeft() {
		return left;
	}
	public DomeFacade getRight() {
		return right;
	}
	
	public void rebuild(float radius, float height) {
		
		float doubleRadius = radius*2f;
		if(height <doubleRadius)
			height = doubleRadius;
		
		
		if(height<= doubleRadius)
		{
			cyl.detachFromParent();
		}else{
			cyl.getLocalScale().z = height-doubleRadius;
			cyl.getLocalScale().x = doubleRadius;
			cyl.getLocalScale().y = doubleRadius;
			if(cyl.getParent()!=this)				
				this.addChild(cyl);			
		}

		
		left.getLocalScale().set(doubleRadius, doubleRadius, doubleRadius);
		right.getLocalScale().set(doubleRadius, doubleRadius, doubleRadius);
		
		left.getLocalTranslation().x = (height)/2f-radius;
		right.getLocalTranslation().x = -  (height)/2f+radius;
		updateWorldData();
		
	}
	
}
