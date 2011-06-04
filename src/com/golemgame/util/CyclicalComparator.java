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
package com.golemgame.util;

import java.util.Comparator;

/**
 * If a number is less than the pivot, then it becomes large
 * @author Sam
 *
 */
public class CyclicalComparator<T extends Ordinal> implements Comparator<T> {
	private int pivot = -1;
	
	public int getPivot() {
		return pivot;
	}
	
	public void setPivot(int pivot) {
		this.pivot = pivot;
	}

	public int compare(T o1, T o2) {
		if ( o1.getOrdinal() <= pivot)
		{
			if (o2.getOrdinal()<= pivot)
			{
				return o1.getOrdinal() - o2.getOrdinal();
			}else
			{
				return 1;
			}
		}else if (o2.getOrdinal()<=pivot)
		{
			return -1;
			
		}
		
		return o1.getOrdinal() - o2.getOrdinal();
	}

}
