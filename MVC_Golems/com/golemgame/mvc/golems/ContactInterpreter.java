package com.golemgame.mvc.golems;

import com.golemgame.mvc.PropertyStore;

public class ContactInterpreter extends StandardFunctionalInterpreter{
	
	//public static final String CONTACT_RADIUS;
	
	public ContactInterpreter()
	{
		this(new PropertyStore());
	 
	}
	
	public ContactInterpreter(PropertyStore store) {
		super(store);	
		getStore().setClassName(GolemsClassRepository.CONTACT_CLASS);
	}

}
