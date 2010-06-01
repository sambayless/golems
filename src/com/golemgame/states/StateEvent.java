package com.golemgame.states;

import java.util.EventObject;

public abstract class StateEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	public StateEvent(Object source) {
		super(source);
	}

}
