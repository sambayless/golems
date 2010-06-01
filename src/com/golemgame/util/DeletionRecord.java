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
