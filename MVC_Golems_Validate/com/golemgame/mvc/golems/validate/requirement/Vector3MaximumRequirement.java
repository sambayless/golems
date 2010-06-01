package com.golemgame.mvc.golems.validate.requirement;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.Vector3Type;
import com.golemgame.mvc.golems.validate.ValidationFailureException;
import com.jme.math.Vector3f;

public class Vector3MaximumRequirement extends DataTypeRequirement {

	private final float maxX;
	private final float maxY;
	private final float maxZ;

	public Vector3MaximumRequirement(float minX, float minY, float minZ,String key) {
		super(DataType.Type.VECTOR3, new Vector3Type(new Vector3f(minX,minY,minZ)), key);
		this.maxX = minX;
		this.maxY = minY;
		this.maxZ = minZ;
	}
	
	@Override
	protected boolean testValue(DataType value) throws ValidationFailureException {
		Vector3Type v =(Vector3Type) value;
		if(v.getValue().x > maxX)
			return false;
		if(v.getValue().y > maxY)
			return false;
		if(v.getValue().z > maxZ)
			return false;
		return super.testValue(value);
	}
	@Override
	protected void enforeValue(DataType value) {
		Vector3Type v =(Vector3Type) value;
		
		if(v.getValue().x > maxX)
			v.getValue().x = maxX;
		if(v.getValue().y > maxY)
			v.getValue().y = maxY;
		if(v.getValue().z > maxZ)
			v.getValue().z = maxZ;

	}




	
}
