package com.golemgame.states.camera;

import com.jme.scene.Node;

public class NullCentroidDisplay implements CentroidDisplay{
	
	private Node centroidNode = new Node();
	public NullCentroidDisplay() {
		super();
		
	}

	public void display() {
		
	}
	
	public void update()
	{
	
		
	}

	public Node getCentroidNode() {
		return centroidNode;
	}
	
}
