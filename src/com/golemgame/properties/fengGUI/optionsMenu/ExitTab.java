package com.golemgame.properties.fengGUI.optionsMenu;

import org.fenggui.Button;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.layout.CenteringLayout;
import org.fenggui.layout.RowLayout;

import com.golemgame.local.StringConstants;
import com.golemgame.properties.fengGUI.ConfirmationBox;
import com.golemgame.properties.fengGUI.ImageTabAdapter;
import com.golemgame.properties.fengGUI.mainmenu.MainMenu;
import com.golemgame.states.GUILayer;
import com.golemgame.states.GeneralSettings;
import com.golemgame.states.StateManager;
import com.golemgame.tool.action.UndoManager;
import com.jme.image.Texture;


public class ExitTab extends ImageTabAdapter {

	private final MainMenu menu;
	
	private Texture hover;
	private Texture active; 
	private Texture inactive;
	private Texture icon;
	
	public ExitTab(MainMenu menu) {
		super(StringConstants.get("MAIN_MENU.EXIT","Exit"));
		this.menu = menu;	
		
		 hover = super.loadTexture("buttons/glass/Glass_Red.png");
		 active =super.loadTexture("buttons/solid/Solid_Red.png");
		 inactive =super.loadTexture("buttons/solid/Solid_Red.png");
		 icon =super.loadTexture("buttons/menu/Power.png");
		
	}

	
	@Override
	public Texture getActive() {
		return active;
	}


	@Override
	public Texture getHover() {
		return hover;
	}


	@Override
	public Texture getIcon() {
		return icon;
	}


	@Override
	public Texture getInactive() {
		return inactive;
	}

	private boolean userConfirm(String title, String message) {
		return ConfirmationBox.showBlockingConfirmBox(title, message, GUILayer.getInstance().getDisplay());
	}
	
	protected void buildGUI() {

		Container centeringContainer = FengGUI.createContainer(getTab());
		
		centeringContainer.setLayoutManager(new CenteringLayout());
		
		Container rowVertical = FengGUI.createContainer(centeringContainer);
		rowVertical.setLayoutManager(new RowLayout(false));
		
		FengGUI.createLabel(rowVertical,StringConstants.get("MAIN_MENU.EXIT.CHECK","Are you sure you want to exit?"));
		Container rowHorizontal = FengGUI.createContainer(rowVertical);
		rowHorizontal.setLayoutManager(new RowLayout(true));
		
		Button ok = FengGUI.createButton(rowHorizontal, StringConstants.get("MAIN_MENU.YES" ,"Yes"));
		Button cancel = FengGUI.createButton(rowHorizontal,StringConstants.get("MAIN_MENU.NO" ,"No") );
		
		ok.addButtonPressedListener(new IButtonPressedListener()
		{

			
			public void buttonPressed(ButtonPressedEvent e) {
				menu.close();
				StateManager.getThreadPool().execute(new Runnable(){

					public void run() {
						
						if(GeneralSettings.getInstance().getMachineChanged().isValue() && !userConfirm("Exit","Are you sure you want to exit without saving first?"))
							return;
						StateManager.exit();
					}
				});
				
			
				
			}
			
		});
		
		cancel.addButtonPressedListener(new IButtonPressedListener()
		{

			
			public void buttonPressed(ButtonPressedEvent e) {
				close(true);
				
			}
			
		});
	}

	
	public void close(boolean cancel) {
		{
			menu.showDefaultTab();
		}
	}
	
	

}
