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
package com.golemgame.tool.control;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.golemgame.model.Model;
import com.golemgame.model.ParentModel;
import com.golemgame.model.effect.ModelEffectPool;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.mvc.PropertyState;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SimplePropertyState;
import com.golemgame.tool.ActionToolSettings;
import com.golemgame.tool.Tool;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionMergeException;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.MoveAction;
import com.golemgame.tool.action.SelectAction;
import com.golemgame.tool.action.SelectionEffect;
import com.golemgame.tool.action.ViewModeAction;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.action.information.FocusInformation;
import com.golemgame.tool.action.information.ModelInformation;
import com.golemgame.tool.action.information.MoveRestrictedInformation;
import com.golemgame.tool.action.information.OrientationInformation;
import com.golemgame.tool.action.information.ProxyStateInformation;
import com.golemgame.tool.action.information.SelectionEffectInformation;
import com.golemgame.tool.action.information.SelectionInformation;
import com.golemgame.tool.action.information.SelectionPriorityInformation;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.Sphere;




public abstract class ControlPoint<E extends ControllableModel> implements Actionable,Serializable {
	private static final long serialVersionUID = 1L;

	
	protected static final float radius = 0.4f;
	protected static final Sphere baseSphere = new Sphere("control sphere base", new Vector3f(), 30, 30, radius);
	protected static final Sphere baseCollisionSphere = new Sphere("ControlSphere", new Vector3f(), 6, 6, radius);
	boolean used = false;
	boolean selected = false;
	

	@SuppressWarnings("unchecked")
	protected ControlPoint<E>[] siblings = new ControlPoint[]{this};
	protected Actionable controlParent = null;

	public PropertyStore getControlStore() {
		if(controllable == null)
			return null;
		return controllable.getPropertyStore();
	}


	protected E controllable;
	
	protected NodeModel visualNode = new NodeModel();
	
	//control points do not keep references to the objects they are controlling; they are reusable
	
	/**
	 * Return the control distance (in local coordinates/rotation)
	 */
	protected abstract Vector3f getControlDistance();

	public abstract void updatePosition();
	
	public NodeModel getModel()
	{
		return visualNode;
		
	}

	
	
	protected Node getNode()
	{
		return visualNode.getNode();
	}
	
	public void setControlSet(ControlPoint<E>[] siblings)
	{
		this.siblings = siblings;
	}
		
	public void enable(E controllable)
	{
		this.controllable = controllable;
		
		
		
		
		this.setVisible(true);
	}
	
	public E getControllable() {
		return controllable;
	}

	public void disable()
	{
		this.setVisible(false);
		this.controllable = null;
	
	}
	
	protected abstract void move(E toControl, Vector3f position);

	protected ControlPoint()
	{
		super();

		ModelEffectPool.getInstance().getControlPointEffect().attachModel(this.getModel());
		this.getModel().setActionable(this);
		
	}
	

	public void setVisible(boolean display)
	{
		if(controllable == null || controllable.getParent() == null)
			return;//this was added as a quick fix.
		
		if (display)
		{
			//FIXME: this can be null and crash (controllable).Seens to cause the shift+control bug.
			if (controllable.getParent().isParent())
			{
				((ParentModel)controllable.getParent()).addChild(visualNode);
				updatePosition();
				((ParentModel)controllable.getParent()).updateWorldData();
			}
			
		
		}else
		{
			if (controllable.getParent().isParent())
			{
				((ParentModel)controllable.getParent()).detachChild(visualNode);
				((ParentModel)controllable.getParent()).updateWorldData();
			}
		}
	}
	
	public Action<?> getAction(Type type) throws ActionTypeException {
		if(this.controllable == null)
			throw new ActionTypeException();
		switch (type)
		{
		case SELECT:
			return new Select(controlParent);
		//case MOVE_RESTRICTED:
		//	return moveRestricted;
		case MODEL:
			return new ModelInfo();
		case MOVE:
			return new Move();
		case FOCUS:
			return focus;
		case VIEWMODE:
			return new ControlView();
		case SELECTINFO:
			return new SelectionInfo();
		
		case SELECTIONEFFECT:
			return new SelectionEffectImpl();
		case SELECT_EFFECT_INFO:
			return new SelectionEffectInfo();
		case SELECTION_PRIORITY:
			return new SelectionPriorityInfo();
		case MOVE_RESTRICTED:
			return new MoveRestrictedInfo();
		case PROXY_PROPERTY_STATE:
		{
			if(controllable!=null)
				return new ProxyStateInfo();
			else
				throw new ActionTypeException("Cannot select control point");
		}
		default:
			throw new ActionTypeException();
		}

			
	}
	
	private static String[] empty = new String[]{};
	protected String[] getKeys()
	{
		return empty;
	}
	
	protected class ProxyStateInfo extends ProxyStateInformation
	{
			@Override
		public PropertyState getCurrentState() {
				if(controllable!=null)
					return new SimplePropertyState(getControlStore(),getKeys());
				else
					return null;
		}
		
	}
	protected class ModelInfo extends ModelInformation
	{
		@Override
		public Actionable getControlled() {
			return ControlPoint.this;
		}
		
		public Model getCollisionModel() {
			return getModel();
		}
		
	}

	
	protected static class Select extends SelectAction
	{

		
		public Select(Actionable controlParent) {
			super();
			//this.controlParent = controlParent;
		}

		
		public boolean doAction() 
		{			
/*			selected = select;
			if (!selected && isUsed())//send deselection signal only if neccesary
			{
				controllable.updateModel();
				 if (siblings != null)
				 {	
					for (ControlPoint<?> control:siblings)
						control.updatePosition();
				 }else
					 updatePosition();
				 
				used = false;
			}*/
			return true;
		}

		
/*		public Actionable getControlled() {
			return ControlPoint.this;
		}
*/
		
