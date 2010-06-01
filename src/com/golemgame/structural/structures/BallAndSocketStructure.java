package com.golemgame.structural.structures;

import java.util.Collection;

import com.golemgame.model.Model;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.model.spatial.shape.CapsuleCombinedFacade;
import com.golemgame.model.spatial.shape.CapsuleCombinedModel;
import com.golemgame.model.spatial.shape.CylinderFacade;
import com.golemgame.model.spatial.shape.CylinderModel;
import com.golemgame.model.spatial.shape.SphereFacade;
import com.golemgame.model.spatial.shape.SphereModel;
import com.golemgame.mvc.PropertyState;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SimplePropertyState;
import com.golemgame.mvc.golems.AxleInterpreter;
import com.golemgame.mvc.golems.BallAndSocketInterpreter;
import com.golemgame.mvc.golems.SpatialInterpreter;
import com.golemgame.properties.Property;
import com.golemgame.structural.collision.CollisionMember;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.ControlAction;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.control.BallSocketControlPoint;
import com.jme.math.Vector3f;

public class BallAndSocketStructure extends PhysicalStructure{
	private static final long serialVersionUID = 1L;

	private static BallSocketControlPoint[] controlPoints = new BallSocketControlPoint[]{new BallSocketControlPoint(BallSocketControlPoint.LENGTH),new BallSocketControlPoint(BallSocketControlPoint.BALL_RADIAL),new BallSocketControlPoint(BallSocketControlPoint.JOINT_RADIAL)};

	private CapsuleCombinedModel leftAxle;
	private SphereModel rightAxle;
	
	private CylinderModel universalLeftAxle;
	private CylinderFacade universalLeftFacade;
	
	private CapsuleCombinedFacade leftFacade;
	private SphereFacade rightFacade;
	
	private NodeModel model;
	private Model[] controlledModels;
	private CollisionMember collisionMember;
	private ControllableImpl controller ;
	
	private Model[] visualModels;
	private BallAndSocketInterpreter interpreter;
	public BallAndSocketStructure(PropertyStore store) {
		super(store);
		this.interpreter = new BallAndSocketInterpreter(store);
		this.model = new NodeModel(this);
	
		leftFacade = new CapsuleCombinedFacade();
		rightFacade = new SphereFacade();
		leftAxle = new  CapsuleCombinedModel(true);		
		rightAxle = new SphereModel(true);
		this.universalLeftAxle = new CylinderModel(true);
		this.universalLeftFacade = new CylinderFacade();
		
		getModel().addChild(rightAxle);			
		getModel().addChild(rightFacade);
		
		if(interpreter.isUniversalJoint())
		{
			getModel().addChild(leftAxle);
			getModel().addChild(leftFacade);
		}else{
			getModel().addChild(universalLeftAxle);
			getModel().addChild(universalLeftFacade);
		}
		
		universalLeftAxle.setActionable(this);
		leftAxle.setActionable(this);
		rightAxle.setActionable(this);
		
		leftFacade.updateWorldData();
		universalLeftFacade.updateWorldData();
		rightFacade.updateWorldData();
		leftAxle.updateWorldData();
		rightAxle.updateWorldData();
		universalLeftAxle.updateWorldData();
		
		controlledModels = new Model[]{leftAxle,rightAxle,leftFacade,rightFacade,universalLeftAxle,universalLeftFacade};
		visualModels = new Model[]{leftFacade,rightFacade,universalLeftFacade};

		this.collisionMember = new CollisionMember(model,this.getActionable());
			collisionMember.registerCollidingModel(leftAxle.getCyl());
			collisionMember.registerCollidingModel(leftAxle.getRight());
			collisionMember.registerCollidingModel(leftAxle.getLeft());
			collisionMember.registerCollidingModel(universalLeftAxle);
			collisionMember.registerCollidingModel(rightAxle);

		controller = new ControllableImpl();
			
		initialize();
	}
	
	
	public void refreshController() {
		controller.refresh();
	}
	

	
	public Model[] getAppearanceModels() {
		return visualModels;
	}

	

	
	protected Model[] getControlledModels() {

		return controlledModels;
	}

	
	protected CollisionMember getStructuralCollisionMember() {
		return collisionMember;
	}

	
	@Override
	public Collection<Property> getPropertySet() {
		 Collection<Property> properties = super.getPropertySet();
		 properties.add(new Property(Property.PropertyType.SCALE_BALL_SOCKET,this.interpreter.getStore()));		

		return properties;
	}


	public NodeModel getModel() {
		return model;
	}


