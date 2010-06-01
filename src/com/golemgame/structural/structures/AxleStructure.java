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
import com.golemgame.mvc.golems.AxleInterpreter;
import com.golemgame.mvc.golems.SpatialInterpreter;
import com.golemgame.properties.Property;
import com.golemgame.structural.Physical;
import com.golemgame.structural.collision.CollisionMember;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.control.AxleMVCControlPoint;
import com.golemgame.tool.control.ControllableModel;
import com.golemgame.tool.control.AxleMVCControlPoint.ControllableJoint;
import com.jme.bounding.BoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Cylinder;


public class AxleStructure extends PhysicalStructure{
	private static final long serialVersionUID = 1L;
	private static AxleMVCControlPoint[] leftControlPoints = new AxleMVCControlPoint[]{ new AxleMVCControlPoint(AxleMVCControlPoint.AxlePosition.LENGTH_LEFT),new AxleMVCControlPoint(AxleMVCControlPoint.AxlePosition.RADIAL_LEFT)};
	private static AxleMVCControlPoint[] rightControlPoints = new AxleMVCControlPoint[]{ new AxleMVCControlPoint(AxleMVCControlPoint.AxlePosition.LENGTH_RIGHT),new AxleMVCControlPoint(AxleMVCControlPoint.AxlePosition.RADIAL_RIGHT)};
	private static AxleMVCControlPoint[] controlPoints = new AxleMVCControlPoint[]{leftControlPoints[0],leftControlPoints[1],rightControlPoints[0],rightControlPoints[1]};
	public static final float INIT_SIZE = 1f;
	
	private CollidableAxle leftAxle;
	private CollidableAxle rightAxle;
	
	private CylinderFacade leftFacade;
	private CylinderFacade rightFacade;
	private NodeModel model;
	private Model[] controlledModels;
	private CollisionMember collisionMember;
	private ControllableImpl controller ;
	private Model[] visualModels;
	
	private MotorPropertiesImpl motorProperties;
	
	private CylinderModel leftBearing;
	private CylinderFacade leftBearingFacade;
	
	private WirePort input;
	private WirePort output;
	private AxleInterpreter interpreter;
	
	public AxleStructure(AxleInterpreter interpreter) {
		super(interpreter);
		this.interpreter = interpreter;
	
		this.model = new NodeModel(this);

		leftFacade = new CylinderFacade();
		rightFacade = new CylinderFacade();
		leftAxle = new CollidableAxle(this,rightAxle,leftFacade);
		rightAxle = new CollidableAxle(this,leftAxle,rightFacade);
		
		leftAxle.getLocalRotation().fromAngleNormalAxis(-FastMath.HALF_PI, Vector3f.UNIT_Y);
		rightAxle.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Y);
		leftFacade.getLocalRotation().fromAngleNormalAxis(-FastMath.HALF_PI, Vector3f.UNIT_Y);
		rightFacade.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Y);
		
		leftAxle.getLocalTranslation().x = -0.5f;
		rightAxle.getLocalTranslation().x = 0.5f;

		
		rightAxle.getLocalScale().set(0.2f,0.2f,1f);
		
		
		leftBearing = new CylinderModel(true);
		leftBearing.getLocalRotation().fromAngleNormalAxis(-FastMath.HALF_PI, Vector3f.UNIT_Y);
		leftBearingFacade = new CylinderFacade();
		
		
		this.getModel().addChild(leftAxle);
		this.getModel().addChild(rightAxle);
		this.getModel().addChild(leftFacade);
		this.getModel().addChild(rightFacade);
				
		leftAxle.setActionable(this);
		rightAxle.setActionable(this);
		leftBearing.setActionable(this);
		leftAxle.updateWorldData();
		rightAxle.updateWorldData();
		leftBearing.updateWorldData();
		
		leftFacade.loadModelData(leftAxle);
		rightFacade.loadModelData(rightAxle);
		leftBearingFacade.loadModelData(leftBearing);
		leftFacade.updateWorldData();
		rightFacade.updateWorldData();
		leftBearingFacade.updateWorldData();
		
		input = new WirePort(this,true);
		input.getModel().getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI,Vector3f.UNIT_Z);
		
		this.getModel().addChild(input.getModel());
		
		
		output = new WirePort(this,false);
		this.getModel().addChild(output.getModel());
		
		
		super.registerWirePort(input);
		super.registerWirePort(output);
		controlledModels = new Model[]{leftAxle,rightAxle,leftFacade,rightFacade,leftBearing,leftBearingFacade};
		visualModels = new Model[]{leftFacade,rightFacade,leftBearingFacade};

			this.collisionMember = new CollisionMember(model,this.getActionable());
			collisionMember.registerCollidingModel(leftAxle);
			collisionMember.registerCollidingModel(rightAxle);
			collisionMember.registerCollidingModel(leftBearing);

			
		
		
		controller = new ControllableImpl(leftAxle, leftFacade, rightAxle, rightFacade,output,input);
		super.initialize();
	
	}
	
	
	public void refreshController() {
		controller.refresh();
	}
	
	
	
	public Model[] getAppearanceModels() {
		return visualModels;
	}
	
	



