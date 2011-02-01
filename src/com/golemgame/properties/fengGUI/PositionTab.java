/*******************************************************************************
 * Copyright 2008, 2009, 2010 Sam Bayless.
 * 
 *     This file is part of Golems.
 * 
 *     Golems is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     Golems is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with Golems. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.golemgame.properties.fengGUI;

import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.TextEditor;
import org.fenggui.decorator.border.TitledBorder;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;

import com.golemgame.local.StringConstants;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.golems.SpatialInterpreter;
import com.golemgame.properties.fengGUI.scale.ScaleTab;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;

public class PositionTab extends PropertyTabAdapter {
	private SpatialInterpreter interpreter;
	
	

	private TextEditor positionX;
	private TextEditor positionY;
	private TextEditor positionZ;

	private TextEditor rotationX;
	private TextEditor rotationY;
	private TextEditor rotationZ;
	
	//private ScrollContainer scaleScroller;
	private Container scaleContainer;
	private Container inputContainer;
	public PositionTab() {
		super(StringConstants.get("PROPERTIES.MATTER.POSITION","Position"));
		
	}

	@Override
	public void close(boolean cancel) {
		super.close(cancel);
		if(!cancel)
		{
			//boolean changed = false;
			standardClosingBehaviourMultiplex(positionX, SpatialInterpreter.LOCALTRANSLATION,0, DataType.Type.VECTOR3, Format.Float);
			standardClosingBehaviourMultiplex(positionY, SpatialInterpreter.LOCALTRANSLATION,1, DataType.Type.VECTOR3, Format.Float);
			standardClosingBehaviourMultiplex(positionZ, SpatialInterpreter.LOCALTRANSLATION,2, DataType.Type.VECTOR3, Format.Float);
			
			standardClosingBehaviourMultiplex(rotationX, SpatialInterpreter.LOCALROTATION,0, DataType.Type.QUATERNION, Format.Angle);
			standardClosingBehaviourMultiplex(rotationY, SpatialInterpreter.LOCALROTATION,1, DataType.Type.QUATERNION, Format.Angle);
			standardClosingBehaviourMultiplex(rotationZ, SpatialInterpreter.LOCALROTATION,2, DataType.Type.QUATERNION, Format.Angle);

			
			
			
			
				Action<?> apply = super.apply();
				
				UndoManager.getInstance().addAction(apply);
			
		}
	}



	@Override
	public boolean embed(ITab tab) {
		if (tab instanceof ScaleTab)
		{
			super.getEmbeddedTabs().add(tab);
			scaleContainer.addWidget(tab.getTab());

			scaleContainer.layout();
	
			this.getTab().layout();
			return true;
		}else if (tab instanceof InputStructureTab)
		{
			super.getEmbeddedTabs().add(tab);
			inputContainer.setVisible(true);
			inputContainer.addWidget(tab.getTab());
			inputContainer.layout();	
			this.getTab().layout();
			return true;
		}
		
		return super.embed(tab);
	}

	@Override
	protected void buildGUI() {
		super.buildGUI();
		getTab().setLayoutManager(new BorderLayout());
		Container internal = FengGUI.createContainer(getTab());
		internal.setLayoutData(BorderLayoutData.NORTH);
		internal.setLayoutManager(new RowLayout(false));
		
		Container positionBig = FengGUI.createContainer(internal);
		positionBig.setLayoutManager(new RowLayout());
		FengGUI.createLabel(positionBig, StringConstants.get("PROPERTIES.MATTER.POSITION","Position: ")).setExpandable(false);
		
		Container position = FengGUI.createContainer(positionBig);
		position.setLayoutManager(new RowLayout());
		FengGUI.createLabel(position,"X").setExpandable(false);
		
		positionX = FengGUI.createTextEditor(position);
		FengGUI.createLabel(position,"Y").setExpandable(false);
		positionY = FengGUI.createTextEditor(position);
		FengGUI.createLabel(position,"Z").setExpandable(false);
		positionZ = FengGUI.createTextEditor(position);
		
		Container rotationBig = FengGUI.createContainer(internal);
		rotationBig.setLayoutManager(new RowLayout());
		 FengGUI.createLabel(rotationBig, StringConstants.get("PROPERTIES.MATTER.ROTATION","Rotation: ") ).setExpandable(false);
	
		Container rotation = FengGUI.createContainer(rotationBig);
		rotation.setLayoutManager(new RowLayout());
		FengGUI.createLabel(rotation,"X").setExpandable(false);
		rotationX = FengGUI.createTextEditor(rotation);
		FengGUI.createLabel(rotation,"Y").setExpandable(false);
		rotationY = FengGUI.createTextEditor(rotation);
		FengGUI.createLabel(rotation,"Z").setExpandable(false);
		rotationZ = FengGUI.createTextEditor(rotation);
		
		//scaleScroller = FengGUI.createScrollContainer(internal);
		//scaleScroller.setShowScrollbars(true);
		scaleContainer = FengGUI.createContainer(internal);
	//	scaleScroller.setInnerWidget(scaleContainer);
	
		scaleContainer.layout();
		scaleContainer.setLayoutManager(new RowLayout(false));
		
		inputContainer= FengGUI.createContainer(internal);
		inputContainer.setVisible(false);
		inputContainer.getAppearance().add(new TitledBorder(StringConstants.get("PROPERTIES.MATTER.INPUT","Input") ));//"Input"));
		inputContainer.setLayoutManager(new RowLayout(false));
	}



	@Override
	public void open() {
		super.open();
		interpreter = new SpatialInterpreter(super.getPrototype());
		interpreter.loadDefaults();
		initializePrototype();
		super.associateWithKey(positionX, SpatialInterpreter.LOCALTRANSLATION);
		super.associateWithKey(positionY, SpatialInterpreter.LOCALTRANSLATION);
		super.associateWithKey(positionZ, SpatialInterpreter.LOCALTRANSLATION);
		super.associateWithKey(rotationX, SpatialInterpreter.LOCALROTATION);
		super.associateWithKey(rotationY, SpatialInterpreter.LOCALROTATION);
		super.associateWithKey(rotationZ, SpatialInterpreter.LOCALROTATION);
		
		super.standardOpeningBehaviour(positionX, SpatialInterpreter.LOCALTRANSLATION, Format.Float, 0, DataType.Type.VECTOR3);
		super.standardOpeningBehaviour(positionY, SpatialInterpreter.LOCALTRANSLATION, Format.Float, 1, DataType.Type.VECTOR3);
		super.standardOpeningBehaviour(positionZ, SpatialInterpreter.LOCALTRANSLATION, Format.Float, 2, DataType.Type.VECTOR3);
		
		super.standardOpeningBehaviour(rotationX, SpatialInterpreter.LOCALROTATION, Format.Angle, 0, DataType.Type.QUATERNION);
		super.standardOpeningBehaviour(rotationY, SpatialInterpreter.LOCALROTATION, Format.Angle, 1, DataType.Type.QUATERNION);
		super.standardOpeningBehaviour(rotationZ, SpatialInterpreter.LOCALROTATION, Format.Angle, 2, DataType.Type.QUATERNION);
		

		
	}

}
