package com.golemgame.model.spatial;

import com.golemgame.model.Model;



/**
 * This is a node model that forces the children it contains into a well-balanced tree of subnodes,
 * based on their translations.
 * @author Sam
 *
 */
public class NodeTreeModel extends NodeModel {
	private static final long serialVersionUID = 1L;
	private final static int MAXIMUM_LOAD = 5;
	private final static float MAX_DISTANCE = 5;

	private TreeNode root;
	
	
	public NodeTreeModel() {
		super();
		root = new TreeNode(8,4);
		super.addChild(root);
	}

	@Override
	public void addChild(Model child) throws ModelTypeException {
	

		//perform BOUNDING BOX collision detection on the tree node root
		//but stop it at the TreeNode level (so needs custom bounding box routine.
		//also modify the routine so that collision is automatically detected if the translations
		//are closer than max distance.
		
		//if it collides with any node, add it to that node
		//otherwise, add it to root
		
		//tree nodes balance themselves; when you add too many items to them, they split themselves
		//when you remove too many items, they absorb into their parents
		TreeNode bestPlacement = root.getBestPlacement(child);
		if(bestPlacement==null)
		{
			root.addChild(child);
		}else
		{
			bestPlacement.addChild(child);
		}
		
	}

	@Override
	public void detachChild(Model child) throws ModelTypeException {
		if(child.getParent() == this)
			super.detachChild(child);
		if(child.getParent()instanceof TreeNode)
		{
			child.getParent().detachChild(child);
		}else if(child.getParent()!=null)
		{
			child.getParent().detachChild(child);
		}
	}
	/**
	 * If the given child is already in this tree, update it so that it is efficiently placed.
	 * @param child
	 */
	public void updateChild(Model child)
	{
		this.detachChild(child);
		this.addChild(child);
	}
	
	
	
}
