package com.golemgame.tool.action.information;


import com.golemgame.tool.action.Action;
import com.jme.math.Vector3f;

public abstract class MoveRestrictedInformation extends ActionInformation {

	@Override
	public Type getType() {
		return Action.MOVE_RESTRICTED;
	}

	/**
	 * Get the closest restricted position to the supplied position. 
	 * @param from The position to be restricted. Implementation note: This Vector3f may be destroyed in the process.
	 * @return The closest restricted position
	 */
	public abstract Vector3f getRestrictedPosition(Vector3f from);
	
}
