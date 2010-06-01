
package com.golemgame.structural.structures;

import com.golemgame.functional.WirePort;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.TouchSensorInterpreter;


public class TouchSensor extends BoxStructure   {
	private static final long serialVersionUID = 1L;
	private WirePort output;

	private TouchSensorInterpreter interpreter;

	public TouchSensor(PropertyStore store) {
		super(store);
		this.interpreter = new TouchSensorInterpreter(store);

		output = new WirePort(this,false);

		output.getModel().getLocalTranslation().y = 0.5f;
		output.getModel().updateWorldData();
		
		super.registerWirePort(output);
		getModel().addChild(output.getModel());
	}


	
	
	public boolean isMindful() {
		return true;
	}
	
	


	
	@Override
	public void refresh() {
		super.refresh();
		output.getModel().getLocalTranslation().y = super.getInterpreter().getExtent().y;
	}


}
