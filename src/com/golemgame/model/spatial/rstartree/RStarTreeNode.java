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
package com.golemgame.model.spatial.rstartree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import com.golemgame.model.Model;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.model.spatial.SpatialModel;
import com.golemgame.states.StateManager;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingCapsule;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.bounding.OrientedBoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;

public class RStarTreeNode extends NodeModel implements RStarTreeNodeElement{

	private static final long serialVersionUID = 1L;
	

	
	private RStarTreeModel rootModel;
	
	public RStarTreeNode(RStarTreeModel rootModel) {
		super();
		this.rootModel = rootModel;
	}

	public void forceAddChild(Model child)
	{
		super.addChild(child);
	}
	
	public void forceDetachChild(Model child)
	{
		super.detachChild(child);
	}
	

	@Override
	public void addChild(Model toInsert) throws ModelTypeException {
		//find the cheapest child to add this to, and do so
		StateManager.getGame().lock();
		try{
			float minCost = Float.POSITIVE_INFINITY;
			RStarTreeNodeElement bestChoice = null;
			
			//if the children are elements, then just insert a new one if you can.
	
			for (Model model:this.getChildren())
			{
			/*	if (!( model instanceof RTreeNodeElement))
					continue;*/
				RStarTreeNodeElement child = (RStarTreeNodeElement) model;
				float cost = child.evaluateCost(toInsert);
				if(cost<=minCost)
				{
					minCost = cost;
					bestChoice = child;
				}
			}
			
			if (bestChoice == null)
				throw new IllegalStateException("RTreeNode has no children");
	
			
			bestChoice.addChild(toInsert);
			
			if (RStarTreeModel.SANITY_CHECK)
			{
				if (toInsert.getParent() == null)
					throw new IllegalStateException();
			}
			
		}finally{
			StateManager.getGame().unlock();
		}
	
	}
	
	
	
	@Override
	public void detachAllChildren() {
	
	}

	


	@Override
	public void detachChild(Model child) throws ModelTypeException {
		if (child instanceof RStarTreeNodeElement)
			super.detachChild(child);
		else
		{
			child.detachFromParent();
		}
	}