	public boolean isPhysical() {
		return true;
	}


	
	@Override
	public void refresh() {
		
		refreshCapsule();
		
		this.rightAxle.getLocalScale().set(interpreter.getRightRadius()*2f,interpreter.getRightRadius()*2f,interpreter.getRightRadius()*2f);
		
		this.leftAxle.getLocalTranslation().zero();
		this.leftAxle.getLocalTranslation().x = interpreter.getRightRadius() + interpreter.getLeftRadius() + interpreter.getLeftLength()/2f;
		this.universalLeftAxle.getLocalTranslation().zero();
		this.universalLeftAxle.getLocalTranslation().x = interpreter.getRightRadius() +  interpreter.getLeftLength()/2f;

		this.leftFacade.getLocalTranslation().set(this.leftAxle.getLocalTranslation());
		this.rightAxle.getLocalTranslation().zero();
		this.rightFacade.loadModelData(rightAxle);
		this.universalLeftFacade.loadModelData(this.universalLeftAxle);
		super.refresh();
	}
	
	private void refreshCapsule()
	{

	//	Vector3f scale = new Vector3f().set(interpreter.getLeftRadius()*2f,interpreter.getLeftRadius()*2f,interpreter.getLeftLength());
		if(interpreter.isUniversalJoint())
		{
			this.model.addChild(universalLeftAxle);
			this.model.addChild(universalLeftFacade);			
			this.model.detachChild(leftAxle);
			this.model.detachChild(leftFacade);
			
			this.universalLeftAxle.getLocalScale().z =   interpreter.getLeftLength();
			this.universalLeftAxle.getLocalScale().y = interpreter.getLeftRadius()*2f;
			this.universalLeftAxle.getLocalScale().x = interpreter.getLeftRadius()*2f;
		}else{
			this.model.detachChild(universalLeftAxle);
			this.model.detachChild(universalLeftFacade);			
			this.model.addChild(leftAxle);
			this.model.addChild(leftFacade);
			
			if(interpreter.getLeftLength()<0f)
				interpreter.setLeftLength(0f);
		
			//cylController.scale.set(scale);
			
			float radius = interpreter.getLeftRadius();
			
			leftAxle.rebuild(radius, interpreter.getLeftLength()+radius*2f);
			
			leftFacade.rebuild(radius, interpreter.getLeftLength()+radius*2f);
		}
	}
	
	public Action<?> getAction(Type type) throws ActionTypeException {
		if (type == Type.CONTROL)
		{
			return new Control();
		}else
			return super.getAction(type);
	}
	
	
	protected class ControllableImpl implements BallSocketControlPoint.ControllableJoint
	{

		private static final long serialVersionUID = 1L;

		public void refresh() {
			
		}

		public PropertyState getCurrentState() {
			return new SimplePropertyState(interpreter.getStore(),new String[]{AxleInterpreter.LEFT_JOINT_LENGTH,AxleInterpreter.RIGHT_JOINT_LENGTH,AxleInterpreter.LEFT_JOINT_RADIUS,AxleInterpreter.RIGHT_JOINT_RADIUS,SpatialInterpreter.LOCALTRANSLATION,SpatialInterpreter.LOCALSCALE,SpatialInterpreter.LOCALROTATION});

		}
		public void updateWorldData() {
			getParent().updateWorldData();
		}
		public SpatialInterpreter getInterpreter() {
		
			return interpreter;
		}

		public Model getParent() {
			return getModel();
		}

		public PropertyStore getPropertyStore() {
			return interpreter.getStore();
		}
		public Vector3f getRelativeFromWorld(Vector3f worldTranslation,
				Vector3f store) {
			updateModel();
			return getParent().worldToLocal(worldTranslation, store);
		}

		
		public Vector3f getWorldFromRelative(Vector3f localTranslation,
				Vector3f store) {

			updateModel();
			return  getParent().localToWorld(localTranslation, store);
		}
		
		public void updateModel() {
			getModel().updateWorldData();
			leftAxle.updateWorldData();
			universalLeftAxle.updateWorldData();
			rightAxle.updateWorldData();
		}
		
	}

	
	protected class Control extends ControlAction
	{
		
		public boolean doAction() 
		{
		
			for (BallSocketControlPoint control:controlPoints)
			{
			
				control.setPropertyStore(getStore());
				control.enable(controller);
				control.setControlSet(controlPoints);
				control.updatePosition();
			}
			if (resolve)
			{
				refresh();
				//collisionMemberStateChange();
			}
			super.doAction();
			return true;
		}



		
		public boolean undoAction() {
			if (!resolve)
			{
				for (BallSocketControlPoint control:controlPoints)
				{
					control.disable();
				}
			}else
			{	
					//centerModels();
					controller.updateModel();
					collisionMemberStateChange();
			}
			return super.undoAction();
		}
		
		
		public Actionable getControlled() {
		
			return BallAndSocketStructure.this;
		}

		
		public void setVisible(boolean visible) {
			for (BallSocketControlPoint control:controlPoints)
			{
				control.setVisible(visible);
			}
			
		}

		
		public Actionable[] getControlPoints() {
			return controlPoints;
		}

	}
	
	
}
