package com.golemgame.functional;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.golemgame.mechanical.StructuralMachine;
import com.golemgame.mechanical.layers.Layer;
import com.golemgame.model.Model;
import com.golemgame.model.ModelIntersectionData;
import com.golemgame.model.ParentModel;
import com.golemgame.model.effect.ModelEffectPool;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.model.spatial.SpatialModel;
import com.golemgame.model.spatial.SpatialModelImpl;
import com.golemgame.mvc.Reference;
import com.golemgame.mvc.golems.WireInterpreter;
import com.golemgame.states.StateManager;
import com.golemgame.structural.structures.PhysicalStructure;
import com.golemgame.structural.structures.BatteryStruct.BatteryInputModel;
import com.golemgame.tool.ActionToolSettings;
import com.golemgame.tool.SingleAxisInformation;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.ModifyAction;
import com.golemgame.tool.action.MoveAction;
import com.golemgame.tool.action.SelectAction;
import com.golemgame.tool.action.SelectionEffect;
import com.golemgame.tool.action.UndoManager;
import com.golemgame.tool.action.ViewModeAction;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.action.UndoManager.UndoProperties;
import com.golemgame.tool.action.information.FocusInformation;
import com.golemgame.tool.action.information.ModelInformation;
import com.golemgame.tool.action.information.Orientation;
import com.golemgame.tool.action.information.OrientationInformation;
import com.golemgame.tool.action.information.SelectionEffectInformation;
import com.golemgame.tool.action.information.SelectionInformation;
import com.golemgame.tool.action.information.SelectionPriorityInformation;
import com.golemgame.tool.action.mvc.AddWireAction;
import com.golemgame.views.Viewable;
import com.jme.bounding.BoundingBox;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.SceneElement;
import com.jme.scene.SharedMesh;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Capsule;
import com.jme.scene.shape.Sphere;
import com.jme.system.DisplaySystem;

public class WirePort implements Actionable, Viewable,Serializable{
	private static final long serialVersionUID =1;

	protected static TriMesh baseGeom;
	protected static TriMesh baseFacade;
	

	private  NodeModel parentModel = new NodeModel();

	private  boolean input;
	//private Reference reference;
	private PhysicalStructure structuralOwner = null;

	private LocalWireManager wireManager = null;// = LocalWireManager.dummyManager;
	
	
	
	public LocalWireManager getWireManager() {
		return wireManager;
	}

	public void setWireManager(LocalWireManager wireManager) {
		this.wireManager = wireManager;
		
	}

	public PhysicalStructure getStructuralOwner() {
		return structuralOwner;
	}

	/*

	public void setReference(Reference reference) {
		if(reference==null)
			reference = Reference.getNullGUID();
		this.reference = reference;
	}
*/


	public void setStructuralOwner(PhysicalStructure structuralOwner) {
		this.structuralOwner = structuralOwner;

	}



