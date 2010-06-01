package com.golemgame.properties.fengGUI.mainmenu;

import org.fenggui.Label;
import org.fenggui.composite.Window;
import org.fenggui.layout.RowLayout;

public class MainWindow extends Window {

	public MainWindow() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MainWindow(boolean closeBtn, boolean maximizeBtn,
			boolean minimizeBtn, boolean autoClose) {
		super(closeBtn, maximizeBtn, minimizeBtn, autoClose);
		// TODO Auto-generated constructor stub
	}

	public MainWindow(boolean closeBtn, boolean maximizeBtn, boolean minimizeBtn) {
		super(closeBtn, maximizeBtn, minimizeBtn);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void build(boolean closeBtn, boolean maximizeBtn,
			boolean minimizeBtn) {
		super.build(closeBtn, maximizeBtn, minimizeBtn);
		super.titleBar.setVisible(false);
	}





}
