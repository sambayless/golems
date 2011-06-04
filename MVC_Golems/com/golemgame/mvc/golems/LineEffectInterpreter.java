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

public class LineEffectInterpreter extends StoreInterpreter  implements Comparable<LineEffectInterpreter>{

	
	
	public static final String NAME = "effect.name";
	public static final String MIN_ANGLE = "angle.min";
	public static final String MAX_ANGLE= "angle.max";
	public static final String MIN_LIFE_SPAN= "life_span.min";
	public static final String MAX_LIFE_SPAN= "life_span.max";
	public static final String NUM_PARTICLES = "particles.num";
	public static final String START_COLOR = "color.start";
	public static final String LINE_WIDTH = "line.width";
	public static final String END_COLOR = "color.end";
	public static final String INITIAL_VELOCITY = "velocity.start";
	public static final String SPEED = "speed";
	public static final String INITIAL_SIZE = "size.start";
	public static final String END_SIZE = "size.final";
	public static final String LUMINOUS = "luminous";
	
	public LineEffectInterpreter() {
		this( new PropertyStore());
	}
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(NAME);
		keys.add(MIN_ANGLE);	
		keys.add(MAX_ANGLE);
		keys.add(MIN_LIFE_SPAN);	
		keys.add(MAX_LIFE_SPAN);	
		keys.add(NUM_PARTICLES);
		keys.add(START_COLOR);
		keys.add(END_COLOR);	
		keys.add(INITIAL_VELOCITY);
		keys.add(SPEED);	
		keys.add(INITIAL_SIZE);
		keys.add(END_SIZE);	
		keys.add(LUMINOUS);
		keys.add(LINE_WIDTH);

		
		return super.enumerateKeys(keys);
	}
	
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(NAME))
			return defaultString;
		if(key.equals(MIN_ANGLE))
			return defaultFloat;
		if(key.equals(MAX_ANGLE))
			return defaultFloat;
		if(key.equals(MIN_LIFE_SPAN))
			return defaultFloat;
		if(key.equals(MAX_LIFE_SPAN))
			return defaultFloat;
		if(key.equals(NUM_PARTICLES))
			return defaultInt;		
		if(key.equals(START_COLOR))
			return defaultColor;
		if(key.equals(END_COLOR))
			return defaultColor;
		if(key.equals(INITIAL_VELOCITY))
			return defaultFloat;
		if(key.equals(LINE_WIDTH))
			return defaultFloat;
		if(key.equals(SPEED))
			return defaultFloat;
		if(key.equals(INITIAL_SIZE))
			return defaultFloat;
		if(key.equals(END_SIZE))
			return defaultFloat;
		if(key.equals(LUMINOUS))
			return defaultBool;
		
		return super.getDefaultValue(key);
	}
	
	public LineEffectInterpreter(PropertyStore store) {
		super(store);
		super.getStore().setClassName(GolemsClassRepository.LINE_EFFECTS_CLASS);
	}
	
	public String getEffectName()
	{
		return getStore().getString(NAME,"(Unnamed)");
	}
	
	public void setEffectName(String name)
	{
		getStore().setProperty(NAME,name);
	}
	
	
	public float getInitialVelocity()
	{
		return getStore().getFloat(INITIAL_VELOCITY);
	}
	public float getMinAngle()
	{
		return getStore().getFloat(MIN_ANGLE);
	}
	public float getMaxAngle()
	{
		return getStore().getFloat(MAX_ANGLE);
	}
	public float getLineWidth()
	{
		return getStore().getFloat(LINE_WIDTH,1f);
	}
	public float getMinLifeSpan()
	{
		return getStore().getFloat(MIN_LIFE_SPAN);
	}
	public float getMaxLifeSpan()
	{
		return getStore().getFloat(MAX_LIFE_SPAN);
	}
	public int getNumberOfParticles()
	{
		return getStore().getInt(NUM_PARTICLES);
	}
	public ColorRGBA getStartColor()
	{
		return getStore().getColor(START_COLOR, new ColorRGBA(1f,1f,1f,1f));
	}
	public ColorRGBA getEndColor()
	{
		return getStore().getColor(END_COLOR, new ColorRGBA(1f,1f,1f,1f));
	}
	public float getSpeed()
	{
		return getStore().getFloat(SPEED);
	}
	public void setSpeed(float speed)
	{
		getStore().setProperty(SPEED, speed);
	}
	public void setLineWidth(float width)
	{
		getStore().setProperty(LINE_WIDTH, width);
	}
	public void setInitialVelocity(float speed)
	{
		getStore().setProperty(INITIAL_VELOCITY, speed);
	}
	
	public void setMinAngle(float angle)
	{
		getStore().setProperty(MIN_ANGLE, angle);
	}
	public void setMaxAngle(float angle)
	{
		getStore().setProperty(MAX_ANGLE, angle);
	}
	public void setMinLifeSpan(float life)
	{
		getStore().setProperty(MIN_LIFE_SPAN, life);
	}
	public void setMaxLifeSpan(float life)
	{
		getStore().setProperty(MAX_LIFE_SPAN, life);
	}
	public void setNumberOfParticles(int num)
	{
		getStore().setProperty(NUM_PARTICLES, num);
	}
	public void setStartColor(ColorRGBA color)
	{
		getStore().setProperty(START_COLOR, color);
	}
	public void setEndColor(ColorRGBA color)
	{
		getStore().setProperty(END_COLOR, color);
	}
	
	public boolean isLuminous()
	{
		return getStore().getBoolean(LUMINOUS, true);
	}
	
	public void setLuminous(boolean luminous)
	{
		getStore().setProperty(LUMINOUS, luminous);
	}
	
	
	public float getInitialSize()
	{
		return getStore().getFloat(INITIAL_SIZE);
	}
	public float getFinalSize()
	{
		return getStore().getFloat(END_SIZE);
	}
	public void setInitialSize(float size)
	{
		getStore().setProperty(INITIAL_SIZE, size);
	}
	public void setFinalSize(float size)
	{
		getStore().setProperty(END_SIZE, size);
	}

	public int compareTo(LineEffectInterpreter o) {
		
		return this.getEffectName().compareTo(o.getEffectName());
	}
	
	
}
