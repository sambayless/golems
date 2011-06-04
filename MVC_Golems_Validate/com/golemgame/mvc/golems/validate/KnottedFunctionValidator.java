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
package com.golemgame.mvc.golems.validate;

import java.util.Iterator;

import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.DoubleType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.functions.KnottedFunctionInterpreter;
import com.golemgame.mvc.golems.validate.requirement.DataTypeRequirement;
import com.golemgame.mvc.golems.validate.requirement.Requirement;


public class KnottedFunctionValidator extends Validator {

	public KnottedFunctionValidator() {
		super();
		
		
		//check to make sure that there are no repeated x values, no x values are out of order,
		//and that no values, x or y, are infinity or NaN.
		// DataTypeRequirement(DataType.Type.COLLECTION,KnottedFunctionInterpreter.X_KNOTS, new CollectionType()))
		
		super.addRequirement(new DataTypeRequirement(DataType.Type.COLLECTION, new CollectionType(),KnottedFunctionInterpreter.X_KNOTS));
		super.addRequirement(new DataTypeRequirement(DataType.Type.COLLECTION, new CollectionType(),KnottedFunctionInterpreter.Y_KNOTS));
		
		Requirement req = new Requirement()
		{

			public void enfore(PropertyStore store) {
				
				
				
				CollectionType xKnots = store.getCollectionType(KnottedFunctionInterpreter.X_KNOTS);
				CollectionType yKnots = store.getCollectionType(KnottedFunctionInterpreter.Y_KNOTS);
				int pos = 0;
				Iterator<DataType> itX = xKnots.getValues().iterator();
				Iterator<DataType> itY = yKnots.getValues().iterator();
				double prev = Double.NEGATIVE_INFINITY;
				while(itX.hasNext() && itY.hasNext())
				{
					pos++;
					DataType elementX = itX.next();
					DataType elementY = itY.next();
					if(elementX == null)
					{
						itX.remove();
						itY.remove();
					}
					else if (elementX.getType()!=DataType.Type.DOUBLE || elementY.getType()!=DataType.Type.DOUBLE)
					{
						itX.remove();
						itY.remove();
					}
					else
					{
						double dX = ((DoubleType)elementX).getValue();
						double dY = ((DoubleType)elementY).getValue();
						if(Double.isNaN(dX) || Double.isInfinite(dX) || Double.isNaN(dY) || Double.isInfinite(dY))
						{
							itX.remove();
							itY.remove();
						}
						else if(dX<=prev)
						{
							itX.remove();
							itY.remove();
						}else//only update dX if we kept this value of dX.
							prev = dX;
					}
				}
				
				//remove any stragglers.
				while(itX.hasNext())
				{
					itX.next();
					itX.remove();
				}
				while(itY.hasNext())
				{
					itY.next();
					itY.remove();
				}
				
			}
	
			public boolean test(PropertyStore store) throws ValidationFailureException {
				CollectionType xKnots = store.getCollectionType(KnottedFunctionInterpreter.X_KNOTS);
				CollectionType yKnots = store.getCollectionType(KnottedFunctionInterpreter.Y_KNOTS);
				
				if (xKnots.getValues().size() != yKnots.getValues().size())
					return false;
				
				int pos = 0;
				Iterator<DataType> itX = xKnots.getValues().iterator();
				Iterator<DataType> itY = yKnots.getValues().iterator();
				double prev = Double.NEGATIVE_INFINITY;
				while(itX.hasNext() && itY.hasNext())
				{
					pos++;
					DataType elementX = itX.next();
					DataType elementY = itY.next();
					if(elementX == null)
					{
						return false;
					}
					else if (elementX.getType()!=DataType.Type.DOUBLE || elementY.getType()!=DataType.Type.DOUBLE)
					{
						return false;
					}
					else
					{
						double dX = ((DoubleType)elementX).getValue();
						double dY = ((DoubleType)elementY).getValue();
						if(Double.isNaN(dX) || Double.isInfinite(dX) || Double.isNaN(dY) || Double.isInfinite(dY))
						{
							return false;
						}
						if(dX<=prev)
						{
							return false;
						}
						prev = dX;
					}
				}
				return true;
			}
			
		};
		
		super.addRequirement(req);
		
		
		
	}

}
