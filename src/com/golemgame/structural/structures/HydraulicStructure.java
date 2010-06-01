package com.golemgame.structural.structures;

import java.util.Collection;

import com.golemgame.functional.WirePort;
import com.golemgame.model.Model;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.model.spatial.SpatialModel;
import com.golemgame.model.spatial.shape.CylinderFacade;
import com.golemgame.model.spatial.shape.CylinderModel;
import com.golemgame.mvc.PropertyState;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SimplePropertyState;
import com.golemgame.mvc.golems.HydraulicInterpreter;
import com.golemgame.mvc.golems.SpatialInterpreter;
import com.golemgame.properties.Property;
import com.golemgame.structural.Physical;
import com.golemgame.structural.collision.CollisionMember;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.ControlAction;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.control.HydraulicControlPoint;
import com.golemgame.tool.control.HydraulicControlPoint.ControllableHydraulic;
import com.jme.bounding.BoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Cylinder;


public class HydraulicStructure extends PhysicalStructure {
	private static final long serialVersionUID = 1L;
	
	private static HydraulicControlPoint[] leftControlPoints = new HydraulicControlPoint[]{ new HydraulicControlPoint(HydraulicControlPoint.SLIDE,true),new HydraulicControlPoint(HydraulicControlPoint.SCALE,true)};
	private static HydraulicControlPoint[] rightControlPoints = new HydraulicControlPoint[]{ new HydraulicControlPoint(HydraulicControlPoint.SLIDE,false),new HydraulicControlPoint(HydraulicControlPoint.SCALE,false)};
	private static HydraulicControlPoint[] controlPoints = new HydraulicControlPoint[]{leftControlPoints[0],leftControlPoints[1],rightControlPoints[0],rightControlPoints[1]};


	
	private CollidableJoint leftAxle;
	private CollidableJoint rightAxle;
	
	private CylinderFacade leftFacade;
	private CylinderFacade rightFacade;
	private NodeModel model;
	private Model[] controlledModels;
	private CollisionMember collisionMember;
	private ControllableImpl controller ;
	

	private Model[] visualModels;
	private SpatialModel connectionModel;
	private SpatialModel connectionFacade;
	
	private MotorPropertiesImpl motorProperties;
	
	private WirePort output;
	private WirePort input;

	private HydraulicInterpreter interpreter;
	public HydraulicStructure(HydraulicInterpreter interpreter) {
		super(interpreter);
		this.interpreter= interpreter;
		
		this.model = new NodeModel(this);

		leftFacade = new CylinderFacade();
		rightFacade = new CylinderFacade();
		leftAxle = new CollidableJoint(leftFacade);
		rightAxle = new CollidableJoint(rightFacade);

		leftAxle.getLocalTranslation().x = -0.625f;
		rightAxle.getLocalTranslation().x = 0.625f;
		leftFacade.getLocalTranslation().x = -0.625f;
		rightFacade.getLocalTranslation().x = 0.625f;
		
		this.getModel().addChild(leftAxle);
		this.getModel().addChild(rightAxle);
		this.getModel().addChild(leftFacade);
		this.getModel().addChild(rightFacade);
		
		leftAxle.setActionable(this);
		rightAxle.setActionable(this);
		
		leftFacade.updateWorldData();
		rightFacade.updateWorldData();
		leftAxle.updateWorldData();
		rightAxle.updateWorldData();
		
		
		connectionModel = new CylinderModel(true);
		connectionFacade = new CylinderFacade();		
		
		connectionModel.getLocalScale().set(0.2f,0.2f,1f);
		connectionFacade.loadModelData(connectionModel);
		
		this.getModel().addChild(connectionModel);
		this.getModel().addChild(connectionFacade);
		connectionModel.setActionable(this);
		connectionModel.updateWorldData();
		connectionFacade.updateWorldData();
		
		input = new WirePort(this,true);
		this.getModel().addChild(input.getModel());
		input.getModel().getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Z);
		
