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
import java.util.List;

import com.golemgame.model.Model;
import com.golemgame.model.ModelIntersectionData;
import com.golemgame.states.StateManager;
import com.golemgame.tool.ActionToolSettings;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.action.information.GroupInformation;
import com.golemgame.tool.action.information.SelectionInformation;
import com.golemgame.tool.action.information.SelectionPriorityInformation;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.system.DisplaySystem;

/**
 * This class may become a base class for other classes; it just provides standard methods for selecting/deselecting items
 * @author Sam
 *
 */
public class SelectionTool implements ISelectionTool{
	
	private final ISelectionRules selectionRules = new StandardSelectionRules();

	
	public ISelectionRules getSelectionRules() {
		return selectionRules;
	}

	public boolean mouseButton(int button, boolean pressed, int x, int y) {
		
		//rules: If you click on something that cannot be selected, you lose the selection.
		//else if multiple select is enabled, add it to the selection.
		//else, replace the previous selection with this selection.
		
		//the most recent selection also becomes the primary selection.
		if(pressed && button==0)
		{
			pickSelection(getSelectionRules(),x,y);
			return true;
		}
		
		return false;
	}
	
	public static final void pickSelection(ISelectionRules rules, int x, int y)
	{
		Vector2f mousePos = new Vector2f(x,y);
		Ray pickRay = new Ray();

		DisplaySystem.getDisplaySystem().getPickRay(mousePos, StateManager.IS_AWT_MOUSE, pickRay);

		ArrayList<ModelIntersectionData> pickResults = new ArrayList<ModelIntersectionData>();
		StateManager.getRootModel().intersectRay(pickRay, pickResults, true);
		performSelection(rules,pickResults);
	}

	public static final void performSelection(ISelectionRules rules, List<ModelIntersectionData> pickResults)
	{
	
			//put the hits under the mouse into prioritized order. Then, test if it can be selected.
			//from this list, manipulate the current selection.
				
			if(!	attemptToSelect(rules,pickResults))
			{
				StateManager.getSelectionManager().clearSelection();
			}
	

	}
	private static class ActionablePriorityPair implements Comparable<ActionablePriorityPair>
	{
		private final int priority;
		private final Actionable actionable;
		private final ModelIntersectionData intersection;
		
		public ActionablePriorityPair(Actionable actionable,ModelIntersectionData intersection, int priority) {
			super();
			this.actionable = actionable;
			this.intersection = intersection;
			this.priority = priority;
		}

		public ModelIntersectionData getIntersection() {
			return intersection;
		}


		public int getPriority() {
			return priority;
		}


		public Actionable getActionable() {
			return actionable;
		}


		
		public int compareTo(ActionablePriorityPair o) {
			if ( this.priority> o.getPriority())
				return 1;
			if ( this.priority < o.getPriority())
				return -1;
					
			return 0;
		}
		
	}
	 
	private static boolean attemptToSelect(ISelectionRules rules, List<ModelIntersectionData> pickResults)
	{
		//find each actionable, and sort by priority
		
		ArrayList<ActionablePriorityPair> actionables = new ArrayList<ActionablePriorityPair>();
		
		for (ModelIntersectionData intersectionData:pickResults)
		{

			Actionable actionable =intersectionData.getModel().getActionable();
			if(actionable != null)
			{
				int priority = 0;
				try{
					priority = ((SelectionPriorityInformation) actionable.getAction(Type.SELECTION_PRIORITY)).getSelectionPriority();
				}catch (ActionTypeException e)
				{

				}
				actionables.add(new ActionablePriorityPair(actionable,intersectionData,-priority)); //invert the priority so that it sorts right
			}
		}
		
		Collections.sort(actionables);

		
		
		for (ActionablePriorityPair actionPair:actionables)
		{
			if (attemptToSelect(rules,actionPair.getActionable(),actionPair.getIntersection().getModel()))
				return true;
					
					
		
		}	
		return false;
	}
	
	private static boolean attemptToSelect(ISelectionRules rules, Actionable actionable, Model selectedModel) {
		
		if (actionable == null)
			return false;
			
			if(!rules.isSelectable(actionable))
				return false;//allow selection veto right here.
			
			Collection<Actionable> toSelect = new HashSet<Actionable>();
			
			boolean allowMultipleSelect = true;
			try{
				SelectionInformation info = (SelectionInformation)actionable.getAction(Action.SELECTINFO);
				if (!info.isSelectable())
					return false;
				
				allowMultipleSelect = info.isMultipleSelectable();
			
			}catch (ActionTypeException e)
			{
				//do nothing - if no selection info is provided, continue 
			}
			
	
			
			toSelect.add(actionable);
			
			if(ActionToolSettings.getInstance().isGroupSelectionMode())
			{
				try{
				//	long time = System.nanoTime();
					List<Actionable> groupList =((GroupInformation)	actionable.getAction(Action.GET_GROUP)).getGroupMembers(ActionToolSettings.getInstance().getStaticsSelectable().isValue());
				//	System.out.println(System.nanoTime()-time);
				//	time = System.nanoTime();
					if(groupList!=null)
					{
						for(Actionable act:groupList)
						{
							if(rules.isSelectable(act))
								toSelect.add(actionable);
						}
					}
					
				//	System.out.println(System.nanoTime()-time);
				}catch(ActionTypeException e)
				{
					
				}
			}
			if(allowMultipleSelect && ActionToolSettings.getInstance().getMultipleSelect().isValue())
			{
				StateManager.getSelectionManager().appendToSelection(toSelect);
			}else{
				StateManager.getSelectionManager().replaceSelection(toSelect);
			}
			return true;
			
		

						/*
						try{//this used to use the selectedmodels actionable - changed to method passed actionable.. dont know what the effect of this is
							ModifyAction action = (ModifyAction) actionable.getAction(Action.MODIFY);
							action.setModify(ActionToolSettings.getInstance().getModify().isValue());
							action.doAction();							
						}catch(ActionTypeException e)
						{
							try{//this used to use the selectedmodels actionable - changed to method passed actionable.. dont know what the effect of this is
								ControlAction action = (ControlAction)actionable.getAction(Action.CONTROL);
								if(ActionToolSettings.getInstance().getModify().isValue())
								{
									action.doAction();
									
									action.setVisible(true);
								}else
								{
									action.undoAction();
									action.setVisible(false);
								}
							}catch(ActionTypeException ex)
							{
								
							}
						}*/
				
			//	return true;

	
	}

	
	public void mouseMovementAction(Vector2f mousePos, boolean left,
			boolean right) {
	
	}

	public void scrollMove(int wheelDelta, int x, int y) {
		
	}

	protected class StandardSelectionRules implements ISelectionRules
	{
		public boolean isSelectable(Actionable actionable) {
			
			try{
				SelectionInformation info = (SelectionInformation)actionable.getAction(Action.SELECTINFO);
				if (!info.isSelectable())
					return false;
			}catch(ActionTypeException e)
			{
				//do nothing
			}
			return true;
		}
	}

	private final SelectionResponder responder = new DefualtSelectionResponder();
	public SelectionResponder getResponder() {
		return responder;
	}

	protected class DefualtSelectionResponder implements SelectionResponder
	{

		public void remove() {
			
		}

		public void updatePrimarySelection(Actionable newPrimarySelction,
				Actionable oldPrimarySelection) {
			
		}

		public void updateSelection(Collection<Actionable> currentSelection,
				Collection<Actionable> removedFromSelection) {
			
		}
		
	}


	
}
