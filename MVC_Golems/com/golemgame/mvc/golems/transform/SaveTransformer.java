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

import java.util.ArrayList;
import java.util.Collection;

import com.golemgame.mvc.PropertyStore;

/**
 * This class has the responsibility of taking old save files, and updating them where possible to meet new standards
 * @author Sam
 *
 */
public class SaveTransformer {

	private Collection<Transformation> transformations = new ArrayList<Transformation>();
	
	
	
	public SaveTransformer() {
		super();
		transformations.add(new FunctionTransformation());
		transformations.add(new HydraulicsDistanceTransformation());
		transformations.add(new EarlyMVCMachineSpaceTransformation());
		transformations.add(new ThresholdTypeTransformation());
		transformations.add(new SlopeTransformation());
		transformations.add(new CapsuleLengthTransformation());
		transformations.add(new OldToNewBallAndSocketTransformation());
		transformations.add(new ArcTubeTransformation());
		transformations.add(new BatteryKeypressTransformation());
		transformations.add(new BetaGrappleTransformation());
	}



	/**
	 * take a top level property store, and transform it as necessary.
	 * Note: This is a mutative operation
	 * @param store
	 */	
	public void apply(PropertyStore store,int majorVersion, int minorVersion, int revision)
	{
		//the top level is a machine space.
		
		for(Transformation transformation:transformations)
		{
			transformation.setVersion(majorVersion, minorVersion, revision);
			transformation.apply(store);
		}
		
	}
	
}
