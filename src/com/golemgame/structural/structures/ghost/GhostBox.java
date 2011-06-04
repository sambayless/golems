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
package com.golemgame.structural.structures.ghost;

import java.util.Collection;

import com.golemgame.model.Model;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.model.spatial.SpatialModel;
import com.golemgame.mvc.PropertyState;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SimplePropertyState;
import com.golemgame.mvc.golems.BoxInterpreter;
import com.golemgame.mvc.golems.GhostBoxInterpreter;
import com.golemgame.mvc.golems.SpatialInterpreter;
import com.golemgame.properties.Property;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.ControlAction;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.control.ControlPoint;
import com.golemgame.tool.control.CubeControlPoint;
import com.golemgame.tool.control.CubeControlPoint.ControllableBox;
import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;

public class GhostBox extends GhostStructure  {
	private static final long serialVersionUID =1L;
	private NodeModel model;

	protected VisualBox visualBox;

	
	private static CubeControlPoint[] controlPoints = new CubeControlPoint[]{
		new CubeControlPoint(CubeControlPoint.LEFT),new CubeControlPoint(CubeControlPoint.FORWARD),new CubeControlPoint(CubeControlPoint.TOP)
		,new CubeControlPoint(CubeControlPoint.RIGHT),new CubeControlPoint(CubeControlPoint.BACKWARD),new CubeControlPoint(CubeControlPoint.BOTTOM)};

	
	private Model[] controlledModels;

	private GhostBoxInterpreter interpreter;
	public GhostBox( PropertyStore store) {
		super(store);
		this.interpreter = new GhostBoxInterpreter(store);

		this.visualBox = new VisualBox(this);
		this.model = new NodeModel(this);
		this.getModel().addChild(visualBox);


		visualBox.setActionable(this);
		controlledModels = new Model[]{visualBox};
		initialize();
		
	}
	@Override
	public Collection<Property> getPropertySet() {
		 Collection<Property> properties = super.getPropertySet();
		 properties.add( new Property(Property.PropertyType.SCALE_BOX,this.interpreter.getStore()));
		 
		return properties;
	}
	protected Model[] getControlledModels()
	{
		return controlledModels;
	}
	

	
	public NodeModel getModel() {
		return model;
	}
	
	
	
	
	protected Vector3f getScale() {
		return visualBox.getLocalScale();
	}


	
	protected void setScale(Vector3f scale) {
		visualBox.getLocalScale().set(scale);
	}


	
	public void refreshController() {
		//this.visualBox.updateModel();
		super.refreshController();
	}

	@Override
	public void refresh() {
		this.visualBox.getLocalScale().set(interpreter.getExtent()).multLocal(2f);
	
		visualBox.updateWorldData();
	
		super.refresh();

	}
	public boolean isMember(Actionable actionable) {
		for(ControlPoint<?> point:controlPoints)
		{
			if(point.equals(actionable) && this.visualBox.equals( point.getControllable()))
				return true;
		}
		return super.isMember(actionable);
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
		/*
		 * New structure: 
		 * Control turns on, or off, control mode for an object.
		 * When control is turned on, the control displays itself. 
		 * When turned off, the controls remove themselves.
		 * When a control is turned on, it automatically cleans up if it was previously on.
		 * Controls cause changes via the interface of ControllableModels.
		 * When the control is turned off, IT decides whether or not to send an update message to the model.
		 * 
		 */



		
		public boolean doAction() 
		{
		
			for (CubeControlPoint control:controlPoints)
			{
			
				control.enable(visualBox);
				control.setControlSet(controlPoints);
			}
			if (resolve)
			{
				
				centerModels();
				visualBox.updateWorldData();
			}
			super.doAction();
			return true;
		}



		
		public boolean undoAction() {
			if (!resolve)
			{
				for (CubeControlPoint control:controlPoints)
				{
					control.disable();
				}
			}else
			{	
					centerModels();
					visualBox.updateWorldData();
				}
			super.undoAction();
			return true;
		}
		
		
		public Actionable getControlled() {
		
			return GhostBox.this;
		}

		
		public void setVisible(boolean visible) {
			for (CubeControlPoint control:controlPoints)
			{
				control.setVisible(visible);
			}
			
		}

		
		public Actionable[] getControlPoints() {
			return controlPoints;
		}

	}
	
	public static class VisualBox extends SpatialModel implements ControllableBox
	{
		private static final long serialVersionUID = 1L;
		public static final float INIT_SIZE = 1f;
		private static Box box = null;
		
		private GhostBox owner;
		
		

		public void updateModel() {

			owner.interpreter.setExtent(getLocalScale().divide(2f));
			owner.centerModels();
			owner.visualBox.updateWorldData();
			owner.interpreter.getLocalTranslation().set(owner.getModel().getLocalTranslation());
			
			owner.refreshController();
			
		}
	
		public VisualBox(GhostBox owner) {
			super();
			this.owner = owner;
			
			registerSpatial();
			
		}
		
		public PropertyStore getPropertyStore() {
			return owner.interpreter.getStore();
		}
		
		
		
		protected Spatial buildSpatial() {
			if (box == null)
			{
				box = new Box("box", new Vector3f(), INIT_SIZE/2f,INIT_SIZE/2f,INIT_SIZE/2f);		 
			}
			
			Spatial	boxModel  =  new Box("box", new Vector3f(), INIT_SIZE/2f,INIT_SIZE/2f,INIT_SIZE/2f);		 

		
			boxModel.getLocalTranslation().zero();
			boxModel.getLocalRotation().loadIdentity();
			boxModel.getLocalScale().set(1,1,1);
			boxModel.setModelBound(new BoundingBox());
			boxModel.updateModelBound();
			
			return boxModel;
		}

		
		public boolean isShareable() {
			return true;
		}

		
		public VisualBox makeSharedModel() {
			VisualBox sharedBox = new VisualBox((GhostBox)null);
		
			return sharedBox;
		}

		
		public Vector3f getLocalTranslation() {
			// TODO Auto-generated method stub
			return super.getLocalTranslation();
		}



		
		public Vector3f getRelativeFromWorld(Vector3f worldTranslation,
				Vector3f store) {
			updateWorldData();
			return getParent().worldToLocal(worldTranslation, store);
		}

		
		public Vector3f getWorldFromRelative(Vector3f localTranslation,
				Vector3f store) {

			updateWorldData();
			return  getParent().localToWorld(localTranslation, store);
		}
	


		public SpatialInterpreter getInterpreter() {
			return owner.interpreter;
		}


		public PropertyState getCurrentState() {
			return new SimplePropertyState(owner.interpreter.getStore(),new String[]{BoxInterpreter.BOX_EXTENT,SpatialInterpreter.LOCALTRANSLATION,SpatialInterpreter.LOCALSCALE,SpatialInterpreter.LOCALROTATION});
		}
	
	}
	
}
