package com.golemgame.tool.action;

public class CycleAction extends Action<CycleAction> {

	public enum Direction
	{
		LEFT,RIGHT;
	}
	
	private Direction direction = Direction.LEFT;	

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	@Override
	public String getDescription() {
		return "Cycle Groups";
	}

	@Override
	public com.golemgame.tool.action.Action.Type getType() {
		return Action.CYCLE;
	}

}
