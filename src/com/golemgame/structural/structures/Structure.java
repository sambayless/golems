package com.golemgame.structural.structures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import com.golemgame.mechanical.StructuralMachine;
import com.golemgame.model.Model;
import com.golemgame.model.effect.Appearance;
import com.golemgame.model.texture.TextureTypeKey.TextureShape;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.Reference;
import com.golemgame.mvc.SustainedView;
import com.golemgame.mvc.golems.StructureInterpreter;
import com.golemgame.properties.Property;
import com.golemgame.properties.PropertySupplier;
import com.golemgame.properties.PropertyTabFactory;
import com.golemgame.properties.fengGUI.CloseListener;
import com.golemgame.properties.fengGUI.HorizontalTabbedWindow;
import com.golemgame.properties.fengGUI.IFengGUIDisplayable;
import com.golemgame.properties.fengGUI.TabbedWindow;
import com.golemgame.states.GUILayer;
import com.golemgame.structural.StructuralAppearanceEffect;
import com.golemgame.tool.ActionToolSettings;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.ModalPropertiesAction;
import com.golemgame.tool.action.MoveAction;
import com.golemgame.tool.action.RotateAction;
import com.golemgame.tool.action.ScaleAction;
import com.golemgame.tool.action.SelectAction;
import com.golemgame.tool.action.SelectionEffect;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.action.information.FocusInformation;
import com.golemgame.tool.action.information.ModelInformation;
import com.golemgame.tool.action.information.MoveRestrictedInformation;
import com.golemgame.tool.action.information.OrientationInformation;
import com.golemgame.tool.action.information.PropertyStoreInformation;
import com.golemgame.tool.action.information.SelectionInformation;
import com.golemgame.views.Viewable;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

public abstract class Structure implements SustainedView,  Actionable, Viewable,Serializable,PropertySupplier{

	private static final long serialVersionUID = 1L;
	private StructuralMachine machine;
	private boolean selectable = true;
	protected Appearance appearance;
	protected StructuralAppearanceEffect structuralAppearanceEffect;
	
	private StructureInterpreter interpreter;	
	
	public Structure(StructureInterpreter interpreter) {
		super();
		this.interpreter = interpreter;
		interpreter.getStore().setSustainedView(this);
	}

	public Structure(PropertyStore store) {
		super();
		this.interpreter = new StructureInterpreter(store);
		interpreter.getStore().setSustainedView(this);
	}


	public void populateProperties(TabbedWindow window)
	{
/*		MaterialPropertiesTab tab = new MaterialPropertiesTab();
		tab.setStructural(PhysicalStructure.this);
		window.addTab(tab);*/
	/*	TextureTab textureTab =new TextureTab();
		textureTab.setStructure(PhysicalStructure.this);
		window.addTab(textureTab);*/
	}
	public void setMachine(StructuralMachine machine) {
		this.machine = machine;
	
	}

	public PropertyStore getStore() {
		return interpreter.getStore();
	}

	public Reference getID()
	{
		return interpreter.getReference();
	}
	
	public void refresh()
	{
		//set the appearance and the machine from here...
		this.getModel().getLocalRotation().set(interpreter.getLocalRotation());
		this.getModel().getLocalTranslation().set(interpreter.getLocalTranslation());

		this.getModel().updateWorldData();
		
		if(! interpreter.getAppearanceStore().equals(this.structuralAppearanceEffect.getStore()) )
		{
			
			this.structuralAppearanceEffect = new StructuralAppearanceEffect(interpreter.getAppearanceStore());
			this.getAppearance().addEffect(structuralAppearanceEffect, true);
		
		}
		this.structuralAppearanceEffect.refresh();
		this.appearance.reapply();
		//this.getModel().updateWorldData();
	}


	public StructuralMachine getMachine() {
		return machine;
	}
	


	public void remove() {
		getModel().detachFromParent();
	
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
		
		//	this.getModel().setSelectable(selectable);
		//can't make this assumption; sometimes things are not selectable but are collidable (functionals).
	}
	
	/**
	 * Get the models in this structure to which appearances are applied.
	 * @return
	 */
	protected Model[] getAppearanceModels()
	{
		return getControlledModels();
	}

	/**
	 * This method must be called by subclasses after construction.
	 */
	protected void initialize()
	{
		structuralAppearanceEffect = new StructuralAppearanceEffect(interpreter.getAppearanceStore());
		structuralAppearanceEffect.setPreferedShape(getPrefferedShape());
		structuralAppearanceEffect.refresh();
		this.getAppearance().addEffect(structuralAppearanceEffect, true);
	}
	
	protected TextureShape getPrefferedShape() {
		return TextureShape.Plane;
	}

