package com.golemgame.mvc.golems.validate.requirement;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.validate.ValidationFailureException;

public interface Requirement {
	/**
	 * 
	 * @param dataType
	 * @return
	 */
	public void enfore(final PropertyStore store);
	
	public boolean test(final PropertyStore store) throws ValidationFailureException;//to allow for nested failure details
}