		output = new WirePort(this,false);
		this.getModel().addChild(output.getModel());
		
		controlledModels = new Model[]{leftAxle,rightAxle,leftFacade,rightFacade,connectionModel,connectionFacade};
		visualModels = new Model[]{leftFacade,rightFacade,connectionFacade};
	
			this.collisionMember = new CollisionMember(model,this.getActionable());
			collisionMember.registerCollidingModel(leftAxle);
			collisionMember.registerCollidingModel(rightAxle);
		//	collisionMember.registerCollidingModel(connectionModel);
					
		
		super.registerWirePort(input);
		super.registerWirePort(output);
		controller = new ControllableImpl(leftAxle, leftFacade, rightAxle, rightFacade,connectionModel, connectionFacade,output,input);
		super.initialize();

		

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

	
	public NodeModel getModel() {
		return model;
	}

/*	
	public Physical[] getPhysical() {
		return new Physical[]{this,leftAxle,rightAxle};
	}*/

	
	public boolean isPhysical() {
		return true;
	}


	
	public boolean isMindful() {

		return true;
	}
	/**
	 * There are two spatial models to each axle, and two axles in each motor: one component is displayed
	 * and the other component is only for trimesh collisions.
	 * @author Sam
	 *
	 */
	public  class CollidableJoint extends SpatialModel implements Physical
	{
		private static final long serialVersionUID = 1L;
		public static final float INIT_SIZE = 1f;
		

		private Model facade;
		
	//	private MaterialWrapper materialWrapper = new MaterialWrapper();
		private boolean isStatic = false;
		public CollidableJoint(Model facade) {
			super();
			this.facade = facade;

			
			this.updateModelData();
			this.updateWorldData();
			registerSpatial();
		}

		
		public boolean isPropagating() {
			return true;
		}




		
		protected Spatial buildSpatial() {
			Spatial spatial= new Cylinder("built", 4,8, INIT_SIZE/2,INIT_SIZE,true);	
			spatial.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.HALF_PI, new Vector3f(1,0,0)));		
			spatial.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, new Vector3f(0,1,0)));

			spatial.setModelBound(new BoundingBox());
			spatial.setCullMode(SceneElement.CULL_ALWAYS);
			spatial.updateModelBound();
			return spatial;
		}







		
	
		
		public boolean isStatic() {
			
			return isStatic;
		}



		
		public void setStatic(boolean isStatic) {
			this.isStatic= isStatic;
			
		}

		public Model getFacade() {
			return facade;
		}
		
		

		
		public Object getContainingInstance() {
			return HydraulicStructure.this;
		}
		
		
		
	}
	
	public float getMaximumDistance() {
		return interpreter.getMaxJointDistance();
	}

	public void setMaximumDistance(float maximumDistance) {
		
		if(maximumDistance<getMinimumDistance())
			maximumDistance = getMinimumDistance();
		
		interpreter.setMaxJointDistance(maximumDistance);
		
		float amount = leftAxle.getLocalTranslation().dot(new Vector3f(1,0,0));//-leftAxle.getLocalScale().z/2f;
		this.controller.slideEnd(leftAxle, -amount);
		this.controller.refresh();
		
	}

