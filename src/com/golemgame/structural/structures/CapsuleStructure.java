package com.golemgame.structural.structures;

import java.util.Collection;

import com.golemgame.model.Model;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.model.spatial.shape.CapsuleCombinedFacade;
import com.golemgame.model.spatial.shape.CapsuleCombinedModel;
import com.golemgame.model.texture.TextureTypeKey.TextureShape;
import com.golemgame.mvc.PropertyState;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SimplePropertyState;
import com.golemgame.mvc.golems.CapsuleInterpreter;
import com.golemgame.mvc.golems.SpatialInterpreter;
import com.golemgame.properties.Property;
import com.golemgame.structural.collision.CollisionMember;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.ControlAction;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.control.CapsuleControlPoint;
import com.golemgame.tool.control.CylinderControlPoint.CylinderPoint;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

public class CapsuleStructure extends PhysicalStructure {
	private static final long serialVersionUID = 1L;
	private static final float RADIUS = 0.5f;
	private static final float HEIGHT = 1f;
	private static CapsuleControlPoint[] controlPoints = new CapsuleControlPoint[]{
		new CapsuleControlPoint(CapsuleControlPoint.CapsulePosition.RADIAL_TOP), new CapsuleControlPoint(CapsuleControlPoint.CapsulePosition.LENGTH_LEFT), new CapsuleControlPoint(CapsuleControlPoint.CapsulePosition.LENGTH_RIGHT)		
	};
	
	private NodeModel model;


	private CapsuleCombinedModel capsuleModel;
	private CapsuleCombinedFacade capsuleFacade;

	private CollisionMember collisionMember;

