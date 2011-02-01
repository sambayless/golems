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
import com.golemgame.model.spatial.shape.TubeFacade;
import com.golemgame.model.spatial.shape.TubeModel;
import com.golemgame.mvc.PropertyState;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SimplePropertyState;
import com.golemgame.mvc.golems.CylinderInterpreter;
import com.golemgame.mvc.golems.SpatialInterpreter;
import com.golemgame.mvc.golems.TubeInterpreter;
import com.golemgame.mvc.golems.validate.GolemsValidator;
import com.golemgame.properties.Property;
import com.golemgame.structural.collision.CollisionMember;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.ControlAction;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.control.TubeControlPoint;
import com.golemgame.tool.control.TubeControlPoint.ControllableTube;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;


public class TubeStructure extends PhysicalStructure {

	private static final long serialVersionUID = 1L;
	private static final float RADIUS = 0.5f;
	private static final float HEIGHT = 1f;
	
	private static TubeControlPoint[] controlPoints = new TubeControlPoint[]{
		new TubeControlPoint(TubeControlPoint.LEFT), new TubeControlPoint(TubeControlPoint.RIGHT), new TubeControlPoint(TubeControlPoint.RADIUS)	,new TubeControlPoint(TubeControlPoint.INNER_RADIUS)	
	};
	
	private NodeModel model;
	private TubeModel tubeModel;
	private TubeFacade facade;
	
	private Model[] controlledModels;
	private Model[] visualModels;
	private ControllableImpl cylController;
	private CollisionMember collisionMember;
	private TubeInterpreter interpreter;
	public TubeStructure(PropertyStore store) {
		super(store);
		this.interpreter = new TubeInterpreter(store);
	
		model = new NodeModel();
		
		tubeModel = new TubeModel(true);
		facade = new TubeFacade();
		
		model.addChild(tubeModel);
		model.addChild(facade);

		  Quaternion rotation = new Quaternion();
	    rotation.fromAngleNormalAxis(FastMath.PI,Vector3f.UNIT_X);
	    rotation.multLocal(new Quaternion().fromAngleNormalAxis(FastMath.PI/2f,Vector3f.UNIT_Y));
	   // rotation.fromAngleNormalAxis(FastMath.HALF_PI/2f - FastMath.HALF_PI/3f + FastMath.HALF_PI/6f, Vector3f.UNIT_Y);
	    tubeModel.getLocalRotation().multLocal(rotation);
	    facade.getLocalRotation().multLocal(rotation);
	    
		this.collisionMember = new CollisionMember(model,this.getActionable());
		collisionMember.registerCollidingModel(tubeModel);
	
		tubeModel.setActionable(this);
		controlledModels = new Model[]{tubeModel,facade};
		visualModels = new Model[]{facade};	
		
		cylController = new ControllableImpl(tubeModel, facade, this);
		super.initialize();
	}


	public Model[] getAppearanceModels() {
		return visualModels;
	}


	
	@Override
	public void refresh() {
		super.refresh();
		tubeModel.getLocalScale().set(interpreter.getRadius()*2f,interpreter.getHeight(),interpreter.getRadius()*2f);
		GolemsValidator.getInstance().makeValid(interpreter.getStore());
		float innerRadius = interpreter.getInnerRadius();
		if(innerRadius> interpreter.getRadius()*0.98f)
			innerRadius = interpreter.getRadius()*0.98f;
		innerRadius/=(interpreter.getRadius()*2f);

	//	tubeModel.setInnerRadius(innerRadius);
		tubeModel.setParameters(innerRadius,interpreter.getArc());
		tubeModel.updateWorldData();
		
		model.detachChild(tubeModel);
		model.addChild(tubeModel);

		facade.loadModelData(tubeModel);
	//	facade.setInnerRadius(innerRadius);
		facade.setParameters(innerRadius,interpreter.getArc());
	}


	@Override
	protected CollisionMember getStructuralCollisionMember() {
		return collisionMember;
	}


	@Override
	protected Model[] getControlledModels() {
		return controlledModels;
	}




	protected Vector3f getScale() {
		return cylController.getLocalScale();
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
		 properties.add( new Property(Property.PropertyType.SCALE_TUBE,this.interpreter.getStore()));
	     return properties;
	}

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
		
			for (TubeControlPoint control:controlPoints)
			{
			
				control.enable(cylController);
				control.setControlSet(controlPoints);
			}
			if (resolve)
			{
				
				centerModels();
				tubeModel.updateWorldData();
				collisionMemberStateChange();
			}
			super.doAction();
			return true;
		}



		
		public boolean undoAction() {
			if (!resolve)
			{
				for (TubeControlPoint control:controlPoints)
				{
					control.disable();
				}
			}else
			{	
					centerModels();
					tubeModel.updateWorldData();
					collisionMemberStateChange();
			}
			return super.undoAction();
		}
		
		
		public Actionable getControlled() {
		
			return TubeStructure.this;
		}

		
		public void setVisible(boolean visible) {
			for (TubeControlPoint control:controlPoints)
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
	
	public static class ControllableImpl implements ControllableTube
	{
		private static final long serialVersionUID = 1L;
		private Model mainModel;
		private Model facade;
		private TubeStructure owner;
		public ControllableImpl(Model sphere, Model facade, TubeStructure owner) {
			super();
			this.mainModel = sphere;
			this.facade = facade;
			this.owner = owner;
		}
		

		public PropertyStore getPropertyStore() {
			return owner.interpreter.getStore();
		}


		public CylinderInterpreter getInterpreter() {
			return owner.interpreter;
		}
		
		public void updateModel() {
		//	owner.interpreter.getLocalTranslation().set(owner.getModel().getLocalTranslation());
		//	owner.interpreter.setHeight(mainModel.getLocalScale().y);
		//	owner.interpreter.setRadius(mainModel.getLocalScale().z/2f);
		//	System.out.println(mainModel.getLocalScale().z/2f + "\t" + owner.interpreter.getRadius());
		//	System.out.println(mainModel.getLocalScale());
			//owner.centerModels();
			owner.centerModels();
			//in case there is any doubt about this: this is the WRONG way to do this, but it works and there is no reason to change it right now.
			//however, the (MVC) model should NOT be updated by coping the (MVC) view, in future implementations.
		//	owner.interpreter.getLocalTranslation().set(owner.getModel().getLocalTranslation());
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
		
		
	

		public void setInnerRadius(float stretch) {
		
			float amount = (stretch- owner.interpreter.getRadius()*2f);
			if(amount<0)
				amount = 0;
			else if (amount>owner.interpreter.getRadius())
				amount = owner.interpreter.getRadius();
			owner.interpreter.setInnerRadius(amount);
			owner.interpreter.refresh();
		}
		
		public float getInnerRadius()
		{
			return owner.interpreter.getInnerRadius()+owner.interpreter.getRadius();
		}



		public float getHeight() {
			return owner.interpreter.getHeight();
		}



		public float getOuterRadius() {
			return owner.interpreter.getRadius();
		}



		public void setHeight(float stretch) {
			owner.interpreter.setHeight(stretch);
			owner.interpreter.refresh();
		}



		public void setLocalTranslation(Vector3f localTranslation) {
			owner.interpreter.getLocalTranslation().set(localTranslation);
			owner.interpreter.refresh();
		}



		public void setOuterRadius(float stretch) {
			owner.interpreter.setRadius(stretch);
			owner.interpreter.refresh();
		}
		
	}
}
