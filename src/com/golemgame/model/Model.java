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
package com.golemgame.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import com.golemgame.model.effect.ModelEffect;
import com.golemgame.model.effect.ModelEffect.ModelEffectType;
import com.golemgame.model.texture.TextureController;
import com.golemgame.structural.collision.CollisionManager;
import com.golemgame.structural.collision.CollisionMember;
import com.golemgame.tool.action.Actionable;
import com.golemgame.util.Deletable;
import com.golemgame.views.Viewable.ViewMode;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector3f;

public interface Model extends Deletable, Serializable {
	
	public TextureController getTextureController();
		
	public void setLoadable(boolean isLoadable);
	
	public void addModelListener(ModelListener listener);
	public boolean removeModelListener(ModelListener listener);
	
	public void registerCollisionMember(CollisionMember member);
	
	public void clearOtherCollisionManagers(CollisionManager keep);
	
	public void removeCollisionMember(CollisionMember member);
	
	public CollisionMember getCollisionMember(CollisionManager from);
	
	//public void addModelEffect(ModelEffect effect);
//	public void removeModelEffect(ModelEffectType type);
//	public void clearModelEffects();
	
	public ModelData getModelData();

	public ModelData getWorldData();
	
	public void loadModelData(ModelData data);
	
	public void loadModelData(Model from);
	
	public void loadWorldData(Model from);
	
	public void loadWorldData(ModelData data);
	

	
	
	 /**
	  * If this Model is associated with an Actionable, return it.
	  * @return
	  */
	public Actionable getActionable();
	
	public void setActionable(Actionable actionable);
	/**
	 * Update the world data of this model. For efficiency reasons, this method does NOT signal update listeners
	 * that the model has changed - so signalModelChanged must be called manually.
	 */
	public void updateWorldData();
	
	/**
	 * Update the model data of this model. Note - this is slow.
	 * This method will automatically signal that the model has changed to listeners.
	 */
	public void updateModelData();
	
	
	
//	/**
//	 * Inform model listeners that the model has changed.
//	 */
//	public void signalModelChanged();
	
	public Collection<ModelListener> getListeners();
	
	public void setVisible(boolean visible);
	public boolean isVisible();
	
	public void setParentModel(ParentModel model) throws ModelTypeException;
	public ParentModel getParent();
	
	public void setViewMode(ViewMode mode);
	
	/**
	 * Inform model listeners that this model has moved.
	 */
	public void signalMoved();
	
	/**
	 * Set whether this visual model can be a part of a collision, in the sense of Spatial.isCollidable.
	 * @param touchable
	 */
	public void setTouchable(boolean touchable);
	public boolean isTouchable();

	public boolean isParent();
	
	
	/**
	 * Get the dimensions of this model, in world units/rotation
	 * @return
	 */
	public Vector3f getDimensions();

	
	/**
	 * Test if the two models are colliding, as defined by implementing classes.
	 * @param collidesWith
	 * @return
	 * @throws ModelTypeException
	 */
	public boolean testCollision(Model collidesWith) throws ModelTypeException;
	
	/**
	 * Given a root model, return a list of all child models of that root that are NOT parent models, and that also collide with this model or its child models.
	 * If the root model is not a parent, then it too may be returned.
	 * @param collideWith
	 * @param ignoreList
	 * @param store A collection to store collisions in. The collection is cleared first.
	 * @return
	 * @throws ModelTypeException
	 */
	public void findCollisions(Model collideWith, Set<Model> ignoreList, ArrayList<ModelCollision> store) throws ModelTypeException;

	/**
	 * Given a root model, return a list of all child models of that root that are NOT parent models, and that also collide with this model.
	 * If the root model is not a parent, then it too may be returned.
	 * The keys in the map are sub-models (or the root model) of this model, the values are the models/sub-models of collide with.
	 * @param collideWith
	 * @param ignoreList
	 * @param store A Collection to add the collisions to. Note: This Collection is not cleared in the process; results are added to the array. This is useful for keeping a tally of the collisions from several tests.
	 * @return
	 * @throws ModelTypeException
	 */
	public void findCollisionsConcat(Model collideWith, Set<Model> ignoreList, ArrayList<ModelCollision> store) throws ModelTypeException;

	
	/**
	 * Return a list of all (non-parent) models that intersect this ray and that either are this model, or are children of this model.
	 * @param useTriangleAccuracy TODO
	 */
	public void intersectRay(Ray toTest, ArrayList<ModelIntersectionData> store, boolean useTriangleAccuracy);
	
	/**
	 * Sets aspects of this model (as defined by implementing classes) to match the provided model.
	 * This must include the local translation, rotation, and scale.
	 * @param setFrom
	 */
	public void setModelData(Model setFrom) throws ModelTypeException;
	
	
	public Vector3f getLocalTranslation();
	public Vector3f getLocalScale();
	public void setLocalTranslation(Vector3f translation);
	public void setLocalScale(Vector3f translation);
	public void setLocalRotation(Quaternion rotation);
	public Quaternion getLocalRotation();
	public Vector3f getWorldTranslation();
	
	public Vector3f worldToLocal(Vector3f worldTranslation,	Vector3f store);
	
	/**
	 * Gets the world location of the given relative location.
	 * @param localTranslation
	 * @param store
	 * @return
	 */
	public Vector3f localToWorld(Vector3f localTranslation,	Vector3f store);
	
	/**
	 * Set the local translation to match the provided world translation
	 */
	public void setWorldTranslation(Vector3f translation);
	
	/**
	 * Detach this model from its parent, if it has one.
	 */
	public void detachFromParent();
	
	public Quaternion getWorldRotation();
	
	public Vector3f getWorldScale();
		
	public void refreshLockedData();
	
	public BoundingVolume getWorldBound();
	
	public static class ModelTypeException extends IllegalStateException
	{	
		private static final long serialVersionUID = 1L;

		public ModelTypeException() {
			super();
			
		}

		public ModelTypeException(String message, Throwable cause) {
			super(message, cause);
		
		}

		public ModelTypeException(String message) {
			super(message);

		}

		public ModelTypeException(Throwable cause) {
			super(cause);
	
		}


		
	}

	public void setCollidable(boolean collidable);
	public boolean isCollidable();
}


