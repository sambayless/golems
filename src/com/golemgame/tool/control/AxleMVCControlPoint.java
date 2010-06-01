package com.golemgame.tool.control;


import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.AxleInterpreter;
import com.golemgame.mvc.golems.validate.GolemsValidator;
import com.golemgame.tool.ActionToolSettings;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.action.information.MoveRestrictedInformation;
import com.golemgame.tool.action.information.OrientationInformation;
import com.jme.bounding.BoundingSphere;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.SceneElement;
import com.jme.scene.SharedMesh;
import com.jme.scene.shape.Sphere;

public class AxleMVCControlPoint extends MVCControlPoint<AxleMVCControlPoint.ControllableJoint> {
	
	private Geometry controlSphere;
	private Geometry facadeSphere;
	private OrientationInformation orientation;
	
	
	public static final float MIN_DISTANCE = 0.1f;




	public static enum AxlePosition
	{
		LENGTH_RIGHT(), RADIAL_LEFT(),RADIAL_RIGHT(),LENGTH_LEFT();		
	}
	

	final float MIN_THICKNESS = 0.2f;
	
	private AxlePosition type;

	public AxleMVCControlPoint(AxlePosition type) {
		super();
		
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
	}




	private Vector3f _unit = new Vector3f();

	private final Vector3f slideAxis = new Vector3f(0,0,1);
	private final Vector3f scaleAxis = new Vector3f(1,0,0);
	private Vector3f _slideVector = new Vector3f();

	
	@Override
	protected void move(ControllableJoint axle, Vector3f position) {
		
		if(type==AxlePosition.LENGTH_RIGHT)
		{
			Vector3f axis = Vector3f.UNIT_X;
			float dist =  axis.dot(position);
			
			dist-= CONTROL_DISTANCE;
		
			{	
				 float curHeight =  controllable.getInterpreter().getJointLength(false);
				 float newHeight = dist;
				
		/*		 float delta = curHeight - newHeight;
				 newHeight += delta/2f;*/
				
				 if(ActionToolSettings.getInstance().isRestrictMovement())
				 {
					 float grid = ActionToolSettings.getInstance().getGridUnits().getValue().dot(axis);;
					 newHeight/= grid;
						
						//Vector3f orig = new Vector3f(from);
					 newHeight =  Math.round(newHeight);	
						newHeight*=grid;
						
					
				 }
				 if(newHeight<0f)
					 newHeight = 0f;
	/*			
				 delta = curHeight - newHeight;
				 delta/=-2f;//this is to account for the part that is moving and the part that is growing.
				 Vector3f dirLocal = new Vector3f(axis);
				 controllable.getParent().getWorldRotation().multLocal(dirLocal);
				 dirLocal.multLocal(delta);*/
				 controllable.getInterpreter().setJointLength(newHeight,false); //increase the height by less, to account for the amount you are also moving the capsule.
				//
				 GolemsValidator.getInstance().makeValid(controllable.getPropertyStore());
				// controllable.getInterpreter().getLocalTranslation().addLocal(dirLocal);
				
				 // controllable.getParent().updateWorldData();
				 controllable.getInterpreter().refresh();
				 controllable.getParent().updateWorldData();
				 controllable.updateModel();
				 this.updatePosition();
			}
			
		}else if(type==AxlePosition.LENGTH_LEFT)
		{
			Vector3f axis = Vector3f.UNIT_X.mult(-1f);
			float dist =  axis.dot(position);
			
			dist-= CONTROL_DISTANCE;
			if( controllable.getInterpreter().isBearing())
			{
				dist -=  controllable.getInterpreter().getBearingLength();
			}
			{	
				 float curHeight =  controllable.getInterpreter().getJointLength(true);
				 float newHeight = dist;
				/*
				 float delta = curHeight - newHeight;
				 newHeight += delta/2f;*/
				
				 if(ActionToolSettings.getInstance().isRestrictMovement())
				 {
					 float grid = ActionToolSettings.getInstance().getGridUnits().getValue().dot(axis);;
					 newHeight/= grid;
						
						//Vector3f orig = new Vector3f(from);
					 newHeight =  Math.round(newHeight);	
						newHeight*=grid;
						
					
				 }
				 if(newHeight<0f)
					 newHeight = 0f;
				
			/*	 delta = curHeight - newHeight;
				 delta/=-2f;//this is to account for the part that is moving and the part that is growing.
				 Vector3f dirLocal = new Vector3f(axis);
				 controllable.getParent().getWorldRotation().multLocal(dirLocal);
				 dirLocal.multLocal(delta);*/
				 controllable.getInterpreter().setJointLength(newHeight,true); //increase the height by less, to account for the amount you are also moving the capsule.
				//
				 GolemsValidator.getInstance().makeValid(controllable.getPropertyStore());
				 
			//	 controllable.getInterpreter().getLocalTranslation().addLocal(dirLocal);
				
				 // controllable.getParent().updateWorldData();
				 controllable.getInterpreter().refresh();
				 controllable.getParent().updateWorldData();
				 controllable.updateModel();
				 this.updatePosition();
			}
			
		}else if(type==AxlePosition.RADIAL_LEFT)
		{
			Vector3f axis = Vector3f.UNIT_Y;
			float dist =  axis.dot(position);
			
			dist-= CONTROL_DISTANCE;

			if(dist> 0)
			{	
				 controllable.getInterpreter().setJointRadius(dist,true);
				 GolemsValidator.getInstance().makeValid(controllable.getPropertyStore());
				 controllable.getInterpreter().refresh();
			}
			this.updatePosition();
		}else 	if(type==AxlePosition.RADIAL_RIGHT)
		{
			Vector3f axis = Vector3f.UNIT_Z.mult(-1f);
			float dist =  axis.dot(position);
			
			dist-= CONTROL_DISTANCE;

			if(dist> 0)
			{	
				 controllable.getInterpreter().setJointRadius(dist,false);
				 GolemsValidator.getInstance().makeValid(controllable.getPropertyStore());
				 controllable.getInterpreter().refresh();
			}
			this.updatePosition();
		}
		for (ControlPoint<?> control:super.siblings)
		{
			control.updatePosition();
		}
/*		axle.updateModel();
		Model joint;
		Model coJoint;
		
		if (controlLeft)
		{
			joint = axle.getLeftJoint();
			coJoint = axle.getRightJoint();
		}else
		{
			coJoint = axle.getLeftJoint();
			joint = axle.getRightJoint();
		}
		
		if (type == LENGTH)
		{

			Vector3f direction = _unit.set(position);
			direction.subtractLocal(joint.getLocalTranslation());

			Vector3f normalToMovement = _slideVector.set(scaleAxis);//this should be the direction that the hinge lies along, in hinge local coordinates
			//joint.getLocalRotation().multLocal(normalToMovement);
		
			float amount = position.dot(normalToMovement);
			if (amount < 0)
				amount *= -1;
			
			amount -= CONTROL_DISTANCE;
			
		//	amount *= 2;
			
			if (amount<MIN_THICKNESS)
				amount = MIN_THICKNESS;
			
			Vector3f axisOfMovement = new Vector3f(1,0,0);
			if (controlLeft)
				axisOfMovement.multLocal(-1);
			

			float maximumWidth =Math.abs( joint.getLocalTranslation().dot(axisOfMovement) -coJoint.getLocalTranslation().dot(axisOfMovement));
			maximumWidth -= MIN_DISTANCE;//axle.getGeometry().getLocalScale().z/2f;
			
			//if (amount>maximumWidth)
			//	amount = maximumWidth;
			
			
			joint.getLocalScale().z = amount;
			
				
			joint.getLocalTranslation().x = -Math.signum(this.controllable.getNaturalTranslation(controlLeft).x)* joint.getLocalScale().z/2f;;
		
			//joint.getLocalScale().z = this.controllable.getNaturalScale(controlLeft).z;
			//coJoint.getLocalScale().z = this.controllable.getNaturalScale(!controlLeft).z;
			
			axle.updateModel();


			
			
		}
		else
		{//Scale the joint
		
			Vector3f direction = _unit.set(position);
			direction.subtractLocal(joint.getLocalTranslation());

			Vector3f normalToMovement = _slideVector.set(slideAxis);//this should be the direction that the hinge lies along, in hinge local coordinates
			//joint.getLocalRotation().multLocal(normalToMovement);
		
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
			

			float maximumWidth =Math.abs( joint.getLocalTranslation().dot(axisOfMovement) -coJoint.getLocalTranslation().dot(axisOfMovement));
			maximumWidth -= MIN_DISTANCE;//axle.getGeometry().getLocalScale().z/2f;
			
		//	if (amount>maximumWidth)
		//		amount = maximumWidth;
			
			
			
			joint.getLocalScale().x = amount*this.controllable.getNaturalScale(controlLeft).x;
			joint.getLocalScale().y = amount*this.controllable.getNaturalScale(controlLeft).y;
		
			
			//axle.getConnection().getLocalScale().x = amount;
			//axle.getConnection().getLocalScale().y = amount;	
			
			
			coJoint.getLocalScale().x = amount*this.controllable.getNaturalScale(!controlLeft).x;
			coJoint.getLocalScale().y = amount*this.controllable.getNaturalScale(!controlLeft).y;
			axle.updateModel();

			
		}*/
		
		/*axle.refresh();
			for (ControlPoint<?> control:super.siblings)
			{
				control.updatePosition();
			}*/
		
	}
	