/*	
	public PhysicalStructure copy(StructuralMachine destination) {
		AxleStructure copy = new AxleStructure(new AxleInterpreter(new PropertyStore(new PropertyStore(getStore()))));
	
		copy.motorProperties.set(this.motorProperties);
		
		copy.leftAxle.loadModelData(this.leftAxle);
		copy.rightAxle.loadModelData(this.rightAxle);
		copy.leftFacade.loadModelData(this.leftFacade);
		copy.rightFacade.loadModelData(this.rightFacade);
		copy.set(this);
		return copy;
	}
	*/
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
		return new Physical[]{this,leftAxle,rightAxle};
	}*/

	
	public boolean isPhysical() {
		return true;
	}



	
	public boolean isMindful() {
		return true;
	}


	@Override
	public Collection<Property> getPropertySet() {
		 Collection<Property> properties = super.getPropertySet();
		 properties.add(new Property(Property.PropertyType.MOTOR,this.interpreter.getMotorPropertiesStore()));
		 properties.add(new Property(Property.PropertyType.PID,this.interpreter.getMotorPropertiesStore()));

		 properties.add(new Property(Property.PropertyType.SCALE_AXLE,this.interpreter.getStore()));		
		 return properties;
	}
	
	
	@Override
	public void refresh() {
		
		super.refresh();
		if(this.motorProperties == null || ! this.motorProperties.getStore().equals(interpreter.getMotorPropertiesStore()))
		{
			motorProperties = new MotorPropertiesImpl( interpreter.getMotorPropertiesStore());
			motorProperties.refresh();
		}
		
		if(interpreter.isBearing())
		{
			if(leftBearing.getParent()==null)
			{
				getModel().addChild(leftBearing);
				getModel().addChild(leftBearingFacade);
				getModel().updateModelData();
			}
		}else{
			leftBearing.detachFromParent();
			leftBearingFacade.detachFromParent();
		}
		
		getModel().updateWorldData();
		leftAxle.updateWorldData();
		rightAxle.updateWorldData();
		leftBearing.updateWorldData();
		
		float leftLength = interpreter.getJointLength(true);
		float rightLength = interpreter.getJointLength(false);

		float leftRadius = interpreter.getJointRadius(true);
		float rightRadius = interpreter.getJointRadius(false);
		
		float leftBearingLength =interpreter.getBearingLength();

		float leftBearingRadius =interpreter.getBearingRadius();
		
		leftAxle.getLocalScale().set(leftRadius*2f,leftRadius*2f,leftLength);
		rightAxle.getLocalScale().set(rightRadius*2f,rightRadius*2f,rightLength);
		leftBearing.getLocalScale().set(leftBearingRadius*2f,leftBearingRadius*2f,leftBearingLength);
		
		
		leftAxle.getLocalTranslation().setX(-leftLength/2f);
		rightAxle.getLocalTranslation().setX(rightLength/2f);
		leftBearing.getLocalTranslation().setX(-leftLength -leftBearingLength/2f);
		
		leftAxle.updateWorldData();
		rightAxle.updateWorldData();
		leftBearing.updateWorldData();
		
		this.leftFacade.loadModelData(leftAxle);
		this.rightFacade.loadModelData(rightAxle);
		leftBearingFacade.loadModelData(leftBearing);
		
		output.getModel().getLocalTranslation().set(leftAxle.getLocalTranslation());
		output.getModel().getLocalTranslation().y = leftAxle.getLocalScale().getY()/2f;
	
		input.getModel().getLocalTranslation().set(leftFacade.getLocalTranslation());//.addLocal(-leftFacade.getLocalScale().z/2f,0,0);
		
		input.refresh();
	

		output.refresh();
	
		//this.output.setReference(interpreter.getOutput());
		//this.input.setReference(interpreter.getInput());
		
		
	}




	
	
	/**
	 * There are two spatial models to each axle, and two axles in each motor: one component is displayed
	 * and the other component is only for trimesh collisions.
	 * @author Sam
	 *
	 */
	public static class CollidableAxle extends SpatialModel implements ControllableModel, Physical
	{
		private static final long serialVersionUID = 1L;
	
		private static final float RADIUS = 0.5f;
		private static final float HEIGHT = 1f;
	

		private AxleStructure owner;
		
		public PropertyStore getPropertyStore() {
			return owner.interpreter.getStore();
		}
		
	
		private boolean isStatic = false;
		private CylinderFacade facade;
	
		public PropertyState getCurrentState() {
			return new SimplePropertyState(owner.interpreter.getStore(),new String[]{AxleInterpreter.LEFT_JOINT_LENGTH,AxleInterpreter.RIGHT_JOINT_LENGTH,AxleInterpreter.LEFT_JOINT_RADIUS,AxleInterpreter.RIGHT_JOINT_RADIUS,SpatialInterpreter.LOCALTRANSLATION,SpatialInterpreter.LOCALSCALE,SpatialInterpreter.LOCALROTATION});
		}
		
		public CollidableAxle(AxleStructure owner, CollidableAxle coAxle , CylinderFacade facade) {
			super();
			this.owner = owner;
			this.facade = facade;

			this.updateModelData();
			this.updateWorldData();
			registerSpatial();
		}

		
		public boolean isPropagating() {
			return true;
		}

		
		
		public AxleInterpreter getInterpreter() {
			return owner.interpreter;
		}
		
		protected Spatial buildSpatial() {
			Spatial spatial= new Cylinder("built", 4,8, INIT_SIZE/2,INIT_SIZE,true);	
			spatial.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.HALF_PI, new Vector3f(1,0,0)));		
			spatial.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, new Vector3f(0,1,0)));
			//spatial.updateRenderState();
			spatial.setModelBound(new BoundingBox());
			spatial.setCullMode(SceneElement.CULL_ALWAYS);
			spatial.updateModelBound();
			return spatial;
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

		
			super.updateWorldData();
		
	
			
			
		}

		
		public Vector3f getDimensions() {
			return new Vector3f(1,1,1);
		}

		
		
		public boolean isStatic() {
			
			return isStatic;
		}



		
		public void setStatic(boolean isStatic) {
			this.isStatic= isStatic;
			
		}

		public CylinderFacade getFacade() {
			return facade;
		}
		

		
		
	}
	
	public class ControllableImpl implements ControllableJoint
	{
		private static final long serialVersionUID = 1L;

		public PropertyStore getPropertyStore() {
			return interpreter.getStore();
		}
		
		public ControllableImpl(CollidableAxle leftAxle, CylinderFacade leftFacade,
				CollidableAxle rightAxle, CylinderFacade rightFacade, WirePort wirePortModel,WirePort inputPortModel) {
			super();

		}

		public Model getParent() {
			return getModel();
		}


		
		public AxleInterpreter getInterpreter() {
			return interpreter;
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
			rightAxle.updateWorldData();
		}
		
		public void updateWorldData() {
			getParent().updateWorldData();
		}

		public PropertyState getCurrentState() {
			return new SimplePropertyState(interpreter.getStore(),new String[]{AxleInterpreter.LEFT_JOINT_LENGTH,AxleInterpreter.RIGHT_JOINT_LENGTH,AxleInterpreter.LEFT_JOINT_RADIUS,AxleInterpreter.RIGHT_JOINT_RADIUS,SpatialInterpreter.LOCALTRANSLATION,SpatialInterpreter.LOCALSCALE,SpatialInterpreter.LOCALROTATION});
		}
		
		public Model getLeftJoint() {
			return leftAxle;
		}

		
		public float getRadius() {
			return interpreter.getJointRadius(true);
		}


		public void setRadius(float radius) {
			interpreter.setJointRadius(radius, true);
			interpreter.setJointRadius(radius/10f, false);
		}


		public float getLength(boolean left) {
			return interpreter.getJointLength(left);
		}


		public void setLength(float length, boolean left) {
			interpreter.setJointLength(length, left);
		}


		public Vector3f getLocalTranslation() {
			return getModel().getLocalTranslation();
		}


		public Vector3f getNaturalTranslation(boolean forLeft) {
			Vector3f store = new Vector3f();
			if (forLeft)
			{
				return store.set(0.5f, 0, 0); 
			}else
			{
				return store.set(-0.25f, 0, 0);
			}
		}

		
		public Model getRightJoint() {
			return rightAxle;
		}

		
		public Vector3f getNaturalScale(boolean forLeft) {
			if(forLeft)
			{
				return new Vector3f(1,1,1);
			}else
			{
				return new Vector3f(.2f,.2f,0.5f);
			}
		
		}
		
		
		public void refresh() {
			
		
			
			interpreter.setJointLength(leftAxle.getLocalScale().z,true);
			interpreter.setJointLength(rightAxle.getLocalScale().z,false);
			
		//	interpreter.setJointRadius(leftAxle.getLocalScale().x/2f, true);
		//	interpreter.setJointRadius(rightAxle.getLocalScale().x/2f, false);
			
			float radius = leftAxle.getLocalScale().x;
			
			interpreter.setJointRadius(radius/2f, true);
			interpreter.setJointRadius(radius/10f, false);
			AxleStructure.this.refresh();
		}

		public Object getContainingInstance() {
			return AxleStructure.this;
		}
	}


	
	
	
	public Action<?> getAction(Type type) throws ActionTypeException {
		if (type == Type.CONTROL)
		{
			return new Control();
		}else
			return super.getAction(type);
	}
	protected class Control extends ControlImpl
	{
		
		
		public boolean doAction() 
		{
		
			for (AxleMVCControlPoint control:controlPoints)
			{
				control.enable(controller);
				control.setControlSet(controlPoints);
			}
			if (resolve)
			{
				
				//centerModels();
				controller.updateModel();
				
			}
			
			
			return super.doAction();
		}



		
		public boolean undoAction() {
			if (!resolve)
			{
				for (AxleMVCControlPoint control:controlPoints)
				{
					control.disable();
				}
			}else
			{	
					//centerModels();
					controller.updateModel();
			}
			return super.doAction();
		}
		
		
		public Actionable getControlled() {
		
			return AxleStructure.this;
		}

		
		public void setVisible(boolean visible) {
			for (AxleMVCControlPoint control:controlPoints)
			{
				control.setVisible(visible);
			}
			
		}

		
		public Actionable[] getControlPoints() {
			return controlPoints;
		}

	}
}
