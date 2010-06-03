package com.golemgame.model.spatial.shape;

import com.golemgame.model.spatial.SpatialModelImpl;
import com.jme.bounding.BoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.state.RenderState;

public class GearTooth extends SpatialModelImpl
{
	private static final long serialVersionUID = 1L;

	private Node toothNode;
	
	private Box leftBox;
	private Box rightBox;
	private Box topBox;
	
	public GearTooth() {
		super(true);

	}

	public void setHasSlopes(boolean hasSlopes)
	{
		if(hasSlopes)
		{
			if(getLeftBox().getParent()!=toothNode)
				toothNode.attachChild(getLeftBox());
			if(getRightBox().getParent()!=toothNode)
				toothNode.attachChild(getRightBox());
		}else
		{
			getLeftBox().removeFromParent();
			getRightBox().removeFromParent();
		}
	}
	
	public void setHasWidth(boolean hasWidth)
	{
		if(hasWidth)
		{
			if(getTopBox().getParent()!=toothNode)
				toothNode.attachChild(getTopBox());
		
		}else
		{
			getTopBox().removeFromParent();
		}
	}
	@Override
	public void applyRenderState(RenderState state, int position) {
		if(state.getType()==RenderState.RS_TEXTURE)//skip texture application
			return;
		else
			super.applyRenderState(state, position);
	}
	@Override
	protected Spatial buildSpatial() {
		
		toothNode = new Node();
		setLeftBox(new Box("", new Vector3f(), 0.5f,0.5f,0.5f));
		setRightBox(new Box("", new Vector3f(), 0.5f,0.5f,0.5f));
		setTopBox(new Box("", new Vector3f(), 0.5f,0.5f,0.5f));
		
		toothNode.attachChild(getLeftBox());
		getLeftBox().updateRenderState();
		toothNode.attachChild(getRightBox());
		getRightBox().updateRenderState();
		toothNode.attachChild(getTopBox());
		getTopBox().updateRenderState();
		toothNode.setModelBound(new BoundingBox());
		toothNode.updateModelBound();
		toothNode.updateRenderState();
		return toothNode;
	}
	
	public void copyFrom(GearTooth from)
	{
		toothNode.getLocalScale().set(from.getLocalScale());
		getLeftBox().getLocalTranslation().set(from.getLeftBox().getLocalTranslation());
		getLeftBox().getLocalScale().set(from.getLeftBox().getLocalScale());
		getLeftBox().getLocalRotation().set(from.getLeftBox().getLocalRotation());
		getRightBox().getLocalTranslation().set(from.getRightBox().getLocalTranslation());
		getRightBox().getLocalScale().set(from.getRightBox().getLocalScale());
		getRightBox().getLocalRotation().set(from.getRightBox().getLocalRotation());
		getTopBox().getLocalTranslation().set(from.getTopBox().getLocalTranslation());
		getTopBox().getLocalScale().set(from.getTopBox().getLocalScale());
		getTopBox().getLocalRotation().set(from.getTopBox().getLocalRotation());
	}
	
	/**
	 * Angle is TWICE the angle of each angled edge section.
	 * @param height
	 * @param width
	 * @param angle
	 */
	public void rebuild(float height, float width, float angle)
	{
		//this is all assumed to be lying on its side - so the y scales for everything should always be 1.
		
		//the top box is easy...
		getTopBox().getLocalScale().set(width, 1f, height );
	
		if(angle>FastMath.HALF_PI)
			angle = FastMath.HALF_PI;
		if(angle>0 )
		{
			if(getLeftBox().getParent()!=toothNode)
				toothNode.attachChild(getLeftBox());
			if(getRightBox().getParent()!=toothNode)
				toothNode.attachChild(getRightBox());
			
			float phi = FastMath.HALF_PI-angle/2f; //this is the angle between the 'floor' of the tooth and the left box's left edge
			
			float l;//this is the LENGTH of the left side of the tooth
			
			l = height/FastMath.sin(phi);
			
			float l2 = FastMath.sqrt(l*l - height*height );
			
			float q = l2*FastMath.cos(angle/2f);
			
			//the left side length is l; this becomes the z scale of the left box.
			//the bottom length of the box is q; this becomes the x scale of the left box.
			//the angle of rotation of the box, around the y axis, is angle/2f.
			
			getLeftBox().getLocalRotation().fromAngleNormalAxis(angle/2f, Vector3f.UNIT_Y);
			getRightBox().getLocalRotation().fromAngleNormalAxis(-angle/2f, Vector3f.UNIT_Y);
			
			getLeftBox().getLocalScale().set(q,1f,l);
			getRightBox().getLocalScale().set(q,1f,l);
			
			
			float h2 = FastMath.sin(angle/2f)*q;
			
			float totalHeight = h2+height;
			float yPos = height - (totalHeight/2f);
			
			float totalWidth = l2 + FastMath.cos(angle/2f) * q;
			
			float xPos = l2 - totalWidth/2f;
			
			getLeftBox().getLocalTranslation().set(-xPos-width/2f,0,yPos);
			getRightBox().getLocalTranslation().set(xPos+width/2f,0,yPos);
			getTopBox().getLocalTranslation().set(0,0f,height/2f );
	
		}else
		{
			getTopBox().getLocalTranslation().set(0,0f,height/2f );
		
			getLeftBox().removeFromParent();
			getRightBox().removeFromParent();
		}
		///translate the left and right boxes 
	}

	public void setTopBox(Box topBox) {
		this.topBox = topBox;
	}

	public Box getTopBox() {
		return topBox;
	}

	public void setLeftBox(Box leftBox) {
		this.leftBox = leftBox;
	}

	public Box getLeftBox() {
		return leftBox;
	}

	public void setRightBox(Box rightBox) {
		this.rightBox = rightBox;
	}

	public Box getRightBox() {
		return rightBox;
	}
	
}