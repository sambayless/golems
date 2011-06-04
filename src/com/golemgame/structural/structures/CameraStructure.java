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
import com.golemgame.mechanical.StructuralMachine;
import com.golemgame.model.Model;
import com.golemgame.model.ParentModel;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.model.spatial.SpatialModel;
import com.golemgame.model.spatial.shape.CameraFacade;
import com.golemgame.model.spatial.shape.CameraModel;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.CameraInterpreter;
import com.golemgame.properties.Property;
import com.golemgame.states.camera.EmbeddedCamera;
import com.golemgame.structural.collision.CollisionMember;
import com.golemgame.structural.collision.NonPropagatingCollisionMember;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;

public class CameraStructure extends FunctionalStructure  {
	private static final long serialVersionUID = 1L;

	private SpatialModel cameraModel;
	private SpatialModel cameraFacade;
	private NodeModel model;
	private CollisionMember collisionMember;
	private Model[] controlledModels;
	
	private EmbeddedCamera embeddedCamera;

	private WirePort input;
	
	private CameraInterpreter interpreter;
	public CameraStructure(PropertyStore store) {
		super(store);
		this.interpreter = new CameraInterpreter(store);
		this.model = new NodeModel(this);

		cameraModel=new CameraModel(true);
	
		cameraFacade = new CameraFacade();
		
		this.getModel().addChild(cameraModel);
		this.getModel().addChild(cameraFacade);
		
		embeddedCamera = new EmbeddedCamera();
		//embeddedCamera.getCameraModel().getLocalRotation().multLocal(new Quaternion().fromAngleNormalAxis(0,Vector3f.UNIT_Y));
		this.model.addChild(embeddedCamera.getCameraModel());

			this.collisionMember = new NonPropagatingCollisionMember(model,this.getActionable());
			collisionMember.registerCollidingModel(cameraModel);	
		
		cameraModel.setActionable(this);
		controlledModels = new Model[]{cameraModel,cameraFacade};
		
		input = new WirePort(this,true);
		input.getModel().getLocalTranslation().x = -2f/3f;
		input.getModel().getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Z);
		input.getModel().updateWorldData();
		super.registerWirePort(input);	
		getModel().addChild(input.getModel());

		super.initialize();
		
		//this.getAppearance().addEffect(new TintableColorEffect(new ColorRGBA(0.1f,0.1f,0.1f,1f)),true);
		this.getStructuralAppearanceEffect().setBaseColor(new ColorRGBA(0.2f,0.2f,0.2f,0.8f));
	}

	
	@Override
	public void addToMachine(StructuralMachine machine) {
		super.addToMachine(machine);
		machine.getEnvironment().addCameraDelegate(embeddedCamera,interpreter.getReference());
		
	}


	protected Model[] getControlledModels() {

		return controlledModels;
	}

	
	protected CollisionMember getStructuralCollisionMember() {

		return collisionMember;
	}

	@Override
	public Collection<Property> getPropertySet() {
		 Collection<Property> properties = super.getPropertySet();
		 properties.add( new Property(Property.PropertyType.CAMERA,this.interpreter.getStore()));
		 
		return properties;
	}

	public ParentModel getModel() {

		return model;
	}

	
	public boolean isPropagating() {
		return false;
	}


	

	public boolean isPhysical() {
		return true;
	}

	


	
	
	@Override
	public void remove() {
		getMachine().getEnvironment().removeCameraDelegate(embeddedCamera);
		
		super.remove();
	}


	@Override
	public void refresh() {
		super.refresh();
		embeddedCamera.setLockAll(interpreter.isOrientationLocked());
		this.embeddedCamera.lockRollPitchYaw(interpreter.isRollLocked(), interpreter.isPitchLocked(), interpreter.isYawLocked());
	}

/*
	@Override
	public void refreshView() {
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
