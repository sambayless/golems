/*******************************************************************************
 * Copyright 2008, 2009, 2010 Sam Bayless.
 * 
 *     This file is part of Golems.
 * 
 *     Golems is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     Golems is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with Golems. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.golemgame.functional;


import com.golemgame.model.spatial.SpatialModel;
import com.jme.bounding.BoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Cylinder;

public class WireModel extends SpatialModel
{
	private static final long serialVersionUID = 1L;
//	private static Cylinder baseModel;
	public WireModel(boolean registerForCollision) {
		super();

		if (registerForCollision)
		{
			super.registerSpatial();
			
		}
	}

	
	private Node internalNode;
	private Spatial fullWire;
	private Node outputWire;
	private Node inputWire;
	
	protected Spatial buildSpatial() {
	//	Node node = new Node();
		internalNode = new Node();
		
		
		fullWire = new Cylinder("wire", 5, 5,   0.05f,1f);
		fullWire.setModelBound(new BoundingBox());
		fullWire.updateModelBound();
		fullWire.updateWorldVectors();
		
		
		
		/*outputWire = new Node();
		Cylinder outputCyl = new Cylinder("wire", 5, 5,   0.05f,0.5f);
		outputCyl.setModelBound(new BoundingBox());
		outputCyl.updateModelBound();
		outputCyl.updateWorldVectors();
		outputCyl.getLocalTranslation().z -=outputCyl.getHeight()/2f;
		outputWire.attachChild(outputCyl);
		outputWire.setModelBound(new BoundingBox());
		outputWire.updateModelBound();
		
		inputWire = new Node();
		Cylinder inputCyl = new Cylinder("wire", 5, 5,   0.05f,0.5f);
		inputCyl.setModelBound(new BoundingBox());
		inputCyl.updateModelBound();
		inputCyl.updateWorldVectors();
		inputCyl.getLocalTranslation().z +=outputCyl.getHeight()/2f;
		inputWire.attachChild(inputCyl);
		inputWire.setModelBound(new BoundingBox());
		inputWire.updateModelBound();*/
		
	//	wire.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_X);
		//node.attachChild(wire);
		//node.updateModelBound();
		
		internalNode.attachChild(fullWire);
		internalNode.setModelBound(new BoundingBox());
		internalNode.updateModelBound();
		return internalNode;
	}
	
	public static enum WireType{
		FULL(),INPUT(),OUTPUT();
	}
	private WireType wireType = WireType.FULL;
	public void setWireType(WireType wireType)
	{
		if(this.wireType == wireType)
			return;
		this.wireType = wireType;
		internalNode.detachAllChildren();
		switch(wireType)
		{
			case FULL:
				internalNode.attachChild(fullWire);
				break;
			case INPUT:
				internalNode.attachChild(inputWire);
				break;
			case OUTPUT:
				internalNode.attachChild(outputWire);
				break;
		}
		internalNode.updateModelBound();
	}





	private static final Vector3f negY = new Vector3f(0,-1f,0);
	/**
	 * Position a wire, given a near point and a far point, in coordinates local to the wire.
	 * @param wire
	 * @param nearPoint
	 * @param farPoint
	 */
	public static void connectWire(WireModel wire,Vector3f position1, Vector3f position2)
	{
		
		
		Vector3f middle = position1.add(position2).divideLocal(2f);

		
	
		wire.getLocalScale().setZ(position1.distance(position2));
		
		//this is the direction along the z axis
		Vector3f direction = position2.subtract(position1).normalizeLocal();
		Vector3f cross ;
		if(direction.distanceSquared(Vector3f.UNIT_Y)>FastMath.FLT_EPSILON && direction.distanceSquared(negY) > FastMath.FLT_EPSILON)
		{
			cross= direction.cross(Vector3f.UNIT_Y).normalizeLocal();
		}
		else
		{
			cross= direction.cross(Vector3f.UNIT_Z).normalizeLocal();
		}
	
		wire.getLocalRotation().fromAxes(cross, direction.cross(cross).normalizeLocal(),direction);
		
		wire.setLocalTranslation(middle);
		
		wire.updateWorldData();
		
/*		Vector3f center = nearPoint.add( farPoint).divide(2f);//center in local coords
	
		
		wire.getLocalTranslation().set(center);
		
		Vector3f dir = farPoint.subtract(nearPoint).normalize();

		wire.getLocalRotation().lookAt(dir, Vector3f.UNIT_Y);

		Vector3f zVector = wire.getLocalRotation().multLocal(new Vector3f(Vector3f.UNIT_Z));
		
		wire.getLocalScale().z = (farPoint.dot(zVector) - nearPoint.dot(zVector));
		
		
		
		wire.updateWorldData();*/
	}
	


}
