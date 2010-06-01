package com.golemgame.util;

import java.util.LinkedList;
import java.util.NoSuchElementException;

public class LRUStack<E> {
	private int size;

	private LinkedList<E>  data = new LinkedList<E>();
	
	public LRUStack(int size) {
		super();
		this.size = size;
	}


	public int getSize() {
		return size;
	}

	public boolean isEmpty()
	{
		return data.isEmpty();
	}
	
	public void clear()
	{
		data.clear();
	}

    /**
     * Pushes an element onto the stack represented by this deque (in other
     * words, at the head of this deque). If this increases the deque beyond
     * size elements, an element will be removed from the tail of the queue.
     *
     * <p>This method is equivalent to {@link #addFirst}.
     *
     * @param e the element to push
     * @throws IllegalStateException if the element cannot be added at this
     *         time due to capacity restrictions
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this deque
     * @throws NullPointerException if the specified element is null and this
     *         deque does not permit null elements
     * @throws IllegalArgumentException if some property of the specified
     *         element prevents it from being added to this deque
     */
	public void push(E object) throws IllegalStateException,ClassCastException,NullPointerException,IllegalArgumentException
	{
		while (data.size() >= size)
		{
			data.removeLast();
		}
		data.addFirst(object);
		
	}
	
	
	/**
		Pops an element from the stack represented by this deque. In other words, removes and returns the first element of this deque. 
		
		This method is equivalent to removeFirst(). 
		
		Returns:
		the element at the front of this deque (which is the top of the stack represented by this deque)
		Throws:
		NoSuchElementException if this deque is empty
	 */
	public E pop() throws NoSuchElementException
	{
		return data.remove();
	}
	
    /**
     * Retrieves, but does not remove, the head of the queue represented by
     * this deque (in other words, the first element of this deque), or
     * returns <tt>null</tt> if this deque is empty.
     *
     * <p>This method is equivalent to {@link #peekFirst()}.
     *
     * @return the head of the queue represented by this deque, or
     *         <tt>null</tt> if this deque is empty
     */
	public E peek()
	{
		return data.peek();
	}

	/**
	 * Resize the stack to a new maximum size. If the stack is larger than the new size, it will be reduced to that size.
	 * @param newSize
	 */
	public void setSize(int newSize) 
	{
		if (newSize < this.size)
		{
			while (data.size() > newSize)
				data.removeFirst();
		}
		
		this.size = newSize;
	}
	
}
