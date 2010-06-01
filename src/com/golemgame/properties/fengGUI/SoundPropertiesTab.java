package com.golemgame.properties.fengGUI;

import java.util.HashMap;
import java.util.Map;

import org.fenggui.CheckBox;
import org.fenggui.ComboBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.ListItem;
import org.fenggui.decorator.background.PlainBackground;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;
import org.fenggui.util.Color;
import org.fenggui.util.Spacing;

import com.golemgame.local.StringConstants;
import com.golemgame.mvc.golems.GearInterpreter;
import com.golemgame.mvc.golems.SurfacePropertiesInterpreter;
import com.golemgame.mvc.golems.MaterialPropertiesInterpreter.MaterialClass;
import com.golemgame.mvc.golems.SurfacePropertiesInterpreter.SurfaceType;
import com.golemgame.settings.SettingChangedEvent;
import com.golemgame.settings.SettingsListener;
import com.golemgame.states.GeneralSettings;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;

public class SoundPropertiesTab  extends PropertyTabAdapter{
private GearInterpreter interpreter;
	
	
	private ComboBox<SurfaceType> surfaceTypes;
	private Map<SurfaceType,ListItem<SurfaceType>> surfaceMap ;
	private CheckBox<?> enabled;
	
	
	public SoundPropertiesTab() {
		super(StringConstants.get("PROPERTIES.SOUND","Sound"));
		
	}

	@Override
	protected void buildGUI() {
		surfaceMap= new HashMap<SurfaceType,ListItem<SurfaceType>>();

		getTab().setLayoutManager(new BorderLayout());
		
		Container soundContainer = FengGUI.createContainer(getTab());
		soundContainer.setLayoutData(BorderLayoutData.NORTH);
		soundContainer.setLayoutManager(new RowLayout());
		
		FengGUI.createLabel(soundContainer,StringConstants.get("PROPERTIES.SOUND.TYPE","Sound Type")).setExpandable(false);
		
		surfaceTypes = FengGUI.createComboBox(soundContainer);
		surfaceTypes.getAppearance().setPadding(new Spacing(0,5));
		surfaceTypes.getAppearance().add(new PlainBackground(Color.WHITE));
		surfaceTypes.setExpandable(true);
		for (SurfaceType mat:SurfaceType.values())
		{
			if(!mat.name().equals("INFER")){
				ListItem<SurfaceType> item = new ListItem<SurfaceType>(mat.toString() ,mat);
				surfaceMap.put(mat, item);
				surfaceTypes.addItem(item);		
			}
		}
		
		enabled = FengGUI.createCheckBox(soundContainer,StringConstants.get("PROPERTIES.SOUND.ENABLED","Enabled"));
		enabled.setShrinkable(true);
		enabled.setExpandable(false);
		
		
	}

	@Override
	public void close(boolean cancel) {
		if(!cancel)
		{
			standardClosingBehaviour(surfaceTypes, SurfacePropertiesInterpreter.SURFACE);
			standardClosingBehaviour(enabled, SurfacePropertiesInterpreter.ENABLED);
		
			Action<?> apply = super.apply();
			
			UndoManager.getInstance().addAction(apply);
		}
	}

	@Override
	public void open() {
		this.interpreter = new GearInterpreter(getPrototype());
		this.interpreter.loadDefaults();
		initializePrototype();
		
		super.associateWithKey(surfaceTypes, SurfacePropertiesInterpreter.SURFACE);
		super.associateWithKey(enabled, SurfacePropertiesInterpreter.ENABLED);
	
		super.standardOpeningBehaviour(surfaceTypes, SurfacePropertiesInterpreter.SURFACE);
		super.standardOpeningBehaviour(enabled, SurfacePropertiesInterpreter.ENABLED);

	
	
	}
	
	public void setMaterial(MaterialClass material)
	{
		surfaceTypes.setSelected(surfaceMap.get(SurfacePropertiesInterpreter.getDefaultSurface(material)));
	}
	
}
