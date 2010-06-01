package com.golemgame.model;


public class ModelCollision {

	
	private final Model source;
	private final Model target;
	public ModelCollision(Model source, Model target) {
		super();
		this.source = source;
		this.target = target;
	}
	public Model getSource() {
		return source;
	}
	public Model getTarget() {
		return target;
	}
	
}
