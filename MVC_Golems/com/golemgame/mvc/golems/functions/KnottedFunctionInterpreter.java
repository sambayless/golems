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
package com.golemgame.mvc.golems.functions;

import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.DoubleType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GolemsClassRepository;

public class KnottedFunctionInterpreter extends FunctionInterpreter{
	public static final String X_KNOTS = "knots.x";
	public static final String Y_KNOTS = "knots.y";
	public KnottedFunctionInterpreter() {
		this(new PropertyStore());
	}
	public KnottedFunctionInterpreter(PropertyStore store) {
		super(store);
		super.getStore().setClassName(GolemsClassRepository.KNOTTED_FUNCTION_CLASS);
	}
	
	
	public CollectionType getXKnots()
	{
		return getStore().getCollectionType(X_KNOTS);
	}
	
	public CollectionType getYKnots()
	{
		return getStore().getCollectionType(Y_KNOTS);
	}
	
	public void setKnot(int pos, double x, double y)
	{
		getXKnots().setElement(new DoubleType(x), pos);
		getYKnots().setElement(new DoubleType(y), pos);
		
	}

	
	
	
	
}
