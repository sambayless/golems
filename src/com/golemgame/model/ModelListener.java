package com.golemgame.model;

import java.io.Serializable;


public interface ModelListener extends Serializable {

	//public void modelChanged (Model source);
	public void modelMoved(Model source);
}
