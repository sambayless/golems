package com.golemgame.mvc.golems.validate;

import com.golemgame.mvc.golems.validate.requirement.Requirement;

public class ValidationFailureException extends Exception {

	private static final long serialVersionUID = 1L;
	private final Requirement failed;
	public ValidationFailureException(Requirement failed) {
		super();
		this.failed = failed;
	}
	
}
