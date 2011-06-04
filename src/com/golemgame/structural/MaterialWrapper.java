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
package com.golemgame.structural;

import java.io.Serializable;
import java.util.LinkedHashMap;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SustainedView;
import com.golemgame.mvc.golems.MaterialPropertiesInterpreter;
import com.golemgame.mvc.golems.MaterialPropertiesInterpreter.MaterialClass;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jmex.buoyancy.IFluidRegion;
import com.jmex.physics.contact.MutableContactInfo;
import com.jmex.physics.material.Material;

public class MaterialWrapper implements Serializable,SustainedView{
	private static final long serialVersionUID = 1L;
	

	public static final Material WEIGHTLESS;
	static
	{
		/**
		 * Materials cannot be weightless, it will throw an assertion error in the physics engine
		 */
		WEIGHTLESS = new Material();
		//TODO: return this to flt_epsion
		WEIGHTLESS.setDensity(FastMath.FLT_EPSILON);
	}
	public static final Material HELIUM_BALLOON;
	public static final Material AIR_BALLOON;
	static
	{
        MutableContactInfo info = new MutableContactInfo();
        {
            info.setMu( 1 );
            info.setBounce( 0.4f );
            info.setMinimumBounceVelocity( 1f );          
        }
        	HELIUM_BALLOON = new Material("helium balloon");
        
        	HELIUM_BALLOON.setDebugColor( new ColorRGBA( 0.1f, 0.5f, 0.1f, 1 ) );
        	HELIUM_BALLOON.setDensity( IFluidRegion.DENSITY_HELIUM );
        	
            info.setMu( 5f );
            info.setBounce( 1.0f );
            HELIUM_BALLOON.putContactHandlingDetails( HELIUM_BALLOON, info );
            info.setBounce( 0.9f );
            HELIUM_BALLOON.putContactHandlingDetails( null, info );
            HELIUM_BALLOON.putContactHandlingDetails( Material.DEFAULT, info );

            info.setMu( 0.9f );
            info.setBounce( 0.85f );
            HELIUM_BALLOON.putContactHandlingDetails( Material.CONCRETE, info );
        
            
        	AIR_BALLOON = new Material("air balloon");
            
        	AIR_BALLOON.setDebugColor( new ColorRGBA( 0.1f, 0.5f, 0.1f, 1 ) );
        	AIR_BALLOON.setDensity( IFluidRegion.DENSITY_AIR );
        	
            info.setMu( 5f );
            info.setBounce( 1.0f );
            AIR_BALLOON.putContactHandlingDetails( AIR_BALLOON, info );
            info.setBounce( 0.9f );
            AIR_BALLOON.putContactHandlingDetails( null, info );
            AIR_BALLOON.putContactHandlingDetails( Material.DEFAULT, info );

            info.setMu( 0.9f );
            info.setBounce( 0.85f );
            AIR_BALLOON.putContactHandlingDetails( Material.CONCRETE, info );
        
	}
	
	/*private MaterialClass baseClass = MaterialClass.DEFAULT;
	
	 private float density = 1;*/
	
	
	 private MaterialPropertiesInterpreter interpreter;
	 
		public MaterialWrapper(PropertyStore store) {
			super();
			this.interpreter = new MaterialPropertiesInterpreter(store);
			
		}
	 
	public MaterialWrapper(MaterialClass baseClass) {
		super();
		this.setBaseClass(baseClass);
	}
	
	public MaterialWrapper(MaterialWrapper copy)
	{
		super();
		this.setBaseClass(copy.getBaseClass());
		this.setDensity(copy.getDensity());
		this.setName(copy.getName());
		this.setSpringPenetrationDepth(copy.getSpringPenetrationDepth());
		this.setSurfaceMotion(copy.getSurfaceMotion());
	}

	public float getSpringPenetrationDepth() {
		return interpreter.getSpringPenetration();
	}

	public void setSpringPenetrationDepth(float springPenetrationDepth) {
		this.interpreter.setSpringPenetration(springPenetrationDepth);
	}

	public MaterialClass getBaseClass() {
		return interpreter.getMaterial();
	}

