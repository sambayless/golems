package com.golemgame.tool.control;


import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.BallAndSocketInterpreter;
import com.golemgame.mvc.golems.validate.GolemsValidator;
import com.golemgame.tool.ActionToolSettings;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.action.information.MoveRestrictedInformation;
import com.jme.bounding.BoundingSphere;
import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.SceneElement;
import com.jme.scene.SharedMesh;
import com.jme.scene.shape.Sphere;

public class BallSocketControlPoint extends MVCControlPoint<BallSocketControlPoint.ControllableJoint> {
	
	private Geometry controlSphere;
	private Geometry facadeSphere;

	
	public static final float MIN_DISTANCE = 0.1f;




	public static enum BallSocketPosition
	{
		LENGTH(), BALL_RADIAL(),JOINT_RADIAL();		
	}
	
	public static final BallSocketPosition LENGTH = BallSocketPosition.LENGTH;
	public static final BallSocketPosition BALL_RADIAL = BallSocketPosition.BALL_RADIAL;
	public static final BallSocketPosition JOINT_RADIAL = BallSocketPosition.JOINT_RADIAL;
	
	final float MIN_THICKNESS = 0.2f;
	
	private BallSocketPosition type;

	public BallSocketControlPoint(BallSocketPosition type) {
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
		boolean universal = interpreter.isUniversalJoint();
		if(type==BallSocketPosition.LENGTH)
		{
			Vector3f axis = Vector3f.UNIT_X;
			float dist =  axis.dot(position);
			
			dist-= CONTROL_DISTANCE;
			dist -= interpreter.getRightRadius();
			if(!universal)
				dist -= interpreter.getLeftRadius()*2f;
			if(dist<0f)
				dist = 0f;
			{	
				
				 if(ActionToolSettings.getInstance().isRestrictMovement())
				 {
					 float grid = ActionToolSettings.getInstance().getGridUnits().getValue().dot(axis);;
					 dist/= grid;
						
						//Vector3f orig = new Vector3f(from);
					 dist =  Math.round(dist);	
					 dist*=grid;
						
					
				 }
				
				interpreter.setLeftLength(dist);
				 GolemsValidator.getInstance().makeValid(controllable.getPropertyStore());
				interpreter.refresh();
			}
			this.updatePosition();
		}else 	if(type==BallSocketPosition.BALL_RADIAL)
		{
			Vector3f axis = Vector3f.UNIT_Y;
			float dist =  axis.dot(position);
			
			dist-= CONTROL_DISTANCE;

			if(dist> 0)
			{	
				interpreter.setRightRadius(dist);
				 GolemsValidator.getInstance().makeValid(controllable.getPropertyStore());
				interpreter.refresh();
			}
			this.updatePosition();
		}else 	if(type==BallSocketPosition.JOINT_RADIAL)
		{
			Vector3f axis = Vector3f.UNIT_Y;
			float dist =  axis.dot(position);
			
			dist-= CONTROL_DISTANCE;

			if(dist> 0)
			{	
				interpreter.setLeftRadius(dist);
				 GolemsValidator.getInstance().makeValid(controllable.getPropertyStore());
				interpreter.refresh();
			}
			this.updatePosition();
		}
		for (ControlPoint<?> control:super.siblings)
		{
			control.updatePosition();
		}

		
	}
	
	private static String[] keys = new String[]{BallAndSocketInterpreter.LEFT_JOINT_LENGTH,BallAndSocketInterpreter.RIGHT_JOINT_RADIUS,BallAndSocketInterpreter.LEFT_JOINT_RADIUS};

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
	}


	@Override
	public void refresh() {
		boolean universal = interpreter.isUniversalJoint();
		if(type == BallSocketPosition.LENGTH)
		{
			Vector3f axis = new Vector3f( Vector3f.UNIT_X);
			float dist = interpreter.getLeftLength();
			dist+= CONTROL_DISTANCE;
			dist += interpreter.getRightRadius();
			if(!universal)
				dist += interpreter.getLeftRadius()*2f ;
			axis.multLocal(dist);
			this.getModel().getLocalTranslation().set(axis);
		}else if (type == BallSocketPosition.BALL_RADIAL)
		{
			Vector3f axis = new Vector3f( Vector3f.UNIT_Y);
			float dist = interpreter.getRightRadius();
			dist+= CONTROL_DISTANCE;
			axis.multLocal(dist);
			this.getModel().getLocalTranslation().set(axis);
		}else if (type == BallSocketPosition.JOINT_RADIAL)
		{
			Vector3f axis = new Vector3f( Vector3f.UNIT_Y);
			float dist = interpreter.getLeftRadius();
			dist+= CONTROL_DISTANCE;
			float offset = interpreter.getLeftLength()/2f;
			offset += interpreter.getRightRadius();
			if(!universal)
				offset += interpreter.getLeftRadius();
			axis.multLocal(dist);
			axis.x+= offset;
			this.getModel().getLocalTranslation().set(axis);
		}
	}
	private BallAndSocketInterpreter interpreter =null;
	@Override
	public void setPropertyStore(PropertyStore store) {
		if(store!=null)
			interpreter = new BallAndSocketInterpreter(store);
		else
			interpreter = new BallAndSocketInterpreter();
	}
	
	

	@Override
	public Action getAction(Type type) throws ActionTypeException {

		if (type == Action.MOVE_RESTRICTED)
			return moveRestricted;
		else
			return super.getAction(type);
	}
	
	private final MoveRestrictedInfo moveRestricted = new MoveRestrictedInfo();
	private final Vector3f cacheUnit = new Vector3f();
	protected class MoveRestrictedInfo extends MoveRestrictedInformation
	{
		@Override
		public Actionable getControlled() {
			return BallSocketControlPoint.this;
		}
		
		@Override
		public Vector3f getRestrictedPosition(Vector3f from) {
			//Get the closest grid node to the provided position;
			if (controllable == null)
				return from;

			if(type == BallSocketPosition.BALL_RADIAL || type ==  BallSocketPosition.JOINT_RADIAL)
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
			}else{
				return from;
				/*Vector3f pos = controllable.getRelativeFromWorld(from, new Vector3f());
			//	System.out.println(pos);
				//pos.subtractLocal(ActionToolSettings.getInstance().getGridOrigin().getValue());
				pos.divideLocal(ActionToolSettings.getInstance().getGridUnits().getValue());
				pos.x = Math.round(pos.x);
				pos.y = Math.round(pos.y);
				pos.z = Math.round(pos.z);
				pos.multLocal(ActionToolSettings.getInstance().getGridUnits().getValue());
				return controllable.getWorldFromRelative(pos, from);*/
			}
		}	
	}
	
}
