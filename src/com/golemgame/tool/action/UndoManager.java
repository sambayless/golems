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
package com.golemgame.tool.action;


import java.util.NoSuchElementException;

import com.golemgame.states.GeneralSettings;
import com.golemgame.util.LRUStack;


@SuppressWarnings("unchecked")
public class UndoManager 
{
	private static UndoManager instance = new UndoManager();
	
	private  LRUStack<Action> undoList;
	private  LRUStack<Action> doList;
	
	private int history = 100;
	
	public static UndoManager getInstance() {
		return instance;
	}
	
	private UndoManager()
	{
		undoList = new LRUStack<Action>(history);
		doList = new LRUStack<Action>(history);
	}
	
	public boolean undo()
	{
		try{
			Action<?> todo = undoList.pop();
			if (todo.getType() == Action.SPACER)
			{
				doList.push(todo);
				return undo();
			}
			todo.setFirstUse(false);
			if (todo.undoAction())
			{
				doList.push(todo);
				Action nextItem;
				if ((nextItem = peekUndo()) != null)
				{
					/**
					 * If the next item on the undo queue is dependent, then undo it as well.
					 */
					if (nextItem.getProperties() == UndoProperties.DEPENDENT)
					{
						undo();
					}else if (nextItem.getDependencySet() != null && nextItem.getDependencySet().equals(todo.getDependencySet()))
					{//if both actions are part of the same dependency set, and that set is not null, then do the next action as well
						undo();
					}
				}
				return true;
			}
			//If the action fails to undo, then no previous actions may undo either.
			undoList.clear();
			return false;
			
		}catch(NoSuchElementException e)
		{
			
			return false;
		}
		
		
	}
	
	public boolean redo()
	{
		try{
			Action<?> todo = doList.pop();
			if (todo.getType() == Action.SPACER)
			{
				undoList.push(todo);
				return redo();
			}
			todo.setFirstUse(false);
			if (todo.doAction())
			{
				undoList.push(todo);
				Action nextItem;
				//While the currently redone action is dependent, redo the following item also.
				if (todo.getProperties() == UndoProperties.DEPENDENT)
				{
					redo();
				}else if ((nextItem = peekRedo()) != null)
				{
					if (nextItem.getDependencySet() != null && nextItem.getDependencySet().equals(todo.getDependencySet()))
					{//if both actions are part of the same dependency set, and that set is not null, then do the next action as well
						redo();
					}
				}
					
				return true;
			}
			//If the action fails to redo, then no further actions are allowed to redo either.
			doList.clear();
			return false;
			
		}catch(NoSuchElementException e)
		{			
			return false;
		}
	}
	
	public void addAction(Action toAdd)
	{
		if (toAdd == null)
			return;
		GeneralSettings.getInstance().getMachineChanged().setValue(true);
		undoList.push(toAdd);
		if (toAdd.getType() != Action.SPACER)
			doList.clear();
	}
	
	public Action peekUndo()
	{
		
		try{
			return undoList.peek();
			
		}catch(NoSuchElementException e)
		{			
			return null;
		}
	}
	public Action peekRedo()
	{
		
		try{
			return doList.peek();
			
		}catch(NoSuchElementException e)
		{			
			return null;
		}
	}
	
	public void mergeAction(Action toMerge)
	{
	
		try{
			if (toMerge.getClass() != undoList.peek().getClass())
				throw new ActionMergeException();
			Action merged = undoList.peek().merge(toMerge);
			undoList.pop();
			addAction(merged);
		}catch(NoSuchElementException e)
		{
			addAction(toMerge);
			
		}catch(ActionMergeException e)
		{
			addAction(toMerge);
		}
		
	}
	
	public void clear()
	{
		undoList.clear();
		doList.clear();
	}
	
	public void addSpacer()
	{
		if (!undoList.isEmpty() && !(undoList.peek() instanceof UndoSpacer))
			addAction(new UndoSpacer());
	}
	
	public static class UndoSpacer extends Action<UndoSpacer>
	{

		@Override
		public Type getType() {
			
			return Action.SPACER;
		}
		@Override
		public String getDescription() {
			return "----";
		}
	
		
	}
	
	
	public enum UndoProperties
	{
		NORMAL(),
		
		/**
		 * A dependent action can't ever be the current item on the undo queue, unless it has just been added;
		 * if you undo an item that was added directly after a dependent, the dependent item will also be undone;
		 * if you redo an item that has dependents following it, they will also be redone.
		 */
		DEPENDENT();
	}
	
}
