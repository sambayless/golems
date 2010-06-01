package com.golemgame.tool.action;

/**
 * This defines a common interface to facilitate context sensitive controls.
 * @author Sam
 *
 */
public interface Actionable 
{

	
	/**
	 * This function will attempt to retrieve an Action corresponding to the type parameter.
	 * The action may or may not be created as a new instance.
	 * If there is no action of the requested type, an ActionType exception will be thrown.
	 * @param type The type of action to retrieve
	 * @return An action that is an instance of the class corresponding to the type parameter
	 * @throws ActionTypeException this will be thrown if there is no action of the type requested
	 */
	public Action getAction(Action.Type type) throws ActionTypeException;
	
	
	
	
}
