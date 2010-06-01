package com.golemgame.mechanical;


/**
 * Compiled controllers serve as the bridge between a compiled component, and the structural component
 * from which it was constructed.
 * @author Sam
 *
 */
public interface CompiledController {
	
	/**
	 * Reset the compiled component to the model's condition.
	 * This includes resetting any mind components attached, as well as the physical components.
	 */
	public void resetPositions();

	
	/**
	 * This updates the original model to match the compiled component's current translation, rotation, etc.
	 */
	public void passToModel();
	
	/**
	 * Called when this compiled physics unit is finished being used.
	 */
	public void finish();
}
