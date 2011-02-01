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

import java.util.Collection;

import com.golemgame.model.Model;
import com.golemgame.model.texture.TextureTypeKey.TextureShape;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GhostInterpreter;
import com.golemgame.physical.PhysicsComponent;
import com.golemgame.structural.StructuralAppearanceEffect;
import com.golemgame.structural.collision.CollisionMember;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsNode;

public class OdeGhostStructure extends OdeStructure {

	private StructuralAppearanceEffect structuralAppearanceEffect;
	private final GhostInterpreter interpreter;
	protected GhostInterpreter getInterpreter() {
		return interpreter;
	}
	public OdeGhostStructure(PropertyStore store) {
		super(store);
		this.interpreter = new GhostInterpreter(store);
		
		this.structuralAppearanceEffect = new StructuralAppearanceEffect(interpreter.getAppearanceStore());
		structuralAppearanceEffect.setPreferedShape(getPrefferedShape());
		this.structuralAppearanceEffect.refresh();
	}
	public void buildCollidable(CollisionMember collidable) {
	
	}
	
	protected TextureShape getPrefferedShape() {
		return TextureShape.Plane;
	}
	
	public StructuralAppearanceEffect getStructuralAppearanceEffect() {
		return structuralAppearanceEffect;
	}

	
	private Vector3f parentTranslation;
	private Quaternion parentRotation;
	
	private Model sensorField = null;
	
	public Model getSensorField() {
		return sensorField;
	}
	protected void setSensorField(Model sensorField) {
		this.sensorField = sensorField;
	}
	public Vector3f getParentTranslation() {
		return parentTranslation;
	}
	public void setParentTranslation(Vector3f parentTranslation) {
		this.parentTranslation = parentTranslation;
	}
	public Quaternion getParentRotation() {
		return parentRotation;
	}
	public void setParentRotation(Quaternion parentRotation) {
		this.parentRotation = parentRotation;
	}

	public float buildCollisionGeometries(PhysicsNode physicsNode,Collection<PhysicsComponent> components,
			Vector3f store) {
		
		return 0;
	}
	public PhysicsCollisionGeometry getGhost() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public void setShowSensorField(boolean show)
	{
		if(this.getSensorField()!=null)
			this.getSensorField().setVisible(show);
	}
}
