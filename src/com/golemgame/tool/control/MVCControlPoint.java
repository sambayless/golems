package com.golemgame.tool.control;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Action.Type;
import com.jme.math.Vector3f;

public abstract class MVCControlPoint<E extends ControllableModel> extends ControlPoint<E> {
	
	public abstract void setPropertyStore(PropertyStore store);



	@Override
	public void disable() {
		setPropertyStore(null);
		super.disable();
	}

	

	public abstract void refresh();
	@Override
	public Action<?> getAction(Type type) throws ActionTypeException {
		if (type == Action.ORIENTATION)
			return new OrientationInfo();
				
		return super.getAction(type);
	}
	private class OrientationInfo extends ControlPoint.OrientationInfoImpl
	{
		Vector3f direction = new Vector3f();
		public OrientationInfo()
		{
			super.useAxis = true;
		}
		
		
		@Override
		public Vector3f getAxis() {

			return new Vector3f(Vector3f.UNIT_X);
		}

	}
	
	
	
}
