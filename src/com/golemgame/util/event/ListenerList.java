package com.golemgame.util.event;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;




/**
 * This is a thread safe iterable list intended to hold Listeners. It is safe to modify the contents of this list 
 * using addListener or removeListener while iterating over the list; these operations will not effect
 * the current iteration.
 * @author Sam
 * 
 * As of java 1.5, this alternative is available: CopyOnWriteArrayList. it seems to be equivalent.
 *
 * @param <E>
 */
public class ListenerList<E> implements Iterable<E>, Serializable{
	
	private static final long serialVersionUID = 1L;

	/**
	 * Listeners holds a reference to an array that is immutable - the array never changes after it is initially populated.
	 * However, the reference may change - that is, it may point to a different array at some point in the future.
	 */
	@SuppressWarnings("unchecked")
	private volatile E[] listeners = (E[]) new Object[0];
	
	private transient Lock listenerLock;
	
	public ListenerList() {
		super();
		initTransients();
	}

	private void initTransients()
	{
		listenerLock = new ReentrantLock();;
	}
		
	/**
	 * Remove all listeners. This method is threadsafe.
	 */
	@SuppressWarnings("unchecked")
	public void clear()
	{
		listenerLock.lock();
		try{
			
			listeners = (E[]) new Object[0];
		}finally
		{
			listenerLock.unlock();
		}
	}
	
	public void addListener(E listener)
	{
		listenerLock.lock();
		try{
			@SuppressWarnings("unchecked")
			E[] newListeners = (E[]) new Object[listeners.length+1];
			
			System.arraycopy(listeners, 0, newListeners, 0, listeners.length);
			newListeners[newListeners.length-1] = listener;
			listeners = newListeners;
		}finally{
			listenerLock.unlock();
		}
	}
	
	public boolean contains(E element)
	{
		if (element == null)
			return false;
		for(E toCheck:this)
		{
			if (toCheck==null)
				continue;
			if(element.equals(toCheck))
				return true;
		}
				
		
		return false;
	}
	
	public boolean isEmpty()
	{
		return listeners.length == 0;
	}
	
	/**
	 * Add a listener to the list. Note: this method is threadsafe, may be called while the list is being iterated over,
	 * and is blocking (only one thread may add or remove at any one time).
	 * @param index
	 */
	public void removeListener(E listener)
	{
		//only let one thread modify listeners at a time
		listenerLock.lock();
		try{		
			int index;
			//find the listener
			for (index = 0; index<listeners.length;index++)
			{
				if (listeners[index].equals(listener))
				{
					break;
				}
			}
			
			this.removeListener(index);
		}finally
		{
			listenerLock.unlock();
		}
	}
	
	/**
	 * Remove a listener from the list. Note: this method is threadsafe, may be called while the list is being iterated over,
	 * and is blocking (only one thread may add or remove at any one time).
	 * @param index
	 */
	@SuppressWarnings("unchecked")
	private void removeListener(int index)
	{
		//only let one thread modify listeners at a time
		listenerLock.lock();
		try{
			if (index>= listeners.length)
				return;//listener was not found
			
			if (listeners.length <= 1)
			{
				listeners = (E[]) new Object[0];
				return;
			}
			
			E[] newListeners = (E[]) new Object[listeners.length-1];
			
			//skip the element we are removing.
			System.arraycopy(listeners, 0, newListeners, 0, index);
			if(listeners.length-index>1)
				System.arraycopy(listeners, index+1, newListeners, index+1, listeners.length - index-1);
			
			listeners = newListeners;
		}finally
		{
			listenerLock.unlock();
		}
	}

	
	public Iterator<E> iterator() {
		return new IteratorImpl(listeners);
	}
	
	private class IteratorImpl implements Iterator<E>
	{
		private E[] listeners;
		private int index = 0;
		
		public boolean hasNext() {
			return index<listeners.length;
		}

		
		public E next() {
			return listeners[index++];
		}

		
		public void remove() {
			removeListener(index-1);
		}

		public IteratorImpl(E[] listeners) {
			this.listeners = listeners;
		}
		
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		
		in.defaultReadObject();
		initTransients();
	}
	

	
	
	public Collection< E> getAll() {
	
		return 	Arrays.asList(listeners);
	}
	
	
}
