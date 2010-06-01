package com.golemgame.structural.structures.ghost;

import java.util.Collection;

import com.golemgame.model.Model;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.model.spatial.SpatialModel;
import com.golemgame.mvc.PropertyState;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SimplePropertyState;
import com.golemgame.mvc.golems.GhostSphereInterpreter;
import com.golemgame.mvc.golems.SpatialInterpreter;
import com.golemgame.mvc.golems.SphereInterpreter;
import com.golemgame.properties.Property;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.ControlAction;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.control.ControlPoint;
import com.golemgame.tool.control.SphereControlPoint;
import com.golemgame.tool.control.SphereControlPoint.ControllableSphere;
import com.jme.bounding.BoundingBox;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.SceneElement;
import com.jme.scene.SharedMesh;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Sphere;

public class GhostSphere extends GhostStructure  {
	private static final long serialVersionUID =1L;
	private NodeModel model;
	public static final int zSamples = 15;
	public static final int radialSamples = 20;
	protected SphereModel sphereModel;
	protected FacadeSphere facadeSphere;

	protected ControllableImpl sphereController;
	protected static SphereControlPoint sphereControlPoint = new SphereControlPoint(SphereControlPoint.SPHERE);
	
	protected static SphereControlPoint [] ellipsoidControlPoints = new SphereControlPoint[]{new SphereControlPoint(SphereControlPoint.X)
	,new SphereControlPoint(SphereControlPoint.Y),new SphereControlPoint(SphereControlPoint.Z)};
	

	private  boolean ellipsoid;

	private Model[] controlledModels;
	private Model[] visualModels;
	

	
	private GhostSphereInterpreter interpreter;
	public GhostSphere(PropertyStore store) {
		super(store);
	
		this.interpreter = new GhostSphereInterpreter(store);
		
		this.ellipsoid = ellipsoid;
		this.sphereModel =  new SphereModel();
		this.model = new NodeModel(this);
		this.getModel().addChild(sphereModel);
		
		facadeSphere = new FacadeSphere();
		this.getModel().addChild(facadeSphere);

		sphereModel.setActionable(this);
		controlledModels = new Model[]{sphereModel,facadeSphere};
		visualModels = new Model[]{facadeSphere};
		sphereController = new ControllableImpl(sphereModel, facadeSphere, this);
		super.initialize();
		
	}
	
	
	@Override
	public Collection<Property> getPropertySet() {
		 Collection<Property> properties = super.getPropertySet();
		 properties.add( new Property(Property.PropertyType.SCALE_SPHERE,this.interpreter.getStore()));
		return properties;
	}
	
	public void refreshController() {
		this.sphereController.updateModel();
		super.refreshController();
	}
	
	
	public Model[] getAppearanceModels() {
		return visualModels;
	}
	protected Model[] getControlledModels()
	{
		return controlledModels;
	}
	
	
	public boolean isMember(Actionable actionable) {
		
		for(ControlPoint<?> point:ellipsoidControlPoints)
		{
			if(point.equals(actionable) && this.sphereController.equals( point.getControllable()))
				return true;
		}
		ControlPoint<?> point = sphereControlPoint;
		{
			if(point.equals(actionable) && this.sphereController.equals( point.getControllable()))
				return true;
		}
		return super.isMember(actionable);
	}
	@Override
	public void refresh() {
	
		sphereModel.getLocalScale().set(interpreter.getExtent()).multLocal(2f);
		facadeSphere.loadModelData(sphereModel);
		super.refresh();
	}

	
	public NodeModel getModel() {
		return model;
	}
	
