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
