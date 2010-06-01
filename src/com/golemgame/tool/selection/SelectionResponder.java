package com.golemgame.tool.selection;

import java.util.Collection;

import com.golemgame.tool.action.Actionable;

/**
 * Tools will provide an implementation of this.
 * @author Sam
 *
 */
public interface SelectionResponder {
	public void remove();
	
	/**
	 * This can be called at any time; the selection need not have been changed.
	 * 
	 * @param currentSelection
	 * @param removedFromSelection guaranteed to hold any elements that have been removed from the selection since the last time this was called.
	 */
	public void updateSelection(Collection<Actionable> currentSelection,Collection<Actionable> removedFromSelection);
	
	/**
	 * May be called even if the primary selection has not changed. 
	 * @param newPrimarySelction May be null
	 * @param oldPrimarySelection May be null
	 */
	public void updatePrimarySelection(Actionable newPrimarySelction, Actionable oldPrimarySelection);
	

}
