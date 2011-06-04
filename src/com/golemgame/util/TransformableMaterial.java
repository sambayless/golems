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
package com.golemgame.util;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jmex.physics.contact.ContactHandlingDetails;
import com.jmex.physics.material.Material;

/**
 * This class wraps around a material, allowing certain properties to be changed (density)
 * while preserving the handling contact details of the original property.
 * @author Sam
 *
 */
public class TransformableMaterial extends Material {
	private static final long serialVersionUID = 1;
	private Material innerMaterial;
	private float density = 1;
		
	public TransformableMaterial(Material innerMaterial) {
		super(innerMaterial.getName());
		this.innerMaterial = innerMaterial;
		this.density = innerMaterial.getDensity();
	}
	
	public TransformableMaterial(TransformableMaterial copy, Material innerMaterial) {
		super(innerMaterial.getName());
		
		this.innerMaterial = innerMaterial;
		this.density = copy.getDensity();
	}

	public Material getInnerMaterial() {
		return innerMaterial;
	}

	public void setInnerMaterial(Material innerMaterial) {
		this.innerMaterial = innerMaterial;
	}

	@Override
	public ContactHandlingDetails getContactHandlingDetails(
			Material contactMaterial) {
		return innerMaterial.getContactHandlingDetails(contactMaterial);
	}

	@Override
	public ColorRGBA getDebugColor(ColorRGBA color) {
		return innerMaterial.getDebugColor(color);
	}

	@Override
	public float getDensity() {
		return density;
	}

	@Override
	public String getName() {
		return innerMaterial.getName();
	}

	@Override
	public float getSpringPenetrationDepth() {

		return innerMaterial.getSpringPenetrationDepth();
	}

	@Override
	public Vector3f getSurfaceMotion(Vector3f store) {

		return innerMaterial.getSurfaceMotion(store);
	}

	@Override
	public void putContactHandlingDetails(Material contactMaterial,
			ContactHandlingDetails details) {
		innerMaterial.putContactHandlingDetails(contactMaterial, details);
	}

	@Override
	public void setDebugColor(ColorRGBA value) {

		innerMaterial.setDebugColor(value);
	}

	@Override
	public void setDensity(float value) {

		density = value;
	}

	@Override
	public void setName(String name) {

		innerMaterial.setName(name);
	}

	@Override
	public void setSpringPenetrationDepth(float springPenetrationDepth) {

		innerMaterial.setSpringPenetrationDepth(springPenetrationDepth);
	}

	@Override
	public void setSurfaceMotion(Vector3f motion) {

		innerMaterial.setSurfaceMotion(motion);
	}

	@Override
	public String toString() {
		return innerMaterial.toString();
	}
	
	
}
