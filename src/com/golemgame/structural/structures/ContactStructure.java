package com.golemgame.structural.structures;

import com.golemgame.functional.WirePort;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.ContactInterpreter;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;


public class ContactStructure extends SphereStructure {

	private static final long serialVersionUID = 1L;
	private WirePort output;
	private WirePort input;
	
	private ContactInterpreter interpreter;
	public ContactStructure(PropertyStore store) {
		super(store);
		interpreter = new ContactInterpreter(store);
		
		output = new WirePort(this,false);
		input = new WirePort(this,true);
	
		output.getModel().getLocalTranslation().y = -0.5f;
		output.getModel().updateWorldData();
		
		input.getModel().getLocalTranslation().y = 0.5f;
		input.getModel().updateWorldData();
		
		
		super.registerWirePort(input);	
	
		super.registerWirePort(output);
		
		getModel().addChild(output.getModel());
		getModel().addChild(input.getModel());
		
	}
	@Override
	public void refresh() {
		super.refresh();
		output.getModel().getLocalTranslation().y = -super.getInterpreter().getRadius();
		input.getModel().getLocalTranslation().y = super.getInterpreter().getRadius();
	}

	
	
}