	public void setBaseClass(MaterialClass baseClass) {
		this.interpreter.setMaterial(baseClass);
		copyPropertiesFromBase();
	}

	private void copyPropertiesFromBase()
	{
		Material base = getMaterial(getBaseClass());
		this.setDensity(base.getDensity());
		this.setName(base.getName());
		this.setSpringPenetrationDepth(base.getSpringPenetrationDepth());
		this.setSurfaceMotion(base.getSurfaceMotion(null));
	}
	
	public float getDensity() {
		return interpreter.getDensity();
	}

	public void setDensity(float density) {
		interpreter.setDensity(density);
	}

	public String getName() {
		return interpreter.getName();
	}

	public void setName(String name) {
		interpreter.setName(name);
	}

	
	
	public Material constructMaterial()
	{
		return buildMaterial(this.getBaseClass());
	}
	
	private Material buildMaterial(MaterialClass materialClass)
	{
		Material from = getMaterial(materialClass);
		
		boolean unchanged = true;
		
		unchanged &= from.getDensity() == this.getDensity();
		
		unchanged &= from.getSpringPenetrationDepth() == this.getSpringPenetrationDepth();

		if (unchanged)
			return from;
		else
		{
			
			Integer hashNumber = generateMaterialHash(this.getDensity(), this.getSpringPenetrationDepth(), this.getSurfaceMotion(), from.getID());
			Material material;
			if ((material = materialMap.get(materialMap)) != null)
			{
				
			}else
			{
				
				material = new Material(this.getName(),from.getID());
				material.setDensity(this.getDensity());
				material.setSpringPenetrationDepth(this.getSpringPenetrationDepth());
				material.setSurfaceMotion(this.getSurfaceMotion());
				material.setContactDetails(from.getContactDetails());
				materialMap.put(hashNumber, material);
			}
			return material;
		}
		
	}
	
	private int generateMaterialHash(float density, float springPenetrationDepth, Vector3f surfaceMotion, int id)
	{
		
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + Float.floatToIntBits(density);
		result = prime * result + Float.floatToIntBits(springPenetrationDepth);
		result = prime * result
				+ ((surfaceMotion == null) ? 0 : surfaceMotion.hashCode());
		return result;
	}
	
	private static final int LRU_CACHE_SIZE = 1000;
	/**
	 * This is an LRU cache for recently used icon textures.
	 * See http://blogs.sun.com/swinger/date/20041012#collections_trick_i_lru_cache
	 */
	private static final LinkedHashMap<Integer, Material>  materialMap = new LinkedHashMap<Integer, Material> ( 10, 0.75f, true )
	 {
		private static final long serialVersionUID = 1;

		
		protected boolean removeEldestEntry(java.util.Map.Entry<Integer, Material> eldest) {
			 return size() > LRU_CACHE_SIZE;
		}


	 };
	
	


	public Vector3f getSurfaceMotion() {
		return interpreter.getSurfaceMotion();
	}

	public void setSurfaceMotion(Vector3f surfaceMotion) {
		interpreter.setSurfaceMotion(surfaceMotion);
	}
	
	
	public void invertView(PropertyStore store) {
		store.set(interpreter.getStore());
		store.refresh();
	}

	public void refresh() {
		//do nothing
		
	}

	public PropertyStore getStore() {
		return interpreter.getStore();
	}
	

	public void remove() {
		// TODO Auto-generated method stub
		
	}
	
	public static Material getMaterial(MaterialClass material)
	{
		switch(material)
		{
			case IRON:
				return Material.IRON;
			case WOOD:
				return Material.WOOD;
			case CONCRETE:
				return Material.CONCRETE;
			case GRANITE:
				return Material.GRANITE;
			case GLASS:
				return Material.GLASS;
			case PLASTIC:
				return Material.PLASTIC;
			case RUBBER:
				return Material.RUBBER;
			case ICE:
				return Material.ICE;
			case SPONGE:
				return Material.SPONGE;
			case HELIUM_BALLOON:
				return MaterialWrapper.HELIUM_BALLOON;
			case DEFAULT:
				return Material.DEFAULT;
			default:
				return Material.DEFAULT;
				
		}
	}
}
