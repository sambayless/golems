package com.golemgame.model.quality.spatial;

import com.jme.renderer.Renderer;
import com.jme.scene.DistanceSwitchModel;
import com.jme.scene.SwitchNode;

/**
 * Allows a floor to be dynamically set on how low the index is allowed to go.
 * This is mainly intended to allow for in game quality settings.
 * @author Sam
 *
 */
public class FlooredDistanceSwitchModel extends DistanceSwitchModel {

	private static final int FRAME_SKIP = 3;
	private int floor = 0;
	
	public int getFloor() {
		return floor;
	}

	public void setFloor(int floor) {
		this.floor = floor;
	}

	@Override
	public int getSwitchChild() {
		int child = super.getSwitchChild();
		return Math.max(child, floor);
	}

	public FlooredDistanceSwitchModel() {
		super();
		
	}

	public FlooredDistanceSwitchModel(int numChildren) {
		super(numChildren);
		
	}
	
	private int count = FRAME_SKIP;
	@Override
	public void render(Renderer r, SwitchNode toSwitch) {
		if(count ++ >= FRAME_SKIP)
		{
			count = 0;
			super.render(r, toSwitch);
		}
		
	}
	
}
