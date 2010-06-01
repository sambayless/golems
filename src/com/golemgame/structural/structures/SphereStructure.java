package com.golemgame.structural.structures;

import java.util.Collection;

import com.golemgame.model.Model;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.model.spatial.shape.SphereFacade;
import com.golemgame.model.spatial.shape.SphereModel;
import com.golemgame.mvc.PropertyState;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SimplePropertyState;
import com.golemgame.mvc.golems.SpatialInterpreter;
import com.golemgame.mvc.golems.SphereInterpreter;
import com.golemgame.properties.Property;
import com.golemgame.structural.collision.CollisionMember;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.ControlAction;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.control.SphereControlPoint;
import com.golemgame.tool.control.SphereControlPoint.ControllableSphere;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

public class SphereStructure extends PhysicalStructure  {
	private static final long serialVersionUID =1L;
	private NodeModel model;
	public static final int zSamples = 15;
	public static final int radialSamples = 20;
	protected SphereModel sphereModel;
	protected SphereFacade facadeSphere;
	private CollisionMember collisionMember;
	protected ControllableImpl sphereController;
	protected static SphereControlPoint sphereControlPoint = new SphereControlPoint(SphereControlPoint.SPHERE);
	
	protected static SphereControlPoint [] ellipsoidControlPoints = new SphereControlPoint[]{new SphereControlPoint(SphereControlPoint.X)
	,new SphereControlPoint(SphereControlPoint.Y),new SphereControlPoint(SphereControlPoint.Z)};
	

	

	private Model[] controlledModels;
	private Model[] visualModels;
	private SphereInterpreter interpreter;
	

	public SphereStructure(PropertyStore store) {
		super(store);
		this.interpreter = new SphereInterpreter(store);
	
		this.sphereModel =  new SphereModel(true);
		this.model = new NodeModel(this);
		this.getModel().addChild(sphereModel);
		
		facadeSphere = new SphereFacade();
		this.getModel().addChild(facadeSphere);
	
			this.collisionMember = new CollisionMember(model,this.getActionable());
			collisionMember.registerCollidingModel(sphereModel);


		
		sphereModel.setActionable(this);
		controlledModels = new Model[]{sphereModel,facadeSphere};
		visualModels = new Model[]{facadeSphere};
		sphereController = new ControllableImpl(sphereModel, facadeSphere, this);
		super.initialize();
		
	}
	
	
	public void refreshController() {
		sphereController.updateModel();
	
	}
	
	
	@Override
	public void refresh() {
		if(interpreter.isEllipsoid())
		{
			sphereModel.getLocalScale().set(interpreter.getExtent()).multLocal(2f);
			facadeSphere.loadModelData(sphereModel);
		}else
		{
			float r  = interpreter.getRadius();
			sphereModel.getLocalScale().set(r,r,r).multLocal(2f);
			facadeSphere.loadModelData(sphereModel);
		}
	
		
		super.refresh();
	}


	public Model[] getAppearanceModels() {
		return visualModels;
	}
	protected Model[] getControlledModels()
	{
		return controlledModels;
	}
	
	
	protected CollisionMember getStructuralCollisionMember() {
		return collisionMember;
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
	
	
	@Override
	public Collection<Property> getPropertySet() {
		 Collection<Property> properties = super.getPropertySet();
		 if(interpreter.isEllipsoid())
		 {
			 properties.add( new Property(Property.PropertyType.SCALE_ELLIPSOID,this.interpreter.getStore()));
		 }else
			 properties.add( new Property(Property.PropertyType.SCALE_SPHERE,this.interpreter.getStore()));
		 
		return properties;
	}
	
	
	public Action<?> getAction(Type type) throws ActionTypeException {
		if (type == Type.CONTROL)
		{
			return new Control();
		}else
			return super.getAction(type);
	}


	public boolean isEllipsoid()
	{
		return interpreter.isEllipsoid();
	}

	public SphereInterpreter getInterpreter() {
		return interpreter;
	}

	
	protected class Control extends ControlAction
	{

		
		public boolean doAction() 
		{
			SphereControlPoint[] controlPoints;
			if (!isEllipsoid())
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
				collisionMemberStateChange();
			}
			return super.doAction();
		}



		
		public boolean undoAction() {
			SphereControlPoint[] controlPoints;
			if (!isEllipsoid())
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
					collisionMemberStateChange();
			}
			return super.undoAction();
		}
		
		
		public Actionable getControlled() {
		
			return SphereStructure.this;
		}

		
		public void setVisible(boolean visible) {
			SphereControlPoint[] controlPoints;
			if (!isEllipsoid())
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
			if (!isEllipsoid())
				controlPoints = new SphereControlPoint[]{sphereControlPoint};
			else
				controlPoints =  ellipsoidControlPoints;
			return controlPoints;
		}

	}
	

	
	
	
	public static class ControllableImpl implements ControllableSphere
	{
		private static final long serialVersionUID = 1L;
		protected SphereModel sphere;
		protected SphereFacade facade;
		protected SphereStructure owner;
		public ControllableImpl(SphereModel sphere, SphereFacade facade, SphereStructure owner) {
			super();
			this.sphere = sphere;
			this.facade = facade;
			this.owner = owner;
		}
		
		public PropertyStore getPropertyStore() {
			return owner.interpreter.getStore();
		}
		
		
		public void updateModel() {

			owner.interpreter.getExtent().set(getLocalScale()).divideLocal(2f);
			owner.refresh();
			
		}
		
		public PropertyState getCurrentState() {
			return new SimplePropertyState(owner.interpreter.getStore(),new String[]{SphereInterpreter.RADII,SpatialInterpreter.LOCALTRANSLATION,SpatialInterpreter.LOCALSCALE,SpatialInterpreter.LOCALROTATION});
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

		
		public SphereInterpreter getInterpreter() {
			return owner.interpreter;
		}
		
		
		
	}
	

	

	
}
