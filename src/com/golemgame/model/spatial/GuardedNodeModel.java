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
package com.golemgame.model.spatial;

import java.util.concurrent.locks.ReentrantLock;

import com.golemgame.model.Model;
import com.golemgame.model.ModelData;
import com.golemgame.model.ParentModel;
import com.golemgame.states.StateManager;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;

/**
 * Note: NOTE SAVEABLE!
 * @author Sam
 *
 */
public class GuardedNodeModel extends NodeModel {

	private static final long serialVersionUID = 1L;
	private ReentrantLock guardLock;
	private ThreadGuardingNode guard;
	
	
	public GuardedNodeModel(ReentrantLock lock) {
		super();
		this.guardLock = lock;
		guard.setGuardLock(lock);
	}

	@Override
	protected Spatial buildSpatial() {
		guard =  new ThreadGuardingNode();
		
		return guard;
	}

	@Override
	public void addChild(Model child) throws ModelTypeException {
		if(guardLock == null || guardLock.isHeldByCurrentThread())
		super.addChild(child);
		else
			StateManager.getLogger().warning("Thread access to update denied");
	}

	@Override
	public void centerOn(Model centerOn) throws ModelTypeException {
		if(guardLock == null || guardLock.isHeldByCurrentThread())
		super.centerOn(centerOn);
		else
			StateManager.getLogger().warning("Thread access to update denied");
	}

	@Override
	public void delete() {
		if(guardLock == null || guardLock.isHeldByCurrentThread())
		super.delete();
		else
			StateManager.getLogger().warning("Thread access to update denied");
	}

	@Override
	public void detachAllChildren() {
		if(guardLock == null || guardLock.isHeldByCurrentThread())
		super.detachAllChildren();
		else
			StateManager.getLogger().warning("Thread access to update denied");
	}

	@Override
	public void detachChild(Model child) throws ModelTypeException {
		if(guardLock == null || guardLock.isHeldByCurrentThread())
		super.detachChild(child);
		else
			StateManager.getLogger().warning("Thread access to update denied");
	}

	@Override
	public void recenter(Vector3f center) {
		if(guardLock == null || guardLock.isHeldByCurrentThread())
		super.recenter(center);
		else
			StateManager.getLogger().warning("Thread access to update denied");
	}

	@Override
	public void setUpdateLocked(boolean updateLocked) {
		if(guardLock == null || guardLock.isHeldByCurrentThread())
		super.setUpdateLocked(updateLocked);
		else
			StateManager.getLogger().warning("Thread access to update denied");
	}

	@Override
	public void updateWorldData() {
		if(guardLock == null || guardLock.isHeldByCurrentThread())
		super.updateWorldData();
		else
			StateManager.getLogger().warning("Thread access to update denied");
	}

	@Override
	public void detachFromParent() {
		if(guardLock == null || guardLock.isHeldByCurrentThread())
		super.detachFromParent();
		else
			StateManager.getLogger().warning("Thread access to update denied");
	}

	@Override
	public void loadModelData(Model from) {
		if(guardLock == null || guardLock.isHeldByCurrentThread())
		super.loadModelData(from);
		else
			StateManager.getLogger().warning("Thread access to update denied");
	}

	@Override
	public void loadModelData(ModelData data) {
		if(guardLock == null || guardLock.isHeldByCurrentThread())
		super.loadModelData(data);
		else
			StateManager.getLogger().warning("Thread access to update denied");
	}

	@Override
	public void loadWorldData(Model from) {
		if(guardLock == null || guardLock.isHeldByCurrentThread())
		super.loadWorldData(from);
		else
			StateManager.getLogger().warning("Thread access to update denied");
	}

	@Override
	public void loadWorldData(ModelData data) {
		if(guardLock == null || guardLock.isHeldByCurrentThread())
		super.loadWorldData(data);
		else
			StateManager.getLogger().warning("Thread access to update denied");
	}

	@Override
	public void refreshLockedData() {
		if(guardLock == null || guardLock.isHeldByCurrentThread())
		super.refreshLockedData();
		else
			StateManager.getLogger().warning("Thread access to update denied");
	}

	@Override
	public void setParentModel(ParentModel model) throws ModelTypeException {
		if(guardLock == null || guardLock.isHeldByCurrentThread())
		super.setParentModel(model);
		else
			StateManager.getLogger().warning("Thread access to update denied");
	}

	@Override
	public void setWorldTranslation(Vector3f translation) {
		if(guardLock == null || guardLock.isHeldByCurrentThread())
		super.setWorldTranslation(translation);
		else
			StateManager.getLogger().warning("Thread access to update denied");
	}

	@Override
	public void updateModelData() {
		if(guardLock == null || guardLock.isHeldByCurrentThread())
		super.updateModelData();
		else
			StateManager.getLogger().warning("Thread access to update denied");
	}

	@Override
	public void updateToWorld() {
		if(guardLock == null || guardLock.isHeldByCurrentThread())
		super.updateToWorld();
		else
			StateManager.getLogger().warning("Thread access to update denied");
	}
	

	
	
}