	protected Vector3f getScale() {
		return sphereController.getLocalScale();
	}


	
	protected void setScale(Vector3f scale) {
		sphereController.getLocalScale().set(scale);
		sphereController.updateModel();
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
			SphereControlPoint[] controlPoints;
			if (!ellipsoid)
				controlPoints = new SphereControlPoint[]{sphereControlPoint};
			else
				controlPoints =  ellipsoidControlPoints;
			
			for (SphereControlPoint control:controlPoints)
			{
			
				control.enable(sphereController);
				control.setControlSet(controlPoints);
			}
			if (resolve)
			{
				
				centerModels();
				sphereModel.updateWorldData();
			
			}
			return super.doAction();
		}



		
		public boolean undoAction() {
			SphereControlPoint[] controlPoints;
			if (!ellipsoid)
				controlPoints = new SphereControlPoint[]{sphereControlPoint};
			else
				controlPoints =  ellipsoidControlPoints;
			if (!resolve)
			{
				for (SphereControlPoint control:controlPoints)
				{
					control.disable();
				}
			}else
			{	
					centerModels();
					sphereModel.updateWorldData();
				
			}
			return super.undoAction();
		}
		
		
		public Actionable getControlled() {
		
			return GhostSphere.this;
		}

		
		public void setVisible(boolean visible) {
			SphereControlPoint[] controlPoints;
			if (!ellipsoid)
				controlPoints = new SphereControlPoint[]{sphereControlPoint};
			else
				controlPoints =  ellipsoidControlPoints;
			for (SphereControlPoint control:controlPoints)
			{
				control.setVisible(visible);
			}
			
		}

		
		public Actionable[] getControlPoints() {
			SphereControlPoint[] controlPoints;
			if (!ellipsoid)
				controlPoints = new SphereControlPoint[]{sphereControlPoint};
			else
				controlPoints =  ellipsoidControlPoints;
			return controlPoints;
		}

	}
	
	public static class SphereModel extends SpatialModel 
	{
		private static final long serialVersionUID = 1L;
		public static final float INIT_SIZE = 1f;

		public SphereModel() {
			super();
			registerSpatial();
			
		}
		
		
		protected Spatial buildSpatial()
		{
			Spatial spatial = new Sphere("sphere", new Vector3f(), 8, 8, INIT_SIZE/2);		
			spatial.setCullMode(SceneElement.CULL_ALWAYS);
			spatial.setModelBound(new BoundingBox());
			spatial.updateModelBound();
			return spatial;
		}
		
		
		public boolean isShareable() {
			return true;
		}

		
		public SphereModel makeSharedModel() {
			SphereModel sharedBox = new SphereModel();
		
			return sharedBox;
		}	
	}
	
	public static class FacadeSphere extends SpatialModel
	{
		private static final long serialVersionUID = 1L;
		public static final float INIT_SIZE = 1f;
		protected static Sphere baseFacade=null;
	
		public FacadeSphere() {
			super();
			this.updateModelData();
		}

		
		protected Spatial buildSpatial() {
			if (baseFacade == null)
			{//create the static sphere mesh
				baseFacade  = new Sphere("sphere", new Vector3f(), 30, 30, INIT_SIZE/2);	
			}
			
			Spatial spatial = new SharedMesh("Sphere",baseFacade);
			spatial.setIsCollidable(false);
			spatial.setModelBound(new BoundingBox());
			return spatial;
		}

		public boolean isShareable() {
			return true;
		}

		
		public FacadeSphere makeSharedModel() {
			return new FacadeSphere();
		}

	}
	
	
	
	public static class ControllableImpl implements ControllableSphere
	{
		private static final long serialVersionUID = 1L;
		protected SphereModel sphere;
		protected FacadeSphere facade;
		protected GhostSphere owner;
		public ControllableImpl(SphereModel sphere, FacadeSphere facade, GhostSphere owner) {
			super();
			this.sphere = sphere;
			this.facade = facade;
			this.owner = owner;
		}
		public PropertyStore getPropertyStore() {
			return owner.interpreter.getStore();
		}
		
		public PropertyState getCurrentState() {
			return new SimplePropertyState(owner.interpreter.getStore(),new String[]{SphereInterpreter.RADII,SpatialInterpreter.LOCALTRANSLATION,SpatialInterpreter.LOCALSCALE,SpatialInterpreter.LOCALROTATION});
		}
		
		public SpatialInterpreter getInterpreter() {
			return owner.interpreter;
		}


		
		
		public void updateModel() {

			owner.interpreter.getExtent().set(getLocalScale()).divideLocal(2f);
			owner.refresh();
			
		}
		public Quaternion getLocalRotation() {
			return sphere.getLocalRotation();
		}

		
		public Vector3f getLocalScale() {
			return sphere.getLocalScale();
		}

		
		public Vector3f getLocalTranslation() {
			return sphere.getLocalTranslation();
		}

		
		public Quaternion getWorldRotation() {
			return sphere.getWorldRotation();
		}

		
		public Vector3f getWorldTranslation() {
			return sphere.getWorldTranslation();
		}

		
		public void updateWorldData() {
			 sphere.updateWorldData();
			 facade.updateWorldData();
		}

		
		public Model getParent() {
			return sphere.getParent();
		}

		
		public Vector3f getRelativeFromWorld(Vector3f worldTranslation,
				Vector3f store) {
			sphere.updateWorldData();
			return getParent().worldToLocal(worldTranslation, store);
		}

		
		public Vector3f getWorldFromRelative(Vector3f localTranslation,
				Vector3f store) {

			sphere.updateWorldData();
			return  getParent().localToWorld(localTranslation, store);
		}
				
		
	}
}