	private static String[] keys = new String[]{AxleInterpreter.LEFT_JOINT_LENGTH,AxleInterpreter.LEFT_JOINT_RADIUS,AxleInterpreter.LOCALTRANSLATION,AxleInterpreter.RIGHT_JOINT_LENGTH,AxleInterpreter.RIGHT_JOINT_RADIUS,AxleInterpreter.BEARING_JOINT_LENGTH,AxleInterpreter.BEARING_JOINT_RADIUS};

	@Override
	protected String[] getKeys() {
		return keys;
	}
	
	private static final float CONTROL_DISTANCE=1f;
	private static final Vector3f control = new Vector3f(CONTROL_DISTANCE,CONTROL_DISTANCE,CONTROL_DISTANCE);
	@Override
	protected Vector3f getControlDistance() {
		return control;
	}
	@Override
	public void updatePosition() {
	
		refresh();
	}


	

	public interface ControllableJoint extends ControllableModel
	{
		public void refresh();
		public AxleInterpreter getInterpreter();
	}


	@Override
	public void refresh() {
		
		if(type == AxlePosition.LENGTH_RIGHT)
		{
			controllable.getParent().updateWorldData();
			Vector3f axis = new Vector3f( Vector3f.UNIT_X);
			float dist =  controllable.getInterpreter().getJointLength(false);
			dist+= CONTROL_DISTANCE;
			//dist += controllable.getInterpreter().getJointRadius(false);
		
			axis.multLocal(dist);
			
			this.getModel().getLocalTranslation().set(axis);
			this.getModel().updateWorldData();
		}else if(type == AxlePosition.LENGTH_LEFT)
		{
			controllable.getParent().updateWorldData();
			Vector3f axis = new Vector3f( Vector3f.UNIT_X).multLocal(-1f);
			float dist =  controllable.getInterpreter().getJointLength(true);
			dist+= CONTROL_DISTANCE;
		//	dist += controllable.getInterpreter().getJointRadius(true);
			float offset = 0f;
			if( controllable.getInterpreter().isBearing())
			{
				offset =  controllable.getInterpreter().getBearingLength();
			}
			axis.multLocal(dist);
			axis.x -= offset;
			this.getModel().getLocalTranslation().set(axis);
			this.getModel().updateWorldData();
		}else if (type == AxlePosition.RADIAL_LEFT)
		{
			Vector3f axis = new Vector3f( Vector3f.UNIT_Y);
			float dist =  controllable.getInterpreter().getJointRadius(true);
			dist+= CONTROL_DISTANCE;
			float offset = controllable.getInterpreter().getJointLength(true)/2f;
			axis.multLocal(dist);
			axis.x -= offset;
			this.getModel().getLocalTranslation().set(axis);
		}else if (type == AxlePosition.RADIAL_RIGHT)
		{
			Vector3f axis = new Vector3f( Vector3f.UNIT_Z.mult(-1f));
			float dist =  controllable.getInterpreter().getJointRadius(false);
			dist+= CONTROL_DISTANCE;	
			float offset = controllable.getInterpreter().getJointLength(false)/2f;
			axis.multLocal(dist);	
			axis.x += offset;
			this.getModel().getLocalTranslation().set(axis);
		}
	}

