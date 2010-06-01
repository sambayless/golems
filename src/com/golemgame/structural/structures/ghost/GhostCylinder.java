package com.golemgame.structural.structures.ghost;

import java.util.Collection;

import com.golemgame.model.Model;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.model.spatial.SpatialModel;
import com.golemgame.mvc.PropertyState;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SimplePropertyState;
import com.golemgame.mvc.golems.CylinderInterpreter;
import com.golemgame.mvc.golems.GhostCylinderInterpreter;
import com.golemgame.mvc.golems.SpatialInterpreter;
import com.golemgame.properties.Property;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.ControlAction;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.control.ControlPoint;
import com.golemgame.tool.control.CylinderControlPoint;
import com.golemgame.tool.control.CylinderControlPoint.ControllableCylinder;
import com.golemgame.tool.control.CylinderControlPoint.CylinderPoint;
import com.jme.bounding.BoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.SceneElement;
import com.jme.scene.SharedMesh;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Cylinder;

public class GhostCylinder extends GhostStructure  {
	private static final long serialVersionUID =1L;
	private NodeModel model;

	protected SpatialModel cylinderModel;
	protected Model facade;

	

	private static CylinderControlPoint[] controlPoints = new CylinderControlPoint[]{
		new CylinderControlPoint(CylinderControlPoint.LEFT), new CylinderControlPoint(CylinderControlPoint.RIGHT), new CylinderControlPoint(CylinderControlPoint.RADIUS)		
	};
	

	private Model[] controlledModels;
	private Model[] visualModels;
	private ControllableImpl cylController;
	
	private GhostCylinderInterpreter interpreter;
	public GhostCylinder(PropertyStore store) {
		super(store);
		
		this.interpreter = new GhostCylinderInterpreter(store);
		this.cylinderModel =buildModel();
		this.model = new NodeModel(this);
		this.getModel().addChild(cylinderModel);
		facade = buildFacade();
		this.getModel().addChild(facade);

		cylinderModel.setActionable(this);
		controlledModels = new Model[]{cylinderModel,facade};
		visualModels = new Model[]{facade};
		cylController = buildController(cylinderModel,facade);
		super.initialize();
		
	}
	
	@Override
	public Collection<Property> getPropertySet() {
		 Collection<Property> properties = super.getPropertySet();
		 properties.add( new Property(Property.PropertyType.SCALE_CYLINDER,this.interpreter.getStore()));
		return properties;
	}
	
	public void refreshController() {
		this.cylController.updateModel();
		super.refreshController();
	}

	@Override
	public void refresh() {
		
		cylinderModel.getLocalScale().set(interpreter.getRadius()*2f,interpreter.getRadius()*2f,interpreter.getHeight());
		cylinderModel.updateWorldData();
		facade.loadModelData(cylinderModel);
	
		super.refresh();
	}

	
	public Model[] getAppearanceModels() {
		return visualModels;
	}

	protected ControllableImpl buildController(Model mainModel, Model facade)
	{
		 return new ControllableImpl(cylinderModel, facade, this);
	}
	protected CylinderModel buildModel()
	{
		return  new CylinderModel();
	}
	
	protected Model buildFacade()
	{
		 return new FacadeCylinder();
	}
	
	protected Model[] getControlledModels()
	{
		return controlledModels;
	}
	
	
	
	protected Vector3f getScale() {
		return cylController.getLocalScale();
	}
	
	public boolean isMember(Actionable actionable) {
		for(ControlPoint<?> point:controlPoints)
		{
			if(point.equals(actionable) && this.cylController.equals( point.getControllable()))
				return true;
		}
		return super.isMember(actionable);
	}

	
	protected void setScale(Vector3f scale) {
		cylController.getLocalScale().set(scale);
		cylController.updateModel();
	}
	

	
	public NodeModel getModel() {
		return model;
	}


	
	public Action<?> getAction(Type type) throws ActionTypeException {
		if (type == Type.CONTROL)
		{
			return new Control();
		}else
			return super.getAction(type);
	}

	protected class Control extends ControlAction
	{



		
		public boolean doAction() 
		{
		
			for (CylinderControlPoint control:controlPoints)
			{
			
				control.enable(cylController);
				control.setControlSet(controlPoints);
			}
			if (resolve)
			{
				
				centerModels();
				cylinderModel.updateWorldData();
			}
			super.doAction();
			return true;
		}



		
		public boolean undoAction() {
			if (!resolve)
			{
				for (CylinderControlPoint control:controlPoints)
				{
					control.disable();
				}
			}else
			{	
					centerModels();
					cylinderModel.updateWorldData();
			}
			return super.undoAction();
		}
		
		
		public Actionable getControlled() {
		
			return GhostCylinder.this;
		}

		
		public void setVisible(boolean visible) {
			for (CylinderControlPoint control:controlPoints)
			{
				control.setVisible(visible);
			}
			
		}

		
		public Actionable[] getControlPoints() {
			return controlPoints;
		}

	}
	
