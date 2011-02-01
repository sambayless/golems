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
package com.golemgame.mvc.golems.transform;

import com.golemgame.mvc.BoolType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.BatteryInterpreter;
import com.golemgame.mvc.golems.GolemsClassRepository;
import com.golemgame.mvc.golems.MachineInterpreter;
import com.golemgame.mvc.golems.MachineSpaceInterpreter;
import com.golemgame.mvc.golems.ModifierInterpreter;
import com.golemgame.mvc.golems.BatteryInterpreter.ThresholdType;


public class ThresholdTypeTransformation extends Transformation {
	private static final String THRESHOLD_INVERTED = "threshold.inverted";
	
	public ThresholdTypeTransformation() {
		super("0.53.8");		
	}

	@Override
	public void apply(PropertyStore store) {
		//take the machine space, and find any battery structures, and update their function stores
		
		MachineSpaceInterpreter machineSpace = new MachineSpaceInterpreter(store);
		for(DataType val: machineSpace.getMachines().getValues())
		{
			if(val instanceof PropertyStore)
			{
			
				apply( new MachineInterpreter((PropertyStore) val));
				
				
			}
		}
		
		
	}
	
	private void apply(MachineInterpreter interpreter)
	{
		for(DataType val: interpreter.getStructures().getValues())
		{
			if(val instanceof PropertyStore)
			{
				 
				PropertyStore store = (PropertyStore) val;
				if(GolemsClassRepository.BATTERY_CLASS.equals( ((PropertyStore) val).getClassName()))
				{
					apply(new BatteryInterpreter(store));
				}else if(GolemsClassRepository.MODIFIER_CLASS.equals( ((PropertyStore) val).getClassName()))
				{
					apply(new ModifierInterpreter(store));
				}	
				
			}
		}
		
	}

	private void apply(BatteryInterpreter interpreter) {

		
		BoolType oldInverted = null;
		
		if (interpreter.getStore().hasProperty(THRESHOLD_INVERTED,DataType.Type.BOOL))
		{
			oldInverted = (BoolType) interpreter.getStore().getProperty(THRESHOLD_INVERTED);
			interpreter.getStore().nullifyKey(THRESHOLD_INVERTED);
		}
			
		if(oldInverted == null)
		{
			//do nothing
		}else if (!oldInverted.getValue()){
			//assume greater than exclusive.
			interpreter.setThresholdType(ThresholdType.GREATER_EQUAL);
		}else
		{
			interpreter.setThresholdType(ThresholdType.LESSER_THAN);
		}
	}
	private void apply(ModifierInterpreter interpreter) {

		
		BoolType oldInverted = null;
		
		if (interpreter.getStore().hasProperty(THRESHOLD_INVERTED,DataType.Type.BOOL))
		{
			oldInverted = (BoolType) interpreter.getStore().getProperty(THRESHOLD_INVERTED);
			interpreter.getStore().nullifyKey(THRESHOLD_INVERTED);
		}
			
		if(oldInverted == null)
		{
			//do nothing
		}else if (!oldInverted.getValue()){
			//assume greater than exclusive.
			interpreter.setThresholdType(ThresholdType.GREATER_EQUAL);
		}else
		{
			interpreter.setThresholdType(ThresholdType.LESSER_THAN);
		}
	}
}
