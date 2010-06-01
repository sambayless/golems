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
