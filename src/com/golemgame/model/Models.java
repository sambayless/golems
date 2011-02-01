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
package com.golemgame.model;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

public final class Models 
{
	/**
	 * Attach a model to a parent model, preserving the world translation,rotation, and scale of the model in the process.
	 * @param toAttach
	 * @param attachTo
	 */
	public static void attachPreservingWorld(Model toAttach, ParentModel attachTo)
	{
		toAttach.updateWorldData();
		attachTo.updateWorldData();
		Vector3f worldTranslation = new Vector3f(toAttach.getWorldTranslation());
	//	Vector3f worldScale = new Vector3f(toAttach.getWorldScale());//do later
		Quaternion worldRotation = new Quaternion(toAttach.getWorldRotation());
		
		attachTo.addChild(toAttach);
		toAttach.updateWorldData();

		attachTo.worldToLocal(worldTranslation, toAttach.getLocalTranslation());
		toAttach.getLocalRotation().set(attachTo.getWorldRotation().inverse()).multLocal(worldRotation);
		
		toAttach.updateWorldData();
	}
	
	
	/**
	 * Attach a model to a parent model, preserving the world translation,rotation, and scale of the model in the process.
	 * @param toAttach
	 * @param attachTo
	 */
	public static void removeFromParentPreservingWorld(Model toRemove)
	{
		
		ParentModel removeFrom = toRemove.getParent();
		removeFrom.updateWorldData();
		toRemove.updateWorldData();
		Vector3f worldTranslation = new Vector3f(toRemove.getWorldTranslation());
		Vector3f worldScale = new Vector3f(toRemove.getWorldScale());
		Quaternion worldRotation = new Quaternion(toRemove.getWorldRotation());
		
		removeFrom.detachChild(toRemove);
	
		
		toRemove.getLocalTranslation().set(worldTranslation);
		toRemove.getLocalRotation().set(worldRotation);
		toRemove.getLocalScale().set(worldScale);
		toRemove.updateWorldData();
	}
}
