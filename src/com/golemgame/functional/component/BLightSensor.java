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
package com.golemgame.functional.component;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;

import com.golemgame.functional.FunctionSettings;
import com.golemgame.functional.component.medium.BLightMedium;
import com.golemgame.states.StateManager;
import com.jme.math.Vector3f;


public class BLightSensor extends BSource {

	private LightSensorCallback callback;
	private FunctionSettings settings;
	private UnivariateRealFunction function;
	
	public BLightSensor(
			UnivariateRealFunction function, FunctionSettings settings,LightSensorCallback callback) {
		super();
		this.callback = callback;
		this.function = function;
		this.settings = settings;
	}

	@Override
	public void updateSource(float time) {
		BLightMedium lightMedium = (BLightMedium) getMind().getSpace().getMedium(BSpace.AvailableMedium.Light);
		
		float intensity =  lightMedium.getLightIntensity(callback.getPosition(), -1);
		try{
		intensity = settings.clampX(intensity);
		float output = (float) function.value(intensity/settings.getScaleX())*settings.getScaleY();	
		this.state = settings.clampY(output);
		}catch(FunctionEvaluationException e)
		{
			StateManager.logError(e);
			state = settings.clampY(intensity);
		}

	}
	
	/**
	 * This interface must be implemented and attached to the BLight to provide it with neccesary
	 * information about the position of the light, on request.
	 * @author Sam
	 *
	 */
	public static interface LightSensorCallback
	{
		public Vector3f getPosition();
	}

}
