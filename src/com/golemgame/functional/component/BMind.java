package com.golemgame.functional.component;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class BMind implements Serializable {
	private static final long serialVersionUID = 1;

	
	private Set<BSource> sources=new HashSet<BSource>();
	private BSpace space;
	private Collection<BComponent> components = new ArrayList<BComponent>();
	
	private transient SignalComponentMap componentMap = new SignalComponentMap();
	
	
	public void removeFromSpace()
	{
		if (space != null)
			space.removeMind(this);
	}
	
	public BMind(BSpace space)
	{
		this.space = space;
	}

	public void addSource(BSource source)
	{
		sources.add(source);
		if (this.getSpace()!= null)
			this.getSpace().addSource(source);
		source.setMind(this);
	}
	
	public void addComponent(BComponent component)
	{
		if(! this.components.contains(component))
			this.components.add(component);
	}
	
	
	public void removeComponent(BComponent component)
	{
		this.components.remove(component);
	}
	
	public void removeSource(BSource source)
	{
		sources.remove(source);
		if (this.getSpace()!= null)
			this.getSpace().removeSource(source);
		source.setMind(null);
	}


	
	public void update(float time)
	{
		
		/*
		 * Two steps:
		 * One, every component generates a signal.
		 * Two, every signal is passed to its destination(s).
		 */
		
		for(BComponent component:components)
		{
			float signal = component.generateSignal(time);
			
			this.componentMap.put(signal, false, component.getPositiveConnections());
			this.componentMap.put(signal, true, component.getInhibittedConnections());
		}
		
		this.componentMap.applyAll();
		
		/*
		 * 		//was in component.update method
		for (BComponent comp:connections.keySet())
			{
				comp.modulateState(signal, connections.get(comp));
				activeComponentSet.add(comp);
			}
		
		 */
	}
	
	
	
/*	private Set<BComponent> activeComponents = new HashSet<BComponent>();//temporary storage for components
	private Set<BComponent> nextComponents = new HashSet<BComponent>();
	
	public void update(float time)
	{
		nextComponents.clear();
		for (BSource source:sources)
		{
			source.update(time, nextComponents);
		}
		
		if (activeComponents.isEmpty())//it may be possible for components to be added to the active list from outside this method
		{
			Set<BComponent> temp = activeComponents;
			activeComponents = nextComponents;//swap these
			nextComponents= temp;
			nextComponents.clear();
		}
		
		while(!activeComponents.isEmpty())
		{			
			for (BComponent component: activeComponents)
			{
				component.update(time, nextComponents);
			}
			
			Set<BComponent> temp = activeComponents;
			activeComponents = nextComponents;//swap these
			nextComponents= temp;
			nextComponents.clear();
		}
		//at this point, activeComponents must be empty
		
	}
*/


	public Set<BSource> getSources() {
		return sources;
	}

	public BSpace getSpace() {
		return space;
	}

	public void setSpace(BSpace space) {
		this.space = space;
	}
	

	private void writeObject(ObjectOutputStream out) throws IOException
	{
		throw new NotSerializableException();
	}
	private void readObject(ObjectInputStream in) throws IOException
	{
		throw new NotSerializableException();
	}
	
}
