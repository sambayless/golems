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
import com.golemgame.model.Model;
import com.golemgame.model.ParentModel;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.model.spatial.SpatialModel;
import com.golemgame.model.spatial.shape.BoxModel;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.OscilloscopeInterpreter;
import com.golemgame.properties.Property;
import com.golemgame.structural.collision.CollisionMember;
import com.golemgame.structural.collision.NonPropagatingCollisionMember;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;


public class OscilloscopeStructure extends FunctionalStructure   {
	private static final long serialVersionUID = 1L;

	private WirePort functionInput;

	private SpatialModel boxModel;
	
	private NodeModel model;
	private CollisionMember collisionMember;
	private Model[] controlledModels;
	

	private Model[] visualModels;
	


	

	private OscilloscopeInterpreter interpreter;
	public OscilloscopeStructure(PropertyStore store) {
		super(store);
		this.interpreter = new OscilloscopeInterpreter(store);
		this.model = new NodeModel(this);

		boxModel = new BoxModel(true);
		
		
		this.getModel().addChild(boxModel);
	
		
		functionInput = new WirePort(this,true);
		
	
		functionInput.getModel().getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI,Vector3f.UNIT_Z);

		
	
		
		
		functionInput.getModel().getLocalTranslation().x = 0.5f;
		functionInput.getModel().updateWorldData();
		
		
		
		super.registerWirePort(functionInput);	

		

		model.addChild(functionInput.getModel());

		
		this.collisionMember = new NonPropagatingCollisionMember(model,this.getActionable());
		collisionMember.registerCollidingModel(boxModel);

		boxModel.setActionable(this);
		controlledModels = new Model[]{boxModel};
		visualModels = new Model[]{boxModel};
		
		
		super.initialize();

	}

	
	@Override
	public Collection<Property> getPropertySet() {
		 Collection<Property> properties = super.getPropertySet();

		 properties.add( new Property(Property.PropertyType.OSCILLOSCOPE,this.interpreter.getStore()));
		 properties.add(new Property(Property.PropertyType.OUTPUT_DEVICE,this.interpreter.getOutputDevice()));
		return properties;
	}


	protected Model[] getControlledModels() {

		return controlledModels;
	}

	
	protected CollisionMember getStructuralCollisionMember() {

		return collisionMember;
	}

	
	protected Model[] getAppearanceModels() {
		return visualModels;
	}

	
	public ParentModel getModel() {

		return model;
	}

	@Override
	public void refresh() {
		super.refresh();


	}

	
	public boolean isMindful() {
		return true;
	}

	
	public boolean isPhysical() {
		return false;
	}
	
	


	/*@Override
	public void refreshView() {

		//this.functionInput.refreshView();
		Layer layer = getLayer();
		if (getViews().contains(Viewable.ViewMode.FUNCTIONAL))
		{
			if(layer.isEditable())
			{
				super.setSelectable(true);
			}
			
			if(layer.isVisible()){
				this.getAppearance().reapply();				
				this.getModel().setVisible(true);
			}else{
				this.getModel().setVisible(false);
			}
		}else
		{
			super.setSelectable(false);
			this.getModel().setVisible(false);
		}
		
	
	}*/


}
