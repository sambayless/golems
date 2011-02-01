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
