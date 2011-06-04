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
package com.golemgame.structural.structures;

import com.golemgame.functional.WirePort;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.DistanceSensorInterpreter;
import com.golemgame.mvc.golems.DistanceSensorInterpreter.SensorMode;
import com.golemgame.properties.fengGUI.DistanceSensorTab;
import com.golemgame.properties.fengGUI.TabbedWindow;
import com.golemgame.structural.DesignViewFactory;
import com.golemgame.structural.structures.ghost.GhostStructure;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.CustomSelectionAction;
import com.golemgame.tool.action.Action.Type;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;


public class DistanceSensor extends PyramidStructure  {

	private static final long serialVersionUID = 1L;
	
	private GhostStructure distanceGhost;

	private WirePort output;

	

	

	public SensorMode getSensorMode() {
		return interpreter.getSensorMode();
	}



	public void setSensorMode(SensorMode sensorMode) {
		interpreter.setSensorMode( sensorMode);
	}



	public boolean isIgnoreStatics() {
		return interpreter.ignoreStatics();
	}



	public void setIgnoreStatics(boolean ignoreStatics) {
		interpreter.setIgnoreStatics(ignoreStatics);
	}


	private DistanceSensorInterpreter interpreter;
	public DistanceSensor(PropertyStore store) {
		super(store);
		this.interpreter = new DistanceSensorInterpreter(store);
	//	distanceGhost = new GhostPyramid(new GhostPyramidInterpreter( interpreter.getGhost()));
		
	//	this.getModel().addChild(distanceGhost.getModel());
		//distanceGhost.getModel().getLocalTranslation().set(1,0,0);
		//distanceGhost.addViewMode(MATERIAL);
		output = new WirePort(this,false);

		output.getModel().getLocalTranslation().y = 0.5f;
		output.getModel().updateWorldData();
		getModel().addChild(output.getModel());
		super.registerWirePort(output);
	}

	

	


	
	public boolean isMindful() {
		return true;
	}
	



	@Override
	public void refresh() {
		
		super.refresh();
	//	this.output.setReference(interpreter.getOutput());
		PropertyStore ghost = interpreter.getGhost();
		
		if (this.distanceGhost == null)
		{
			GhostStructure dist = (GhostStructure) DesignViewFactory.constructView(ghost);
			if(dist.getStructuralAppearanceEffect().getBaseColor().equals(ColorRGBA.gray))
			{
				dist.getStructuralAppearanceEffect().getInterpreter().setBaseColor(new ColorRGBA(1f,0f,0,0.7f) );
			}

			dist.addViewMode(MATERIAL);
			setDistanceGhostPrivate(dist);
		}else if (! this.distanceGhost.getStore().equals(ghost))
		{
			GhostStructure dist = (GhostStructure) DesignViewFactory.constructView(ghost);
			
			dist.getStructuralAppearanceEffect().getInterpreter().setBaseColor(this.distanceGhost.getStructuralAppearanceEffect().getBaseColor() );
			dist.getInterpreter().setLocalTranslation(new Vector3f(distanceGhost.getInterpreter().getLocalTranslation()));
			dist.getInterpreter().setLocalRotation(new Quaternion(distanceGhost.getInterpreter().getLocalRotation()));
	
			setDistanceGhostPrivate(dist);
			dist.addViewMode(MATERIAL);
		}
	
		
		ghost.refresh();
		
	}
	
	private void setDistanceGhostPrivate(GhostStructure distanceGhost)
	{
		if(this.distanceGhost!= null)
		{
			this.getModel().detachChild(this.distanceGhost.getModel());
			getViewManager().removeViewable(distanceGhost);
		}
		
		this.distanceGhost = distanceGhost;
		if(distanceGhost != null){
			this.getModel().addChild(distanceGhost.getModel());
			getViewManager().registerViewable(distanceGhost);
		}
	}

	





	
	public Action<?> getAction(Type type) throws ActionTypeException {
		if(type == Action.SELECT)
		{	
			return new SelectDistance();
		}else if (type == Action.CUSTOM_SELECTION)
		{
			return new CustomSelection();
		}
		return super.getAction(type);
	}

	
	
	/*
	 * When this is deleted, its ghost material is deleted.
	 * On selection, this makes ghost pyramid visible.
	 * It also attaches a selection listener to tool;
	 * anytime anything becomes selected, it checks to see if it is the ghost material
	 * or this; if not, the ghost material becomes invisible.
	 * 
	 * 
	 */




	

	public GhostStructure getDistanceGhost() {
		return distanceGhost;
	}
	
	public void setDistanceGhost(GhostStructure distanceGhost) {
		interpreter.setGhost(distanceGhost.getStore());
		interpreter.refresh();
	}

	/**
	 * Setting the distance ghost to invisible doesnt neccesarily mean it is invisible - just that it
	 * it set back to the same view mode as its parent.
	 * @param visible
	 */
	private void setDistanceVisible(boolean visible)
	{
		if (distanceGhost != null)
		{
			if(visible)
				distanceGhost.addViewMode(ViewMode.GHOST);
			else
			{
				distanceGhost.clearViews();
				distanceGhost.addViewModes(getViews());
			}
		}
	}
	
	
	public void populateProperties(TabbedWindow window) {
		DistanceSensorTab sensorTab = new DistanceSensorTab();
		sensorTab.setSensor(this);
		window.addTab(sensorTab);
		super.populateProperties(window);
	}

	private class SelectDistance extends Select
	{

		
		public boolean doAction() {
			if(super.isSelect() )
				setDistanceVisible(true);
		//	else
		//		setDistanceVisible(false);
			return super.doAction();
		}
		
	}
	
	private class CustomSelection extends CustomSelectionAction
	{

		
		public Actionable getControlled() {
			return DistanceSensor.this;
		}

		
		public void forceFinish() {
			
			setDistanceVisible(false);
			super.forceFinish();
		}

		
		public boolean selectionOccurs(Actionable selection) {
			if(selection == DistanceSensor.this || selection == DistanceSensor.this.distanceGhost || distanceGhost.isMember(selection))
			{
				return false;
			}else
			{
				setDistanceVisible(false);
				return true;
			}
			
		}
		

		
	}
}
