package com.golemgame.constructor;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import com.golemgame.states.StateManager;

/**
 * Revamp this class so it has a number of update streams.
 * Toss the order functionality; just allow updatables to be added to the given stream.
 * Finally; each stream will be updated independantly, allowing different threads to service a particular stream.
 * @author Sam
 *
 */
public class UpdateManager 
{
	private static final UpdateManager instance = new UpdateManager();
	
	public static enum Stream
	{
		GL_UPDATE(),GL_RENDER(),PHYISCS_UPDATE(),PHYSICS_RENDER(), PHYISCS_POST_UPDATE;
	}
	
	private Collection<Updatable>[] streams;
	

	@SuppressWarnings("unchecked")
	private UpdateManager()
	{		
		streams = new Collection[Stream.values().length];
		for (Stream stream:Stream.values())
		{
			streams[stream.ordinal()] = new CopyOnWriteArrayList<Updatable>();
		}
	}
	
	public void clearStream(Stream stream)
	{
		streams[stream.ordinal()].clear();
	}
	
	public void update(float time, Stream stream)
	{
		for (Updatable toUpdate:streams[stream.ordinal()])
		{
		//	if(toUpdate != null)
				toUpdate.update(time);
		}
	}

	public boolean add(Updatable toAdd)
	{
		return add(toAdd, Stream.GL_UPDATE);
	}
	
	public boolean remove(Updatable toRemove)
	{
		for(Collection<Updatable> stream:streams)
		{
			if (stream.remove(toRemove))
				return true;
		}
		return false;
	}
	
	/**
	 * Adds this updateable if it is not already present
	 * @param toAdd
	 * @return
	 */
	public boolean add(Updatable toAdd, Stream toStream)
	{
		if(toAdd != null)
			return streams[toStream.ordinal()].add(toAdd);
		else
		{
			StateManager.getLogger().warning("Null updateable added");
			return false;
		}
	
	}

	public boolean remove(Updatable toRemove, Stream fromStream)
	{
		return streams[fromStream.ordinal()].remove(toRemove);
	}

	
		
	

	public static final UpdateManager getInstance() {
		return instance;
	}



	
	
}