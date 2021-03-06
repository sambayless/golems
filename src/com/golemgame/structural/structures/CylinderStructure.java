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

import com.golemgame.model.Model;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.model.spatial.SpatialModel;
import com.golemgame.model.spatial.shape.CylinderFacade;
import com.golemgame.model.spatial.shape.CylinderModel;
import com.golemgame.model.texture.TextureTypeKey.TextureShape;
import com.golemgame.mvc.PropertyState;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SimplePropertyState;
import com.golemgame.mvc.golems.CylinderInterpreter;
import com.golemgame.mvc.golems.SpatialInterpreter;
import com.golemgame.properties.Property;
import com.golemgame.structural.collision.CollisionMember;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.ControlAction;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.control.CylinderControlPoint;
import com.golemgame.tool.control.CylinderControlPoint.ControllableCylinder;
import com.golemgame.tool.control.CylinderControlPoint.CylinderPoint;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

public class CylinderStructure extends PhysicalStructure  {
	private static final long serialVersionUID =1L;
	private NodeModel model;

	protected SpatialModel cylinderModel;
	protected Model facade;
	private CollisionMember collisionMember;
	

	private static CylinderControlPoint[] controlPoints = new CylinderControlPoint[]{
		new CylinderControlPoint(CylinderControlPoint.LEFT), new CylinderControlPoint(CylinderControlPoint.RIGHT), new CylinderControlPoint(CylinderControlPoint.RADIUS)		
	};
	

	private Model[] controlledModels;
	private Model[] visualModels;
	private ControllableImpl cylController;
	private CylinderInterpreter interpreter;
	public CylinderStructure(PropertyStore store) {
		super(store);
		this.interpreter = new CylinderInterpreter(store);
		this.cylinderModel =buildModel();
		this.model = new NodeModel(this);
		this.getModel().addChild(cylinderModel);
		facade = buildFacade();
		this.getModel().addChild(facade);
	
			this.collisionMember = new CollisionMember(model,this.getActionable());
			collisionMember.registerCollidingModel(cylinderModel);
	

		
		cylinderModel.setActionable(this);
		controlledModels = new Model[]{cylinderModel,facade};
		visualModels = new Model[]{facade};
		cylController = buildController(cylinderModel,facade);
		super.initialize();
		
	}
	
	
	public CylinderInterpreter getInterpreter() {
		return interpreter;
	}


	public Model[] getAppearanceModels() {
		return visualModels;
	}

	protected ControllableImpl buildController(Model mainModel, Model facade)
	{
		 return new ControllableImpl(cylinderModel, facade, this);
	}
	protected SpatialModel buildModel()
	{
		return  new CylinderModel(true);
	}
	
	@Override
	public void refresh() {
		super.getStructuralAppearanceEffect().setPreferedShape(TextureShape.Cylinder);
		cylinderModel.getLocalScale().set(interpreter.getRadius()*2f,interpreter.getRadius()*2f,interpreter.getHeight());
		cylinderModel.updateWorldData();
		facade.loadModelData(cylinderModel);
	
		super.refresh();
	}


	protected Model buildFacade()
	{
		 return new CylinderFacade();
	}
	
	protected Model[] getControlledModels()
	{
		return controlledModels;
	}
	
	
	protected CollisionMember getStructuralCollisionMember() {
		return collisionMember;
	}
	
	protected Vector3f getScale() {
		return cylController.getLocalScale();
	}
	
	@Override
	protected TextureShape getPrefferedShape() {
		return TextureShape.Cylinder;
	}

	
	protected void setScale(Vector3f scale) {
		cylController.getLocalScale().set(scale);
		cylController.updateModel();
	}
	

	
	public NodeModel getModel() {
		return model;
	}
	
	@Override
	public Collection<Property> getPropertySet() {
		 Collection<Property> properties = super.getPropertySet();
		 properties.add( new Property(Property.PropertyType.SCALE_CYLINDER,this.interpreter.getStore()));
	     return properties;
	}
	
/*	public PhysicalStructure copy(StructuralMachine destination) {
		CylinderStructure copy = new CylinderStructure((getStore().deepCopy()));

		
		copy.cylinderModel.loadModelData(this.cylinderModel);
		copy.facade.loadModelData(this.facade);
		copy.set(this);
		return copy;
	}

	*/
	public Action<?> getAction(Type type) throws ActionTypeException {
		if (type == Type.CONTROL)
		{
			return new Control();
		}else
			return super.getAction(type);
	}

