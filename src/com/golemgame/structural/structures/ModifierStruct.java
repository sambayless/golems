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

import com.golemgame.functional.FunctionSettings;
import com.golemgame.functional.WirePort;
import com.golemgame.model.Model;
import com.golemgame.model.ParentModel;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.model.spatial.SpatialModel;
import com.golemgame.model.spatial.SpatialModelImpl;
import com.golemgame.model.spatial.shape.SphereFacade;
import com.golemgame.model.spatial.shape.SphereModel;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.ModifierInterpreter;
import com.golemgame.mvc.golems.ModifierInterpreter.ModifierSwitchType;
import com.golemgame.mvc.golems.functions.FunctionSettingsInterpreter.FunctionType;
import com.golemgame.properties.Property;
import com.golemgame.structural.collision.CollisionMember;
import com.golemgame.structural.collision.NonPropagatingCollisionMember;
import com.jme.bounding.BoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;


public class ModifierStruct extends FunctionalStructure  {
	private static final long serialVersionUID = 1L;
	private WirePort output;
	private WirePort functionInput;
	private WirePort switchInput;
	private SpatialModel sphereModel;
	private SpatialModel sphereFacade;
	private NodeModel model;
	private CollisionMember collisionMember;
	private Model[] controlledModels;
	
	private FunctionSettings settings;
	

	private Model[] visualModels;
	

	
	public FunctionType getFunctionType() {
		return settings.getFunctionType();
	}

	

	private ModifierInterpreter interpreter;
	public ModifierStruct( PropertyStore store) {
		super(store);
		this.interpreter = new ModifierInterpreter(store);
		this.model = new NodeModel(this);

		sphereModel = new SphereModel(true);
		sphereFacade = new SphereFacade();
		
		
		this.getModel().addChild(sphereModel);
		this.getModel().addChild(sphereFacade);
		
		output = new WirePort(this,false);
		functionInput = new WirePort(this,true);
		
		output.getModel().getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI,Vector3f.UNIT_Z);
		functionInput.getModel().getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI,Vector3f.UNIT_Z);

		
	
		output.getModel().getLocalTranslation().x = -0.5f;
		output.getModel().updateWorldData();
		
		functionInput.getModel().getLocalTranslation().x = 0.5f;
		functionInput.getModel().updateWorldData();
		
		
		
		switchInput = new WirePort(this,true,new ModifierInputModel(),null);
		
		switchInput.getModel().getLocalTranslation().y = 0.5f;
		switchInput.getModel().updateWorldData();
		
		super.registerWirePort(functionInput);	
		super.registerWirePort(switchInput);
		super.registerWirePort(output);
		
		model.addChild(output.getModel());
		model.addChild(functionInput.getModel());
		model.addChild(switchInput.getModel());
		
		this.collisionMember = new NonPropagatingCollisionMember(model,this.getActionable());
		collisionMember.registerCollidingModel(sphereModel);

		sphereModel.setActionable(this);
		controlledModels = new Model[]{sphereModel};
		visualModels = new Model[]{sphereFacade};
		
		super.initialize();

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
		/*this.functionInput.setReference(interpreter.getInput());
		this.output.setReference(interpreter.getOutput());
		this.switchInput.setReference(interpreter.getAuxInput());*/

		
		if (this.settings == null || ! this.settings.getStore().equals(interpreter.getFunctionStore()))
		{
			this.settings = new FunctionSettings(interpreter.getFunctionStore());	
		}
		settings.setMinX(-1f);//esnure this is the case... fix for some save files...
		settings.refresh();
		

	}
	public static class ModifierInputModel extends SpatialModelImpl
	{
		private static final long serialVersionUID = 1L;
		
		
		public ModifierInputModel() {
			super(true);
			
		}



		
		protected Spatial buildSpatial() {
			Node switchNode = new Node();
			Box box = new Box("Switch", new Vector3f(), 0.10f,0.04f,0.1f);
			switchNode.attachChild(box);
			switchNode.setModelBound(new BoundingBox());
			switchNode.updateModelBound();
			return switchNode;
		}
	};
	
	
	public boolean isMindful() {
		return true;
	}

	
	public boolean isPhysical() {
		return false;
	}
	
	


	/*
	@Override
	public void refreshView() {
		super.refreshView();
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

/*	@Override
	public void removeViewMode(com.golemgame.views.Viewable.ViewMode viewMode) {
		this.output.removeViewMode(viewMode);
		this.switchInput.removeViewMode(viewMode);
		this.functionInput.removeViewMode(viewMode);

		super.removeViewMode(viewMode);
	}*/
	
/*	public void setThresholdInverted(boolean invert) {
		interpreter.setThresholdInverted(invert);
		interpreter.refresh();
	}

	public boolean isThresholdInverted() {
		return interpreter.isThresholdInverted();
	}*/
	
	
	

	@Override
	public Collection<Property> getPropertySet() {
		 Collection<Property> properties = super.getPropertySet();

		 properties.add(new Property(Property.PropertyType.FUNCTION_APPLIED,this.interpreter.getFunctionStore()));		 
		 properties.add(new Property(Property.PropertyType.MODIFIER,this.interpreter.getStore()));	
		 return properties;
	}

	
	
	

	
	
}
