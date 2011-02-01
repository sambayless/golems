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

import java.util.logging.Level;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;

import com.golemgame.functional.FunctionSettings;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.BatteryInterpreter.ThresholdType;
import com.golemgame.mvc.golems.ModifierInterpreter.ModifierSwitchType;
import com.golemgame.states.StateManager;



public class BFunctionApplier extends BComponent{
	private static final long serialVersionUID =1;

	private FunctionSettings settings;

	

	private UnivariateRealFunction function;

	private final BAuxInput switchInput = new BAuxInput();
	private float threshold = 0;
	private boolean switchActive = false;
	private float switchValue = 0f;
	private ThresholdType thresholdType;
	
	boolean hasHoldValue = false;
	
	private ModifierSwitchType switchType = ModifierSwitchType.On;



	private float holdValue= 0;
	
	public ModifierSwitchType getSwitchType() {
		return switchType;
	}
	
	public void setSwitchType(ModifierSwitchType switchType) {
		this.switchType = switchType;
	}
	
	public BFunctionApplier(FunctionSettings settings)
	{
		super();
	
		this.settings = settings;
		
		
		this.function =settings.buildFunction();

	}
	public BFunctionApplier()
	{
		this(new FunctionSettings(new PropertyStore()));


	}
	
	@Override
	public float generateSignal(float time) 
	{

		if(this.switchActive && this.switchType == ModifierSwitchType.Pause && this.hasHoldValue)
			return this.holdValue;
		else if((!this.switchActive) && this.switchType == ModifierSwitchType.Pause )
		{
			holdValue = 0;
			this.hasHoldValue = false;
		}
		
		 if ((!this.switchActive) && (this.switchType == ModifierSwitchType.On))	
		 {
			 state = 0;//clear state
			 return 0;
		 }
		 
		float output = 0;
	
		
			try{
				//a null pointer here is tied to physics not deleting...
				output = settings.clampY( (float) function.value(settings.clampX(state/settings.getScaleX()))*settings.getScaleY());
		
			}catch(FunctionEvaluationException e)
			{
				StateManager.getLogger().log(Level.WARNING, e.getStackTrace().toString());
				//send no signal on error
			}
		
			
			
		if (this.switchType == ModifierSwitchType.Invert && this.switchActive)
			output = -output;
		else if (this.switchType == ModifierSwitchType.Multiply)//doesnt care if the switch is active
			output *= this.switchValue;
		else if (this.switchActive && this.switchType == ModifierSwitchType.Pause)
		{
			this.holdValue = output;
			this.hasHoldValue = true;
		}
		
		state = 0;
		return output;
	}


	public UnivariateRealFunction getFunction() {

		return function;
	}

	public void setFunction(UnivariateRealFunction function) {
		this.function =function;
	}


	
	
	protected class BAuxInput extends BComponent
	{
		private static final long serialVersionUID =1;
		@Override
		public float generateSignal(float time) 
		{
			switchValue = state;

			switch (thresholdType)
			{
				case GREATER_EQUAL:
					if(state>=threshold)
					{
						setSwitchActive(true);
					}else
						setSwitchActive(false);
					break;
				case LESSER_EQUAL:
					if(state<=threshold)
					{
						setSwitchActive(true);
					}else
						setSwitchActive(false);
					break;
				case GREATER_THAN:
					if(state>threshold)
					{
						setSwitchActive(true);
					}else
						setSwitchActive(false);
					break;
				case LESSER_THAN:
					if(state<threshold)
					{
						setSwitchActive(true);
					}else
						setSwitchActive(false);					
					break;
			}

			state = 0;
			
			return 0;
		}
		
	}

	public float getThreshold() {
		return threshold;
	}
	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}
	public BAuxInput getSwitchInput() {
		return switchInput;
	}
	
	/**
	 * Set whether the function applier is 'closed' (allows signals through) 
	 * or 'open' (outputs only 0).
	 * @param b
	 */
	public void setSwitchActive(boolean active) {
		this.switchActive = active;
	}
	public void setThresholdType(ThresholdType thresholdType) {
		this.thresholdType = thresholdType;
		
	}
}
