package com.golemgame.tool.action;

public class CustomSelectionAction extends Action<CustomSelectionAction> {
	private static final long serialVersionUID = 1L;

	@Override
	public String getDescription() {
		return "Custom Selection";
	}

	@Override
	public com.golemgame.tool.action.Action.Type getType() {
		return Action.CUSTOM_SELECTION;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	/**
	 * 
	 * @param selection
	 * @return True if this custom selection is finished.
	 */
	public boolean selectionOccurs(Actionable selection)
	{
		return true;
	}
	
	public void forceFinish()
	{
		
	}
	
}
