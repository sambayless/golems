package com.golemgame.tool.control;


import com.golemgame.model.Model;
import com.golemgame.mvc.golems.HydraulicInterpreter;
import com.golemgame.tool.ActionToolSettings;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.action.information.MoveRestrictedInformation;
import com.golemgame.tool.action.information.OrientationInformation;
import com.jme.bounding.BoundingSphere;
import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.SceneElement;
import com.jme.scene.SharedMesh;
import com.jme.scene.shape.Sphere;

public class HydraulicControlPoint extends ControlPoint<HydraulicControlPoint.ControllableHydraulic> {

	private Geometry controlSphere;
	private Geometry facadeSphere;
	private OrientationInformation orientation;
	
	
	public static enum SlidePosition
	{
		SLIDE(), SCALE();		
	}
	
	public static final SlidePosition SLIDE = SlidePosition.SLIDE;
	public static final SlidePosition SCALE = SlidePosition.SCALE;
	
	final float MIN_THICKNESS = 0.2f;
	
	private SlidePosition type;
	



//	private Vector3f center = null;
//	private Quaternion startingRotation = new Quaternion();

	private boolean controlLeft = false;

	


	public boolean isLeft() {
		return controlLeft;
	}

	public HydraulicControlPoint(SlidePosition type, boolean controlLeft) {
		super();
		this.controlLeft = controlLeft;
		this.type = type;
		controlSphere = new Sphere("ControlSphere", new Vector3f(), 6, 6, radius);
		controlSphere.setCullMode(SceneElement.CULL_ALWAYS);
		facadeSphere = new SharedMesh("ControlSphereFacade",baseSphere);
		facadeSphere.setIsCollidable(false);
		
		
		this.visualNode.getNode().attachChild(controlSphere);
		this.visualNode.getNode().attachChild(facadeSphere);
		//tie these together
		facadeSphere.setLocalRotation(controlSphere.getLocalRotation());
		facadeSphere.setLocalScale(controlSphere.getLocalScale());
		facadeSphere.setLocalTranslation(controlSphere.getLocalTranslation());
		this.visualNode.getSpatial().updateGeometricState(0, true);
		this.visualNode.getSpatial().updateRenderState();		
		this.visualNode.getSpatial().setModelBound(new BoundingSphere());
		this.visualNode.getSpatial().updateModelBound();
		this.visualNode.getSpatial().updateWorldBound();
		
		orientation = new OrientationInfo();

	}

	@Override
	protected Vector3f getControlDistance() {
		// TODO Auto-generated method stub
		return null;
	}

	private Vector3f _unit = new Vector3f();

	private final Vector3f slideAxis = new Vector3f(0,0,1);
	private Vector3f _slideVector = new Vector3f();


	@Override
	protected void move(ControllableHydraulic hydraulic, Vector3f position) {

		hydraulic.updateModel();
		Model joint;
		Model coJoint;
		
		if (controlLeft)
		{
			joint = hydraulic.getLeftJoint();
			coJoint = hydraulic.getRightJoint();
		}else
		{
			coJoint = hydraulic.getLeftJoint();
			joint = hydraulic.getRightJoint();
		}
	
		
		if (type == SLIDE)
		{
			//move the slide joint along the axis of sliding
			//Stop it if it is more than max distance away from the cojoint, etc.
			//refresh the slide center afterwards		
			//The axis of movement in the parents coordinates
			Vector3f axisOfMovement = new Vector3f(1,0,0);
			if (controlLeft)
				axisOfMovement.multLocal(-1);
			//joint.getLocalRotation().multLocal(axisOfMovement);
		
			
			float amount = position.dot(axisOfMovement)-joint.getLocalScale().z/2f - CONTROL_DISTANCE;
			
			controllable.slideEnd(joint, amount);
		}else
		{//Scale the joint
			//get the factor by which to scale
			Vector3f direction = _unit.set(position);
			direction.subtractLocal(joint.getLocalTranslation());

			Vector3f normalToMovement = _slideVector.set(slideAxis);//this should be the direction that the hinge lies along, in hinge local coordinates
		//	joint.getLocalRotation().multLocal(normalToMovement);
			//normalToMovement.normalizeLocal();
			float amount = position.dot(normalToMovement);
			if (amount < 0)
				amount *= -1;
			
			amount -= CONTROL_DISTANCE;
			
			amount *= 2;
			
			if (amount<MIN_THICKNESS)
				amount = MIN_THICKNESS;
			
			Vector3f axisOfMovement = new Vector3f(1,0,0);
			if (controlLeft)
				axisOfMovement.multLocal(-1);
			//joint.getLocalRotation().multLocal(axisOfMovement);
			
			float maximumWidth =joint.getLocalTranslation().dot(axisOfMovement) -coJoint.getLocalTranslation().dot(axisOfMovement);
			maximumWidth -= hydraulic.getMinimum()/2f;//divide by two because this is mirrored on the other joint
			
			
		
			if (amount>maximumWidth)
				amount = maximumWidth;
			
			
			
			joint.getLocalScale().set(amount,amount,amount);
			//slide.getGeometry().getLocalScale().y = amount;	
		//	slide.getConnection().getLocalScale().x = amount;
		//	slide.getConnection().getLocalScale().y = amount;	
			
			coJoint.getLocalScale().set(amount,amount,amount);
		//	((SlideMaterialNode)slide.getCoJoint()).getGeometry().getLocalScale().set(amount,amount,amount);
		//	((SlideMaterialNode)slide.getCoJoint()).getGeometry().getLocalScale().y = amount;	

				

		
		}
		
		hydraulic.refresh();
		hydraulic.updateModel();
		for (ControlPoint<?> control:super.siblings)
		{
			control.updatePosition();
		}
	}
	
