package com.golemgame.mvc.golems.validate;

import com.golemgame.mvc.golems.GolemsClassRepository;
import com.golemgame.mvc.golems.OscilloscopeInterpreter;
import com.golemgame.mvc.golems.output.GraphOutputDeviceInterpreter;
import com.golemgame.mvc.golems.validate.requirement.MulteClassStoreValidationRequirement;
import com.golemgame.mvc.golems.validate.requirement.WeakStoreValidationRequirement;

public class OscilloscopeValidator extends PhysicalValidator {

	public OscilloscopeValidator() {
		super();
				
		GraphOutputDeviceInterpreter outputDevice = new GraphOutputDeviceInterpreter();
		
		super.requireData(OscilloscopeInterpreter.OUTPUT_DEVICE, outputDevice.getStore());
		super.addRequirement(new MulteClassStoreValidationRequirement(OscilloscopeInterpreter.OUTPUT_DEVICE, new String[]{GolemsClassRepository.GRAPH_OUTPUT_DEVICE_CLASS,GolemsClassRepository.TEXT_OUTPUT_DEVICE_CLASS,GolemsClassRepository.COLOR_OUTPUT_DEVICE_CLASS},GolemsValidator.getInstance()));


	}


	
	
	
}
