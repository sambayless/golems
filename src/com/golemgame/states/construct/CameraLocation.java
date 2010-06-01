package com.golemgame.states.construct;

import com.golemgame.states.GeneralSettings;
import com.golemgame.states.StateManager;
import com.golemgame.tool.ActionToolSettings;
import com.jme.math.Vector3f;

public class CameraLocation implements ConstructionLocation {

	private float distanceFromCamera = 25;
	
	public void getBuildLocation(Vector3f store) {
		
		Vector3f camNormal = StateManager.getCameraManager().getCameraNormal();
	
		Vector3f camPosition = StateManager.getCameraManager().getCameraLocationNode().getWorldTranslation();
		//float zoom = StateManager.getCameraManager().getCameraZoom();

		float minDist = (ActionToolSettings.getInstance().getGridUnits().getValue().length());
		if(StateManager.getCameraManager().getCameraZoom() - distanceFromCamera>minDist)
		{
			store.set(camPosition).addLocal(camNormal.mult(-distanceFromCamera));
		}else{
			store.set(StateManager.getCameraManager().getCameraPosition()).addLocal(camNormal.mult(minDist));
		}
		
	}

}
