package com.golemgame.tool.action;

public abstract class AnonymousAction extends Action<AnonymousAction> {
	private String name;
	
	public AnonymousAction(String name) {
		super();
		this.name = name;
	}

	@Override
	public String getDescription() {
		return name;
	}

	@Override
	public Type getType() {
		return Type.ANONYMOUS;
	}

	@Override
	public String toString() {
		return name;
	}

}
