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
import com.golemgame.mvc.golems.HingeInterpreter;
import com.golemgame.mvc.golems.SpatialInterpreter;
import com.golemgame.properties.Property;
import com.golemgame.structural.Physical;
import com.golemgame.structural.collision.CollisionMember;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.ControlAction;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.control.ControllableModel;
import com.golemgame.tool.control.HingeControlPoint;
import com.golemgame.tool.control.HingeControlPoint.ControllableHinge;
import com.jme.bounding.BoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;


public class HingeStructure extends PhysicalStructure {
	private static final long serialVersionUID = 1L;
	
	private static HingeControlPoint[] leftControlPoints = new HingeControlPoint[]{ new HingeControlPoint(HingeControlPoint.HINGE,true),new HingeControlPoint(HingeControlPoint.SCALE,true)};
	private static HingeControlPoint[] rightControlPoints = new HingeControlPoint[]{ new HingeControlPoint(HingeControlPoint.HINGE,false),new HingeControlPoint(HingeControlPoint.SCALE,false)};
	private static HingeControlPoint[] controlPoints = new HingeControlPoint[]{leftControlPoints[0],leftControlPoints[1],rightControlPoints[0],rightControlPoints[1]};



	
	private CollidableHinge leftHinge;
	private CollidableHinge rightHinge;
	
	private NodeModel model;
	private Model[] controlledModels;
	private CollisionMember collisionMember;
	private ControllableImpl controller ;
	private Model[] visualModels;
	
	private SpatialModel connectionModel;
	private SpatialModel connectionFacade;
	
	private MotorPropertiesImpl motorProperties;
	
	private WirePort input;
	private WirePort output;
	
	
	private HingeInterpreter interpreter;
	public HingeStructure(HingeInterpreter interpreter) {
		super(interpreter);
		this.interpreter = interpreter;
		
		this.model = new NodeModel(this);
		

		leftHinge = new CollidableHinge(this,rightHinge);
		rightHinge = new CollidableHinge(this,leftHinge);
		
		
		connectionModel = new CylinderModel(true);
		connectionFacade = new CylinderFacade();		
		
	//	leftHinge.getLocalRotation().fromAngleNormalAxis(-FastMath.HALF_PI, Vector3f.UNIT_Y);
	//	rightHinge.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Y);

		
		leftHinge.getLocalTranslation().x = -0.6f;
		rightHinge.getLocalTranslation().x = 0.6f;

		connectionModel.getLocalRotation().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Z);
		connectionModel.getLocalScale().set(0.19f,0.19f,1f);
		connectionFacade.loadModelData(connectionModel);
		
		this.getModel().addChild(leftHinge);
		this.getModel().addChild(rightHinge);
		this.getModel().addChild(connectionModel);
		this.getModel().addChild(connectionFacade);
		
		
		leftHinge.setActionable(this);
		rightHinge.setActionable(this);
		connectionModel.setActionable(this);

		leftHinge.updateWorldData();
		rightHinge.updateWorldData();
		connectionModel.updateWorldData();
		connectionFacade.updateWorldData();

