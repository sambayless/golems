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
package com.golemgame.tool.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.golemgame.tool.action.Actionable;

public class SelectionToolManager {
	
	private static final SelectionResponder dummy = new SelectionResponder()
	{
		public void updatePrimarySelection(Actionable newPrimarySelction,
				Actionable oldPrimarySelection) {	
		}


		public void remove() {	
		}


		public void selectionCleared() {	
		}

		public void updateSelection(Collection<Actionable> currentSelection,
				Collection<Actionable> removedFromSelection) {
		}		
	};

	private Lock selectionLock = new ReentrantLock();
	
	
	private Collection<Actionable> unmodifiableSelection = null; //cached for efficiency.
	
	/**
	 * Important: the selection set NEVER leaves the manager.
	 */
	private Collection<Actionable> selection = new HashSet<Actionable>();
	private Actionable primarySelection = null;
	private SelectionResponder responder = dummy;
	
	public SelectionToolManager() {
		super();
		updateSelection();
	}

	public SelectionResponder getResponder() {
		selectionLock.lock();
		try{
			return responder;
		}finally{
			selectionLock.unlock();
		}	
	}

	public void setResponder(SelectionResponder responder) {
		selectionLock.lock();
		try{
			if(this.responder!=responder)
				this.responder.remove();
			
			if(responder!=null)
				this.responder = responder;
			else
				this.responder = dummy;
		}finally{
			selectionLock.unlock();
		}
		
	}

	public void clearSelection(){
		selectionLock.lock();
		try{
			removeFromSelection(getSelection());
		}finally{
			selectionLock.unlock();
		}
	}
	
	public void replaceSelection(Actionable newSelection)
	{
		replaceSelection(Collections.singletonList(newSelection));
	}
	
	public void appendToSelection(Actionable newSelection)
	{
		appendToSelection(Collections.singletonList(newSelection));
	}
	
	public void removeFromSelection(Actionable newSelection)
	{
		removeFromSelection(Collections.singletonList(newSelection));
	}
	
	public void replaceSelection(Collection<Actionable> newSelection)
	{
		selectionLock.lock();
		try{
		//	ArrayList<Actionable> added = new ArrayList<Actionable>();
			ArrayList<Actionable> removed = new ArrayList<Actionable>();

			ArrayList<Actionable> curSelection = new ArrayList<Actionable>(selection);
			selection.retainAll(newSelection);
			boolean removePrimary = false;
			for(Actionable c:curSelection)
			{	if(!selection.contains(c))
				{
					removed.add(c);
					if(c==getPrimarySelection())
						removePrimary = true;				
				}
			}
			
			Actionable oldPrimary = this.getPrimarySelection();
			if(removePrimary)
				this.primarySelection = null;
			
			
			updateSelection();
			responder.updateSelection(getSelection(),removed);
			
			if(removePrimary)
				responder.updatePrimarySelection(getPrimarySelection(), oldPrimary);;
			
		}finally{
			selectionLock.unlock();
		}
	}
	
	public void appendToSelection(Collection<Actionable> toAdd)
	{
		selectionLock.lock();
		try{
			ArrayList<Actionable> added = new ArrayList<Actionable>();
			
			for(Actionable r:toAdd)
			{
				if(selection.add(r))
					added.add(r);
			}
			updateSelection();
			responder.updateSelection(getSelection(), new ArrayList<Actionable>());
			
		}finally{
			selectionLock.unlock();
		}
	}
	
	public void removeFromSelection(Collection<Actionable> toRemove)
	{
		selectionLock.lock();
		try{
			boolean removePrimary = false;
			ArrayList<Actionable> removed = new ArrayList<Actionable>();
			
			for(Actionable r:toRemove)
			{
				if(selection.remove(r))
					removed.add(r);
				
				removePrimary |= (r==getPrimarySelection());
				
			}
			updateSelection();
			
			Actionable oldPrimary = this.getPrimarySelection();
			if(removePrimary)
				this.primarySelection = null;
			
			responder.updateSelection(getSelection(), removed);
			
			if(removePrimary)
				responder.updatePrimarySelection(getPrimarySelection(), oldPrimary);;
		}finally{
			selectionLock.unlock();
		}
	}
	
	public Collection<Actionable> getSelection()
	{
		return unmodifiableSelection;
	}
	/**
	 * Also adds to the selection if it isnt currently a member.
	 * 
	 * @param a
	 */
	public void setPrimarySelection(Actionable a)
	{
		selectionLock.lock();
		try{
		
			Actionable oldP = primarySelection;
			primarySelection = a;			
			getResponder().updatePrimarySelection(a, oldP);
			
		}finally{
			selectionLock.unlock();
		}
	}
	
	/**
	 * Also adds to the selection if it isnt currently a member
	 * @param a
	 */
	public Actionable getPrimarySelection() throws NoSuchElementException
	{
		selectionLock.lock();
		try{
			if(primarySelection==null)
				throw new NoSuchElementException();
			return primarySelection;
		}finally{
			selectionLock.unlock();
		}
	}
	
	private void updateSelection()
	{
		selectionLock.lock();
		try{
			unmodifiableSelection = Collections.unmodifiableCollection(selection);
		}finally{
			selectionLock.unlock();
		}
		
	}
}