	public void insertNode(RStarTreeNodeElement node)
	{
		StateManager.getGame().lock();
		try{
			if(this.getChildren().contains(node))
				return;
			
			if(this.getChildren().size()< rootModel.getB())
			{
				super.addChild(node);
			}
			else
			{
				//take some of the elements of this node (including the new rect), and insert them into a new node
				//then insert that new node into this parent
				
				RStarTreeNode splitNode = new RStarTreeNode(this.rootModel);
				
				if(getParent() instanceof RStarTreeNode)			
				{
					((RStarTreeNode)getParent()).insertNode(splitNode);
					
				}else if (getParent() instanceof RStarTreeModel)
				{
					RStarTreeNode newParent = new RStarTreeNode(this.rootModel);
					((RStarTreeModel)getParent()).setRoot(newParent);
				
					newParent.insertNode(this);
					newParent.insertNode(splitNode);
				}else
					throw new IllegalStateException();
				
				//divide the children, including the new node, between this node and the split as well as possible
				
				/*
				 * If an insertion causes an overflow, calculate the distance from the
center of each of the B + 1 entries to the center of the MBR enclosing all B + 1 entries.
Then sort the entries in decreasing order of this distance. Remove the p furthest entries,
where p is set to 30% of B, and re-insert the p removed entries into the tree. Some subset
of the p re-inserts may be inserted into nodes other than the initial node that overflowed.
For each of the p re-inserts, if they do not cause an overflow, do nothing; otherwise, split
the node using the algorithm below.

The third difference is in the node splitting algorithm. When a split is needed, the node
entries are first sorted twice along each of the d dimensions. The two sorts are based on the
low and on the high MBR endpoint values, respectively. Remember that nodes must have
a minimum of b and a maximum of B entries. Thus, using one of the sorted lists, the B +1
entries can be partitioned into two groups, S1 and S2, by splitting anyplace after the i-th
entry, b ≤ i ≤ B−b+1, of the sorted list. S1 and S2 consist of the entries before and after the
split position, respectively. In order to choose the best split, the following three objective
functions were considered (for 2-d data) and tested using different combinations:
1. area-value =area(MBR(S1)) + area(MBR(S2))
2. perimeter-value = perimeter(MBR(S1)) + perimeter(MBR(S2))
3. overlap-value = area(MBR(S1) ∩ MBR(S2))
Notice that for a fixed area, the MBR with smallest perimeter is the square.
Based on experiments, the following split policy is adopted. The R* tree computes the
perimeter-values for each possible grouping (S1, S2) over both sorted lists of all dimensions
and chooses the dimension that yields the minimum perimeter-value. Once the dimension
has been chosen, the algorithm then chooses the grouping for that dimension that minimizes
the overlap-value.
				 */
				
				//splitNode.insertNode(node);
				
				ArrayList<Model> children = new ArrayList<Model>(getChildren());
				children.add(node);
				Iterator<Model> iterator = children.iterator();
				while(iterator.hasNext())
				{
					if (iterator.next() == null)
						iterator.remove();
				}
				//first, attempt to kick out the farthest 30% of the nodes, including the new node.
				//this introduces recursive complications - forget it for now.
				
				//sort the children on each x, y, z dimension by the left most and rightmost box position (or top, bottom, or front, back).
				 ArrayList<Model> bestS1 = new ArrayList<Model>();
				 ArrayList<Model> bestS2 = new ArrayList<Model>();
				 
				 float bestCost = Float.POSITIVE_INFINITY;
				 
				 
				 
				for (BoundComparator.CompareMode compareMode:BoundComparator.CompareMode.values())
				{
					
					boundComparator.setCompareMode(compareMode);
					
					Collections.sort(children,boundComparator);
					
					//The children can be split into any two groups such that both have between b/2 and b elements.
					//could try all these combinations... for now lets just cut it in half and leave it at that. (because were keeping b at B/2... might change that later)
					
					children.get(0).getWorldBound().clone(perimterComputer);
					
					//compute perimeter for S1:
					for (int i = 1;i<children.size()/2;i++)
						perimterComputer.mergeLocal(((SpatialModel) children.get(i)).getWorldBound());
					//minimize the extents of the box
					float cost = perimterComputer.xExtent + perimterComputer.yExtent + perimterComputer.zExtent;
					
					((SpatialModel) children.get(children.size()/2)).getWorldBound().clone(perimterComputer);
					
					//compute perimeter for S1:
					for (int i = children.size()/2 + 1;i<children.size();i++)
						perimterComputer.mergeLocal(((SpatialModel) children.get(i)).getWorldBound());
					
					cost += perimterComputer.xExtent + perimterComputer.yExtent + perimterComputer.zExtent;
					
					if (cost<bestCost)
					{
						bestCost = cost;
						bestS1.clear();
						bestS2.clear();
						for (int i = 0;i<children.size()/2;i++)
							bestS1.add(children.get(i));
						for (int i = children.size()/2;i<children.size();i++)
							bestS2.add(children.get(i));
					}
				}
				
				for (Model model:bestS2)
				{
					this.forceDetachChild(model);
					splitNode.forceAddChild((RStarTreeNodeElement)model);
				}
				
				if (bestS1.contains(node))
				{//at most, add this one to this node
					this.forceAddChild((RStarTreeNodeElement)node);
				}
				
			}
			
			if (RStarTreeModel.SANITY_CHECK)
			{
				if (node.getParent() == null || this.getParent() == null)
					throw new IllegalStateException();
			}
		}finally{
			StateManager.getGame().unlock();
		}
	}
	
/*	@Override
	public void detachChild(Model child) throws ModelTypeException {
		
		super.detachChild(child);
	}*/
	
	public void deleteAndRedistribute(RStarTreeNodeElement element)
	{
		StateManager.getGame().lock();
		try{

			Collection<Model> children = new ArrayList<Model>(element.getChildren());
	
			if (this.getChildren().size()> 1)
			{
				//redistribute the children of the removed element among its neighbours, if it has them
				super.detachChild(element);
						element.detachAllChildren();
				for(Model child:children)
					rootModel.addChild(child);
			}
			
			if (this.getChildren().size()<rootModel.getB()/2)
			{
				if (getParent() instanceof RStarTreeNode)
					((RStarTreeNode)getParent()).deleteAndRedistribute(this);
				else if (this.getChildren().isEmpty())
				{//if this is the top of the tree, and it has no children, kill it and make the top of the tree a new leaf
					((RStarTreeModel) getParent()).setRoot(new RStarTreeLeaf(this.rootModel));
				}
			}
			if (RStarTreeModel.SANITY_CHECK)
			{
				for (Model child:children)
				{
					if(child.getParent() == null)
						throw new IllegalStateException();
				}
			}
		}finally{
			StateManager.getGame().unlock();
		}
	}
	
