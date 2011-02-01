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
import java.util.HashMap;
import java.util.Map;

/**
 * This class maintains a list of deletables that should be deleted/undeleted simultaneously.
 * It also maintains a store of informtation that may be lossed during deletion and may be useful for undeleting.
 * @author Sam
 *
 */
public class DeletionRecord {

	private ArrayList<Deletable> prerequisites = new ArrayList<Deletable>();
	
	private Map<String,Object> store = new HashMap<String,Object>();
	
	public boolean undelete()
	{
		for(int i = prerequisites.size()-1; i >= 0; i-- )
		{
			Deletable deletion = prerequisites.get(i);
			if (deletion.isDeleted())
				if (!deletion.undelete())
				{
					return false;
				}
		}
		return true;
	}
	
	public void delete()
	{

		for (Deletable deletable:prerequisites)
			deletable.delete();
	}
	public DeletionRecord()
	{
		
	}
	public void add(Deletable toAdd)
	{
		prerequisites.add(toAdd);
	}
	
	public void store(Object store, String key)
	{
		this.store.put(key,store);
	}
	
	public Object retrieive(String key)
	{
		return retrieive(key,null);
	}
	
	public Object retrieive(String key, Object defaultObject)
	{
		Object ret = store.get(key);
		if(ret == null)
			return defaultObject;
		return ret;
	}
	
}
