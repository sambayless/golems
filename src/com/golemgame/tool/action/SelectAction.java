package com.golemgame.tool.action;

import java.util.Set;

import com.golemgame.model.Model;


public abstract class SelectAction extends Action<SelectAction> {

	private Set<Model> ignoreCollisions = null;
	public Set<Model> getIgnoreCollisions() {
		return ignoreCollisions;
	}


	public void setIgnoreCollisions(Set<Model> ignoreCollisions) {
		this.ignoreCollisions = ignoreCollisions;
	}

	protected boolean select = true;
	public void setSelect(boolean select)
	{
		this.select = select;
	}
	

	@Override
	public Type getType() 
	{
		
		return Action.SELECT;
	}
	
	public SelectAction() {
		
	}

	public boolean isSelect() {
		return select;
	}

	@Override
	public SelectAction copy() {
		try{
			SelectAction copy = (SelectAction) this.clone();
			copy.select = select;
			copy.ignoreCollisions = this.ignoreCollisions;
			return copy;
		}catch(Exception e)
		{
			return null;
		}		
	}
	

	@Override
	public SelectAction merge(SelectAction mergeWith) throws ActionMergeException 
	{
		
	
			
			mergeWith.ignoreCollisions.retainAll(ignoreCollisions);
			return mergeWith;

	}
	
	@Override
	public String getDescription() {
		return "Select";
	}
}