	public StructuralAppearanceEffect getStructuralAppearanceEffect() {
		return structuralAppearanceEffect; 
	}

	
	protected abstract Model[] getControlledModels();
	
	/**
	 * Centers the controlled models on their natural centers - not their physical centroids.
	 */
	public void centerModels()
	{
		if (getControlledModels() == null)
			return;
		Vector3f translation = new Vector3f(0,0,0);
		int i = 0;
		for (Model model:getControlledModels())
		{
			translation.addLocal(model.getLocalTranslation());
			i++;
		}
		translation.divideLocal(i);
		
	
		for (Model model:getControlledModels())
		{
			model.getLocalTranslation().subtractLocal(translation);
			model.updateWorldData();
		}
		
		this.getModel().getLocalRotation().multLocal(translation);
		this.getModel().getLocalTranslation().addLocal(translation);
		this.getModel().updateWorldData();

		
	}


	public Appearance getAppearance() {
		if (this.appearance == null)
		{
			this.appearance = new Appearance(this.getAppearanceModels());
		}
		return appearance;
	}
	

	public abstract Model getModel();

	/**
	 * Subclasses should override this to provide scaling behaviour
		 */
	protected void setScale(Vector3f scale)
	{
		
	}

	/**
	 * Subclasses should override this to provide scaling info
	 * @return
	 */
	protected Vector3f getScale()
	{
		return new Vector3f(1,1,1);
	}

	public Collection<Property> getPropertySet()
	{
		 ArrayList<Property> properties = new ArrayList<Property>();
		 properties.add(new Property(Property.PropertyType.POSITION,this.interpreter.getStore()));	
		 return properties;
	}

	public boolean isDeleted()
	{
		return false;
	}
	
	
	public boolean isSelectable()
	{
		return selectable;
	}

	

	protected void showSelectEffect(boolean engage)
	{
	
	}
	
	
	private transient ModalPropertiesAction properties = null;
	
	private synchronized ModalPropertiesAction getProperties()
	{
		if (properties == null)
		{
			properties = new Properties();
		}
		return properties;
	}
		
	
	
	public Action<?> getAction(Type type) throws ActionTypeException {
		switch(type)
		{
			case SELECT:
				return new SelectImpl();
			case MOVE:
				return new Move(interpreter,this);
			
			case MOVE_RESTRICTED:
				return new MoveRestrictedInfo();
		//	case DELETE:
			//	return new Delete();
			case SELECTINFO:
				return new SelectionInfo();
			case ROTATE:
				return new Rotate(interpreter);
				
			case SCALE:
				return new Scale(interpreter);
			case PROPERTIES:
				return  getProperties();
			case FOCUS:
				return new Focus();
			case SELECTIONEFFECT:
				return new SelectionEffectImpl2();
			case ORIENTATION:
				return new OrientationInfo();
			case MODEL:
				return new ModelInfo();
	
			case PROPERTY_STORE:
				return new PropertyStoreInfo();
			default:
				throw new ActionTypeException(type.toString());
		}
	}


	protected class Properties extends ModalPropertiesAction
	{
		private boolean currentlyShown = false;
		
		public boolean doAction() 
		{
			//final GUIState dialog = GUIState.getInstance();
			//MaterialWindow.getInstance().setMaterial(MaterialNode.this);
		
			GUILayer layer = GUILayer.getLoadedInstance();

			//only show one properties window for a specific structure at any one time.
			if (! currentlyShown)
			{
				TabbedWindow window = new HorizontalTabbedWindow();
				window.setTitle("Properties");
				populateProperties(window);
				
				PropertyTabFactory factory = new PropertyTabFactory();
				factory.populateWindow(window, getPropertySet());
				
				window.display(layer.getDisplay());
				
				window.addCloseListener(new CloseListener()
				{

					
					public void close(IFengGUIDisplayable closed) {
						currentlyShown = false;
						
					}
					
				});
				return true;
			}else
				return false;
		}
		
	}
	protected static class SelectImpl extends SelectAction
	{

		
	
		
		public boolean doAction() {
			return true;
		}

		
		public boolean undoAction() {
			return true;
		}
		
	}
	
	
	protected class MoveRestrictedInfo extends MoveRestrictedInformation
	{

		@Override
		public Actionable getControlled() {
			return Structure.this;
		}
		
		public Vector3f getRestrictedPosition(Vector3f from) {
			//Get the closest grid node to the provided position;	
			
			from.subtractLocal(ActionToolSettings.getInstance().getGridOrigin().getValue());
			from.divideLocal(ActionToolSettings.getInstance().getGridUnits().getValue());
			from.x = Math.round(from.x);
			from.y = Math.round(from.y);
			from.z = Math.round(from.z);
			from.multLocal(ActionToolSettings.getInstance().getGridUnits().getValue());
			return from;
		}	
	}
	
