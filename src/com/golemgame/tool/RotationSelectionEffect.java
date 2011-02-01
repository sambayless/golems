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
package com.golemgame.tool;

import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.util.TextureManager;


public class RotationSelectionEffect extends MovementSelectionEffect {

	float currentAngle = 0;
	float angleIncrement = (FastMath.PI/2f);
	
	@Override
	protected Texture getTexture() {
		return TextureManager.loadTexture(this.getClass().getClassLoader().getResource("com/golemgame/data/textures/misc/diskRotate.png"), Texture.MM_LINEAR_LINEAR,Texture.FM_LINEAR,-1,false);

	}

/*	@Override
	protected void doUpdate(float time) {
		// TODO Auto-generated method stub
		super.doUpdate(time);
		//this animation has two parts: 1: rotate from the current base rotation to the given rotation
		//2: spin the base rotation by a given amount
		
		//while this size is < target extent, expand
		if(getElapsedTime()< EXPAND_TIME)
		{
			currentAngle = angleIncrement * getElapsedTime()/EXPAND_TIME;
			
		//	System.out.println(currentAngle + "\t" + getElapsedTime());
		//getModel().getLocalRotation().slerp(startingRotation, targetRotation, getElapsedTime()/EXPAND_TIME);
			Quaternion animationRotation = new Quaternion().fromAngleNormalAxis(currentAngle, Vector3f.UNIT_Y);
			getModel().getLocalRotation().set(animationRotation.mult( getModel().getLocalRotation()));
		}else
		{
		//	getModel().getLocalRotation().set(targetRotation);
			currentAngle = angleIncrement;
			Quaternion animationRotation = new Quaternion().fromAngleNormalAxis(currentAngle, Vector3f.UNIT_Y);
			getModel().getLocalRotation().multLocal(animationRotation);
			currentAngle = 0;
		//	getModel().getLocalRotation().multLocal(animationRotation.mult(EXPAND_TIME));

		}
	}*/



	
}
