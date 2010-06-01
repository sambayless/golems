package com.golemgame.tool.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.golemgame.mvc.PropertyStore;

/**
 * If two actions of different types are always performed by a tool as a single 'unit',
 * (for example, a rotation actually consists of a rotate action and a move action to keep
 * center in the same place), they should be placed into this container, and this action list
 * should then be added to the undoManager; this way, merging will work properly, and as well the
 * action will be treated by the undo manager as a single unit (undone all at once).
 * @author Sam
 *
 */
public class ActionList extends Action<ActionList> {

	String description;
	
	ArrayList<Action> actionList = new ArrayList<Action>();
	
	private  Set<PropertyStore> stores = new HashSet<PropertyStore>();
	
	public ActionList(String description) {
		super();
		this.description = description;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setFirstUse(boolean firstUse) {
		for (Action<?> action:actionList)
		{
			action.setFirstUse(firstUse);
		}
		super.setFirstUse(firstUse);
	}

	@Override
	public Type getType() {
		
		return Action.COMBINED;
	}

	public void clear()
	{
		actionList.clear();
	}
	public void add(Action toAdd)
	{
		actionList.add(toAdd);
		if(toAdd.getStores() != null)
		{
			for (PropertyStore store:toAdd.getStores())
				this.stores.add(store);
		}
	}
	public void prependAction(Action toAdd)
	{
		actionList.add(0, toAdd);
		if(toAdd.getStores() != null)
		{
			for (PropertyStore store:toAdd.getStores())
				this.stores.add(store);
		}
	}

	@Override
	public ActionList copy() {
		ActionList newList = new ActionList(this.getDescription());
		
		for (Action action:actionList)
		{
			newList.add(action);
		}
		return newList;
	}

	public void mergeAction (Action action)
	{
		if(actionList.isEmpty()){
			add(action);
		}else 
		{		
			try{
				Action<?> mergedAction = actionList.get(actionList.size()-1).merge(action);
				actionList.remove(actionList.size()-1);
				actionList.add(mergedAction);
				if(mergedAction.getStores() != null)
				{
					for (PropertyStore store:mergedAction.getStores())
						this.stores.add(store);
				}
			}catch(ActionMergeException e)
			{
				add(action);
			}catch(ClassCastException e)
			{
				add(action);
			}
		}
	}
	
	@Override
	public boolean doAction() {
		
		for (Action action:actionList)
		{
			if (! action.doAction())
				return false;//break immediately
		}
		
		//refresh as neccesary
		
		for (PropertyStore store:stores)
		{
			store.refresh();
		}
		return true;
	}

	@Override
	public ActionList merge(ActionList mergeWith) throws ActionMergeException {
		if (mergeWith.actionList.size() != actionList.size())
			return this;
		Action [] actions =  actionList.toArray(new Action[actionList.size()]);
		actionList.clear();
		//ActionList newList = new ActionList(this.getDescription());
		
		for (int i = 0; i < actions.length; i++)
		{		
			
			actionList.add(actions[i].merge(mergeWith.actionList.get(i)));
			//The exception is thrown
		}
		
		return this;
	}

	@Override
	public boolean undoAction() {
		
		for (int i = actionList.size() -1; i >= 0; i--  )
		{
			if (!actionList.get(i).undoAction())
				return false;//break immediately
		}
		
		for (PropertyStore store:stores)
		{
			store.refresh();
		}
		return true;
	}

	public ArrayList<Action> getActionList() {
		return actionList;
	}
	
	
}
