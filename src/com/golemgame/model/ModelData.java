package com.golemgame.model;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

public class ModelData {
	private final Vector3f localTranslation;
	private final Vector3f localScale;
	private final Quaternion localRotation;
	
	public ModelData(Model model)
	{
		this(model, false);
	}
	
	public ModelData(Model model, boolean worldData)
	{
		if (!worldData)
		{
			localTranslation = new Vector3f(model.getLocalTranslation());
			localScale = new Vector3f(model.getLocalScale());
			localRotation = new Quaternion(model.getLocalRotation());
		}else
		{
			model.updateWorldData();
			localTranslation = new Vector3f(model.getWorldTranslation());
			localScale = new Vector3f(model.getWorldScale());
			localRotation = new Quaternion(model.getWorldRotation());
		}
	}

	public Vector3f getLocalTranslation() {
		return localTranslation;
	}

	public Vector3f getLocalScale() {
		return localScale;
	}

	public Quaternion getLocalRotation() {
		return localRotation;
	}
	
	
}
