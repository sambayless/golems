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
