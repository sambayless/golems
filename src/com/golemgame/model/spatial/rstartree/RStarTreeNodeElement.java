package com.golemgame.model.spatial.rstartree;

import com.golemgame.model.Model;
import com.golemgame.model.ParentModel;

public interface RStarTreeNodeElement extends ParentModel{
	public float evaluateCost(Model model);
	
	
}