	private Model[] controlledModels;
	private Model[] visualModels;
	private CapsuleControllableImpl cylController;
	private CapsuleInterpreter interpreter;
	public CapsuleStructure(PropertyStore store) {
		super(store);
		this.interpreter = new CapsuleInterpreter(store);
		
		this.model = new NodeModel();

		capsuleFacade = new CapsuleCombinedFacade();
	
		capsuleModel = new  CapsuleCombinedModel(true);
	
		
		
		getModel().addChild(capsuleModel);
	
		getModel().addChild(capsuleFacade);
	
		capsuleModel.setActionable(this);
	
		capsuleFacade.updateWorldData();
	
		capsuleModel.updateWorldData();
	
		controlledModels = new Model[]{capsuleModel,capsuleFacade};
		visualModels = new Model[]{capsuleFacade};

		this.collisionMember = new CollisionMember(model,this.getActionable());
			collisionMember.registerCollidingModel(capsuleModel.getCyl());
			collisionMember.registerCollidingModel(capsuleModel.getRight());
			collisionMember.registerCollidingModel(capsuleModel.getLeft());
			

	
		cylController = new CapsuleControllableImpl(capsuleModel, capsuleFacade,this);
		super.initialize();
		
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
	
	@Override
	protected TextureShape getPrefferedShape() {
		return TextureShape.Capsule;
	}


	public void refreshController() {
	//	this.interpreter.setHeight(this.cylController.scale.z);
	//	this.interpreter.setRadius(this.cylController.scale.x/2f);
	//	this.centerModels();	
	//	interpreter.getLocalTranslation().set(getModel().getLocalTranslation());
	//	refresh();
	}
	
	@Override
	public void refresh() {
	

		Vector3f scale = new Vector3f().set(interpreter.getRadius()*2f,interpreter.getRadius()*2f,interpreter.getHeight());
		
	
		cylController.scale.set(scale);
		
		float radius = interpreter.getRadius();
		
		this.capsuleModel.rebuild(radius,interpreter.getHeight()+radius*2f);
		this.capsuleFacade.rebuild(radius,interpreter.getHeight()+radius*2f);
	
		super.refresh();
	}
	
	public NodeModel getModel() {
		return model;
	}	
	
	
	protected Vector3f getScale() {
		return cylController.getLocalScale();
	}

	@Override
	public Collection<Property> getPropertySet() {
		 Collection<Property> properties = super.getPropertySet();
		 properties.add( new Property(Property.PropertyType.SCALE_CAPSULE,this.interpreter.getStore()));
	     return properties;
	}
	
	protected void setScale(Vector3f scale) {
		cylController.getLocalScale().set(scale);
		cylController.updateModel();
	}
	
	
/*	public PhysicalStructure copy(StructuralMachine destination) {
		CapsuleStructure copy = new CapsuleStructure(getStore().deepCopy());

		copy.cylController.scale.set(this.cylController.scale);//otherwise, scale will be lost on the copy cylinder when its model is updated for, example, refreshing.
		copy.cylinderModel.loadModelData(this.cylinderModel);
		copy.leftCap.loadModelData(this.leftCap);
		copy.rightCap.loadModelData(this.rightCap);
		copy.cylinderFacade.loadModelData(this.cylinderFacade);
		copy.leftCapFacade.loadModelData(this.leftCapFacade);
		copy.rightCapFacade.loadModelData(this.rightCapFacade);
		copy.set(this);
		return copy;
	}*/

	
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
		
			for (CapsuleControlPoint control:controlPoints)
			{
			
				control.enable(cylController);
				control.setControlSet(controlPoints);
			}
			if (resolve)
			{
				
				centerModels();
				capsuleModel.updateWorldData();
				collisionMemberStateChange();
			}
			super.doAction();
			return true;
		}



		
		public boolean undoAction() {
			if (!resolve)
			{
				for (CapsuleControlPoint control:controlPoints)
				{
					control.disable();
				}
			}else
			{	
					centerModels();
					capsuleModel.updateWorldData();
					collisionMemberStateChange();
			}
			super.undoAction();
			return true;
		}
		
		
		public Actionable getControlled() {
		
			return CapsuleStructure.this;
		}

		
		public void setVisible(boolean visible) {
			for (CapsuleControlPoint control:controlPoints)
			{
				control.setVisible(visible);
			}
			
		}

		
		public Actionable[] getControlPoints() {
			return controlPoints;
		}

	}
		

	
	

	

	
	public static class CapsuleControllableImpl implements CapsuleControlPoint.ControllableJoint
	{
		private static final long serialVersionUID = 1L;
	
		private Vector3f scale = new Vector3f(1,1,2);
		private CapsuleStructure owner;
		private CapsuleCombinedModel model;
		private CapsuleCombinedFacade facade;

		public CapsuleControllableImpl( CapsuleCombinedModel model, CapsuleCombinedFacade facade,CapsuleStructure owner) {
			super();
			this.model = model;
			this.facade = facade;
			
			this.owner = owner;
		}


		public PropertyStore getPropertyStore() {
			return owner.interpreter.getStore();
		}
		public CapsuleInterpreter getInterpreter() {
			return owner.interpreter;
		}
		public float getValidatedScale(float stretch, CylinderPoint type) {
			if (type == CylinderPoint.RADIUS)
			{
				//restrict the radius to be <= half the length
				if (stretch> (scale.z/2f) * HEIGHT)
					stretch =(scale.z/2f) * HEIGHT;
			}else
			{
				if (stretch< scale.x*RADIUS*2f)
				{
					stretch = scale.x*RADIUS*2f;
				}
			}
			return stretch;
		}

		
		public void updateModel() {
			
		//	owner.refreshController();
			owner.getModel().updateWorldData();
	//		model.updateWorldData();
		}
		
		public PropertyState getCurrentState() {
			return new SimplePropertyState(owner.interpreter.getStore(),new String[]{CapsuleInterpreter.CYL_HEIGHT,CapsuleInterpreter.CYL_RADIUS,SpatialInterpreter.LOCALTRANSLATION,SpatialInterpreter.LOCALSCALE,SpatialInterpreter.LOCALROTATION});
		}
		
		public Quaternion getLocalRotation() {
			return model.getLocalRotation();
		}

		
		public Vector3f getLocalScale() {
			return scale;
		}

		
		public Vector3f getLocalTranslation() {
			return model.getLocalTranslation();
		}

		
		public Quaternion getWorldRotation() {
			return model.getWorldRotation();
		}

		
		public Vector3f getWorldTranslation() {
			return model.getWorldTranslation();
		}

		
		public void updateWorldData() {
			model.updateWorldData();
			model.getParent().updateWorldData();
		}

		
		public Model getParent() {
			return model.getParent();
		}

		
		public Vector3f getRelativeFromWorld(Vector3f worldTranslation,
				Vector3f store) {
			model.updateWorldData();
			getParent().updateWorldData();
			return getParent().worldToLocal(worldTranslation, store);
		}

		
		public Vector3f getWorldFromRelative(Vector3f localTranslation,
				Vector3f store) {

			model.updateWorldData();
			getParent().updateWorldData();
			return  getParent().localToWorld(localTranslation, store);
		}


		public void refresh() {
			
			getInterpreter().refresh();
			model.updateWorldData();
			
		}
		
	
		
		
	}








	public CapsuleInterpreter getInterpreter() {
	
		return interpreter;
	}




	
}
