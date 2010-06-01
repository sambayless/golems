package com.golemgame.structural.structures;

import java.util.Collection;

import com.golemgame.functional.WirePort;
import com.golemgame.model.Model;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.RocketInterpreter;
import com.golemgame.mvc.golems.RocketPropellantInterpreter;
import com.golemgame.properties.Property;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;


public class RocketStructure extends CylinderStructure implements RocketProperties {
	private static final long serialVersionUID = 1L;
	

	public static final float MAX_LINEAR_VELOCITY = 300;

	private WirePort input;

	private RocketControllableImpl controller;
	
	private RocketPropellentProperties propellentProperties= null;
	
	
	private RocketInterpreter interpreter;
	public RocketStructure(PropertyStore store) {
		super(store);
		this.interpreter = new RocketInterpreter(store);
		input = new WirePort(this,true);
		input.getModel().getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Z);
		super.registerWirePort(input);
		this.getModel().addChild(input.getModel());
		//this.controller.setInputWirePort(input.getModel());
	
	}

	public boolean isMindful() {
		return true;
	}
	
	protected ControllableImpl buildController(Model mainModel, Model facade) {
		controller= new RocketControllableImpl(mainModel, facade, this);
		return controller;
	}


	@Override
	public Collection<Property> getPropertySet() {
		 Collection<Property> properties = super.getPropertySet();
		 properties.add(new Property(Property.PropertyType.ROCKET,this.interpreter.getStore()));
		 properties.add(new Property(Property.PropertyType.ROCKET_EFFECTS,this.interpreter.getPropellantProperties()));
		 properties.add(new Property(Property.PropertyType.PARTICLE_EFFECTS,this.interpreter.getPropellantProperties().getPropertyStore(RocketPropellantInterpreter.PARTICLE_EFFECTS)));
		 
		 return properties;
	}

	
	public float getMaxForce() {
	
		return interpreter.getMaxAcceleration();
	}


	
	public void setMaxForce(float maxAcceleration) {
		interpreter.setMaxAcceleration(maxAcceleration);
		
	}




	@Override
	public void refresh() {
		super.refresh();
		
		if (propellentProperties == null || !propellentProperties.getStore().equals(interpreter.getPropellantProperties()))
		{
			propellentProperties = new RocketPropellentProperties(interpreter.getPropellantProperties());
			propellentProperties.refresh();
		}
		
		//System.out.println(input.getModel().getLocalTranslation());
		this.input.getModel().getLocalTranslation().x = -interpreter.getHeight()/2f;
		input.getModel().updateWorldData();
		//System.out.println(input.getModel().getLocalTranslation());
		this.input.refresh();
	}

	public RocketPropellentProperties getPropellentProperties() {
		return propellentProperties;
	}
	
	
	public static class RocketControllableImpl extends ControllableImpl  
	{
		private static final long serialVersionUID = 1L;
	/*	private Model inputWirePort;
		public void setInputWirePort(Model inputWirePort) {
			this.inputWirePort = inputWirePort;
		}
*/
		private Model mainModel;
		public RocketControllableImpl(Model mainModel, Model facade,
				RocketStructure owner) {
			super(mainModel, facade, owner);
			
			this.mainModel = mainModel;
		}
		
		public void updateModel() {
		
			super.updateModel();
			//inputWirePort.getLocalTranslation().x = -mainModel.getLocalScale().x/2f;
		}


	
		
	}
}