		public boolean undoAction() {
			//selected = !select;
			
			return true;
		}
		
		
		
	};
	
	
	private class SelectionInfo extends SelectionInformation
	{
		@Override
		public Actionable getControlled() {
			return ControlPoint.this;
		}
		
		public boolean isMultipleSelectable() {
			return false;
		}
		
	}
	
	private class Move extends MoveAction
	{

		private PropertyState before = PropertyState.getDummy();
		private PropertyState after = PropertyState.getDummy();
		
		public void setPosition(Vector3f position) 
		{
			 
			if(controllable != null)
			{
				controllable.getWorldFromRelative(position, oldPosition);
			this.position.set(position);
			}
		}

		
		public boolean doAction() 
		{
			if (isFirstUse())
			{
				
				if (controllable == null)
					return false;
				before = controllable.getCurrentState();
				used = true;//flag that this control point has been used
			//	controllable.getParent().updateWorldData();
			
				move(controllable,controllable.getRelativeFromWorld(position, new Vector3f()));
				//controllable.updateModel();
				after = controllable.getCurrentState();
			}else
			{
				after.restore();
				after.refresh();
			}
			return true;
		}
		
		
		public Actionable getControlled() {
			return ControlPoint.this;
		}

		
		public boolean undoAction() 
		{//this assumes that control point is attached to the same node as toControl
			
		/*	if (toControl == null)
				return false;
			move(toControl,toControl.getRelativeFromWorld(oldPosition, new Vector3f()));*/
			before.restore();
			before.refresh();
			return true;
		}

		
		public MoveAction merge(MoveAction mergeWith)
				throws ActionMergeException {
	
			if (mergeWith instanceof ControlPoint.Move)
			{
				Move move = new Move();
				move.after  = ((ControlPoint.Move) mergeWith).after;
				move.before = before;
				return move;
			}else
				throw new ActionMergeException();
			
		
			
		
		}
		
	};

		

		//private OrientationInformation orientationInfo = new OrientationInfoImpl();
		protected class OrientationInfoImpl extends OrientationInformation
		{
			
	
		
			
			public boolean useAxis() {
				return true;
			}

			
			public Actionable getControlled() {
				return ControlPoint.this;
			}
			
		}
		
		private FocusInformation focus = new Focus();
		private class Focus extends FocusInformation
		{

			
			public Actionable getControlled() {
				return ControlPoint.this;
			}

			
			public Vector3f getCenterVector() 
			{
				Tool.updateToWorld(visualNode.getSpatial());
				return visualNode.getWorldTranslation();
			}
			
			
			public Model getCenterModel() 
			{
				
				return visualNode;
			}
		};
		public class ControlView extends ViewModeAction
		{

			
			public Actionable getControlled() {
				return ControlPoint.this;
			}

			
			public boolean doAction() {
				// ControlPoint.this.toControl.getMachine().setViewMode(Machine.MATERIAL);
				
				return true;
				
			}
			
		}

		
		private class SelectionEffectInfo extends SelectionEffectInformation
		{

			@Override
			public Actionable getControlled() {
				return ControlPoint.this;
			}
			public boolean usesStandardEffects() {
				return false;
			}
			
		}
		
		private class SelectionEffectImpl extends SelectionEffect
		{
			@Override
			public Actionable getControlled() {
				return ControlPoint.this;
			}
			
			public void setAction(Type type) {
				
			}

			
			public boolean doAction() {
				if (engage)
				{
					ModelEffectPool.getInstance().getSelectedEffect().attachModel(getModel());

				}else
					ModelEffectPool.getInstance().getControlPointEffect().attachModel(getModel());

			
				visualNode.getSpatial().updateRenderState();
				return true;
			}
			
		}
		
		private class SelectionPriorityInfo extends SelectionPriorityInformation
		{
			@Override
			public Actionable getControlled() {
				return ControlPoint.this;
			}
			
			public int getSelectionPriority() {
				return 1024;
			}
			
		}
		
		protected  class MoveRestrictedInfo extends MoveRestrictedInformation
		{
			E toControl = null;
			
			@Override
			public Actionable getControlled() {
				return ControlPoint.this;
			}
			
			public MoveRestrictedInfo() {
				super();
				toControl = controllable;
			}



			public Vector3f getRestrictedPosition(Vector3f from) {
				//Get the closest grid node to the provided position;	
				
				
				Vector3f pos = toControl.getRelativeFromWorld(from, new Vector3f());
			//	System.out.println(pos);
				//pos.subtractLocal(ActionToolSettings.getInstance().getGridOrigin().getValue());
				pos.divideLocal(ActionToolSettings.getInstance().getGridUnits().getValue());
				pos.x = Math.round(pos.x);
				pos.y = Math.round(pos.y);
				pos.z = Math.round(pos.z);
				pos.multLocal(ActionToolSettings.getInstance().getGridUnits().getValue());
				return toControl.getWorldFromRelative(pos, from);
			}	
		}
		
		

		public boolean isUsed() {
			return used;
		}	
		

		private void writeObject(ObjectOutputStream out) throws IOException
		{
			throw new NotSerializableException();
		}
		private void readObject(ObjectInputStream in) throws IOException
		{
			throw new NotSerializableException();
		}

		
		
}
