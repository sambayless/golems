package com.golemgame.tool;

import java.util.Collection;

import com.golemgame.model.Model;
import com.golemgame.tool.action.Actionable;





public interface IActionTool extends ITool {

	public void select(Actionable toSelect, Model selectedModel, boolean primarySelection) throws FailedToSelectException;

	public Collection<Actionable> getSelectedActionables();

	/**
	 * Is anything currently selected.
	 * @return
	 */
	public boolean isSelected();
	
	public Actionable getPrimarySelection();

	public static class FailedToSelectException extends Exception
	{

		public FailedToSelectException() {
			super();
		
		}

		public FailedToSelectException(String message, Throwable cause) {
			super(message, cause);
			
		}

		public FailedToSelectException(String message) {
			super(message);
			
		}

		public FailedToSelectException(Throwable cause) {
			super(cause);
			
		}

		private static final long serialVersionUID = 1L;
		
	}

	public void deselect(Actionable actionable);
}
