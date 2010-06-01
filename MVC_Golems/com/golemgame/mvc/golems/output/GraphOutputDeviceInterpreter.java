package com.golemgame.mvc.golems.output;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GolemsClassRepository;
import com.golemgame.mvc.golems.output.OutputDeviceInterpreter.OutputType;

public class GraphOutputDeviceInterpreter extends OutputDeviceInterpreter{

	//note: Unusually, these keys are enumerated in the device interpreter, not here


	public GraphOutputDeviceInterpreter() {
		this(new PropertyStore());
	}
	
	public GraphOutputDeviceInterpreter(PropertyStore store) {
		super(store);	
		store.setClassName(GolemsClassRepository.GRAPH_OUTPUT_DEVICE_CLASS);
		super.setNumberOfInputs(1);
		super.setDeviceType(OutputType.GRAPH);
	}
	
}
