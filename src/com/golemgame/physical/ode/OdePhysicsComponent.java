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
package com.golemgame.physical.ode;

import com.golemgame.model.spatial.SpatialModel;
import com.golemgame.mvc.golems.SurfacePropertiesInterpreter.SurfaceType;
import com.golemgame.physical.PhysicsComponent;
import com.golemgame.physical.sound.SoundComponent;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jphya.body.Body;
import com.jphya.surface.FunctionSurface;

public class OdePhysicsComponent implements PhysicsComponent{
	private OdePhysicsObject parent = null;
	private SpatialModel spatial = null;
	private float mass = 0;
	private SoundComponent soundComponent = null;
	private SurfaceType soundSurface = SurfaceType.NONE;
	
	public void setSoundSurface(SurfaceType soundSurface) {
		this.soundSurface = soundSurface;
	}
	public float getMass() {
		return mass;
	}
	public void setMass(float mass) {
		this.mass = mass;
	}
	public void setSpatial(SpatialModel spatial) {
		this.spatial = spatial;
	}
	private final PhysicsCollisionGeometry collisionGeometry;
	public PhysicsCollisionGeometry getCollisionGeometry() {
		return collisionGeometry;
	}
	

	/*	public OdePhysicsComponent(PhysicsCollisionGeometry collisionGeometry) {
		super();
	
		this.collisionGeometry = collisionGeometry;
	}*/

	
	public OdePhysicsComponent(PhysicsCollisionGeometry collisionGeometry,SpatialModel spatial) {
		super();
		this.spatial = spatial;
		this.collisionGeometry = collisionGeometry;
		
	}
	public OdePhysicsObject getParent() {
		return parent;
	}
	public Quaternion getLocalRotation(Quaternion store) {
		store.set(collisionGeometry.getLocalRotation());
		return store;
	}
	public Vector3f getLocalTranslation(Vector3f store) {
		store.set(collisionGeometry.getLocalTranslation());
		return store;
	}
	
	public Vector3f getWorldTranslation(Vector3f store) {
		store.set(collisionGeometry.getWorldTranslation());
		return store;
	}
	
	protected void setParent(OdePhysicsObject parent) {
		this.parent = parent;
	}
	public SpatialModel getSpatial() {
		return spatial;
	}

	public SoundComponent getSoundComponent() {
		return soundComponent;
	}
	public void setSoundComponent(SoundComponent soundComponent) {
		this.soundComponent = soundComponent;
	}
	public SurfaceType getSoundSurface() {
		return soundSurface;
	}
	private float linearResonanceScalar = 0;
	public void setLinearResonanceScalar(float linearResonanceScalar) {
		this.linearResonanceScalar = linearResonanceScalar;
	}
	public float getLinearResonanceSize() {
		
		return linearResonanceScalar;
	}


	
	
	
}
