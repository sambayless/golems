package com.golemgame.model.spatial.rstartree;

import com.golemgame.model.Model;
import com.golemgame.model.spatial.NodeModel;

/**
 * This contains an rtree, but is not part of the rtree itself
 * @author Sam
 *
 */
public class RStarTreeModel extends NodeModel{

	public static final boolean SANITY_CHECK = true;
	private static final long serialVersionUID = 1L;
	private RStarTreeNodeElement root;
	
	public RStarTreeModel() {
		super();
		root = new RStarTreeLeaf(this);
		super.addChild(root);
	}

	@Override
	public void addChild(Model child) throws ModelTypeException {
		root.addChild(child);
	
	}
	
	public int getB()
	{
		return 40;
	}

	@Override
	public void detachChild(Model child) throws ModelTypeException {
		root.detachChild(child);
	}
	
	public void setRoot(RStarTreeNodeElement root)
	{
		super.detachChild(this.root);
		this.root = root;	
		super.addChild(root);
	}

	@Override
	public void detachAllChildren() {
		this.root.detachAllChildren();
	}


	
	@Override
	public String toString() {
		return "R*Tree Model [" + getChildren().size() + "]";
	}
	
}