	public static class CylinderModel extends SpatialModel
	{
		private static final long serialVersionUID = 1L;
		public static final float INIT_SIZE = 1f;

		public CylinderModel() {
			super();
	
			registerSpatial();
			
		}
		
		
		protected Spatial buildSpatial()
		{
			Spatial spatial= new Cylinder("built", 4,8, INIT_SIZE/2,INIT_SIZE,true);	
			spatial.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.HALF_PI, new Vector3f(1,0,0)));		
			spatial.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, new Vector3f(0,1,0)));
			spatial.setCullMode(SceneElement.CULL_ALWAYS);
			spatial.setModelBound(new BoundingBox());
			spatial.updateModelBound();
			return spatial;
		}
		
		
		public boolean isShareable() {
			return true;
		}

		
		public CylinderModel makeSharedModel() {
			CylinderModel sharedBox = new CylinderModel();
		
			return sharedBox;
		}


	}
	
	public static class FacadeCylinder extends SpatialModel
	{
		private static final long serialVersionUID = 1L;
		public static final float INIT_SIZE = 1f;
		protected static Cylinder baseFacade=null;

		public FacadeCylinder() {
			super();
			this.updateModelData();
		}



		
		protected Spatial buildSpatial() {
			if (baseFacade == null)
			{
				baseFacade = new Cylinder("built", 30,30, INIT_SIZE/2,INIT_SIZE,true);
			}
			
			Spatial spatial = new SharedMesh("Sphere",baseFacade);
			spatial.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.HALF_PI, new Vector3f(1,0,0)));		
			spatial.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, new Vector3f(0,1,0)));

			spatial.setIsCollidable(false);
			spatial.setModelBound(new BoundingBox());
			return spatial;
		}

		
		public boolean isShareable() {
			return true;
		}

		
		public FacadeCylinder makeSharedModel() {
			return new FacadeCylinder();
		}

	}
	
	public static class ControllableImpl implements ControllableCylinder
	{
		private static final long serialVersionUID = 1L;
		private Model mainModel;
		private Model facade;
		private GhostCylinder owner;
		public ControllableImpl(Model sphere, Model facade, GhostCylinder owner) {
			super();
			this.mainModel = sphere;
			this.facade = facade;
			this.owner = owner;
		}
		
		
		public float getValidatedScale(float stretch, CylinderPoint type) {
			return stretch;
		}
		
		
		public PropertyState getCurrentState() {
			return new SimplePropertyState(owner.interpreter.getStore(),new String[]{CylinderInterpreter.CYL_HEIGHT,CylinderInterpreter.CYL_RADIUS,SpatialInterpreter.LOCALTRANSLATION,SpatialInterpreter.LOCALSCALE,SpatialInterpreter.LOCALROTATION});
		}
		public SpatialInterpreter getInterpreter() {
			return owner.interpreter;
		}

		public void updateModel() {
			//	owner.interpreter.getLocalTranslation().set(owner.getModel().getLocalTranslation());
				owner.interpreter.setHeight(mainModel.getLocalScale().z);
				owner.interpreter.setRadius(mainModel.getLocalScale().x/2f);
				//owner.centerModels();
				owner.centerModels();
				//in case there is any doubt about this: this is the WRONG way to do this, but it works and there is no reason to change it right now.
				//however, the (MVC) model should NOT be updated by coping the (MVC) view, in future implementations.
				owner.interpreter.getLocalTranslation().set(owner.getModel().getLocalTranslation());
				owner.refresh();
				
			}
			
		
		public Quaternion getLocalRotation() {
			return mainModel.getLocalRotation();
		}

		public PropertyStore getPropertyStore() {
			return owner.interpreter.getStore();
		}
		
		public Vector3f getLocalScale() {
			return mainModel.getLocalScale();
		}

		
		public Vector3f getLocalTranslation() {
			return mainModel.getLocalTranslation();
		}

		
		public Quaternion getWorldRotation() {
			return mainModel.getWorldRotation();
		}

		
		public Vector3f getWorldTranslation() {
			return mainModel.getWorldTranslation();
		}

		
		public void updateWorldData() {
			 mainModel.updateWorldData();
			 facade.updateWorldData();
		}

		
		public Model getParent() {
			return mainModel.getParent();
		}

		
		public Vector3f getRelativeFromWorld(Vector3f worldTranslation,
				Vector3f store) {
			mainModel.updateWorldData();
			return getParent().worldToLocal(worldTranslation, store);
		}

		
		public Vector3f getWorldFromRelative(Vector3f localTranslation,
				Vector3f store) {

			mainModel.updateWorldData();
			return  getParent().localToWorld(localTranslation, store);
		}
				
	}
}
