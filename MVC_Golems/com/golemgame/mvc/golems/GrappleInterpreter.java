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
package com.golemgame.mvc.golems;

import java.util.Collection;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.jme.renderer.ColorRGBA;

public class GrappleInterpreter extends PhysicalStructureInterpreter {
	public static final String MAX_FORCE = "force.max";
	public static final String MAX_DISTANCE = "distance.max";
	
	/**
	 * @Deprecated IGNORE_STATICS
	 */
	public static final String IGNORE_STATICS = "ignore.statics";
	public static final String BEAM_COLOR = "beam.color";
	
	public static final String BEAM_COLOR_COLLIDE = "beam.color.collide";
	public static final String BEAM_ENABLED = "beam.enabled";
	public static final String IS_BEAM_LUMINOUS = "beam.luminous";
	
	private final StandardFunctionalInterpreter portInterpreter;
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(MAX_FORCE);
		keys.add(MAX_DISTANCE);	
		keys.add(IGNORE_STATICS);	
		keys.add(BEAM_COLOR);	
		portInterpreter.enumerateKeys(keys);
		return super.enumerateKeys(keys);
	}
	
	@Override
	public DataType getDefaultValue(String key) {

		if(key.equals(MAX_FORCE))
			return defaultFloat;
		if(key.equals(MAX_DISTANCE))
			return defaultFloat;
		if(key.equals(IGNORE_STATICS))
			return defaultBool;
		if(key.equals(BEAM_COLOR))
			return defaultColor;
		return super.getDefaultValue(key);
	}
	
	public GrappleInterpreter(PropertyStore store) {
		super(store);
		portInterpreter = new StandardFunctionalInterpreter(store);
		store.setClassName(GolemsClassRepository.GRAPPLE_CLASS);
	}

	public GrappleInterpreter() {
		this(new PropertyStore());		
	}
	
	public PropertyStore getForceOutput()
	{
		return portInterpreter.getOutput(1);
	}
	
	public PropertyStore getDistanceOutput()
	{
		return portInterpreter.getOutput(0);
	}
	
	public PropertyStore getInput()
	{
		return portInterpreter.getInput();
	}
	

	public void setMaxDistance(float accel)
	{
		this.getStore().setProperty(MAX_DISTANCE, accel);
	}
	
	public float getMaxDistance()
	{
		return getStore().getFloat(MAX_DISTANCE,50f);
	}
	
	
	public void setMaxForce(float accel)
	{
		this.getStore().setProperty(MAX_FORCE, accel);
	}
	
	public float getMaxForce()
	{
		return getStore().getFloat(MAX_FORCE,10f);
	}
	
	public boolean isBeamLuminous()
	{
		return getStore().getBoolean(IS_BEAM_LUMINOUS,true);
	}
	public void setBeamLuminous(boolean enabled)
	{
		getStore().setProperty(IS_BEAM_LUMINOUS, enabled);
	}
	
	public boolean isBeamEnabled()
	{
		return getStore().getBoolean(BEAM_ENABLED,true);
	}
	public void setBeamEnabled(boolean enabled)
	{
		getStore().setProperty(BEAM_ENABLED, enabled);
	}
	
	public ColorRGBA getBeamPullColor()
	{
		return getStore().getColor(BEAM_COLOR_COLLIDE, ColorRGBA.blue);
	}
	public void setBeamPullColor(ColorRGBA color)
	{
		getStore().setProperty(BEAM_COLOR_COLLIDE, color);
	}
	
	public ColorRGBA getBeamPushColor()
	{
		return getStore().getColor(BEAM_COLOR, ColorRGBA.red);
	}
	public void setBeamPushColor(ColorRGBA color)
	{
		getStore().setProperty(BEAM_COLOR, color);
	}
/*	
	public boolean isIgnoreStatics(){
		return getStore().getBoolean(IGNORE_STATICS);
	}
	
	public void setIgnoreStatics(boolean ignore)
	{
		getStore().setProperty(IGNORE_STATICS, ignore);
	}*/
}
