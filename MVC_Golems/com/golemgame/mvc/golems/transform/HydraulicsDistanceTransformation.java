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

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GolemsClassRepository;
import com.golemgame.mvc.golems.HydraulicInterpreter;
import com.golemgame.mvc.golems.MachineInterpreter;
import com.golemgame.mvc.golems.MachineSpaceInterpreter;

public class HydraulicsDistanceTransformation extends Transformation {
	/*
	 *  interpreter.getMinJointDistance();
	 */
	public HydraulicsDistanceTransformation() {
		super("0.53.7");		
	}
	
	  public static final String JOINT_DISTANCE_OLD = "distance";
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
					if(GolemsClassRepository.HYDRAULIC_CLASS.equals( ((PropertyStore) val).getClassName()))
					{
						apply(new HydraulicInterpreter(store));
					}
					
				}
			}
			
		}
		private void apply(HydraulicInterpreter interpreter) {
			float oldDistance = 0;
			
			if (interpreter.getStore().hasProperty(JOINT_DISTANCE_OLD,DataType.Type.FLOAT))
			{
				oldDistance = interpreter.getStore().getFloat(JOINT_DISTANCE_OLD);
				
				float radius = interpreter.getJointRadius();
				
				
				interpreter.setMinJointDistance(interpreter.getMinJointDistance()-radius*2f);
				interpreter.setMaxJointDistance(interpreter.getMaxJointDistance()-radius*2f);
				
				interpreter.setJointDistance(oldDistance-radius*2f);
				
				interpreter.getStore().nullifyKey(JOINT_DISTANCE_OLD);
			}
			
			
		

		}

}
