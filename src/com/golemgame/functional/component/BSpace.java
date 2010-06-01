package com.golemgame.functional.component;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import com.golemgame.functional.component.medium.BLightMedium;
import com.golemgame.functional.component.medium.BMedium;



/**
 * All minds in a given simulation (even if they are not directly interacting, or related),
 * should be registered to the same BSpace.
 * 
 * @author Sam
 *
 */
public class BSpace implements Serializable{
	static final long serialVersionUID =1;
	private ArrayList<BMind> minds= new ArrayList<BMind>();
	
	public enum AvailableMedium	{
		Light();
	}
	
	private CopyOnWriteArrayList<BUpdateCallback> callbacks = new  CopyOnWriteArrayList<BUpdateCallback> ();
	
	
	private BLightMedium lightMedium = new BLightMedium();
	

	/**
	 * Sources are components that are updated at every step, and that produce signals on their own,
	 * in the absence of inputs.
	 * 
	 * Sensors are sources.
	 */
	private Set<BSource> sources = new HashSet<BSource>();
	public BSpace()
	{
		//for (int i = 0; i<BObservable.SENSOR_COUNT; i++)
			//sensorTypes[i] = new ArrayList<BSensor>();
		
	}
	
	public void clear()
	{		
		this.callbacks.clear();
		Iterator<BMind> i = minds.iterator();
		while (i.hasNext())
		{
			i.next();
			i.remove();
		}
		sources.clear();
	
	}
	public void addSource(BSource source)
	{
		sources.add(source);
	}
	
	public void removeSource(BSource source)
	{
		sources.remove(source);
	}
	
	public void attachMind(BMind mind)
	{
		minds.add(mind);
		mind.setSpace(this);
		for (BSource source:mind.getSources())
		{
			if (source.isUpdatable())
				sources.add(source);
		}
	}
	
	public void removeMind(BMind mind)
	{
		minds.remove(mind);
		for (BSource source:mind.getSources())
		{
			if (source.isUpdatable())
				sources.remove(source);
		}
	}
	
	public BMedium getMedium(AvailableMedium type)
	{
		switch(type)
		{
			case Light:
				return this.lightMedium;
			default:
				return null;//since this switch is on this classes enum, this shouldn't occur
		}
	}
	
	/**
	 * Note: This method should be called from the physics thread only.
	 * @param time
	 */
	public void update (float time)
	{
		for (BUpdateCallback update:callbacks)
			update.beforeBUpdate();
 		updateSources(time);
		for (BMind mind:minds)
		{
			mind.update(time);
			//update first takes the signals from any sources (including holdover sources)
			//and applies them to each attached component.
			//then it generates a signal from each effected component, and sends that signal
			//to its downstream components, etc, until a cycle occurs or there are no downstream components
			//when a cycle is about to occur, instead of sending the signal to that cycle component, 
			//the signal is stored as a holdover value in that component, and the component is added as (temporary)
			//source to the mind.\
			
			//As before, you need to assure that when a component sends a signal
			
			
			
			/*
			 *Totally different, perhaps more realistic approach. 
			 *No cycle checking
			 *Update the BSpace many, many times for each physics update (thousands of times perhaps)
			 *
			 *Signals take a certain amount of time to get out of each component (assume its constant for now)
			 *So: Each step "advances" the signal forward by one component depthwise (though it may be going in many 
			 *parallel streams).
			 *
			 *
			 * 
			 * 
			 */
		}
		for (BUpdateCallback update:callbacks)
			update.afterBUpdate();
	}

	private void updateSources(float time)
	{
		for (BSource source : sources)
		{
			source.updateSource(time);
		}
		
	}
	
	//Just like physics space, bspace should not be serialized - for now; though it may be useufl later.
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		throw new NotSerializableException();
	}
	private void readObject(ObjectInputStream in) throws IOException
	{
		throw new NotSerializableException();
	}
	
	public void addUpdateCallback(BUpdateCallback callback)
	{
		this.callbacks.add(callback);
	}
	
	public void removeUpdateCallback(BUpdateCallback callback)
	{
		this.callbacks.remove(callback);
	}
	
	
}

