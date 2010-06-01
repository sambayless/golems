package com.golemgame.tool.action;

import com.golemgame.settings.SettingChangedEvent;
import com.golemgame.settings.SettingsListener;
import com.golemgame.tool.ActionToolSettings;



public abstract class ControlAction extends Action<ControlAction> {
//attempts to make the control points for a tool appear

	private static ControlAction currentControlCenter = null;
	
	
	
	protected boolean resolve = false;
	
	public Type getType() {
		return Action.CONTROL;
	}
	
	static
	{
		ActionToolSettings.getInstance().getModify().addSettingsListener(new SettingsListener<Boolean>()
				{

					
					public void valueChanged(SettingChangedEvent<Boolean> e) {
						if (!e.getNewValue())
							if(currentControlCenter != null)
							{
								currentControlCenter.setResolve(true);//NOTE: this may cause problems in the future
								currentControlCenter.doAction();//this is not the best way to do this: just trying to ensure that when control points are 
								//deselected, collision resolution happens no matter what.
								currentControlCenter.setVisible(false);
							}
						
					}
			
				});
		
	}

	
	public boolean doAction() {
		if (currentControlCenter != null)
		{
			currentControlCenter.setVisible(false);
		}
		currentControlCenter = this;
	
		return true;
	}



	
	public boolean undoAction() {
		if (currentControlCenter != null && currentControlCenter != this)
		{

			currentControlCenter.setVisible(false);
		}
		setVisible(false);
		currentControlCenter = null;
		return super.undoAction();
	}



	
	public ControlAction copy() {
		try{
			ControlAction copy = (ControlAction) this.clone();
			copy.resolve = resolve;
	
			return copy;
		}catch(Exception e)
		{
			return null;
		}		
	}
	
	public String getDescription() {
		return "Alter Properties";
	}
//	public abstract ControlPoint[] getControls();

	
	public ControlAction merge(ControlAction mergeWith) throws ActionMergeException {

	
			ControlAction merged = this;
			merged.resolve = mergeWith.resolve;
			return merged;
	
	}
	public boolean isResolve() {
		return resolve;
	}

	
	
	public void setResolve(boolean resolve) {
		this.resolve = resolve;
	}
	
	public abstract void setVisible(boolean visible);

	public abstract Actionable[] getControlPoints();
	

}
