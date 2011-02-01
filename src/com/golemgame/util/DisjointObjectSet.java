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


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisjointObjectSet<E> {
	private final ArrayList<E> objects = new ArrayList<E>();


	private Map<E, Integer> inverseMap = new HashMap<E, Integer>();
	private DisjointSet disjoint = null;
	public DisjointObjectSet() {
		super();
		
	}
	public ArrayList<E> getElements() {
		return objects;
	}
	public void addElement(E o)
	{
		objects.add(o);
	}
	
	/**
	 * Union the two trees that x and y belong to.
	 * @param x
	 * @param y
	 * @return
	 */
	public void union(E x, E y)
	{
		if(disjoint == null)
			initialize();
		
		disjoint.union(getElement(x), getElement(y));
	}
	
	/**
	 * Return the collection that x belongs to.
	 * @param x
	 * @return
	 */
	public Collection<E> find(E x)
	{
		if(disjoint == null)
			initialize();
		int setNum = disjoint.find(getElement(x));
		
		Collection<E> set = new ArrayList<E>();
		for(int i = 0;i<objects.size();i++)
		{
			if(disjoint.find(i) == setNum)
				set.add(objects.get(i));
		}
		return set;
		
	}
	
	public List<Collection<E>> enumerateSets()
	{
		if(disjoint == null)
			initialize();
		ArrayList<Collection<E>> sets = new ArrayList<Collection<E>>();
		Map<Integer,Collection<E>> setMap = new HashMap<Integer,Collection<E>>();
		for(int i = 0;i<disjoint.size();i++)
		{
			int set = disjoint.find(i);
			if(!setMap.containsKey(set))
			{
				setMap.put(set, new ArrayList<E>());
				sets.add(setMap.get(i));
			}
			setMap.get(set).add(objects.get(i));
		}
		
		//for(int i = 0;i<setMap.size();i++)
			
		return sets;
	}
	
	private int getElement(E x)
	{
		return inverseMap.get(x);
	}
	
	private void initialize()
	{
		disjoint = new DisjointSet(objects.size());
		for(int i = 0;i<objects.size();i++)
			inverseMap.put(objects.get(i), i);
	}
}
