package com.golemgame.structural;

import java.io.Serializable;
import java.util.Collection;

import com.golemgame.functional.LocalWireManager;
import com.golemgame.functional.WirePort;
import com.golemgame.mechanical.StructuralMachine;
import com.golemgame.model.Model;
import com.golemgame.model.effect.Appearance;
import com.golemgame.model.effect.ModelEffect;
import com.golemgame.mvc.Reference;
import com.golemgame.mvc.SustainedView;
import com.golemgame.tool.action.Actionable;
import com.golemgame.views.Viewable;


public interface Structural extends Serializable,Viewable,SustainedView,Actionable{
	public boolean isPhysical();
	public boolean isMindful();
	//public Physical[] getPhysical();
	

	public Actionable getActionable();
	public Model getModel();
	
	public Collection<LocalWireManager> getWireManagers();
	
	/**
	 * If the permanent appearance of the structure is being changed, do so through the appearance object
	 * @return
	 */
	public Appearance getAppearance();
	
	public StructuralAppearanceEffect getStructuralAppearanceEffect();
	/**
	 * Transient effects - like selection - can be added directly here
	 * @param effect
	 */
	public void addModelEffect(ModelEffect effect);
	
	public Reference getLayerReference();
	
	//public void setMachine(StructuralMachine machine);
	public StructuralMachine getMachine();

	public Collection<WirePort> getWirePorts();
	
	public Reference getID();
}
