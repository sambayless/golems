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
package com.golemgame.mvc;

import java.io.Serializable;
import java.util.Random;

public class Reference implements Serializable{

	private static final long serialVersionUID = 1L;

	private long id;

	private static final Random random = new Random();

	//package level access
	long getValue()
	{
		return id;
	}
	
	public Reference(Reference copyFrom) {
		this( copyFrom.id);
	}
	

	public Reference(long guid) {
		super();
		this.id = guid;
	}
	
	public static final Reference createUniqueReference()
	{
		long id = 0;
		while (id==0) 
			id = random.nextLong();
		
		return new Reference(id);
	}
	
	/**
	 * Reassign the value of this reference.
	 */
	public void makeNew()
	{
		id = 0;
		while (id==0) 
			id = random.nextLong();
	}
	
	private static final Reference nullID =  new Reference(0);
	
	public static final Reference getNullReference()
	{
		return new Reference(nullID);//it is important that that new nullids be unique references...
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Reference other = (Reference) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public boolean isNull() {
		return this.equals(nullID);
	}

	@Override
	public String toString() {
		return String.valueOf( id);
	}


	
}
