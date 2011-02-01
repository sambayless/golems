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

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.golemgame.mvc.PropertyStore;

public abstract class Action<E extends Action<?>> implements Cloneable,Serializable
{
	private static final long serialVersionUID = 1L;//Actions are NOT serializable
	//Actions are retrieved from actionable elements
	//Subclasses of action will provide setting methods to define the 
	//properties of the action
	
	/**
	 * Some actions manipulate one or more property stores.
	 * Common actions (such as refreshing, etc) can be done through this interface
	 */
	private PropertyStore[] stores;
	
	protected void setStore(PropertyStore stores) {
		this.stores = new PropertyStore[]{ stores};
	}

	
	
	protected void setStores(PropertyStore[] stores) {
		this.stores = stores;
	}

	public PropertyStore[] getStores() {
		return stores;
	}

	public static enum Type
	{
		DESELECT(), SELECT(), MOVE(), ROTATE(), DELETE(),CREATE(), CONTROL(), ORIENTATION(), 
		FOCUS(), COPY(), VIEWMODE(),MODIFY(),SPACER(),COMBINED(),SELECTIONEFFECT(),PROPERTIES(),
		MOVE_RESTRICTED(), GET_GROUP(),MODEL(), CENTER(),SELECTINFO(),COLLISION_STATE(),STATIC_INFO(),
		SCALE(),CUSTOM_SELECTION(),COLLISION_INFO(), ANONYMOUS(),PHYSICAL_INFO(),SELECT_EFFECT_INFO(),
		STATE(),STATE_INFO(),SELECTION_PRIORITY(),SINGLE_AXIS_INFO,MAKE_GROUP(),ADD_MEMBER(),REMOVE_MEMBER(),
		CYCLE(),STORE(), ADD_COMPONENT, REMOVE_COMPONENT, COPY_COMPONENT, PROPERTY_STORE, ADD_WIRE, REMOVE_WIRE,
		COORDINATE_SYSTEM_INFO(), MERGE_COMPONENT(),PROXY_PROPERTY_STATE(),SET_LAYER();
	}
	
	public static final Type SELECT = Type.SELECT;
	public static final Type MOVE = Type.MOVE;
	public static final Type ROTATE = Type.ROTATE;
	
	public static final Type CREATE = Type.CREATE;
	public static final Type DELETE = Type.DELETE;
	public static final Type CONTROL = Type.CONTROL;
	public static final Type ORIENTATION = Type.ORIENTATION;
	public static final Type FOCUS = Type.FOCUS;
	public static final Type COPY = Type.COPY;
	public static final Type VIEWMODE = Type.VIEWMODE;
	public static final Type MODIFY = Type.MODIFY;
	public static final Type SPACER = Type.SPACER;	
	public static final Type COMBINED = Type.COMBINED;
	public static final Type SELECTIONEFFECT = Type.SELECTIONEFFECT;
	public static final Type PROPERTIES = Type.PROPERTIES;
	public static final Type MOVE_RESTRICTED = Type.MOVE_RESTRICTED;
	public static final Type MODEL = Type.MODEL;
	public static final Type GET_GROUP = Type.GET_GROUP;
	public static final Type CENTER = Type.CENTER;
	public static final Type SELECTINFO = Type.SELECTINFO;
	public static final Type COLLISION_STATE = Type.COLLISION_STATE;
	public static final Type STATIC_INFO = Type.STATIC_INFO;
	public static final Type SCALE = Type.SCALE;
	public static final Type CUSTOM_SELECTION = Type.CUSTOM_SELECTION;
	public static final Type COLLISION_INFO = Type.COLLISION_INFO;
	public static final Type PHYSICAL_INFO = Type.PHYSICAL_INFO;
	public static final Type SELECT_EFFECT_INFO = Type.SELECT_EFFECT_INFO;
	public static final Type STATE = Type.STATE;
	public static final Type STATE_INFO = Type.STATE_INFO;
	public static final Type SELECTION_PRIORITY = Type.SELECTION_PRIORITY;
	public static final Type SINGLE_AXIS_INFO = Type.SINGLE_AXIS_INFO;
	public static final Type MAKE_GROUP = Type.MAKE_GROUP;
	public static final Type ADD_MEMBER = Type.ADD_MEMBER;
	public static final Type REMOVE_MEMBER = Type.REMOVE_MEMBER;
	public static final Type CYCLE = Type.CYCLE;
	public static final Type STORE = Type.STORE;
	public static final Type ADD_COMPONENT = Type.ADD_COMPONENT;
	public static final Type REMOVE_COMPONENT = Type.REMOVE_COMPONENT;
	public static final Type COPY_COMPONENT = Type.COPY_COMPONENT;
	public static final Type PROPERTY_STORE = Type.PROPERTY_STORE;
	public static final Type ADD_WIRE = Type.ADD_WIRE;
	public static final Type REMOVE_WIRE = Type.REMOVE_WIRE;
	public static final Type COORDINATE_SYSTEM_INFO = Type.COORDINATE_SYSTEM_INFO;
	public static final Type MERGE_COMPONENT = Type.MERGE_COMPONENT;
	public static final Type SET_LAYER = Type.SET_LAYER;
	
	
	/**
	 * Flag to indicate whether this is a 'fresh' action, or if it has been used before and is now doing/undoing.
	 */
	private boolean firstUse = true;
	
