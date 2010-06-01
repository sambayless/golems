package com.golemgame.model.quality.spatial;

import java.lang.ref.WeakReference;

import com.golemgame.model.quality.ModelQualityDelegate;


public class SwitchModelQualityDelegate implements ModelQualityDelegate{
	private WeakReference<FlooredDistanceSwitchModel> switchModelRef;

	public SwitchModelQualityDelegate(FlooredDistanceSwitchModel switchModel) {
		super();
		this.switchModelRef = new WeakReference<FlooredDistanceSwitchModel>( switchModel);
	}

	
	public boolean setQuality(int quality) {
		
		FlooredDistanceSwitchModel switchModel = this.switchModelRef.get();
		if(switchModel == null)
		{
			
			return false;
		}
		if (quality>-3)
		{
			switchModel.setFloor(0);
		}else if (quality>-7)
		{
			switchModel.setFloor(1);
		}else
		{
			switchModel.setFloor(2);
		}
		return true;
	}
	
	
}
