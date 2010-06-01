package com.golemgame.properties.fengGUI.optionsMenu;

import org.fenggui.FengGUI;
import org.fenggui.TextEditor;
import org.fenggui.layout.RowLayout;

import com.golemgame.local.StringConstants;
import com.golemgame.properties.fengGUI.TabAdapter;
import com.golemgame.states.GeneralSettings;

public class MemoryTab extends TabAdapter  {
	private TextEditor mem;
	public MemoryTab() {
		super(StringConstants.get("MAIN_MENU.DISPLAY.MEMORY" ,"Memory"));
		
	}

	@Override
	protected void buildGUI() {
		
		getTab().setLayoutManager(new RowLayout());
		FengGUI.createLabel(getTab(), "Maximum Memory (in MB)");
		mem = FengGUI.createTextEditor(getTab());
		
		
	}

	@Override
	public void close(boolean cancel) {
		
		try{
			int memory = Integer.valueOf(mem.getText());
			if(memory<64)
				memory = 64;
			GeneralSettings.getInstance().getMaxMemory().setValue(memory);
		}catch(NumberFormatException e){
			
		}
		
		super.close(cancel);
	}

	@Override
	public void open() {
		mem.setText(String.valueOf(GeneralSettings.getInstance().getMaxMemory().getValue()));
		super.open();
	}

}
