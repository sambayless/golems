package com.golemgame.tool.action;

import com.golemgame.mvc.Reference;

public class RemoveMemberAction extends Action<RemoveMemberAction> {
	public Reference member;
	
	public Reference getMember() {
		return member;
	}

	public void setMember(Reference member) {
		this.member = member;
	}

	@Override
	public String getDescription() {
		return "Remove Member";
	}

	@Override
	public com.golemgame.tool.action.Action.Type getType() {
		return Action.REMOVE_MEMBER;
	}
}
