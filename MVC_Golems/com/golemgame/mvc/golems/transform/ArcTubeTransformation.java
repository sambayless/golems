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
