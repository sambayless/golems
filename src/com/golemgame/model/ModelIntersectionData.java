package com.golemgame.model;

import com.jme.math.Vector3f;

public class ModelIntersectionData {
	private final Model model;
	private final Vector3f intersection;
	public ModelIntersectionData(Model model, Vector3f intersection) {
		super();
		this.model = model;
		this.intersection = intersection;
	}
	public Model getModel() {
		return model;
	}
	public Vector3f getIntersection() {
		return intersection;
	}
	
}
