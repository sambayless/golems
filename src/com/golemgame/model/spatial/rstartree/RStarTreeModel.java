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
