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
import com.golemgame.mvc.golems.CapsuleInterpreter;
import com.golemgame.mvc.golems.GolemsClassRepository;
import com.golemgame.mvc.golems.MachineInterpreter;
import com.golemgame.mvc.golems.MachineSpaceInterpreter;
import com.golemgame.mvc.golems.TubeInterpreter;
import com.jme.math.FastMath;

public class ArcTubeTransformation  extends Transformation{


	public ArcTubeTransformation() {
		super("0.54.7");
		
	}
	@Override
	public void apply(PropertyStore store) {
		
		if(! ( super.getMajorVersion() <= 0 && super.getMinorVersion()<= 54 && super.getRevision()< 7))
			return;//only apply this to earlier files
		
		//take the machine space, and find any tube structures, and update their function stores
		
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
				if(GolemsClassRepository.TUBE_CLASS.equals( ((PropertyStore) val).getClassName()))
				{
					apply(new TubeInterpreter(store));
				}
				
			}
		}
		
	}
	

	private void apply(TubeInterpreter interpreter) {
		if(interpreter.getStore().getFloat(TubeInterpreter.ARC) <= 0f)
			interpreter.setArc(FastMath.TWO_PI);

	}

}
