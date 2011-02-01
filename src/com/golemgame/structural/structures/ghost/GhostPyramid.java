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
import com.golemgame.mvc.golems.GhostPyramidInterpreter;
import com.golemgame.mvc.golems.PyramidInterpreter;
import com.golemgame.mvc.golems.SpatialInterpreter;
import com.golemgame.properties.Property;
import com.golemgame.tool.Tool;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.ControlAction;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.control.ControlPoint;
import com.golemgame.tool.control.PyramidControlPoint;
import com.golemgame.tool.control.PyramidControlPoint.ControllablePyramid;
import com.jme.bounding.BoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Pyramid;

public class GhostPyramid extends GhostStructure  {
	private static final long serialVersionUID =1L;
	private NodeModel model;

	private PyramidModel pyramidModel;

	private static PyramidControlPoint[] controlPoints = new PyramidControlPoint[]{
		new PyramidControlPoint(PyramidControlPoint.PEAK),new PyramidControlPoint(PyramidControlPoint.BASE),new PyramidControlPoint(PyramidControlPoint.WIDTH),new PyramidControlPoint(PyramidControlPoint.BOTTOM) };
	

	private Model[] controlledModels;
	
	private GhostPyramidInterpreter interpreter;
	public GhostPyramid(PropertyStore store) {
		super(store);
		
		this.interpreter = new GhostPyramidInterpreter(store);
		this.pyramidModel =buildModel();
		this.model = new NodeModel(this);
		this.getModel().addChild(pyramidModel);

		pyramidModel.setActionable(this);
		controlledModels = new Model[]{pyramidModel};
		
		getModel().getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Z);
		super.initialize();
		
	}
	
	@Override
	public Collection<Property> getPropertySet() {
		 Collection<Property> properties = super.getPropertySet();
		 properties.add( new Property(Property.PropertyType.SCALE_PYRAMID,this.interpreter.getStore()));
		return properties;
	}
	
	public void refreshController() {
		this.pyramidModel.updateModel();
		super.refreshController();
	}
	@Override
	public void refresh() {
		this.pyramidModel.getLocalScale().set(interpreter.getPyramidScale());
		
		super.refresh();
	}

	
	public boolean isMember(Actionable actionable) {
		for(ControlPoint<?> point:controlPoints)
		{
			if(point.equals(actionable) && this.pyramidModel.equals( point.getControllable()))
				return true;
		}
		return super.isMember(actionable);
	}

	
	protected PyramidModel buildModel()
	{
		return  new PyramidModel(this);
	}
	
	protected Model[] getControlledModels()
	{
		return controlledModels;
	}
	


	
	public NodeModel getModel() {
		return model;
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
		
			for (PyramidControlPoint control:controlPoints)
			{
			
				control.enable(pyramidModel);
				control.setControlSet(controlPoints);
			}
			if (resolve)
			{
				
				centerModels();
				pyramidModel.updateWorldData();
	
			}
			return super.doAction();
		}



		
		public boolean undoAction() {
			if (!resolve)
			{
				for (PyramidControlPoint control:controlPoints)
				{
					control.disable();
				}
			}else
			{	
					centerModels();
					pyramidModel.updateWorldData();
		
			}
			return super.undoAction();
		}
		
		
		public Actionable getControlled() {
		
			return GhostPyramid.this;
		}

		
		public void setVisible(boolean visible) {
			for (PyramidControlPoint control:controlPoints)
			{
				control.setVisible(visible);
			}
			
		}

		
		public Actionable[] getControlPoints() {
			return controlPoints;
		}

	}
	
	public static class PyramidModel extends SpatialModel implements ControllablePyramid
	{
		private static final long serialVersionUID = 1L;
		public static final float INIT_SIZE = 1f;
		
		private GhostPyramid owner;
		
		
		public void updateModel() {

			owner.interpreter.getPyramidScale().set(getLocalScale());
			owner.pyramidModel.updateWorldData();
			
			owner.centerModels();
			owner.pyramidModel.updateWorldData();
			owner.interpreter.getLocalTranslation().set(owner.getModel().getLocalTranslation());
			
			
	
			owner.refresh();
			
		}

		public SpatialInterpreter getInterpreter() {
			return owner.interpreter;
		}
		public PropertyStore getPropertyStore() {
			return owner.interpreter.getStore();
		}
		

		public PropertyState getCurrentState() {
			return new SimplePropertyState(owner.interpreter.getStore(),new String[]{PyramidInterpreter.PYRAMID_SCALE,SpatialInterpreter.LOCALTRANSLATION,SpatialInterpreter.LOCALSCALE,SpatialInterpreter.LOCALROTATION});
		}

		
		public Quaternion getScaleOffset() {
			return new Quaternion();
		}

		public PyramidModel(GhostPyramid owner) {
			super();
			this.owner = owner;
			registerSpatial();
		}
		
		
		protected Spatial buildSpatial()
		{
			Spatial spatial= new Pyramid("pyramid", INIT_SIZE, INIT_SIZE);// new Box("box", new Vector3f(), INIT_SIZE/2f,INIT_SIZE/2f,INIT_SIZE/2f);		 

			spatial.getLocalTranslation().zero();
			spatial.getLocalRotation().loadIdentity();
			spatial.getLocalScale().set(1,1,1);
			spatial.setModelBound(new BoundingBox());
			spatial.updateModelBound();
			return spatial;
		}
		
		
		public boolean isShareable() {
			return true;
		}

		
		public PyramidModel makeSharedModel() {
			PyramidModel sharedBox = new PyramidModel((GhostPyramid)null);
		
			return sharedBox;
		}




		
		public Vector3f getRelativeFromWorld(Vector3f worldTranslation,
				Vector3f store) {
			Tool.updateToWorld(getSpatial());
			return getParent().worldToLocal(worldTranslation, store);
		}

		
		public Vector3f getWorldFromRelative(Vector3f localTranslation,
				Vector3f store) {

			Tool.updateToWorld(getSpatial());
			return  getParent().localToWorld(localTranslation, store);
		}
	
	}
}
