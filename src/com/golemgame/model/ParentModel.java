package com.golemgame.model;

import java.util.Collection;

import com.jme.math.Vector3f;

public interface ParentModel extends Model{
	
	public void addChild(Model child) throws ModelTypeException;
	public void detachChild(Model child) throws ModelTypeException;
	public void detachAllChildren();
	/**
	 * Centers this node on the coordinates provided, maintaining the world coordinates of all children
	 * @param center Coordinate to center on (in world coordinates)
	 */
	public void recenter(Vector3f centerOn);
	public Collection<Model> getChildren() ;
	
	public boolean isUpdateLocked();
	public void setUpdateLocked(boolean updateLocked);
}
