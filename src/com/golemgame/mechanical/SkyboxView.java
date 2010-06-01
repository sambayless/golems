package com.golemgame.mechanical;

import java.io.IOException;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SustainedView;
import com.golemgame.mvc.golems.SkyboxInterpreter;
import com.golemgame.states.StateManager;
import com.golemgame.states.camera.skybox.SkyBoxData;
import com.golemgame.states.camera.skybox.SkyBoxDataFactory;

public class SkyboxView implements SustainedView {

	private SkyboxInterpreter interpreter;
	
	
	public SkyboxView(PropertyStore store) {
		super();
		interpreter = new SkyboxInterpreter(store);
		store.setSustainedView(this);
	}

	public void refresh() {
		SkyBoxData skyBox;
		try {
			skyBox = SkyBoxDataFactory.getInstance().construct(getStore());
			StateManager.getCameraManager().getSkyBoxManager().setSkyBox(skyBox);
		} catch (IOException e) {
			StateManager.logError(e);
		}
	}

	public PropertyStore getStore() {
		
		return interpreter.getStore();
	}

	public void remove() {
		
	}

}
