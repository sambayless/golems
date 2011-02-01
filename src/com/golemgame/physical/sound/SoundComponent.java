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
package com.golemgame.physical.sound;

import com.golemgame.physical.PhysicsComponent;
import com.golemgame.physical.PhysicsObject;
import com.jphya.body.Body;

public class SoundComponent {

	//source: wikipedia, and http://www.uk-piano.org/sound.html
	public final static float SPEED_SOUND_AIR = 343f;
	public final static float SPEED_SOUND_WATER = 1445.0568f;
	public final static float SPEED_SOUND_WOOD =  3859.3776f; //oak
	public final static float SPEED_SOUND_IRON =  5127.3456f;
	public final static float SPEED_SOUND_LEAD = 1228.344f;
	public final static float CONVERSION = 1;//meters to cm
	
	private  Body body;
	public void setBody(Body body) {
		this.body = body;
	}

	private float baseFreq = 1f;
	
	protected float getBaseFreq() {
		return baseFreq;
	}

	protected void setBaseFreq(float baseFreq) {
		this.baseFreq = baseFreq;
	}

	public SoundComponent(PhysicsObject physics) {
		super();
		this.physics = physics;
	}

	private final PhysicsObject physics;
	public PhysicsObject getPhysicsObject()
	{
		return physics;
	}
	
	public Body getBody(){
		return body;
	}
	
	/**
	 * This is a float which scales the base frequency of the sound type (which depends on the modal resonance type)
	 * @return
	 */
	public float getNaturalResonanceFrequency(){
		return 1f;
	}
	
	public float getSpeedOfSoundInMaterial()
	{
		return SPEED_SOUND_AIR;
	}
	
	public float getFrequencyScale()
	{
		return getNaturalResonanceFrequency()/baseFreq;
	}
	
	public static float getBoxFrequency(float w, float l, float h,float v)
	{
		float n = 1;
		return (v/2f) * 1f/(w+l+h); //FastMath.sqrt((n/w)*(n/w) + (n/l)*(n/l) + (n/h)*(n/h));
	}
	
	public static float getCylinderFrequency(float length, float diameter,float v)
	{
		//reroute to soundbox right now, since its the only one that sounds decent.

		float n = 1;		
		return(n * v )/(2f*(length*diameter*0.8f));//using the approximate correction from http://en.wikipedia.org/wiki/Acoustic_resonance
	}

	public static float getBaseBoxFrequency() {
		
		return 1f/(3f);
	}
}