	/**
	 * Signal any special handling this action should have when being undone/redone
	 */
	private UndoManager.UndoProperties properties = UndoManager.UndoProperties.NORMAL;
	
	
	public boolean isFirstUse() {
		return firstUse;
	}

	public void setFirstUse(boolean firstUse) {
		this.firstUse = firstUse;
	}

	public abstract Type getType();
	
	/**
	 * Get the actionable instance from which this action was retrieved
	 * @return
	 */
	public Actionable getControlled()
	{
		return null;
	}

	/**
	 * May perform an action, as defined by subclasses of the action type
	 * @return True if the action succeeded, false if it failed
	 */
	public boolean doAction()
	{
		return false;
	}
	/**
	 * May undo an action. Behaviour is undefined if 'doAction' has not yet been called.
	 * @return True if the action was succesfuly undone, false otherwise
	 */
	public boolean undoAction()
	{
		return false;
	}

	/**
	 * Attempt to produce a new instance of this action.
	 * @return A new instance of this action, or null.
	 */
	@SuppressWarnings("unchecked")
	public E copy()
	{
		try
		{
			return (E) this.clone();
		}catch(CloneNotSupportedException e)
		{
			return null;
		}
	}
	
	/**
	 * Attempt to produce a new action which is a combination of this action,
	 * and another instance of the same type. For example, the merger of two
	 * Move actions would move a node from the starting position of this instance
	 * to the end position of action 'mergeWith.' 
	 * @param mergeWith The action to merge with. If the merge succeeds, mergeWith will be performed second.
	 * @return The newly created instance of action, which is a merger this action and 'mergeWith'
	 * @throws ActionMergeException if the merge fails.
	 */
	public E merge(E mergeWith) throws ActionMergeException
	{
		throw new ActionMergeException();
	}
	
	/**
	 * Get a string description of the Action.
	 * @return
	 */
	public abstract String getDescription();

	private void writeObject(ObjectOutputStream out) throws IOException
	{
		throw new NotSerializableException();
	}
	private void readObject(ObjectInputStream in) throws IOException
	{
		throw new NotSerializableException();
	}

	/**
	 * Signals any special handling this action should have when being undone/redone
	 */
	public UndoManager.UndoProperties getProperties() {
		return properties;
	}

	/**
	 * Signal any special handling this action should have when being undone/redone
	 */
	public void setProperties(UndoManager.UndoProperties properties) {
		this.properties = properties;
	}
	
	/**
	 * Get the dependency set of this action, if it has one.
	 * @return
	 */
	public ActionDependencySet getDependencySet()
	{
		return dependencySet;
	}
	
	private ActionDependencySet dependencySet = null;
	
	public void setDependencySet(ActionDependencySet dependencySet)
	{
		this.dependencySet = dependencySet;
	}
}
