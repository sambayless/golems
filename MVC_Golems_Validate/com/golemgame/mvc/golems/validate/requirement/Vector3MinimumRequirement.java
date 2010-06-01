package com.golemgame.mvc.golems.validate.requirement;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.Vector3Type;
import com.golemgame.mvc.golems.validate.ValidationFailureException;
import com.jme.math.Vector3f;

public class Vector3MinimumRequirement extends DataTypeRequirement {

	private final float minX;
	private final float minY;
	private final float minZ;

	public Vector3MinimumRequirement(float minX, float minY, float minZ,String key) {
		super(DataType.Type.VECTOR3, new Vector3Type(new Vector3f(minX,minY,minZ)),key);
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
	}


	@Override
	protected boolean testValue(DataType value) throws ValidationFailureException {
		Vector3Type v =(Vector3Type) value;
		if(v.getValue().x < minX)
			return false;
		if(v.getValue().y < minY)
			return false;
		if(v.getValue().z < minZ)
			return false;
		return super.testValue(value);
	}


	@Override
	protected void enforeValue(DataType value) {
		Vector3Type v =(Vector3Type) value;
		
		if(v.getValue().x < minX)
			v.getValue().x = minX;
		if(v.getValue().y < minY)
			v.getValue().y = minY;
		if(v.getValue().z < minZ)
			v.getValue().z = minZ;

	}

	
}