		input = new WirePort(this,true);
		this.getModel().addChild(input.getModel());
		input.getModel().getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Z);
		
		output = new WirePort(this,false);
		this.getModel().addChild(output.getModel());
		output.getModel().getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_X);
		
		
		super.registerWirePort(input);
		super.registerWirePort(output);
		controlledModels = new Model[]{leftHinge,rightHinge,connectionModel,connectionFacade};
		visualModels = new Model[]{leftHinge,rightHinge,connectionFacade};
		
		
	
			this.collisionMember = new CollisionMember(model,this.getActionable());
			collisionMember.registerCollidingModel(leftHinge);
			collisionMember.registerCollidingModel(rightHinge);
			collisionMember.registerCollidingModel(connectionModel);


		
	
		
		controller = new ControllableImpl(leftHinge, rightHinge,connectionModel, connectionFacade,output,input);
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

	
/*	public Physical[] getPhysical() {
		return new Physical[]{this,leftHinge,rightHinge};
	}
	*/
	public boolean isMindful() {
		return true;
	}

	
	public boolean isPhysical() {
		return true;
	}
	/**
	 * There are two spatial models to each Hinge, and two Hinges in each motor: one component is displayed
	 * and the other component is only for trimesh collisions.
	 * @author Sam
	 *
	 */
	public static class CollidableHinge extends SpatialModel implements ControllableModel, Physical
	{

		private static final long serialVersionUID = 1L;

		private HingeStructure owner;
		
	//	private MaterialWrapper materialWrapper = new MaterialWrapper();
		private boolean isStatic = false;

		public boolean isPropagating() {
			return true;
		}

		public PropertyStore getPropertyStore() {
			return owner.interpreter.getStore();
		}
		
		public CollidableHinge(HingeStructure owner, CollidableHinge coHinge) {
			super();
			this.owner = owner;

			this.updateModelData();
			this.updateWorldData();
			registerSpatial();
		}

		
		public Vector3f getRelativeFromWorld(Vector3f worldTranslation,
				Vector3f store) {
			updateWorldData();
			return getParent().worldToLocal(worldTranslation, store);
		}

		
		public Vector3f getWorldFromRelative(Vector3f localTranslation,
				Vector3f store) {

			updateWorldData();
			return  getParent().localToWorld(localTranslation, store);
		}


		
		public void updateModel() {

			owner.centerModels();
			super.updateWorldData();
		
			
			
			
		}

		public PropertyState getCurrentState() {
			return new SimplePropertyState(owner.interpreter.getStore(),new String[]{HingeInterpreter.LEFT_JOINT_ANGLE,HingeInterpreter.RIGHT_JOINT_ANGLE,HingeInterpreter.LEFT_JOINT_LENGTH,HingeInterpreter.RIGHT_JOINT_LENGTH,SpatialInterpreter.LOCALTRANSLATION,SpatialInterpreter.LOCALSCALE,SpatialInterpreter.LOCALROTATION});
		}
		
		
		protected Spatial buildSpatial() {

			Spatial spatial = new Box("hinge", new Vector3f(), 0.5f, 0.5f, 0.5f);
			spatial.getLocalScale().set(1,0.2f,1);
			spatial.setModelBound(new BoundingBox());
			
			//spatial.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, new Vector3f(0,1,0));
			spatial.updateModelBound();
			return spatial;
		}

		
		public boolean isStatic() {
			
			return isStatic;
		}


		
		public void setStatic(boolean isStatic) {
			this.isStatic= isStatic;
			
		}

	

		public SpatialInterpreter getInterpreter() {
			return owner.interpreter;
		}
		
		
		
	}
	
	
	@Override
	public void refresh() {
		super.refresh();
		if(this.motorProperties == null || ! this.motorProperties.getStore().equals(interpreter.getMotorPropertiesStore()))
		{
			motorProperties = new MotorPropertiesImpl( interpreter.getMotorPropertiesStore());
			motorProperties.refresh();
		}
		
		float width = interpreter.getJointLength(true);
		
		leftHinge.getLocalScale().set(width,0.2f,width);
		rightHinge.getLocalScale().set(width,0.2f,width);
		
		connectionModel.getLocalScale().set(.19f,.19f,width);
		connectionFacade.loadModelData(connectionModel);
		
		//
	//	System.out.println(interpreter.getJointAngle(false));
	//	interpreter.setJointAngle(FastMath.PI/2f, false);
		
		
		leftHinge.getLocalRotation().fromAngleNormalAxis(interpreter.getJointAngle(true),Vector3f.UNIT_Z);
		rightHinge.getLocalRotation().fromAngleNormalAxis(interpreter.getJointAngle(false),Vector3f.UNIT_Z);
	
		
		
		
		leftHinge.getLocalTranslation().set(-width/2f-0.1f ,0,0);
		leftHinge.getLocalRotation().multLocal(leftHinge.getLocalTranslation());
		
		rightHinge.getLocalTranslation().set(width/2f+0.1f,0,0);
		rightHinge.getLocalRotation().multLocal(rightHinge.getLocalTranslation());
		
		getModel().updateWorldData();
		leftHinge.updateWorldData();
		rightHinge.updateWorldData();
		connectionModel.updateWorldData();
		
		output.getModel().getLocalTranslation().zero();
		output.getModel().getLocalTranslation().addLocal(0,0,-leftHinge.getLocalScale().x/2f);
		output.getModel().getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_X);
		
	
		output.refresh();
		input.getModel().getLocalRotation().set(leftHinge.getLocalRotation()).multLocal(new Quaternion().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Z));
		
		Vector3f pos = new Vector3f(-leftHinge.getLocalScale().z - connectionModel.getLocalScale().x/2f,0,0);
		
	//	secondPortModel.getModel().getLocalTranslation().set(leftHinge.getLocalTranslation()).addLocal(-leftHinge.getLocalScale().z/2f,0,0);
		leftHinge.getLocalRotation().multLocal(pos);
		input.getModel().getLocalTranslation().set(pos);
		input.refresh();
	
	/*	this.output.setReference(interpreter.getOutput());
		this.input.setReference(interpreter.getInput());*/
		
	
	}
	
	public class ControllableImpl implements ControllableHinge
	{
		private static final long serialVersionUID = 1L;

		public PropertyStore getPropertyStore() {
			return interpreter.getStore();
		}
		
		
		public HingeInterpreter getInterpreter() {
			return interpreter;
		}
		
		public ControllableImpl(CollidableHinge leftHinge,
				CollidableHinge rightHinge,SpatialModel connectionModel,SpatialModel connectionFacade, WirePort wirePortModel, WirePort secondPortModel) {
			super();
		
		
		
		}
		public void updateWorldData() {
			getParent().updateWorldData();
		}
		public PropertyState getCurrentState() {
			return new SimplePropertyState(interpreter.getStore(),new String[]{HingeInterpreter.LEFT_JOINT_ANGLE,HingeInterpreter.RIGHT_JOINT_ANGLE,HingeInterpreter.LEFT_JOINT_LENGTH,HingeInterpreter.RIGHT_JOINT_LENGTH,SpatialInterpreter.LOCALTRANSLATION,SpatialInterpreter.LOCALSCALE,SpatialInterpreter.LOCALROTATION});
		}
		
		public Vector3f getNaturalScale(boolean forLeft) {
			return new Vector3f(1,1,1);
		}

		
		public Model getParent() {
			return getModel();
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
			leftHinge.updateWorldData();

			rightHinge.updateWorldData();
	
		}

		
		public Model getLeftJoint() {
			return leftHinge;
		}

		
		public Vector3f getNaturalTranslation(boolean forLeft) {
			Vector3f store = new Vector3f();
			if (forLeft)
			{
				return store.set(-0.6f, 0, 0); 
			}else
			{
				return store.set(0.6f, 0, 0);
			}
		}
		
		public Vector3f getLocalCenter(Vector3f store,boolean left)
		{
			if (store == null)
				store = new Vector3f();
			if (left)
			{
				return store.set(0.6f, 0, 0); 
			}else
			{
				return store.set(-0.6f, 0, 0);
			}
		}
		
		public Model getRightJoint() {
			return rightHinge;
		}

		
		public void refresh() {
			float width = leftHinge.getLocalScale().x;
			interpreter.setJointLength(width, true);
			interpreter.setJointLength(width, false);
		
		/*	Vector3f angleProbe = new Vector3f(1,0,0);
			leftHinge.getLocalRotation().multLocal(angleProbe);
			rightHinge.getLocalRotation().inverse().multLocal(angleProbe);
			
			Vector3f compare = new Vector3f(-1,0,0);
			float angle = compare.angleBetween(angleProbe) * angleProbe.dot(Vector3f.UNIT_Y);*/
			
		
				//interpreter.setJointAngle(angle);
			
			HingeStructure.this.refresh();
		}
		
		
		
		public Object getContainingInstance() {
			return HingeStructure.this;
		}

		public void setAngle(float angle, boolean left) {
		
			interpreter.setJointAngle(angle,left);
			
		
			HingeStructure.this.refresh();
			
		}
	}

	
	@Override
	public Collection<Property> getPropertySet() {
		 Collection<Property> properties = super.getPropertySet();
		 properties.add(new Property(Property.PropertyType.MOTOR,this.interpreter.getMotorPropertiesStore()));		
		 properties.add(new Property(Property.PropertyType.PID,this.interpreter.getMotorPropertiesStore()));
		 properties.add(new Property(Property.PropertyType.SCALE_HINGE,this.interpreter.getStore()));		
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
		
			for (HingeControlPoint control:controlPoints)
			{
				control.enable(controller);
				control.setControlSet(controlPoints);
			}
			if (resolve)
			{
				
				//centerModels();
				controller.updateModel();
				collisionMemberStateChange();
			}
			super.doAction();
			return true;
		}



		
		public boolean undoAction() {
			if (!resolve)
			{
				for (HingeControlPoint control:controlPoints)
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
		
			return HingeStructure.this;
		}

		
		public void setVisible(boolean visible) {
			for (HingeControlPoint control:controlPoints)
			{
				control.setVisible(visible);
			}
			
		}

		
		public Actionable[] getControlPoints() {
			return controlPoints;
		}

	}
}