	protected class Control extends ControlAction
	{



		
		public boolean doAction() 
		{
		
			for (CylinderControlPoint control:controlPoints)
			{
			
				control.enable(cylController);
				control.setControlSet(controlPoints);
			}
			if (resolve)
			{
				
				centerModels();
				cylinderModel.updateWorldData();
				collisionMemberStateChange();
			}
			super.doAction();
			return true;
		}



		
		public boolean undoAction() {
			if (!resolve)
			{
				for (CylinderControlPoint control:controlPoints)
				{
					control.disable();
				}
			}else
			{	
					centerModels();
					cylinderModel.updateWorldData();
					collisionMemberStateChange();
			}
			return super.undoAction();
		}
		
		
		public Actionable getControlled() {
		
			return CylinderStructure.this;
		}

		
		public void setVisible(boolean visible) {
			for (CylinderControlPoint control:controlPoints)
			{
				control.setVisible(visible);
			}
			
		}

		
		public Actionable[] getControlPoints() {
			return controlPoints;
		}

	}
	

	
	

	public void refreshController() {
		cylController.updateModel();
	}
	
	public static class ControllableImpl implements ControllableCylinder
	{
		private static final long serialVersionUID = 1L;
		private Model mainModel;
		private Model facade;
		private CylinderStructure owner;
		public ControllableImpl(Model sphere, Model facade, CylinderStructure owner) {
			super();
			this.mainModel = sphere;
			this.facade = facade;
			this.owner = owner;
		}
		
		public PropertyStore getPropertyStore() {
			return owner.interpreter.getStore();
		}
		
		public float getValidatedScale(float stretch, CylinderPoint type) {
			return stretch;
		}
		
		public CylinderInterpreter getInterpreter() {
			return owner.interpreter;
		}
		
		public void updateModel() {
		//	owner.interpreter.getLocalTranslation().set(owner.getModel().getLocalTranslation());
			owner.interpreter.setHeight(mainModel.getLocalScale().z);
			owner.interpreter.setRadius(mainModel.getLocalScale().x/2f);
			//owner.centerModels();
			owner.centerModels();
			//in case there is any doubt about this: this is the WRONG way to do this, but it works and there is no reason to change it right now.
			//however, the (MVC) model should NOT be updated by coping the (MVC) view, in future implementations.
			owner.interpreter.getLocalTranslation().set(owner.getModel().getLocalTranslation());
			owner.refresh();
			
		}
		
		public PropertyState getCurrentState() {
			return new SimplePropertyState(owner.interpreter.getStore(),new String[]{CylinderInterpreter.CYL_HEIGHT,CylinderInterpreter.CYL_RADIUS,SpatialInterpreter.LOCALTRANSLATION,SpatialInterpreter.LOCALSCALE,SpatialInterpreter.LOCALROTATION});
		}
		
		public Quaternion getLocalRotation() {
			return mainModel.getLocalRotation();
		}

		
		public Vector3f getLocalScale() {
			return mainModel.getLocalScale();
		}

		
		public Vector3f getLocalTranslation() {
			return mainModel.getLocalTranslation();
		}

		
		public Quaternion getWorldRotation() {
			return mainModel.getWorldRotation();
		}

		
		public Vector3f getWorldTranslation() {
			return mainModel.getWorldTranslation();
		}

		
		public void updateWorldData() {
			 mainModel.updateWorldData();
			 facade.updateWorldData();
		}

		
		public Model getParent() {
			return mainModel.getParent();
		}

		
		public Vector3f getRelativeFromWorld(Vector3f worldTranslation,
				Vector3f store) {
			mainModel.updateWorldData();
			return getParent().worldToLocal(worldTranslation, store);
		}

		
		public Vector3f getWorldFromRelative(Vector3f localTranslation,
				Vector3f store) {

			mainModel.updateWorldData();
			return  getParent().localToWorld(localTranslation, store);
		}
		
		
	
		
		public void loadInitialize() {

		}
		
	}
}
