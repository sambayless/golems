package com.golemgame.tool.selection;

import com.golemgame.tool.action.Actionable;

/**
 * 
 * @author Sam
 *
 */
public interface ISelectionRules {
	public boolean isSelectable(Actionable a);
	public static ISelectionRules noRules =  new ISelectionRules()
	{
		public boolean isSelectable(Actionable a) {
			return true;
		}		
	};
}
