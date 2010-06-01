package com.golemgame.model.spatial;

import java.util.ArrayList;
import java.util.List;

import com.golemgame.model.Model;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Vector3f;

public class TreeNode extends NodeModel {

	private static final long serialVersionUID = 1L;
	
	private static final float MAX_DISTANCE = 25;

	private  int minChildren;
	private  int maxChildren;
	
	private TreeNode parent = null;
	private ArrayList<TreeNode> nodes = new ArrayList<TreeNode>(8);

	/**
	 * This can possibly be imroved a bit if needed.
	 * @param maxChildren
	 * @param minChildren
	 */
	
	public TreeNode(int maxChildren, int minChildren) {
		super();
		this.maxChildren = maxChildren;
		this.minChildren = minChildren;
	}

	public List<TreeNode> getNodes() {
		return nodes;
	}
	
	public boolean isLeaf()
	{
		return nodes.isEmpty();
	}
	
	public void attachNodeTreeNode(TreeNode node)
	{
		addChild(node);
		this.nodes.add(node);
	}
	
	public void removeNodeTreeNode(TreeNode node)
	{
		detachChild(node);
		this.nodes.remove(node);
	}
	
	@Override
	public void addChild(Model child) throws ModelTypeException {
		//break this node appart if needed
	
		if(this.getChildren().size()<maxChildren)
		{
			super.addChild(child);
			//this.updateWorldData();
		}
		else
		{
			super.addChild(child);
			//pick the farthest child, find the half closest to it, and make them into a new treenode
			float farthestDistance = -1;
			Vector3f position = this.getCenter();
			Model farthest = null;
			Vector3f farthestPos = null;
			for(Model model:getChildren())
			{
				Vector3f pos;
				float distance =0;
				if(model instanceof SpatialModel)
				{
					
					BoundingVolume bound = ((SpatialModel)model).getSpatial().getWorldBound();
					if (bound!=null)
					{
						pos = bound.getCenter();
					}else
					{
						pos = model.getWorldTranslation();
					}
				}else
				{
					pos = model.getWorldTranslation();
				}
				distance = position.distanceSquared(pos);
				if(distance>farthestDistance)
				{
					farthestPos = pos;
					farthestDistance = distance;
					farthest = model;
				}
			}
	
			//find the distances of each node to the farthest one
			float[] distances = new float[getChildren().size()];
			Model[] models = new Model[getChildren().size()];
			{
				int i = 0;
				for(Model model:getChildren())
				{
					models[i] = model;
					if(model instanceof SpatialModel)
					{
						model.refreshLockedData();
						if(((SpatialModel)model).getSpatial().getWorldBound() ==null)
							((SpatialModel)model).getSpatial().updateWorldBound();
						if(((SpatialModel)model).getSpatial().getWorldBound() !=null)
							distances[i] = ((SpatialModel)model).getSpatial().getWorldBound().getCenter().distanceSquared(farthestPos);
						else
							distances[i] = Float.MAX_VALUE-1;
					}       
					i++;
				}
			}
			TreeNode newNode = new TreeNode(this.maxChildren,this.minChildren);
			
			//find the half of the spatials closest to the farthest one
			
			for (int e = 0;e<maxChildren/2 -1;e++)
			{
				float smallest = Float.MAX_VALUE;
				int bestPick = -1;
				for (int i = 0; i<models.length;i++)
				{
					if(models[i]==farthest)
						continue;
					if(distances[i] < smallest)
					{
						bestPick = i;
						smallest = distances[i];
					}
				}
				if(bestPick== -1)
					break;//just in case
				this.detachChild(models[bestPick]);
				newNode.addChild(models[bestPick]);
				distances[bestPick] = Float.MAX_VALUE;
			}
			this.detachChild(farthest);
			newNode.addChild(farthest);
			
			attachNodeTreeNode(newNode);
			
			
		}
	}

	@Override
	public void detachChild(Model child) throws ModelTypeException {
		//absorb this node into parent if needed.
		super.detachChild(child);
		if(child instanceof TreeNode)
			this.getNodes().remove(child);
		if(this.getChildren().isEmpty())
		{
			if(this.getParent()instanceof TreeNode)//dont delete toplevel treenodes
				this.getParent().detachChild(this);
		}
	}

	public Vector3f getCenter()
	{
		return this.getSpatial().getWorldBound().getCenter();
	}
	
    public TreeNode getBestPlacement(Model scene) {
    	
        if (this.getSpatial().getWorldBound() != null) {
        	Vector3f center = this.getCenter();
        
        	if(scene.getWorldTranslation().distanceSquared(center) < MAX_DISTANCE)
            {
            	return this;
            }else{
            	//find the nearest subNode
            	float shortestDistance = Float.MAX_VALUE;
            	TreeNode closest = null;
            	for(TreeNode node:this.getNodes())
            	{
            		for(TreeNode n:this.getNodes())
            		{
            			float dist =  n.getCenter().distanceSquared(scene.getWorldTranslation());
            			if(dist<shortestDistance)
            			{
            				closest = n;
            				shortestDistance = dist;
            			}
            		}
            		if(closest!=null)
            		{
            			return closest.getBestPlacement(scene);
            		}
            	}
            	
            }
        }
        return null;
    }

	
	
	
	
}
