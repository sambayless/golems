package com.golemgame.functional;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

import com.golemgame.mechanical.layers.Layer;
import com.golemgame.model.Model;
import com.golemgame.model.effect.ModelEffectPool;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.Reference;
import com.golemgame.mvc.SustainedView;
import com.golemgame.mvc.golems.WireInterpreter;
import com.golemgame.structural.structures.PhysicalStructure;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.DeleteAction;
import com.golemgame.tool.action.MoveAction;
import com.golemgame.tool.action.SelectAction;
import com.golemgame.tool.action.SelectionEffect;
import com.golemgame.tool.action.ViewModeAction;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.action.information.FocusInformation;
import com.golemgame.tool.action.information.ModelInformation;
import com.golemgame.tool.action.information.Orientation;
import com.golemgame.tool.action.information.OrientationInformation;
import com.golemgame.tool.action.information.SelectionEffectInformation;
import com.golemgame.tool.action.information.SelectionInformation;
import com.golemgame.tool.action.information.SelectionPriorityInformation;
import com.golemgame.tool.action.mvc.RemoveWireAction;
import com.golemgame.views.Viewable;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;

public class Wire implements Actionable, Viewable,Serializable,SustainedView {
	private static final long serialVersionUID = 1L;
	private Wires wires;

	
	public void setWires(Wires wires)
	{
		this.wires = wires;
		
	}
	public Wires getWires() {
		return wires;
	}

	private boolean selectable;



//	private LocalWireManager manager;
	
	private WireModel wire;

	private boolean disabled = false;
	
	
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	public LocalWireManager getManager() {
		Reference ref = this.getReference(false);
		if (ref !=null && getWires()!=null)
		{
			WirePort w = getWires().getWirePort(ref);
			if(w!=null)
			{
				return w.getWireManager();
			}
			
		}
		return null;
	
	//	return manager;
	}
	public boolean isNegative() {
		return interpreter.isNegative();
	}
	
	public void attachToManager(LocalWireManager manager)
	{
		//this.manager = manager;
	}
	
	public Wire(PropertyStore store)
	{
		this(new WireInterpreter(store));
	}
	
	private WireInterpreter interpreter;
	
	public Wire(WireInterpreter interpreter)
	{
		this.interpreter = interpreter;	
		wire = new WireModel(true);
		wire.setActionable(this);

		
		this.interpreter.getStore().setSustainedView(this);
	}
	
	public Reference getReference(boolean input)
	{
		return  interpreter.getPortID(input);
	}
	
/*	public Wire(StructuralMachine machine, WirePort port1, WirePort port2, boolean negative) {
		super();
		
		
		this.input = port1.isInput()?port1:port2;
		this.output = port2.isInput()?port1:port2;;
		
		
		wire = new WireModel(true);
		wire.setActionable(this);
		wireListener = new WireModelListener();
		
		getWires().addWire(this);
		refresh();
		this.setNegative(negative);
		this.setSelectable(true);

		

	
	}*/
	
	private static final Vector3f negY = new Vector3f(0,-1f,0);
	public void refreshPosition(Vector3f position1, Vector3f position2)
	{
		
		//Vector3f position1 = input.getModel().getWorldTranslation();
		//Vector3f position2 = output.getModel().getWorldTranslation();
		//position1 = this.getWires().getModel().worldToLocal(position1, new Vector3f());
		//position2 = this.getWires().getModel().worldToLocal(position2, new Vector3f());
		
		Vector3f middle = position1.add(position2).divideLocal(2f);

		
	
		wire.getLocalScale().setZ(position1.distance(position2));
		
		//this is the direction along the z axis
		Vector3f direction = position2.subtract(position1).normalizeLocal();
		Vector3f cross ;
		if(direction.distanceSquared(Vector3f.UNIT_Y)>FastMath.FLT_EPSILON && direction.distanceSquared(negY) > FastMath.FLT_EPSILON)
		{
			cross= direction.cross(Vector3f.UNIT_Y).normalizeLocal();
		}
		else
		{
			cross= direction.cross(Vector3f.UNIT_Z).normalizeLocal();
		}
		wire.getLocalRotation().fromAxes(cross, direction.cross(cross).normalizeLocal(),direction);
		
		wire.setLocalTranslation(middle);
		
		wire.updateWorldData();
	}
	
	public void remove() {
		this.getModel().detachFromParent();
	}
	
	public void refresh()
	{
		this.getStore().setSustainedView(this);
		
		
		if (isNegative())
		{
			ModelEffectPool.getInstance().getWireNegativeEffect().attachModel(wire);
		}else
			ModelEffectPool.getInstance().getWirePositiveEffect().attachModel(wire);

		
	}
	
