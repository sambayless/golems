package com.golemgame.properties.fengGUI.optionsMenu;
import org.fenggui.CheckBox;
import org.fenggui.ComboBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.ListItem;
import org.fenggui.decorator.border.TitledBorder;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.event.mouse.MouseAdapter;
import org.fenggui.event.mouse.MouseClickedEvent;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;

import com.golemgame.local.StringConstants;
import com.golemgame.menu.color.ColorPatch;
import com.golemgame.menu.color.ColorWindow;
import com.golemgame.menu.color.ColorDialog.ColorDialogListener;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.SkyboxInterpreter;
import com.golemgame.mvc.golems.SkyboxInterpreter.Sky;
import com.golemgame.properties.fengGUI.TabAdapter;
import com.golemgame.states.GUILayer;
import com.golemgame.states.GeneralSettings;
import com.golemgame.states.StateManager;
public class SkyTab extends TabAdapter{

	private ComboBox<Sky> background;
	private ColorPatch colorPatch;
	private ColorWindow colorDialog;
	private CheckBox grid;
	public SkyTab() {
		super( StringConstants.get("MAIN_MENU.ENVIRONMENT.BACKGROUND" ,"Background"));
		
	}

	@Override
	protected void buildGUI() {
	
		getTab().setLayoutManager(new BorderLayout());
		Container mainContainer = FengGUI.createContainer(getTab());
		mainContainer.setLayoutData(BorderLayoutData.NORTH);
		mainContainer.getAppearance().add(new TitledBorder(StringConstants.get("MAIN_MENU.ENVIRONMENT.BACKGROUND" ,"Background")));
		mainContainer.setLayoutManager(new RowLayout(false));
		//FengGUI.createLabel(mainContainer,"Background");
		background = FengGUI.<Sky>createComboBox(mainContainer);
		
		for(Sky sky:SkyboxInterpreter.Sky.values())
		{
			background.addItem(new ListItem<Sky>(sky.getName(), sky));
		}
		Container outerOptionsContainer = FengGUI.createContainer(mainContainer);
		outerOptionsContainer.setLayoutManager(new BorderLayout());
		final Container optionsContainer = FengGUI.createContainer(outerOptionsContainer);
		outerOptionsContainer.setLayoutData(BorderLayoutData.NORTH);
		optionsContainer.setLayoutData(BorderLayoutData.CENTER);
		optionsContainer.setLayoutManager(new RowLayout(false));
		


		final Container colorPatchContainer = FengGUI.createContainer(optionsContainer);
		colorPatchContainer.setLayoutData(BorderLayoutData.CENTER);
		colorPatchContainer.setLayoutManager(new BorderLayout());
		Label l = FengGUI.createLabel(colorPatchContainer,StringConstants.get("MAIN_MENU.ENVIRONMENT.COLOR" , "Color (Click to Change):"));
		l.setLayoutData(BorderLayoutData.WEST);
		l.setExpandable(false);
		l.setShrinkable(true);
		colorPatch = new ColorPatch();
		colorPatchContainer.addWidget(colorPatch);
		colorPatch.setExpandable(true);
		colorPatch.setMinSize(150,15);
		//colorPatch.setSizeToMinSize();
		//colorPatch.setExpandable(false);
		//colorPatch.setShrinkable(false);
		
		colorPatch.setLayoutData(BorderLayoutData.EAST);
		
		background.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(
					SelectionChangedEvent selectionChangedEvent) {
				colorPatchContainer.setVisible(selectionChangedEvent.getToggableWidget().getValue() == Sky.COLOR);
				/*if(selectionChangedEvent.getToggableWidget().getValue() == Sky.COLOR &! (colorPatchContainer.getParent() == optionsContainer))
				{
					optionsContainer.addWidget(colorPatchContainer);
				}else
					optionsContainer.removeWidget(colorPatchContainer);
				getTab().layout();*/
				
			}
			
		});
		colorDialog = new ColorWindow();
		colorPatch.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseClickedEvent mouseClickedEvent) {
				if(colorPatchContainer.isVisible())
				{
					colorDialog.setInitialColor(colorPatch.getColor().getRed(),colorPatch.getColor().getGreen(),colorPatch.getColor().getBlue(),colorPatch.getColor().getAlpha());
					colorDialog.setOwner(SkyTab.this);
					colorDialog.addColorListener(new ColorDialogListener()
					{
						public void cancel() {
						}
						public void colorChosen(int red, int green, int blue,int alpha) {
							if (colorDialog.getOwner() != SkyTab.this)
								return;
							colorPatch.getColor().setColor(red, green, blue, alpha);
						}
					});
					colorDialog.display(GUILayer.getLoadedInstance().getDisplay(),Container.MAX_PRIORITY);
				}
			}			
		});
		
		final Container gridContainer = FengGUI.createContainer(optionsContainer);
		gridContainer.setLayoutData(BorderLayoutData.SOUTH);
		gridContainer.setLayoutManager(new BorderLayout());
		grid = FengGUI.createCheckBox(gridContainer,StringConstants.get("MAIN_MENU.ENVIRONMENT.SHOW_GRID" ,"Show grid in design mode"));
		grid.setLayoutData(BorderLayoutData.WEST);
		grid.setExpandable(false);
		grid.setShrinkable(true);
										
	}

	@Override
	public void close(boolean cancel) {
		super.close(cancel);
		if(!cancel)
		{
			GeneralSettings.getInstance().getShowGrid().setValue(grid.isSelected());
			
			PropertyStore skybox = StateManager.getMachineSpace().getInterpreter().getSkybox();
			SkyboxInterpreter interpreter = new SkyboxInterpreter(skybox);
			

			interpreter.setImage(background.getSelectedItem().getValue());
			interpreter.setColor(colorPatch.getColorRGBA());
			interpreter.refresh();
			
		}
	
	}

	@Override
	public void open() {
		super.open();
		PropertyStore skybox = StateManager.getMachineSpace().getInterpreter().getSkybox();
		SkyboxInterpreter interpreter = new SkyboxInterpreter(skybox);
		colorPatch.setColor(interpreter.getColor());
		Sky sky = interpreter.getImage();
		for(ListItem<Sky> i:background.getList().getItems()){
			if(i.getValue()==sky)
			{
				background.setSelected(i);
				break;
			}
		}
		
		
		grid.setSelected(GeneralSettings.getInstance().getShowGrid().isValue());
		
		
		
		
	}

}
