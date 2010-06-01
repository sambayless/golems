package com.golemgame.model.spatial;

import java.util.concurrent.locks.ReentrantLock;

import com.jme.bounding.BoundingVolume;
import com.jme.scene.Node;
import com.jme.scene.Spatial;

/**
 * A node that only passes update events to its children if the current thread controls the given lock.
 * Prevents unauthorized, accidental access to the physics thread (to prevent possible Locked Space ODE crashes).
 * @author Sam
 *
 */
public class ThreadGuardingNode extends Node {
	
	//private final Thread updateThread;

	private static final long serialVersionUID = 1L;
	private ReentrantLock guardLock;
	
	public void setGuardLock(ReentrantLock guardLock) {
		this.guardLock = guardLock;
	}

	public ThreadGuardingNode() {
		this(dummyLock);
	}

	public ThreadGuardingNode( ReentrantLock guardLock) {
		super();
		this.guardLock = guardLock;
	}

	@Override
	public void updateModelBound() {
		if(guardLock.isHeldByCurrentThread())
			super.updateModelBound();
	/*	else
			StateManager.getLogger().warning("Thread access to update denied");*/
	}

	@Override
	public void updateWorldBound() {
		if(guardLock.isHeldByCurrentThread())
			super.updateWorldBound();
		/*else
			StateManager.getLogger().warning("Thread access to update denied");*/
	}

	@Override
	public void updateWorldData(float time) {
		if(guardLock.isHeldByCurrentThread())
			super.updateWorldData(time);
	/*	else
			StateManager.getLogger().warning("Thread access to update denied");*/
	}

	@Override
	public void updateGeometricState(float time, boolean initiator) {
		if(guardLock.isHeldByCurrentThread())
			super.updateGeometricState(time, initiator);
		/*else
			StateManager.getLogger().warning("Thread access to update denied");*/
	}

	@Override
	public void updateWorldVectors() {
		if(guardLock.isHeldByCurrentThread())
			super.updateWorldVectors();
	/*	else
			StateManager.getLogger().warning("Thread access to update denied");*/
	}

	@Override
	public int attachChild(Spatial child) {
		if(guardLock.isHeldByCurrentThread())
			return super.attachChild(child);
	/*	else
			StateManager.getLogger().warning("Thread access to update denied");*/
		return -1;
	}

	@Override
	public int attachChildAt(Spatial child, int index) {
		if(guardLock.isHeldByCurrentThread())
			return super.attachChildAt(child, index);
	/*	else
			StateManager.getLogger().warning("Thread access to update denied");*/
		return -1;
	}

	@Override
	public void detachAllChildren() {
		if(guardLock.isHeldByCurrentThread())
			super.detachAllChildren();
	/*	else
			StateManager.getLogger().warning("Thread access to update denied");*/
	}

	@Override
	public int detachChild(Spatial child) {
		if(guardLock.isHeldByCurrentThread())
			return super.detachChild(child);
/*		else
			StateManager.getLogger().warning("Thread access to update denied");*/
		return -1;
	}

	@Override
	public Spatial detachChildAt(int index) {
		if(guardLock.isHeldByCurrentThread())
			return super.detachChildAt(index);
	/*	else
			StateManager.getLogger().warning("Thread access to update denied");*/
		return null;
	}

	@Override
	public int detachChildNamed(String childName) {
		if(guardLock.isHeldByCurrentThread())
			return super.detachChildNamed(childName);
	/*	else
			StateManager.getLogger().warning("Thread access to update denied");*/
		return -1;
	}

	@Override
	public void lockBounds() {
		if(guardLock.isHeldByCurrentThread())
			super.lockBounds();
	/*	else
			StateManager.getLogger().warning("Thread access to update denied");*/
	}

	@Override
	public void lockBranch() {
		if(guardLock.isHeldByCurrentThread())
			super.lockBranch();
		/*else
			StateManager.getLogger().warning("Thread access to update denied");*/
	}

	@Override
	public void lockMeshes() {
		if(guardLock.isHeldByCurrentThread())
			super.lockMeshes();
	/*	else
			StateManager.getLogger().warning("Thread access to update denied");*/
	}

	@Override
	public void lockShadows() {
		if(guardLock.isHeldByCurrentThread())
			super.lockShadows();
/*		else
			StateManager.getLogger().warning("Thread access to update denied");*/
	}

	@Override
	public void lockTransforms() {
		if(guardLock.isHeldByCurrentThread())
			super.lockTransforms();
/*		else
			StateManager.getLogger().warning("Thread access to update denied");*/
	}

	@Override
	protected void refreshBranchToLeaves(int locksToDirty) {
		if(guardLock.isHeldByCurrentThread())
			super.refreshBranchToLeaves(locksToDirty);
	/*	else
			StateManager.getLogger().warning("Thread access to update denied");*/
	}

	@Override
	public void setModelBound(BoundingVolume modelBound) {
		if(guardLock.isHeldByCurrentThread())
			super.setModelBound(modelBound);
		/*else
			StateManager.getLogger().warning("Thread access to update denied");*/
	}

	@Override
	public void unlockBounds() {
		if(guardLock.isHeldByCurrentThread())
			super.unlockBounds();
	/*	else
			StateManager.getLogger().warning("Thread access to update denied");*/
	}

	@Override
	public void unlockBranch() {
		if(guardLock.isHeldByCurrentThread())
			super.unlockBranch();
	/*	else
			StateManager.getLogger().warning("Thread access to update denied");*/
	}

	@Override
	public void unlockMeshes() {
		if(guardLock.isHeldByCurrentThread())
			super.unlockMeshes();
	/*	else
			StateManager.getLogger().warning("Thread access to update denied");*/
	}

	@Override
	public void unlockShadows() {
		if(guardLock.isHeldByCurrentThread())
			super.unlockShadows();
	/*	else
			StateManager.getLogger().warning("Thread access to update denied");*/
	}

	@Override
	public void unlockTransforms() {
		if(guardLock.isHeldByCurrentThread())
			super.unlockTransforms();
	/*	else
			StateManager.getLogger().warning("Thread access to update denied");*/
	}
	
	private final static ReentrantLock dummyLock = new ReentrantLock()
	{
		private static final long serialVersionUID = 1L;

		@Override
		public boolean isHeldByCurrentThread() {
			return true;
		}
		
	};
	
}