	private static String[] keys = new String[]{HydraulicInterpreter.JOINT_DISTANCE,HydraulicInterpreter.LOCALTRANSLATION,HydraulicInterpreter.JOINT_RADIUS};

	@Override
	protected String[] getKeys() {
		return keys;
	}
	
	private static final float CONTROL_DISTANCE=1f;
	
	@Override
	public void updatePosition() {
		if (controllable == null)
			return;
		Model joint;
		Model coJoint;
		
		if (controlLeft)
		{
			joint = controllable.getLeftJoint();
			coJoint = controllable.getRightJoint();
		}else
		{
			coJoint = controllable.getLeftJoint();
			joint = controllable.getRightJoint();
		}
		Vector3f control;
		
		if (type == SLIDE)
		{
			if (controlLeft)
			{
				control= new Vector3f(-CONTROL_DISTANCE,0,0);
				control.x -= joint.getLocalScale().z/2f;// - slide.getNaturalCylScale(new Vector3f()).z;
				control.addLocal(joint.getLocalTranslation());
			}
			else
			{
				control= new Vector3f(CONTROL_DISTANCE,0,0);
				control.x += joint.getLocalScale().z/2f;// - slide.getNaturalCylScale(new Vector3f()).z;
				control.addLocal(joint.getLocalTranslation());
			}
			this.getModel().getLocalTranslation().set(control);
		}else
		{
			if (controlLeft)
			{
				
				control= new Vector3f();
				control.z -= CONTROL_DISTANCE;
				control.z -= joint.getLocalScale().x/2f;// -hinge.getNaturalBoxScale(new Vector3f()).z;

			}else
			{
				control= new Vector3f();
				control.z += CONTROL_DISTANCE;
				control.z += joint.getLocalScale().x /2f;// -hinge.getNaturalBoxScale(new Vector3f()).z;

			}
			control.x = (controllable.getLeftJoint().getLocalTranslation().x + controllable.getRightJoint().getLocalTranslation().x )/2f;
			control.y = (controllable.getLeftJoint().getLocalTranslation().y + controllable.getRightJoint().getLocalTranslation().y )/2f;

					//control = new Vector3f(0,0,CONTROL_DISTANCE);
			//control.z += hinge.getLocalScale().length();
			this.getModel().getLocalTranslation().set(control);
		}

	
		this.getModel().updateWorldData();
	}

	@Override
	public Action<?> getAction(Type type) throws ActionTypeException {
		if (type == Action.ORIENTATION)
			return orientation;
		else if (type == Action.MOVE_RESTRICTED)
			return new MoveRestrictedInfo();
		return super.getAction(type);
	}
	
	protected  class MoveRestrictedInfo extends MoveRestrictedInformation
	{
		ControllableHydraulic toControl = null;
		
		@Override
		public Actionable getControlled() {
			return HydraulicControlPoint.this;
		}
		
		public MoveRestrictedInfo() {
			super();
			toControl = controllable;
		}



		public Vector3f getRestrictedPosition(Vector3f from) {
			//Get the closest grid node to the provided position;	
			
			
			Vector3f pos = toControl.getRelativeFromWorld(from, new Vector3f());
		//	System.out.println(pos);
			//pos.subtractLocal(ActionToolSettings.getInstance().getGridOrigin().getValue());
			Vector3f grid = new Vector3f( ActionToolSettings.getInstance().getGridUnits().getValue());
			grid.x/=2f;
			grid.z/=2f;
			//grid.z = 1;
			grid.x = 1;
			pos.divideLocal(grid);
		//	pos.x = Math.round(pos.x);
			pos.y = Math.round(pos.y);
			pos.z = Math.round(pos.z);
			pos.multLocal(grid);
			return toControl.getWorldFromRelative(pos, from);
		}	
	}
	
	private class OrientationInfo extends ControlPoint.OrientationInfoImpl
	{
		Vector3f direction = new Vector3f();
		public OrientationInfo()
		{
			super.useAxis = true;
		}
		
		
		@Override
		public Vector3f getAxis() {
			Model joint;
			Model coJoint;
			
			if (controlLeft)
			{
				joint = controllable.getLeftJoint();
				coJoint = controllable.getRightJoint();
			}else
			{
				coJoint = controllable.getLeftJoint();
				joint = controllable.getRightJoint();
			}
			joint.updateWorldData();
	
			if (type == SLIDE)
			{
				Vector3f dir =new Vector3f().set(1,0,0);
				joint.getWorldRotation().multLocal(dir);
				dir.normalizeLocal();
				return dir;
			}else
			{
				Vector3f dir =new Vector3f().set(0,0,1);
				joint.getWorldRotation().multLocal(dir);
				dir.normalizeLocal();
				return dir;
			}
		}

	}
	
	public interface ControllableHydraulic extends ControllableModel
	{
		public void refresh();
		public Model getLeftJoint();
		public Model getRightJoint();
		public Vector3f getNaturalTranslation(boolean forLeft);
		public Vector3f getNaturalScale(boolean forLeft);
//		public HydraulicInterpreter getInterpreter();
		public float getMinimum();
		public float getMaximum();
		public void slideEnd(Model joint, float amount);
	}
}
