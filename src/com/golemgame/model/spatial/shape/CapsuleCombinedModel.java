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
package com.golemgame.model.spatial.shape;

import com.golemgame.model.spatial.NodeModel;
import com.golemgame.tool.action.Actionable;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;

public class CapsuleCombinedModel extends NodeModel{

	private static final long serialVersionUID = 1L;
	private final UncappedCylinderModel cyl;
	private final DomeModel left;
	private final DomeModel right;
	
	
	public CapsuleCombinedModel() {
		this(false);
		
	}
	
	public void setActionable(Actionable a)
	{
		this.left.setActionable(a);
		this.right.setActionable(a);
		this.cyl.setActionable(a);		
	}
	
	
	
	public CapsuleCombinedModel(boolean register) {
		super();
		
		left = new DomeModel(register);
		right = new DomeModel(register);
		cyl = new UncappedCylinderModel(register);
		
		left.getLocalRotation().fromAngleNormalAxis(-FastMath.HALF_PI, Vector3f.UNIT_Z);
		right.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Z);
		
		super.addChild(left);
		super.addChild(right);
		super.addChild(cyl);
	}
	public UncappedCylinderModel getCyl() {
		return cyl;
	}
	public DomeModel getLeft() {
		return left;
	}
	public DomeModel getRight() {
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
