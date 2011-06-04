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
package com.golemgame.tool.action;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.golemgame.model.Model;
import com.golemgame.model.Models;
import com.golemgame.model.ParentModel;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.tool.ActionToolSettings;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.action.information.FocusInformation;
import com.golemgame.tool.action.information.ModelInformation;
import com.golemgame.tool.action.information.MoveRestrictedInformation;
import com.golemgame.tool.action.information.OrientationInformation;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

/**
 * This class allows for a set of models to be attached.
 * Rotation and movement actions can be performed on the whole action node.
 * @author Sam
 *
 */
public class ActionNode implements Actionable {

	private NodeModel model;
	
	private Map<Model,ParentModel> modelMap = new HashMap<Model,ParentModel>();
	
	public ActionNode() {
		super();
		model = new NodeModel();
	}
	
	/**
	 * Attach a model, preserving its world vectors, and saving its old parent for later re-attachment.
	 * @param model
	 */
	public void addModel(Model model)
	{
		modelMap.put(model, model.getParent());
		
		Models.attachPreservingWorld(model, getModel());
	}
	
	public Collection<Model> getChildModels()
	{
		return modelMap.keySet();
	}
	
	/**
	 * Remove each model, return it to its previous parent with preserved world vectors
	 */
	public void detachModels()
	{
		for(Model model:modelMap.keySet())
		{
			ParentModel parent = modelMap.get(model);
			if(parent!=null)
			{
				Models.attachPreservingWorld(model, parent);
			}else
			{
				getModel().detachChild(model);
			}
		}
	}
	
	public NodeModel getModel() {
		return model;
	}

	
	public Action<?> getAction(Type type) throws ActionTypeException {
		switch(type)
		{
	
		case MOVE:
			return new Move();
		
		case MOVE_RESTRICTED:
			return new MoveRestrictedInfo();
			
		case ROTATE:
			return new Rotate();
			
		case FOCUS:
			return new Focus();

		case ORIENTATION:
			return new OrientationInfo();

		case MODEL:
			return new ModelInfo();

		case CENTER:
			return new Center();

		default:
			throw new ActionTypeException();

		}
	}
	
	protected class ModelInfo extends ModelInformation
	{

		
		public Model getCollisionModel() {
			return getModel();
		}
		
	}
	
	protected class Rotate extends RotateAction
	{

		
		public Actionable getControlled() {
			return ActionNode.this;
		}

		
		public boolean doAction() {
			getModel().getLocalRotation().set(rotation);
			getModel().updateWorldData();
		//	visualModel.getNode().getLocalTranslation().set(position);
			getModel().signalMoved();
			return true;
		}
		
		public void setRotation(Quaternion rotation) {
			oldRotation.set(getModel().getLocalRotation());
			
			this.rotation.set(rotation);
		}

		
		public boolean undoAction() {
			getModel().signalMoved();
		//	visualModel.getNode().getLocalTranslation().set(oldPosition);
			getModel().getLocalRotation().set(oldRotation);
			getModel().updateWorldData();
			return true;
		}
	};
	protected class Move extends MoveAction
	{

		
		public boolean doAction() {
			getModel().updateWorldData();
			getModel().getParent().worldToLocal(position, getModel().getLocalTranslation());
			getModel().updateWorldData();
			getModel().signalMoved();
			//getModel().getSpatial().getChild(0).updateWorldVectors();
			
			return true;
		}

		
		public boolean undoAction() {
			if (getModel() == null || getModel().getParent() == null)
				System.out.println();
			getModel().getParent().updateWorldData();
			//visualModel.getNode().updateWorldVectors();
			getModel().getParent().worldToLocal(oldPosition, getModel().getLocalTranslation());
			getModel().updateWorldData();
			getModel().signalMoved();
			return true;
		}

		
		public void setPosition(Vector3f position) 
		{
			getModel().getParent().updateWorldData();
			getModel().updateWorldData();
			oldPosition.set(getModel().getWorldTranslation());
			this.position.set(position);
		}
		

		
		public Actionable getControlled() {
			return ActionNode.this;
		}

		
	};
	protected static class MoveRestrictedInfo extends MoveRestrictedInformation
	{
		
		public Vector3f getRestrictedPosition(Vector3f from) {
			//Get the closest grid node to the provided position;	
			
			from.subtractLocal(ActionToolSettings.getInstance().getGridOrigin().getValue());
			from.divideLocal(ActionToolSettings.getInstance().getGridUnits().getValue());
			from.x = Math.round(from.x);
			from.y = Math.round(from.y);
			from.z = Math.round(from.z);
			from.multLocal(ActionToolSettings.getInstance().getGridUnits().getValue());
			return from;
		}	
	}
	protected class Center extends CenterAction
	{

		
		public void setCenter(Vector3f centerOn) {
			getModel().updateWorldData();
			this.oldTranslation.set(getModel().getWorldTranslation());
			this.center.set(centerOn);
			
		}

		
		public boolean doAction() {
			getModel().recenter(this.center);
			return true;
		}

		
		public Actionable getControlled() {
			return ActionNode.this;
		}

		
		public boolean undoAction() {
			getModel().recenter(this.oldTranslation);
			return true;
		}

	}
	private class Focus extends FocusInformation
	{
		
		
		public Actionable getControlled() {
			return ActionNode.this;
		}

		
		public Vector3f getCenterVector() 
		{
			getModel().updateWorldData();
			return getModel().getWorldTranslation();
		}
		
		
		public Model getCenterModel() 
		{
			
			return getModel();
		}
	};
	private class OrientationInfo extends OrientationInformation
	{		


		
		public Actionable getControlled() {
			return ActionNode.this;
		}		
	}	
}
