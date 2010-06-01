package com.golemgame.properties.fengGUI;

import java.util.ArrayList;

import org.fenggui.CheckBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;

import com.golemgame.local.StringConstants;
import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.PhysicalStructureInterpreter;
import com.golemgame.mvc.golems.decorators.GlueInterpreter;
import com.golemgame.save.direct.mvc.golems.DesignViewFactory;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;

public class PhysicalTab extends PropertyTabAdapter {

	private PhysicalStructureInterpreter interpreter;
	
	private Container embedContainer;
	private Container matterContainer;
	private Container positionContainer;
	private CheckBox<Object> isStatic ;
	private CheckBox<Object> isSticky ;	
	private Container soundContainer;
	public PhysicalTab() {
		super(StringConstants.get("PROPERTIES.MATTER","Matter"));
		
	}

	@Override
	protected void buildGUI() {
		getTab().setLayoutManager(new BorderLayout());
		Container mainContainer = FengGUI.createContainer(getTab());
		mainContainer.setLayoutManager(new RowLayout(false));
		mainContainer.setLayoutData(BorderLayoutData.NORTH);
		embedContainer = FengGUI.createContainer(mainContainer); 
		embedContainer.setLayoutManager(new RowLayout(false));
		matterContainer = FengGUI.createContainer(embedContainer);
		
		soundContainer = FengGUI.createContainer(embedContainer);
		
		Container grid = FengGUI.createContainer(mainContainer);
		grid.setLayoutManager(new RowLayout(false));
		 isStatic = FengGUI.createCheckBox(grid, StringConstants.get("PROPERTIES.MATTER.STATIC_DESCRIPTION", "Make this object static (it won't react to physics, but other objects can still crash into it)"));//);
	      isSticky = FengGUI.createCheckBox(grid,StringConstants.get("PROPERTIES.MATTER.STICKY_DESCRIPTION","Make this object sticky"));// ");
	      
	      Container internalPosContainer = FengGUI.createContainer(mainContainer);
	      internalPosContainer.setLayoutManager(new RowLayout());
	      positionContainer = FengGUI.createContainer(internalPosContainer);
	      positionContainer.setExpandable(false);
	      positionContainer.setWidth(500);
	     
	}
	private SoundPropertiesTab soundTab = null;
	private MatterTab matterTab = null;
	@Override
	public boolean embed(ITab tab) {
		if(tab instanceof MatterTab)
		{
			super.getEmbeddedTabs().add(tab);
			matterContainer.addWidget(tab.getTab());
			matterTab = (MatterTab)tab;
			if(soundTab!=null)
				((MatterTab)tab).setSoundTab(soundTab);
			//this.getTab().layout();
			return true;
		}else if(tab instanceof PositionTab)
		{
			super.getEmbeddedTabs().add(tab);
			positionContainer.addWidget(tab.getTab());
			//this.getTab().layout();
			return true;
		}else if(tab instanceof SoundPropertiesTab)
		{
			soundTab = (SoundPropertiesTab)tab;
			super.getEmbeddedTabs().add(tab);
			soundContainer.addWidget(tab.getTab());
			if(matterTab!=null)
				matterTab.setSoundTab(soundTab);
			//this.getTab().layout();
			return true;
		}
		return super.embed(tab);
	}

	@Override
	public void close(boolean cancel) {
		super.close(cancel);
		if(!cancel)
		{
			standardClosingBehaviour(isStatic, PhysicalStructureInterpreter.PHYSICAL_STATIC);
			CollectionType decorators = interpreter.getPhysicalDecorators();
			if(super.isAltered(isSticky))
			{
				super.setValueAltered(PhysicalStructureInterpreter.PHYSICAL_DECORATORS, true);
				PropertyStore currentGlue = null;
				for(DataType val:decorators.getValues())
				{
					if (val instanceof PropertyStore)
					{
						PropertyStore decorator = (PropertyStore) val;
						
						if (decorator.getClassName().equals(DesignViewFactory.GLUE_DECORATOR_CLASS))
						{
							currentGlue = decorator;
						}
					}
				}
				
				if(isSticky.isSelected())
				{		
					if(currentGlue == null)
					{
						decorators.addElement(new GlueInterpreter().getStore());						
					}
					
				}else
				{
					if (currentGlue != null)
						decorators.removeElement(currentGlue);
				}
			}
		
			
			Action<?> apply = super.apply();
			
			UndoManager.getInstance().addAction(apply);
		}
		
	}

	@Override
	public void open() {
		super.open();
		this.interpreter = new PhysicalStructureInterpreter(getPrototype());
		this.interpreter.setStatic(false);
		this.interpreter.getPhysicalDecorators();//there is a reason why im not loading defaults here!
		initializePrototype();
		
		super.associateWithKey(this.isStatic, PhysicalStructureInterpreter.PHYSICAL_STATIC);
		
	//	super.associateWithKey(this.dropDown, PhysicalStructureInterpreter.MATERIAL);

		super.standardOpeningBehaviour(isStatic, PhysicalStructureInterpreter.PHYSICAL_STATIC);
		
		
		ArrayList<DataType> multipleValuesStore = new ArrayList<DataType>();
		CollectionType decorators = super.getPropertyStoreAdjuster().getCurrentCollection(PhysicalStructureInterpreter.PHYSICAL_DECORATORS,multipleValuesStore);
	
		//determine if the unioned decorators list contains sticky...
		
		PropertyStore glue = null;
		
		for (DataType val:decorators.getValues())
		{
			if (val instanceof PropertyStore)
			{
				PropertyStore decorator = (PropertyStore) val;
				
				if (decorator.getClassName().equals(DesignViewFactory.GLUE_DECORATOR_CLASS))
				{
					glue = decorator;
					break;
				}
			}
		}
		//note this is broken - it doesnt identify correctly if some elements have glue and others dont.
		boolean hasMultipleGlueValues = false;
		for (DataType val:multipleValuesStore)
		{
			if (val instanceof PropertyStore)
			{
				PropertyStore decorator = (PropertyStore) val;
				
				if (decorator.getClassName().equals(DesignViewFactory.GLUE_DECORATOR_CLASS))
				{
					hasMultipleGlueValues = true;
					break;
				}
			}
		}
		
		if(!hasMultipleGlueValues)
		{
			super.setMultipleValues(isSticky, false,PhysicalStructureInterpreter.PHYSICAL_DECORATORS);
			isSticky.setSelected(glue!=null);
			super.setUnaltered(isSticky);
		}else
		{
			super.setMultipleValues(isSticky, true,PhysicalStructureInterpreter.PHYSICAL_DECORATORS);
		}
	

	}

	

}