	@Override
	public void setPropertyStore(PropertyStore store) {

	}
	@Override
	public Action<?> getAction(Type type) throws ActionTypeException {

		if (type == Action.MOVE_RESTRICTED)		
			return new MoveRestrictedInfo();
		
		return super.getAction(type);
	}
	
	private final Vector3f cacheUnit = new Vector3f();
	private Quaternion compareRotation = null;
	private Vector3f localDirection = null;
	private Quaternion cacheRotation = null;
	
	protected class MoveRestrictedInfo extends MoveRestrictedInformation
	{
		@Override
		public Actionable getControlled() {
			return AxleMVCControlPoint.this;
		}
		
		@Override
		public Vector3f getRestrictedPosition(Vector3f from) {
			//Get the closest grid node to the provided position;
			if (controllable == null)
				return from;
			
			if (type == AxlePosition.RADIAL_LEFT || type == AxlePosition.RADIAL_RIGHT)
			{
			
				controllable.getParent().updateWorldData();
				controllable.updateWorldData();

				from = controllable.getParent().worldToLocal(from,from);
		
				cacheUnit.set(getControlDistance());
				from.subtractLocal(cacheUnit.divideLocal(2f));//Have to account for control distance...
				
				cacheUnit.set(ActionToolSettings.getInstance().getGridUnits().getValue()).divideLocal(2f);

				from.divideLocal(cacheUnit);
				from.x = Math.round(from.x);
				from.y = Math.round(from.y);
				from.z = Math.round(from.z);
				from.multLocal(cacheUnit);			
				
				cacheUnit.set(getControlDistance());
				from.addLocal(cacheUnit.divideLocal(2f));//Have to account for control distance...

				from = controllable.getParent().localToWorld(from,from);
				return from;
			}else
			{
				/*Vector3f axis = new Vector3f( type == CapsulePosition.LENGTH_RIGHT? Vector3f.UNIT_X:Vector3f.UNIT_X.mult(-1f));
				controllable.getParent().updateWorldData();
				controllable.updateWorldData();
				controllable.getParent().updateWorldData();
				from = controllable.getParent().worldToLocal(from,from);
					

			//	Vector3f bottom = controllable.getParent().getLocalTranslation().subtract(axis.mult(controllable.getInterpreter().getHeight())).multLocal(0.5f));
			//	controllable.getParent().getWorldRotation().multLocal(axis);
				from.subtractLocal(axis.divideLocal(2f));

			
				from.subtractLocal(getControlDistance());//Have to account for control distance...
						
				from.divideLocal(ActionToolSettings.getInstance().getGridUnits().getValue());
				//Vector3f orig = new Vector3f(from);
				from.x = Math.round(from.x);
				from.y = Math.round(from.y);
				from.z = Math.round(from.z);
			//	orig.subtractLocal(orig)
				from.multLocal(ActionToolSettings.getInstance().getGridUnits().getValue());			
				
			
				
				from.addLocal(getControlDistance());//Have to account for control distance...

				from.addLocal(axis);
				System.out.println(from);
				from = controllable.getParent().localToWorld(from,from);*/
				return from;
			}			
		}	
			
		
	}
	
}
