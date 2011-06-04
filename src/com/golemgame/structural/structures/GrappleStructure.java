/*******************************************************************************
 * Copyright 2008, 2009, 2010 Sam Bayless.
 * 
 *     This file is part of Golems.
 * 
 *     Golems is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     Golems is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with Golems. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.golemgame.structural.structures;

import java.util.Collection;

import com.golemgame.functional.WirePort;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GrappleInterpreter;
import com.golemgame.mvc.golems.BeamInterpreter;
import com.golemgame.mvc.golems.RocketPropellantInterpreter;
import com.golemgame.properties.Property;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;

public class GrappleStructure extends CapsuleStructure {

	private static final long serialVersionUID = 1L;

	private final GrappleInterpreter interpreter;
	
//	private WirePort forceOutput;
	private WirePort distanceOutput;
	private WirePort forceInput;
	
	public GrappleStructure(PropertyStore store) {
		super(store);
		interpreter = new GrappleInterpreter(store);
		
/*
		forceOutput = new WirePort(this,false);

		forceOutput.getModel().getLocalTranslation().y = 0.5f;
		forceOutput.getModel().updateWorldData();
		
		super.registerWirePort(forceOutput);
		getModel().addChild(forceOutput.getModel());*/
		
		distanceOutput = new WirePort(this,false);

		distanceOutput.getModel().getLocalTranslation().y = 0.5f;
		distanceOutput.getModel().updateWorldData();
		
		super.registerWirePort(distanceOutput);
		getModel().addChild(distanceOutput.getModel());
		
		
		forceInput = new WirePort(this,true);

		forceInput.getModel().getLocalTranslation().x = 0.5f;
		forceInput.getModel().getLocalRotation().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Z);
		forceInput.getModel().updateWorldData();
		
		super.registerWirePort(forceInput);
		getModel().addChild(forceInput.getModel());
		
	}
	
	@Override
	public void refresh() {
		super.refresh();
		forceInput.getModel().getLocalTranslation().x = super.getInterpreter().getHeight()/2f + super.getInterpreter().getRadius();
		distanceOutput.getModel().getLocalTranslation().y = super.getInterpreter().getRadius();
		forceInput.getModel().updateWorldData();
		distanceOutput.getModel().updateWorldData();
	
		forceInput.refresh();
		distanceOutput.refresh();
		//System.out.println(forceInput.getModel().getLocalTranslation());
	}

	@Override
	public Collection<Property> getPropertySet() {
		 Collection<Property> properties = super.getPropertySet();
		 properties.add(new Property(Property.PropertyType.GRAPPLE,this.interpreter.getStore()));
		 
	//	 properties.add(new Property(Property.PropertyType.BEAM_EFFECTS,this.interpreter.getLaserProperties()));
	//	 properties.add(new Property(Property.PropertyType.LINE_EFFECTS,this.interpreter.getLaserProperties().getPropertyStore(BeamInterpreter.LASER_EFFECTS)));
		 
		 return properties;
	}

}
