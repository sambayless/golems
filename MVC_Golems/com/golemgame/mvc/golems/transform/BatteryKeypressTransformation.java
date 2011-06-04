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

import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.EnumType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.BatteryInterpreter;
import com.golemgame.mvc.golems.GolemsClassRepository;
import com.golemgame.mvc.golems.InputInterpreter;
import com.golemgame.mvc.golems.MachineInterpreter;
import com.golemgame.mvc.golems.MachineSpaceInterpreter;
import com.golemgame.mvc.golems.WireInterpreter;
import com.golemgame.mvc.golems.WirePortInterpreter;
import com.golemgame.mvc.golems.input.KeyboardInputDeviceInterpreter;

public class BatteryKeypressTransformation extends Transformation {

	public BatteryKeypressTransformation() {
		super("0.54.7");
	}
	

	@Override
	public void apply(PropertyStore store) {
		
		if(! ( super.getMajorVersion() <= 0 && super.getMinorVersion()<= 54 && super.getRevision()< 7))
			return;//only apply this to earlier files
		
		//take the machine space, and find any battery structure
		
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
		//for(DataType val: interpreter.getStructures().getValues())
		CollectionType structures = interpreter.getStructures();
	
		for(int i = 0;i<structures.getValues().size();i++)
		{
			DataType val = structures.getElement(i);
			if(val instanceof PropertyStore)
			{
				 
				PropertyStore store = (PropertyStore) val;
				if(GolemsClassRepository.BATTERY_CLASS.equals( ((PropertyStore) val).getClassName()))
				{
					apply(new BatteryInterpreter(store),interpreter);
				}
				
			}
		}
		
	}
	
	private enum KeyboardInteractionType
	{
		HeldDown(), Toggle();
	}
	
	private static final EnumType defaultInteractionType = new EnumType(KeyboardInteractionType.HeldDown);
	
	private static final String INTERACTION_TYPE = "interactionType";
	private static final String INTERACT = "interact";
	private static final String KEY_CODE = "keyCode";
	
	private void apply(BatteryInterpreter interpreter,MachineInterpreter machine) {
		
		if(interpreter.getStore().hasProperty(INTERACT))
		{		
			if( interpreter.getStore().getBoolean(INTERACT) && interpreter.getStore().hasProperty(INTERACTION_TYPE) && interpreter.getStore().hasProperty(KEY_CODE))
			{
				KeyboardInteractionType interactionType = interpreter.getStore().getEnum(INTERACTION_TYPE, KeyboardInteractionType.HeldDown);
				int keyCode = interpreter.getStore().getInt(KEY_CODE);
				
				InputInterpreter input = new InputInterpreter();
				KeyboardInputDeviceInterpreter keyboard = new KeyboardInputDeviceInterpreter();
				keyboard.setInteractionType(interactionType == KeyboardInteractionType.HeldDown ? KeyboardInputDeviceInterpreter.KeyEventType.HeldDown :  KeyboardInputDeviceInterpreter.KeyEventType.Toggle);
				keyboard.setKeyCode(keyCode);
				keyboard.setOutputNegative(true);
				input.setInputDevice(keyboard.getStore());
				input.setLocalTranslation(interpreter.getLocalTranslation().clone());
				input.setLayer(interpreter.getLayer());
				input.setName("Battery Keypress");
				
				//add the wire connector
				
				
				WirePortInterpreter batterySwitchInput = new WirePortInterpreter(interpreter.getInput());
				WirePortInterpreter inputOutput = new WirePortInterpreter(input.getOutput());
			
			
		
			
				WireInterpreter wire = new WireInterpreter();
			
				wire.setPortID(batterySwitchInput.getID(),  true);
				wire.setPortID(inputOutput.getID(), false);
				wire.setNegative(false);
			
				inputOutput.addWire(wire.getStore());
			//	batterySwitchInput.addWire(wire.getStore());
				
				machine.addStructure(input.getStore());
				
			}	
			interpreter.getStore().nullifyKey(INTERACT);//keep the others, in case we go back		
		}
		
		

	}

	

}