	public boolean isDisabled()
	{ 
		
		return disabled;//this.getWires().getWirePort(this.getReference(true)).isDisabled() || this.getWires().getWirePort(this.getReference(false)).isDisabled();
	}
	

/*	
	protected void refresh(Vector3f farPoint)
	{
		if(isDisabled())
		{
			this.setSelectable(false);
			this.getModel().setVisible(false);
			return;
		}
		
		Vector3f nearPoint = new Vector3f();
		
		input.getModel().getParent().worldToLocal(farPoint, farPoint);
		wire.getLocalScale().z = (farPoint.length());
		
		nearPoint.set(input.getModel().getLocalTranslation());
		

		Vector3f center = nearPoint.add( farPoint).divide(2f);//center in local coords
	
		
		wire.getLocalTranslation().set(center);
		
		Vector3f dir = farPoint.subtract(nearPoint).normalize();
		
		wire.getLocalRotation().fromAxes(dir.cross(farPoint).cross(dir).normalize(),dir.cross(farPoint).normalize(), dir);
		
		
		wire.updateWorldData();
	}*/
	

	public Model getModel()
	{
		return wire;
	}
	
	public Action<?> getAction(Type type) throws ActionTypeException {
		switch (type)
		{
		
			case SELECT:
				return new Select();
			case MOVE:
				return new Move();
			case SELECTIONEFFECT:
				return new SelectionEffectImpl();
			case FOCUS:
				return new Focus();
			case ORIENTATION:
				return new OrientInfo();
			case MODEL:
				return new ModelInfo();
			case SELECTINFO:
				return new SelectionInfo();
			case VIEWMODE:
				return new ViewModeImpl();
	
			case DELETE:
				return new Delete(this);
			case SELECT_EFFECT_INFO:
				return new SelectionEffectInfo();
			case SELECTION_PRIORITY:
				return new SelectionPriorityInfo();
			default:
				throw new ActionTypeException();
		}
	}
	
	
	public boolean isSelectable()
	{
		return selectable;
	}
	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}

	private Collection<ViewMode> views = new HashSet<ViewMode>();
	
	public boolean addViewMode(ViewMode viewMode) {

		if(views.add(viewMode))
		{
			refreshView();
			return true;
		}
		return false;
	}
	
	


	public Collection<ViewMode> getViews() {
		return views;
	}
	public void refreshView() {
		
	
		if(isDisabled())
		{
			this.setSelectable(false);
			this.getModel().setVisible(false);
			this.getModel().setCollidable(false);
			return;
		}
		Layer layer = null;
		LocalWireManager manager = getManager();
		if(manager!=null)
		{
			PhysicalStructure s =manager.getStructure();
			if(s!=null)
				layer = s.getLayer();
		}
		
		if (views.contains(ViewMode.GROUPMODE))
		{
				//wires can't be grouped right now.. make this invisible
				this.setSelectable(false);
				this.getModel().setVisible(false);
				this.getModel().setCollidable(false);
		}else if(views.contains(ViewMode.FUNCTIONAL))//group mode takes precedence over functional view.
		{
			if(layer!=null &&  layer.isVisible())
			{
				this.setSelectable(true);
				
				//if BOTH ends of this wire are ALSO visible... otherwise if only one is... otherwise...
				boolean inputVisible = false ;
				boolean outputVisible = false;
				if(this.wires!=null)
				{
					WirePort in = this.wires.getWirePort(this.getReference(true));
					WirePort out = this.wires.getWirePort(this.getReference(false));
					
					if(in!=null)
						inputVisible =  in.getModel().isVisible();
					if(out!=null)
						outputVisible = out.getModel().isVisible();
					if(outputVisible && inputVisible)
						this.getModel().setVisible(true);
					else
						this.getModel().setVisible(false);
				/*	this.getModel().setVisible(true);
					if(outputVisible && inputVisible)
						this.wire.setWireType(WireModel.WireType.FULL);
					else if(outputVisible)
						this.wire.setWireType(WireModel.WireType.INPUT);//this just doesnt seem to work reliably...
					else if(inputVisible)
						this.wire.setWireType(WireModel.WireType.OUTPUT);
					else
						this.getModel().setVisible(false);*/
				}
			
				
				this.getModel().setCollidable(true);
				if (isNegative())
				{
					ModelEffectPool.getInstance().getWireNegativeEffect().attachModel(wire);
				}else
					ModelEffectPool.getInstance().getWirePositiveEffect().attachModel(wire);
			}else
			{
				this.setSelectable(false);
				this.getModel().setVisible(false);
				this.getModel().setCollidable(false);
			}
			
		}else
		{
			this.getModel().setCollidable(false);
			this.setSelectable(false);
			this.getModel().setVisible(false);
		}
		
	}
	public boolean removeViewMode(ViewMode viewMode) {
		if (views.remove(viewMode)){
			refreshView();
			return true;
		}
		return false;
	}

	public boolean addViewModes(Collection<Viewable.ViewMode> viewModes) {
		if(views.addAll(viewModes))
		{
			refreshView();
			return true;
		}
		return false;
	}
	
	public boolean clearViews() {
		if (views.isEmpty())
			return false;
		
		views.clear();
		refreshView();
		return true;
	}



	private class ViewModeImpl extends ViewModeAction
	{
		
	}
	
	
	protected class ModelInfo extends ModelInformation
	{

		
		public Model getCollisionModel() {
			
			return Wire.this.getModel();
		}
		
	}
	

	protected static class Delete extends DeleteAction
	{
		private  Wire temporaryWireReference;
		
		
		public Delete(Wire temporaryWireReference) {
			super();
			this.temporaryWireReference = temporaryWireReference;
		}


		private RemoveWireAction remove;
		public boolean doAction() {
			if(isFirstUse())
			{
				try {
					LocalWireManager manager = temporaryWireReference.getManager();
					//it seems that sometimes we get orphaned wires... dont know what causes this, but check for it
					if(manager !=null){
						remove = (RemoveWireAction) manager.getAction(REMOVE_WIRE);
					 	remove.setComponent(temporaryWireReference.getStore());
					 	temporaryWireReference = null;
					}
				 
			
				} catch (ActionTypeException e) {
					return false;
				}
				setFirstUse(false);
			}
			if(remove!=null)
				remove.doAction();
			return true;
		}

		
		public boolean undoAction() {
			if(remove!=null)
				return remove.undoAction();
			return false;
		}
		
	}
	
	private class Select extends SelectAction
	{
		
		public Actionable getControlled() {
			return Wire.this;
		}


		
		public boolean doAction() 
		{

			return true;
		}


		
		public boolean undoAction() 
		{

			return false;
		}

	};

	private class Move extends MoveAction
	{

		
		public boolean doAction() {

			return true;
		}

		
		public boolean undoAction() {

			return false;
		}

		
		public void setPosition(Vector3f position) 
		{

		}
		
	
		
		
		public Actionable getControlled() {
			return Wire.this;
		}

		
	}

	
	protected class SelectionEffectImpl extends SelectionEffect
	{

		
		public void setAction(Type type) {
			// TODO Auto-generated method stub
			
		}

	

		
		public boolean doAction() 
		{
			if (super.isFirstUse())
			{
				if (super.isEngage())
				{
					ModelEffectPool.getInstance().getWireSelectedEffect().attachModel(wire);
				}else
				{
					if (isNegative())
					{
						ModelEffectPool.getInstance().getWireNegativeEffect().attachModel(wire);
					}else
						ModelEffectPool.getInstance().getWirePositiveEffect().attachModel(wire);
				}
			}
			return true;
		}
		
	}
	protected class SelectionInfo extends SelectionInformation
	{

		
		@Override
		public boolean isMultipleSelectable() {
			return false;
		}

		public boolean isSelectable() {
			
			Layer layer = null;
			LocalWireManager manager = getManager();
			if(manager!=null)
			{
				PhysicalStructure s =manager.getStructure();
				if(s!=null)
					layer = s.getLayer();
			}
			
			return Wire.this.isSelectable() &&  (layer==null?true:layer.isEditable());
		
		}
		
	}
	
	


	
	private class Focus extends FocusInformation
	{
		
		
		public Actionable getControlled() {
			return Wire.this;
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
		
	}


	private class  OrientInfo extends OrientationInformation
	{
		
	
		
		
		public boolean updateOrientation() {
			orientation = OrientationInformation.CAMERA;
			return true;//Camera is always assumed to have changed
		}

		
		public boolean updateOrientation(Orientation orientation) {
			orientation = OrientationInformation.CAMERA;
			return true;
		}

		
		public boolean updateOrientation(Vector3f direction, Vector3f horizontal) {
			orientation = OrientationInformation.CAMERA;
			return true;
		}


	}
	private class SelectionEffectInfo extends SelectionEffectInformation
	{

		
		public boolean usesStandardEffects() {
			return false;
		}
		
	}
	
	private class SelectionPriorityInfo extends SelectionPriorityInformation
	{

		
		public int getSelectionPriority() {
			return 64;
		}
		
	}
	

	/*
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Wire other = (Wire) obj;
		if (input == null) {
			if (other.input != null)
				return false;
		} else if (!input.equals(other.input))
			return false;
		if (output == null) {
			if (other.output != null)
				return false;
		} else if (!output.equals(other.output))
			return false;
		return true;
	}
	*/
	public void setNegative(boolean negative) {
		this.interpreter.setNegative(negative);
		refresh();
	};
	
	
	
	
	
	public PropertyStore getStore() {
		return interpreter.getStore();
	}
	public void invertView(PropertyStore store) {
		store.set(interpreter.getStore());
		
	}

//	private final static WirePort dummyPort = new WirePort( null, false);


	public boolean isInput(Reference reference) {
		return interpreter.getPortID(true).equals(reference);
	}



	
}