	private boolean disabled = false;
	public boolean isDisabled() {
		return disabled;
	}


	
	/**
	 * A disabled wire port will hide all the wires connected to it, and will not be selectable.
	 * @param disabled
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
		refresh();
		this.getModel().signalMoved();
		refreshView();
	}
	
//	private transient DeletionRecord deletionRecord = null;
//	private final Set<WirePort> connections = new HashSet<WirePort>();
	//private final Map<WirePort,Wire> wires = new HashMap<WirePort,Wire>();
	
	
	public WirePort(PhysicalStructure structuralOwner , boolean input) 
	{
		this (structuralOwner,input,null,null);
	}
	
	public WirePort(PhysicalStructure structuralOwner , boolean input,
			BatteryInputModel batteryInputModel) {
		this (structuralOwner,input,batteryInputModel,null);
	}
	
	public WirePort(PhysicalStructure structuralOwner , boolean input,SpatialModel collisionModel, SpatialModel facadeModel) 
	{
		
		this.input = input;
		this.structuralOwner = structuralOwner;

		if(collisionModel == null)
		{
			collisionModel = new WirePortModel();
			facadeModel = new WirePortFacade();
			
			//keep these as dummy implementations for serialization
			new SpatialModelImpl(true)
			{
				private static final long serialVersionUID = 1L;
	
				
				protected Spatial buildSpatial() {
				//	if (baseGeom == null)
					{
				//		baseGeom =  new Capsule("connect", 5,5,5, 0.1f,0.5f);	
				//		baseGeom.setCullMode(SceneElement.CULL_ALWAYS);
					}
					TriMesh connection = new Capsule("connect", 5,5,5, 0.1f,0.5f);	//new SharedMesh("base", baseGeom);
					connection.setCullMode(SceneElement.CULL_ALWAYS);
					connection.setModelBound(new BoundingBox());
					connection.updateModelBound();
					return connection;
				}

			};
			
			new SpatialModelImpl(false)
			{
				private static final long serialVersionUID = 1L;
				
				protected Spatial buildSpatial() {
					if (baseFacade == null)
					{
						baseFacade =  new Capsule("connect", 10,10,10, 0.1f,0.5f);			
					}
					

					TriMesh facade = new SharedMesh("facade", baseFacade);
					facade.setIsCollidable(false);
					facade.setModelBound(new BoundingBox());
					facade.updateModelBound();
					return facade;
				}
		
			};
		}else
		{
			if (facadeModel == null)
				facadeModel = collisionModel;
		}

		collisionModel.setActionable(this);
		
	
		

		
		
		parentModel.addChild(collisionModel);
		parentModel.addChild(facadeModel);
		

		
	}
	
	

	
	
	
	public boolean addViewMode(Viewable.ViewMode viewMode) {
		if(views.add(viewMode)){
			refreshView();
			return true;
		}
		return false;
	/*	for (Wire wire:this.getWires())
		{
			wire.addViewMode(viewMode);
		}*/
	
	}

	public boolean addViewModes(Collection<Viewable.ViewMode> viewModes) {
		if(views.addAll(viewModes))
		{
			refreshView();
			return true;
		}
		return false;
	}
	
	private Collection<Viewable.ViewMode> views = new HashSet<Viewable.ViewMode>();
	
	public Collection<Viewable.ViewMode> getViews() {
		return views;
	}

	public void refreshView() {
	
		Collection<Wire> wires = this.getWires();
		if(wires !=null){
			for (Wire wire:wires)
			{
				wire.refreshView();
			}
		}
		if(isDisabled())
		{
			this.setSelectable(false);
	
			this.getModel().setVisible(false);
			this.getModel().setCollidable(false);
			return;
		}
		Layer layer = this.structuralOwner.getLayer();
		if (views.contains(Viewable.ViewMode.FUNCTIONAL))
		{
			if(layer.isEditable())
			{
				this.setSelectable(true);
				this.getModel().setCollidable(true);
			}else{
				this.setSelectable(false);
				this.getModel().setCollidable(false);
			}
			
			if(layer.isVisible())
			{
				this.getModel().setVisible(true);
				if(this.isInput())
				{
					ModelEffectPool.getInstance().getWirePortInputEffect().attachModel(this.getModel());
				}else
				{
					ModelEffectPool.getInstance().getWirePortOutputEffect().attachModel(this.getModel());
				}
			}else{
				this.getModel().setVisible(false);
			}
			
			
			
			
		}else
		{
			this.setSelectable(false);
			this.getModel().setVisible(false);
			this.getModel().setCollidable(false);
			
		}
		
		
	}

	public boolean removeViewMode(Viewable.ViewMode viewMode) {
		if(views.remove(viewMode))
		{
			refreshView();
			return true;
		}
		return false;
	/*	for (Wire wire:this.getWires())
		{
			wire.removeViewMode(viewMode);
		}*/
	
	}

	private final static ArrayList<Wire> dummyWires = new ArrayList<Wire>(0);
	
	public Collection<Wire> getWires()
	{
		if(this.getMachine()==null||this.getMachine().getWires()==null)
			return null;
		
		Collection<Wire> wires = this.getMachine().getWires().getWires(this.getReference());
		return wires == null? dummyWires:wires;
		/*if(this.wireManager == null)
			return dummyWires;
		if (this.isInput())
		{
			return this.getMachine().getWires().get
		}else
			return this.wireManager.getWires();*/
	}

	public Model getModel()
	{
		return parentModel;
	}
	public boolean isSelectable()
	{
		return selectable;
	}
	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
		
	}
	
	
	

	public void refreshSelector()
	{
//test
		Vector3f farPoint = new Vector3f();
		this.getModel().updateWorldData();
		farPoint.set(this.getModel().getWorldTranslation());
	
		Vector3f localFarPoint = new Vector3f();
		wireSelector.getModel().updateWorldData();
		wireSelector.getModel().worldToLocal(farPoint,localFarPoint);

		
		WireModel.connectWire(wireSelectorConnection,new Vector3f(wireSelector.getSelectionModel().getLocalTranslation()),localFarPoint);
		
	}
	

	
	public void refresh()
	{

/*		for(Wire wire:this.getWires())
			wire.refresh();*/
		
	
		

			
			for(Wire wire: this.getWires())
			{
				wire.setDisabled(this.isDisabled());
			
			}
			

		
	}
	
	private boolean selectable = true;
	
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
				return new ViewMode();
		
			case MODIFY:
				return new Modify();
			case SELECT_EFFECT_INFO:
				return new SelectionEffectInfo();
			case SELECTION_PRIORITY:
				return new SelectionPriorityInfo();
			case SINGLE_AXIS_INFO:
				return new SingleAxisInfo();
			default:
				throw new ActionTypeException(); // Involved in the control+shift freezing error?
		}
	}
	
	private static final WireSelectionModel wireSelector = new WireSelectionModel();
	private static final WireModel wireSelectorConnection = new WireModel(false);
	
	static
	{
		wireSelector.getModel().addChild(wireSelectorConnection);
	}
	
	private class ViewMode extends ViewModeAction
	{
		
	}
	
	
	protected class ModelInfo extends ModelInformation
	{

		
		public Model getCollisionModel() {
			return wireSelector.getModel();
		}
		
	}
	
	
	
	private class Select extends SelectAction
	{
		private WirePort intersectedWireNode = null;
		private boolean negative = false;
		
		public Actionable getControlled() {
			return WirePort.this;
		}


		private AddWireAction addWire = null;
		public boolean doAction() 
		{
			if (super.isFirstUse())
			{
				if (!super.select)
				{
					this.intersectedWireNode = intersectedNode;
					this.negative = ActionToolSettings.getInstance().getModify().isValue();
					if (intersectedWireNode != null)
					{
						
						try {
							if(WirePort.this.isInput())
							{
	
								addWire = (AddWireAction) intersectedWireNode.getWireManager().getAction(ADD_WIRE);
								
							}else
							{
								addWire = (AddWireAction) getWireManager().getAction(ADD_WIRE);
								
							}
							
						
							
								WireInterpreter wire = new WireInterpreter();
							
								wire.setPortID(WirePort.this.getReference(), WirePort.this.isInput());
								wire.setPortID(intersectedWireNode.getReference(), !WirePort.this.isInput());
								wire.setNegative(negative);
								addWire.setComponent(wire.getStore());
								
								//XXX: EXPERIMENTAL - this may have unintended consequences in other places. Its for wires created 
								//by the move tool, ie by the user, so they can be undone. If this can be called in other ways, it might be
								//a problem.
								addWire.setProperties(UndoProperties.DEPENDENT);
								UndoManager.getInstance().addAction(addWire);
								
						} catch (ActionTypeException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
	
					}
				
				}else
				{
				
				}
					//isNegative = false;
			}
			if (addWire != null)
				return addWire.doAction();
			return false;
		}


		
		public boolean undoAction() 
		{
			if (addWire != null)
				return addWire.undoAction();
			return false;
		}

	};
	private transient WirePort intersectedNode = null;
	private class Move extends MoveAction
	{

		
		public boolean doAction() {
			if(wireSelector.getModel().getParent() == null)
				return false;
			
			wireSelector.getModel().getParent().worldToLocal(position, wireSelector.getModel().getLocalTranslation());
			//wireSelector.getModel().getLocalTranslation().set(position);
			wireSelector.getModel().updateWorldData();
		//	System.out.println(wireSelectorConnection.getWorldScale() + "\t" + wireSelectorConnection.getWorldRotation());
			WirePort collisionIO = null;
			if ((collisionIO = rayIONodeCheck(position)) != null)
			{

				collisionIO.getModel().updateWorldData();//change to position later?
 		        Vector3f pos = collisionIO.getModel().getWorldTranslation();
 		      //  NullIO.this.getParent().worldToLocal(pos, position);
 		        position.set(pos);
 		        
 		      //  NullIO.this.getLocalTranslation().set(position);
 		        
 		        wireSelector.getModel().getParent().updateWorldData();
 		    //  wireSelector.getModel().getLocalTranslation().set(wireSelector.getModel().getParent().worldToLocal(position, new Vector3f()));
 				wireSelector.getModel().getParent().worldToLocal(position, wireSelector.getModel().getLocalTranslation());	
 				wireSelector.getModel().updateWorldData();
 				if (super.isFirstUse())
 					intersectedNode = collisionIO;
			}else
				if (super.isFirstUse())
 					intersectedNode = null;

			refreshSelector();

	    	
			return true;
		}

		
		public boolean undoAction() {
			if (super.isFirstUse())
					intersectedNode = null;
			
			return false;
		}

		
		public void setPosition(Vector3f position) 
		{
			wireSelector.getModel().updateWorldData();
			oldPosition.set(wireSelector.getModel().getWorldTranslation());
			this.position.set(position);
		}
		
	
		
		
		public Actionable getControlled() {
			return WirePort.this;
		}

		
	}

	
	protected class SelectionEffectImpl extends SelectionEffect
	{

		
		public void setAction(Type type) {
			// TODO Auto-generated method stub
			
		}

	

		
		public boolean doAction() {
			if (engage)
			{			
				StateManager.getRootModel().addChild(wireSelector.getModel());
				wireSelector.getModel().loadWorldData(WirePort.this.getModel());
				//display the wire selector wire.
				
				isNegative = ActionToolSettings.getInstance().getModify().isValue();
				if(isNegative)
				{
					ModelEffectPool.getInstance().getWireNegativeEffect().attachModel(wireSelectorConnection);
				}else
				{
					ModelEffectPool.getInstance().getWirePositiveEffect().attachModel(wireSelectorConnection);
				}
				refreshSelector();
			}else
			{
				StateManager.getRootModel().detachChild(wireSelector.getModel());
			}
			return super.doAction();
		}
		
	}
	protected class SelectionInfo extends SelectionInformation
	{

		@Override
		public boolean isMultipleSelectable() {
			return false;
		}
		public boolean isSelectable() {
			return WirePort.this.isSelectable();
		}
		
	}
	private class SelectionEffectInfo extends SelectionEffectInformation
	{

		
		public boolean usesStandardEffects() {
			return false;
		}
		
	}
	
	private transient boolean isNegative = false;


	private class Modify extends ModifyAction
	{

		
		public boolean doAction() {
			if (super.isFirstUse())
			{
				isNegative = super.isModify();
				if(isNegative)
				{
					ModelEffectPool.getInstance().getWireNegativeEffect().attachModel(wireSelectorConnection);
				}else
				{
					ModelEffectPool.getInstance().getWirePositiveEffect().attachModel(wireSelectorConnection);
				}
			}
			return true;
		}
		
	}

	
	private class Focus extends FocusInformation
	{
		
		
		public Actionable getControlled() {
			return WirePort.this;
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
		
		
		
		
		@Override
		public Vector3f getOrigin() {
			WirePort.this.getModel().updateWorldData();
			return WirePort.this.getModel().getWorldTranslation();
		}


		@Override
		public boolean use2DCameraPlane() {
			return true;
		}


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



	};
	
	private class SelectionPriorityInfo extends SelectionPriorityInformation
	{

		
		public int getSelectionPriority() {
			return 128;
		}
		
	}
	
	private class SingleAxisInfo extends SingleAxisInformation
	{

		@Override
		public boolean useSingleAxis() {
			return false;
		}
		
	}
	
	public boolean clearViews() {
		if(views.isEmpty())
			return false;
			
		views.clear();
	/*	for(Wire wire:getWires())
			wire.clearViews();*/
		refreshView();
		return true;
	}
	
	public class FunctionalView extends ViewModeAction
	{

		
		public Actionable getControlled() {
			return WirePort.this;
		}

		
		public boolean doAction() {
			WirePort.this.getMachine().addViewMode(Viewable.FUNCTIONAL);
			
			return true;
			
		}
		
	}

	public StructuralMachine getMachine() {
		return structuralOwner.getMachine();
	}
	
	private static class WireSelectionModel
	{
		private static final long serialVersionUID = 1L;
		protected static TriMesh baseGeom;
		protected static TriMesh baseFacade;
		private static final float radius = 0.15f;
		private ParentModel parentModel;
		private SpatialModel collisionModel;
		
		public WireSelectionModel()
		{
			parentModel = new NodeModel();
			

			
			 collisionModel = new SpatialModelImpl(true)
			 {
				private static final long serialVersionUID = 1L;
				
				
				protected Spatial buildSpatial() {
					if (baseGeom == null)
					{
						baseGeom = new Sphere("ControlSphere", new Vector3f(), 6, 6, radius);
					

						baseGeom.setCullMode(SceneElement.CULL_ALWAYS);
					}
					TriMesh connection = new SharedMesh("base", baseGeom);
					connection.setCullMode(SceneElement.CULL_ALWAYS);
					connection.setModelBound(new BoundingBox());
					connection.updateModelBound();
					return connection;
				}
				private void writeObject(ObjectOutputStream out) throws IOException
				{
					throw new NotSerializableException();
				}
				private void readObject(ObjectInputStream in) throws IOException
				{
					throw new NotSerializableException();
				}
			 };
			
			
			
			SpatialModel facadeModel = new SpatialModelImpl(false)
			{
				private static final long serialVersionUID = 1L;
				
				protected Spatial buildSpatial() {

					if (baseFacade == null)
					{
						baseFacade =  new Sphere("control sphere base", new Vector3f(), 30, 30, radius);
								
					}
					

					TriMesh facade = new SharedMesh("facade", baseFacade);
					facade.setIsCollidable(false);
					facade.setModelBound(new BoundingBox());
					facade.updateModelBound();
					return facade;
				}
				private void writeObject(ObjectOutputStream out) throws IOException
				{
					throw new NotSerializableException();
				}
				private void readObject(ObjectInputStream in) throws IOException
				{
					throw new NotSerializableException();
				}
			};

			parentModel.addChild(collisionModel);
			parentModel.addChild(facadeModel);
			ModelEffectPool.getInstance().getControlPointEffect().attachModel(parentModel);
		}
		
		public ParentModel getModel()
		{
			return parentModel;
		}
		
		public Model getSelectionModel()
		{
			return collisionModel;
		}
		
		public void setActionable(Actionable actionable)
		{
			collisionModel.setActionable(actionable);
		}
	}

	private WirePort rayIONodeCheck(Vector3f position)
	{
		Ray intersectionRay = new Ray();
		Vector3f ray = DisplaySystem.getDisplaySystem().getScreenCoordinates(position);
		DisplaySystem.getDisplaySystem().getPickRay(new Vector2f(ray.getX(),ray.getY()), false, intersectionRay);
		//DisplaySystem.getDisplaySystem().getPickRay(new Vector2f(MouseInput.get().getXAbsolute(),MouseInput.get().getYAbsolute()),  StateManager.IS_AWT_MOUSE, intersectionRay);
		//DisplaySystem.getDisplaySystem().getPickRay(new Vector2f(MouseInput.get().getXAbsolute(),MouseInput.get().getYAbsolute()),  StateManager.IS_AWT_MOUSE, intersectionRay);
		
		//PickResults pickRes = new BoundingPickResults();
		ArrayList<ModelIntersectionData> pickRes = new ArrayList<ModelIntersectionData>();
		getMachine().getModel().intersectRay(intersectionRay, pickRes, false);   


	       for (ModelIntersectionData intersection:pickRes)
	        {
	        	
	    	   if (intersection.getModel().getActionable() instanceof WirePort && intersection.getModel().getActionable()!= this)
	    	   {
	    		  WirePort otherWire = (WirePort) intersection.getModel().getActionable();
	    			 if (testTransfer(this, otherWire))
		     	      {
		        			 return (WirePort) intersection.getModel().getActionable();
		     	      }
	    			 break;
	    	   }
	    
	        }
	       return null;
	}
	

	public static boolean testTransfer(WirePort toTransfer, WirePort toTest)
	{
		if (toTest == null || toTransfer == null)
			return false;

		if (toTest.isInput() == toTransfer.isInput())
			return false;
		
		return true;
	}
	


	public boolean isInput() {
		return input;
	}
	
	
	
/*	public static void removeConnection(WirePort input, WirePort output)
	{
		Wire oldWire = 	input.getMachine().getWires().getWire(input,output);


		
	}*/

	public Collection<Wire> getConnections() {
		return this.getWires();
	}
	




	
	
	
	
	public static class WirePortFacade extends SpatialModelImpl
	{
		private static final long serialVersionUID = 1L;
		
		public WirePortFacade() {
			super();
		}
		public WirePortFacade(boolean registerSpatial) {
			super(registerSpatial);	
		}

		
		
		protected Spatial buildSpatial() {
			if (baseFacade == null)
			{
				baseFacade =  new Capsule("connect", 10,10,10, 0.1f,0.5f);			
			}
			

			TriMesh facade = new SharedMesh("facade", baseFacade);
			facade.setIsCollidable(false);
			facade.setModelBound(new BoundingBox());
			facade.updateModelBound();
			return facade;
		}

	}
	
	
	public static class WirePortModel extends SpatialModelImpl
	{
		private static final long serialVersionUID = 1L;

		public WirePortModel() {
			super(true);
			
		}
		public WirePortModel(boolean registerSpatial) {
			super(registerSpatial);
			
		}
	
		protected Spatial buildSpatial() {
		//	if (baseGeom == null)
			{
		//		baseGeom =  new Capsule("connect", 5,5,5, 0.1f,0.5f);	
		//		baseGeom.setCullMode(SceneElement.CULL_ALWAYS);
			}
			TriMesh connection = new Capsule("connect", 5,5,5, 0.1f,0.5f);	//new SharedMesh("base", baseGeom);
			connection.setCullMode(SceneElement.CULL_ALWAYS);
			connection.setModelBound(new BoundingBox());
			connection.updateModelBound();
			return connection;
		}
		
	}

	



	public Reference getReference() {
		if(wireManager!=null)
			return wireManager.getReference();
		else
			return Reference.getNullReference();
	}
	
	public void remove()
	{
		this.views = null;
		this.intersectedNode = null;
		this.structuralOwner = null;
		this.wireManager = null;
	}
}

