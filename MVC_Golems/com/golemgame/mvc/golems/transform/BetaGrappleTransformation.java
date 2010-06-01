package com.golemgame.mvc.golems.transform;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.BoxInterpreter;
import com.golemgame.mvc.golems.CapsuleInterpreter;
import com.golemgame.mvc.golems.GolemsClassRepository;
import com.golemgame.mvc.golems.GrappleInterpreter;
import com.golemgame.mvc.golems.MachineInterpreter;
import com.golemgame.mvc.golems.MachineSpaceInterpreter;
import com.jme.math.Vector3f;

public class BetaGrappleTransformation  extends Transformation{


	public BetaGrappleTransformation() {
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
				if(GolemsClassRepository.GRAPPLE_CLASS.equals( ((PropertyStore) val).getClassName()))
				{
					apply(new GrappleInterpreter(store));
				}
				
			}
		}
		
	}
	

	private void apply(GrappleInterpreter interpreter) {
		if(interpreter.getStore().hasProperty(BoxInterpreter.BOX_EXTENT))
		{
			Vector3f extent = interpreter.getStore().getVector3f(BoxInterpreter.BOX_EXTENT);
			interpreter.getStore().setProperty(CapsuleInterpreter.CYL_RADIUS, extent.getY());
			interpreter.getStore().setProperty(CapsuleInterpreter.CYL_HEIGHT, extent.getX());
			interpreter.getStore().nullifyKey(BoxInterpreter.BOX_EXTENT);
		}
	}

}