	protected class SelectionInfo extends SelectionInformation
	{

		@Override
		public Actionable getControlled() {
			return Structure.this;
		}
		
		public boolean isSelectable() {
			return Structure.this.isSelectable();// ;
		}
		
	}
	public Reference getLayerReference() {
		return interpreter.getLayer();
	}
	private class PropertyStoreInfo extends PropertyStoreInformation
	{

		@Override
		public PropertyStore getStore() {
			return Structure.this.getStore();
		}
		
	}
	
	protected static class Move extends MoveAction
	{

		private final StructureInterpreter interpreter;
		private Structure tempStructure = null;
		
		public Move(StructureInterpreter interpreter,Structure tempStructure ) {
			super();
			this.interpreter = interpreter;
			this.tempStructure = tempStructure;
		}


		public boolean doAction() {
		
			 interpreter.getLocalTranslation().set(position);
			
			 interpreter.refresh();
	
			return true;
		}

		
		public boolean undoAction() {

			 interpreter.getLocalTranslation().set(oldPosition);
			 interpreter.refresh();
			return true;
		}

		
		public void setPosition(Vector3f position) 
		{
		
			oldPosition.set(interpreter.getLocalTranslation());
			tempStructure.getModel().getParent().worldToLocal( position,this.position);//deal with ghosts later...
			//System.out.println(position + ","+ this.position);
			tempStructure = null;
		}
		
/*
 * 	
		public boolean undoAction() {
		//	getModel().getParent().updateWorldData();
		//	
			getModel().getParent().worldToLocal(oldPosition, interpreter.getLocalTranslation());
			refresh();
			getModel().updateWorldData();
			getModel().signalMoved();
			return true;
		}

		
		public void setPosition(Vector3f position) 
		{
			getModel().updateWorldData();
			oldPosition.set(getModel().getWorldTranslation());
			this.position.set(position);
 */
	

		
	};
	
	protected static class Scale extends ScaleAction
	{

		private final StructureInterpreter interpreter;
		
		
		public Scale(StructureInterpreter interpreter) {
			super();
			this.interpreter = interpreter;
		}

		
		public void setScale(Vector3f scale) {
			super.newScale.set(scale);
			super.oldScale.set(interpreter.getLocalScale());
		}
		


		
		public boolean doAction() {
			interpreter.setLocalScale(newScale);
			interpreter.refresh();
			return true;
		}

		
		public boolean undoAction() {
			interpreter.setLocalScale(oldScale);
			interpreter.refresh();
			return true;
		}

	

		
	}

	protected class ModelInfo extends ModelInformation
	{

		@Override
		public Actionable getControlled() {
			return Structure.this;
		}
		
		public Model getCollisionModel() {
			return getModel();
		}
		
	}
	
	protected static class Rotate extends RotateAction
	{

	
		private final StructureInterpreter interpreter;
		
		public Rotate(StructureInterpreter interpreter) {
			super();
			this.interpreter = interpreter;
		}

		public boolean doAction() {
			interpreter.getLocalRotation().set(rotation);
			interpreter.refresh();
			return true;
		}
		
		public void setRotation(Quaternion rotation) {
			oldRotation.set(interpreter.getLocalRotation());
			
			this.rotation.set(rotation);
		}

		
		public boolean undoAction() {

			interpreter.getLocalRotation().set(oldRotation);
			interpreter.refresh();
			return true;
		}
	};
	

	private class Focus extends FocusInformation
	{
		
		
		public Actionable getControlled() {
			return Structure.this;
		}

		
		public Vector3f getCenterVector() 
		{
			getModel().updateWorldData();
			return getModel().getWorldTranslation();
		}
		
		
		public Model getCenterModel() 
		{
			
			return getModel();
		}
	};
	

	private class OrientationInfo extends OrientationInformation
	{		
	

		
		public Actionable getControlled() {
			return Structure.this;
		}		
	}	
	
	protected class SelectionEffectImpl2 extends SelectionEffect
	{
		Action.Type type;
		
		public void setAction(Action.Type type) {
			this.type = type;
			
		}
		
		
		public boolean doAction() 
		{
			
			if(type == Action.SELECT)
			{
				showSelectEffect(engage);
				return true;
			}
			return false;
		}
		
		
		public Actionable getControlled() {
			
			return Structure.this;
		}

		
		public boolean undoAction() {
			return super.undoAction();
		}
		
	}
	
}
