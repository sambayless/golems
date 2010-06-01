package com.golemgame.constructor;

import java.util.concurrent.Future;

public interface TerminatingUpdatable extends Updatable {
	public Future<Object> getTermination();
}
