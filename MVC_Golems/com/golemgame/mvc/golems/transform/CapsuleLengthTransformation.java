package com.golemgame.mvc.golems.transform;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.CapsuleInterpreter;
import com.golemgame.mvc.golems.GolemsClassRepository;
import com.golemgame.mvc.golems.MachineInterpreter;
import com.golemgame.mvc.golems.MachineSpaceInterpreter;

public class CapsuleLengthTransformation  extends Transformation{


	public CapsuleLengthTransformation() {
		super("0.54.0");
		
	}
	@Override
	public void apply(PropertyStore store) {
		
		if(! ( super.getMajorVersion() <= 0 && super.getMinorVersion()< 54))
			return;//only apply this to earlier files
		
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
				if(GolemsClassRepository.CAP_CLASS.equals( ((PropertyStore) val).getClassName()))
				{
					apply(new CapsuleInterpreter(store));
				}
				
			}
		}
		
	}
	

	private void apply(CapsuleInterpreter interpreter) {

		float oldHeight = interpreter.getHeight();
		
		float radius = interpreter.getRadius();
		
		interpreter.setHeight(oldHeight-radius*2f);		

	}

}