	private static final BoundingBox _storeBox = new BoundingBox();
	private static final BoundingSphere _storeSphere = new BoundingSphere();
	private static final BoundingCapsule _storeCapsule = new BoundingCapsule();
	private static final OrientedBoundingBox _storeOBB = new OrientedBoundingBox();
	
	public float evaluateCost(Model model) {
		if (this.getChildren().isEmpty())
			return 0;
		BoundingVolume thisBound = this.getWorldBound();
		BoundingVolume targetBound = model.getWorldBound();
		if( targetBound == null)
		{
			if(thisBound == null)
				return 0;
			else 
				return thisBound.getVolume();
		}
		else if (thisBound == null)
		{
			return targetBound.getVolume();
		}else{
			//simple alternative implementation
			//return bound.getCenter(_center).distanceSquared(model.getWorldTranslation());	
			float v = thisBound.getVolume();
			BoundingVolume cloneBound;
			if(thisBound instanceof BoundingBox)
				cloneBound = thisBound.clone(_storeBox);		
			else if(thisBound instanceof BoundingSphere)
					cloneBound = thisBound.clone(_storeSphere);
			else if(thisBound instanceof BoundingCapsule)
				cloneBound = thisBound.clone(_storeCapsule);
			else if(thisBound instanceof OrientedBoundingBox)
				cloneBound = thisBound.clone(_storeOBB);
			else
				cloneBound = thisBound.clone(null);
			
			cloneBound.mergeLocal(targetBound);
			
			return cloneBound.getVolume()-v;
		}
	}

	
	private static final BoundingBox perimterComputer = new BoundingBox();
	
	private static final BoundComparator boundComparator = new BoundComparator();
	
	private static class BoundComparator implements Comparator<Model> 
	{
		public enum CompareMode
		{
			Left, Right, Front,Back,Top,Bottom;
		}
		
		private CompareMode compareMode = CompareMode.Left;
		
		private static final Vector3f _store1 = new Vector3f();
		private static final Vector3f _store2 = new Vector3f();		
		
		public void setCompareMode(CompareMode compareMode) {
			this.compareMode = compareMode;
		}
		
		private static final BoundingBox _storeBox1 = new BoundingBox();
		private static final BoundingBox _storeBox2 = new BoundingBox();
		
		public int compare(Model o1, Model o2) {
			//these MUST be bounding boxes!
			//if these are not BOTH bounding boxes, then clone it
			BoundingVolume boundV1 =  o1.getWorldBound();
			BoundingVolume boundV2 = o2.getWorldBound();
			if (boundV1 == null || boundV2 == null)
				return 0;
			
			BoundingBox bound1 ;
			BoundingBox bound2;
			if (boundV1 instanceof BoundingBox)
			{
				bound1 =(BoundingBox) boundV1;
			}else
			{
				bound1 = _storeBox1;
				boundV1.clone(_storeBox1);
			}
			
			if (boundV2 instanceof BoundingBox)
			{
				bound2 =(BoundingBox) boundV2;
			}else
			{
				bound2 = _storeBox2;
				boundV2.clone(_storeBox2);
			}
			
			
		
			Vector3f center1 = bound1.getCenter(_store1);
			Vector3f center2 = bound1.getCenter(_store2);
			
			switch(compareMode)
			{
				case Left:				
					center1.x -= bound1.xExtent;
					center2.x -= bound2.xExtent;
					return (int) FastMath.sign(center1.x - center2.x);
				
				case Right:				
					center1.x += bound1.xExtent;
					center2.x += bound2.xExtent;
					return (int) FastMath.sign(center1.x - center2.x);
					
				case Top:				
					center1.y += bound1.yExtent;
					center2.y += bound2.yExtent;
					return (int) FastMath.sign(center1.y - center2.y);
					
				case Bottom:				
					center1.y -= bound1.yExtent;
					center2.y -= bound2.yExtent;
					return (int) FastMath.sign(center1.y - center2.y);
					
				case Front:				
					center1.z += bound1.zExtent;
					center2.z += bound2.zExtent;
					return (int) FastMath.sign(center1.z - center2.z);
					
				case Back:				
					center1.z -= bound1.zExtent;
					center2.z -= bound2.zExtent;
					return (int) FastMath.sign(center1.z - center2.z);
			}
			return 0;
		}
	
	}
	


	@Override
	public String toString() {
		return "R*Tree Node [" + getChildren().size() + "]";
	}
	
	
}
