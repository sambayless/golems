package com.golemgame.tool.action;

import com.golemgame.mvc.Reference;

public class AddMemberAction extends Action<AddMemberAction>{

	public Reference member;
	
	public Reference getMember() {
		return member;
	}

	public void setMember(Reference member) {
		this.member = member;
	}

	@Override
	public String getDescription() {
		return "Add Member";
	}

	@Override
	public com.golemgame.tool.action.Action.Type getType() {
		return Action.ADD_MEMBER;
	}

}
