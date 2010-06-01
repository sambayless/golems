package com.golemgame.tool;

import com.golemgame.states.StateManager;


public class ToolSelectionEffectManager {
	private final static ToolSelectionEffectManager instance = new ToolSelectionEffectManager();

	private ToolSelectionEffect currentEffect = null;
	
	private ToolSelectionEffectManager() {
		super();
	}

	public static ToolSelectionEffectManager getInstance() {
		return instance;
	}
	
	public ToolSelectionEffect getCurrentEffect() {
		return currentEffect;
	}
	public void forceDisengage()
	{
		setCurrentEffect(null);
	}
	public void setCurrentEffect(ToolSelectionEffect effect)
	{
		if(currentEffect!=null && currentEffect != effect)
			currentEffect.forceDisengage();
		
		ToolSelectionEffect oldEffect = currentEffect;
		currentEffect= effect;

		if(currentEffect!=null  )
		{
			
			StateManager.getRootModel().addChild(effect.getModel());
/*			if(oldEffect!=null )
			{
				currentEffect.getModel().setModelData(oldEffect.getModel());
			}*/
			
			
			
		}
	}
	
	
	
}