/*	
	protected void set(PhysicalStructure from) {
		super.set(from);
		if(from instanceof HydraulicStructure)
		{
			HydraulicStructure h =	(HydraulicStructure)from;
			this.setMinimumDistance(h.getMinimumDistance());
			this.setMaximumDistance(h.getMaximumDistance());
			
		}
	}*/
	public float getMinimumDistance() {
		return interpreter.getMinJointDistance();
	}

	public void setMinimumDistance(float minimumDistance) {
	
		if(minimumDistance <  0.01f)
			minimumDistance = 0.01f;
		
		interpreter.setMinJointDistance(minimumDistance);
		
		float amount = leftAxle.getLocalTranslation().dot(new Vector3f(1,0,0));
		this.controller.slideEnd(leftAxle, -amount);
		this.controller.refresh();
		
	}
	
	public void checkBounds()
	{
		//TODO:
	}
	


	@Override
	public void refresh() {
		super.refresh();
		if(this.motorProperties == null || ! this.motorProperties.getStore().equals(interpreter.getMotorPropertiesStore()))
		{
			motorProperties = new MotorPropertiesImpl(interpreter.getMotorPropertiesStore());
			motorProperties.refresh();
		}
		
		
		float distance = interpreter.getJointDistance();
		
		if (distance<interpreter.getMinJointDistance())
		{
			interpreter.setJointDistance(interpreter.getMinJointDistance());
			 distance = interpreter.getJointDistance();
		}
		
		
		float radius = interpreter.getJointRadius();
		
		leftAxle.getLocalTranslation().x = -distance/2f - radius;
		rightAxle.getLocalTranslation().x = distance/2f + radius;
		
		leftAxle.getLocalScale().x = radius*2f;
		leftAxle.getLocalScale().y = radius*2f;
		leftAxle.getLocalScale().z = radius*2f;
		rightAxle.getLocalScale().x = radius*2f;
		rightAxle.getLocalScale().y = radius*2f;
		rightAxle.getLocalScale().z = radius*2f;
		
		
		connectionModel.getLocalScale().setX(radius*.2f*2f);
		connectionModel.getLocalScale().setY(radius*.2f*2f);
		
		//float distance = rightAxle.getLocalTranslation().distance(leftAxle.getLocalTranslation());
		connectionModel.getLocalScale().setZ(distance);
		
		connectionModel.getLocalTranslation().set(leftAxle.getLocalTranslation()).addLocal(rightAxle.getLocalTranslation()).divideLocal(2f);
		
		connectionFacade.loadModelData(connectionModel);
		
		output.getModel().getLocalTranslation().set(leftAxle.getLocalTranslation());
		output.getModel().getLocalTranslation().y = leftAxle.getLocalScale().y/2f;			
		output.refresh();
		
			
		input.getModel().getLocalTranslation().set(leftAxle.getLocalTranslation()).addLocal(-leftAxle.getLocalScale().z/2f,0,0);
		input.refresh();
		
		leftFacade.loadModelData(leftAxle);
		rightFacade.loadModelData(rightAxle);
		leftAxle.updateWorldData();
		rightAxle.updateWorldData();
		
/*		this.output.setReference(interpreter.getOutput());
		this.input.setReference(interpreter.getInput());*/
	
	}
	
	public class ControllableImpl implements ControllableHydraulic
	{
		private static final long serialVersionUID = 1L;


		public PropertyStore getPropertyStore() {
			return interpreter.getStore();
		}
		
		
		public ControllableImpl(Model leftAxle, Model leftFacade,
				Model rightAxle, Model rightFacade,Model connectionModel,Model connectionFacade, WirePort wirePortModel ,WirePort secondPortModel) {
			super();
	
		
		}

		public PropertyState getCurrentState() {
			return new SimplePropertyState(interpreter.getStore(),new String[]{HydraulicInterpreter.JOINT_DISTANCE,HydraulicInterpreter.JOINT_RADIUS,SpatialInterpreter.LOCALTRANSLATION,SpatialInterpreter.LOCALSCALE,SpatialInterpreter.LOCALROTATION});
		}
		public void updateWorldData() {
			getParent().updateWorldData();
		}
		
		public Model getParent() {
			return getModel();
		}
		
		public HydraulicInterpreter getInterpreter() {
			return interpreter;
		}

		public Vector3f getNaturalScale(boolean forLeft) {
			return new Vector3f(1,1,1);
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
			leftFacade.updateWorldData();
			rightAxle.updateWorldData();
			rightFacade.updateWorldData();
		}

		
		public Model getLeftJoint() {
			return leftAxle;
		}

		
		public Vector3f getNaturalTranslation(boolean forLeft) {
			Vector3f store = new Vector3f();
			if (forLeft)
			{
				return store.set(0.6f, 0, 0); 
			}else
			{
				return store.set(-0.6f, 0, 0);
			}
		}

		
		public Model getRightJoint() {
			return rightAxle;
		}

		
		public void refresh() {
			updateModel();
		//	centerModels();
			
			float radius = leftAxle.getLocalScale().x/2f;
			float offset = ((rightAxle.getLocalTranslation().x + leftAxle.getLocalTranslation().x)/2f) ;
			
			
			
			Vector3f translate = new Vector3f(offset,0,0);
			interpreter.getLocalRotation().multLocal(translate);
			interpreter.getLocalTranslation().addLocal(translate);
			
			interpreter.setJointRadius(radius);
			interpreter.setJointDistance(leftAxle.getLocalTranslation().subtract(rightAxle.getLocalTranslation()).length() - radius*2f);
			HydraulicStructure.this.refresh();
		}

		
		public float getMaximum() {
			return getMaximumDistance();
		}

		
		public float getMinimum() {
			return getMinimumDistance();
		}

		
		public void slideEnd(Model joint, float amount) {
			
			boolean controlLeft = (joint == leftAxle);
			
			Model coJoint = controlLeft?rightAxle:leftAxle;
		
			Vector3f axisOfMovement = new Vector3f(1,0,0);
			if (controlLeft)
				axisOfMovement.multLocal(-1);
			
			float distanceOfCoJoint = -coJoint.getLocalTranslation().dot(axisOfMovement);
			   float adjustForCylinderSize =  joint.getLocalScale().x/2f;
		        adjustForCylinderSize +=coJoint.getLocalScale().x/2f;
		    
		    float minimumDistance = getMinimum() + adjustForCylinderSize;
		  //  if (minimumDistance<adjustForCylinderSize)
		   // 	minimumDistance = adjustForCylinderSize;
		    
			
			if (amount > getMaximum()  - distanceOfCoJoint + adjustForCylinderSize)
				amount =getMaximum() - distanceOfCoJoint + adjustForCylinderSize;
			else if (amount < minimumDistance- distanceOfCoJoint)
				amount = minimumDistance - distanceOfCoJoint;
			
			amount += distanceOfCoJoint;

			joint.getLocalTranslation().set(axisOfMovement).multLocal(amount).addLocal(coJoint.getLocalTranslation());//this subtracts the local translation of the other node

			joint.updateWorldData();
		}
		
	
		
		public Object getContainingInstance() {
			return HydraulicStructure.this;
		}
		
	}
	
	@Override
	public Collection<Property> getPropertySet() {
		 Collection<Property> properties = super.getPropertySet();
		 properties.add(new Property(Property.PropertyType.MOTOR,this.interpreter.getMotorPropertiesStore()));		 
		// properties.add(new Property(Property.PropertyType.HYDRAULICS,this.interpreter.getStore()));		 
		 properties.add(new Property(Property.PropertyType.PID,this.interpreter.getMotorPropertiesStore()));
		 properties.add(new Property(Property.PropertyType.SCALE_HYDRAULICS,this.interpreter.getStore()));		
		 return properties;
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
		
			for (HydraulicControlPoint control:controlPoints)
			{
				control.enable(controller);
				control.setControlSet(controlPoints);
			}
			if (resolve)
			{
				
				centerModels();
				controller.updateModel();
				collisionMemberStateChange();
			}
			return super.doAction();
		}



		
		public boolean undoAction() {
			if (!resolve)
			{
				for (HydraulicControlPoint control:controlPoints)
				{
					control.disable();
				}
			}else
			{	
					centerModels();
					controller.updateModel();
					collisionMemberStateChange();
			}
			return super.undoAction();
		}
		
		
		public Actionable getControlled() {
		
			return HydraulicStructure.this;
		}

		
		public void setVisible(boolean visible) {
			for (HydraulicControlPoint control:controlPoints)
			{
				control.setVisible(visible);
			}
			
		}

		
		public Actionable[] getControlPoints() {
			return controlPoints;
		}

	};
	
}


