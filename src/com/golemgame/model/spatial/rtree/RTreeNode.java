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
package com.golemgame.model.spatial.rtree;

import java.util.ArrayList;
import java.util.List;

import com.golemgame.model.Model;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.model.spatial.SpatialModel;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingCapsule;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.bounding.OrientedBoundingBox;
import com.jme.math.Vector3f;

public class RTreeNode extends NodeModel {

	private static final long serialVersionUID = 1L;


	private  int minChildren;
	private  int maxChildren;
	

	private ArrayList<RTreeNode> nodes = new ArrayList<RTreeNode>(8);

	private int axis = 0;
	


	public RTreeNode(int maxChildren, int minChildren, int axis) {
		super();
		this.axis =axis;
		this.maxChildren = maxChildren;
		this.minChildren = minChildren;
	
	}

	public List<RTreeNode> getNodes() {
		return nodes;
	}
	
	public boolean isLeaf()
	{
		return nodes.isEmpty();
	}
	
	public void attachTreeNode(RTreeNode node)
	{
		addChild(node);
		this.nodes.add(node);
	}
	
	public void removeTreeNode(RTreeNode node)
	{
		detachChild(node);
		this.nodes.remove(node);
	}
	
	

	@Override
	public void addChild(Model child) throws ModelTypeException {
		//break this down into several steps:
		//first: find the best sub-node to add the child to, if this is not a leaf
		//if this IS a leaf, add it to this node
		
		//if this node is full, split it into (some number) of sub-trees; put ALL models in the subtrees.
		if(!isLeaf())
		{
			RTreeNode best = this.getBestPlacement(child);
			best.addChild(child);
		}else
		{
			if(this.getChildren().size()<this.maxChildren)
			{
				super.addChild(child);
			}else{
				//split this node up
				splitNode();
				this.addChild(child);//repeat
			}
		}
	}
	
	public void splitNode()
	{
		//split this node into two sub trees, put all children into those sub trees
		RTreeNode sub1 = new RTreeNode(this.maxChildren,this.minChildren,(this.axis+1)%3);
		RTreeNode sub2 = new RTreeNode(this.maxChildren,this.minChildren,(this.axis+1)%3);
		
		this.getNodes().add(sub1);
		this.getNodes().add(sub2);
		
		
		float mean = getValue(axis, this);
				
		
		int count1 = 0;
		int count2 = 0;
		while(!this.getChildren().isEmpty())
		{
			SpatialModel child = (SpatialModel) this.getChildren().iterator().next();
			super.detachChild(child);
			//to handle the situation where all children are in the same spot
			if((getValue(axis, child)>=mean) && (count1<this.maxChildren - 2) || (count2 > this.maxChildren-2))
			{
				sub1.addChild(child);
				count1++;
			}else
			{
				sub2.addChild(child);
				count2++;
			}
		}
		super.addChild(sub1);
		super.addChild(sub2);
	//	sub1.updateWorldData();
	//	sub2.updateWorldData();
	}

	private final static Vector3f _store = new Vector3f();
	
	/**
	 * Return the position (xMin, yMin, zMin, xMax, yMax, zMax) corresponding to the given type
	 * @param type
	 * @return
	 */
	protected static float getValue(int type, SpatialModel model)
	{
		Vector3f center;
		if(model.getWorldBound()==null)//might be important to investigate the source of this glitch later
			center = model.getSpatial().getWorldTranslation();
		else
			center = model.getWorldBound().getCenter(_store);

		
		switch (type%3)
		{
		case 0:
			return center.x;
		case 1:
			return center.y;
		case 2:
			return center.z;
			
		}
		return 0;
	}
	
	@Override
	public void detachChild(Model child) throws ModelTypeException {
		//absorb this node into parent if needed.
		super.detachChild(child);
		if(child instanceof RTreeNode)
			this.getNodes().remove(child);
		if(this.getChildren().isEmpty())
		{
			if(this.getParent()instanceof RTreeNode)//dont delete toplevel treenodes
				this.getParent().detachChild(this);
		}
		this.getSpatial().updateWorldBound();
	}

	public Vector3f getCenter()
	{
		return this.getWorldBound().getCenter();
	}
	
    public RTreeNode getBestPlacement(Model scene) {

            	//find the nearest subNode
            	float cheapest = Float.MAX_VALUE;
            	RTreeNode best = this.getNodes().get(0);
            
            	for(RTreeNode n:this.getNodes())
            	{
            		float cost =	n.costToAdd(scene);
            		if(cost<cheapest)
            		{
            			cheapest = cost;
            			best = n;
            		}
            	}           	
            return best;

    }
	private static final Vector3f _center = new Vector3f();
	private static final BoundingBox _storeBox = new BoundingBox();
	private static final BoundingSphere _storeSphere = new BoundingSphere();
	private static final BoundingCapsule _storeCapsule = new BoundingCapsule();
	private static final OrientedBoundingBox _storeOBB = new OrientedBoundingBox();
	
	public float costToAdd(Model model)
	{
		BoundingVolume bound = this.getWorldBound();
		BoundingVolume modelBound = model.getWorldBound();
		if( modelBound == null)
			return Float.MAX_VALUE-1;
		else if (bound == null)
		{
			return modelBound.getVolume();
		}else{
			//simple implementation
			//return bound.getCenter(_center).distanceSquared(model.getWorldTranslation());	
			float v = bound.getVolume();
			BoundingVolume cloneBound;
			if(bound instanceof BoundingBox)
				cloneBound = bound.clone(_storeBox);		
			else if(bound instanceof BoundingSphere)
					cloneBound = bound.clone(_storeSphere);
			else if(bound instanceof BoundingCapsule)
				cloneBound = bound.clone(_storeCapsule);
			else if(bound instanceof OrientedBoundingBox)
				cloneBound = bound.clone(_storeOBB);
			else
				cloneBound = bound.clone(null);
			
			cloneBound.mergeLocal(modelBound);
			
			return cloneBound.getVolume()-v;
		}
			
			
		
		
	}
	
	public int count()
	{
		int total = 1;
		for (Model child:this.getChildren())
		{
			if (child instanceof RTreeNode)
			{
				total += ((RTreeNode)child).count();
			}
		}
		return total;
	}
	
	

}
