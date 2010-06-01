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
