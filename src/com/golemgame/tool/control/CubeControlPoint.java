package com.golemgame.tool.control;


import com.golemgame.save.direct.mvc.golems.BoxInterpreter;
import com.golemgame.tool.ActionToolSettings;
import com.golemgame.tool.CameraTool;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.action.information.MoveRestrictedInformation;
import com.golemgame.tool.action.information.Orientation;
import com.golemgame.tool.action.information.OrientationInformation;
import com.jme.bounding.BoundingSphere;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.SceneElement;
import com.jme.scene.SharedMesh;

public class CubeControlPoint extends ControlPoint<CubeControlPoint.ControllableBox> {

	private Geometry controlSphere;
	private Geometry facadeSphere;

	
	private OrientationInformation orientation;

	
	public static enum CubePoint
	{
		LEFT(new Vector3f(1,0,0),true), RIGHT(new Vector3f(1,0,0)), TOP(new Vector3f(0,1,0)), BOTTOM(new Vector3f(0,1,0),true), FORWARD(new Vector3f(0,0,1)), BACKWARD(new Vector3f(0,0,1),true);
		public Vector3f direction;
		public Vector3f inverse;
		public int flip;
		
		private CubePoint(Vector3f direction)
		{
			this(direction,false);
		}
		
		private CubePoint(Vector3f direction, boolean flip)
		{
			this.flip=flip?-1:1;
			this.direction = direction;
			this.inverse = new Vector3f(1,1,1).subtractLocal(Math.abs(direction.x), Math.abs(direction.y), Math.abs(direction.z));
			
		}
		
	}
	
	public static CubePoint LEFT = CubePoint.LEFT;
	public static CubePoint RIGHT = CubePoint.RIGHT;
	public static CubePoint TOP = CubePoint.TOP;
	public static CubePoint BOTTOM = CubePoint.BOTTOM;
	public static CubePoint FORWARD = CubePoint.FORWARD;
	public static CubePoint BACKWARD = CubePoint.BACKWARD;
	
	private CubePoint point;
	

	public CubeControlPoint(CubePoint point) 
	{
		super();
		this.point = point;

		controlSphere = new SharedMesh("ControlSphereCollision",baseCollisionSphere);
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
	
	final float MIN_THICKNESS = 0.1f;
	private static final float CONTROL_DISTANCE=1f;
	
	private static final Vector3f control = new Vector3f(CONTROL_DISTANCE,CONTROL_DISTANCE,CONTROL_DISTANCE);

	@Override
	protected Vector3f getControlDistance() {
		return control;
	}



	@Override
	protected void move (ControllableBox toControl, Vector3f moveTo)
	{
		if (toControl == null )
			return;
		toControl.signalMoved();

		Vector3f scale = toControl.getLocalScale();
		Vector3f old = new Vector3f(scale);

		Vector3f pos = moveTo;

		float stretch;

		Vector3f bottom = toControl.getLocalTranslation().subtract(scale.mult(point.direction).multLocal(0.5f*point.flip));
		pos.subtractLocal(bottom);
		stretch= pos.dot(point.direction)*point.flip - CONTROL_DISTANCE;//*point.flip)  );

		if (stretch < MIN_THICKNESS/2f)
			stretch = MIN_THICKNESS/2f;
		
		 Vector3f mod = new Vector3f();
		 mod.set(point.direction);
	 	 mod.multLocal(stretch);			

	 	 scale.multLocal(point.inverse).addLocal(mod);//.add(0f,+CONTROL_DISTANCE/2f,0f));			 
	 	toControl.getLocalTranslation().addLocal(scale.subtract(old).divideLocal(2f).multLocal(point.flip));
		 toControl.updateWorldData();
		 toControl.updateModel();
		 if (super.siblings != null)
		 {	
			for (ControlPoint<?> control:siblings)
				control.updatePosition();
		 }else
			 this.updatePosition();
		 
	}

	

	@Override
	public void updatePosition()
	{			
			
			if (controllable == null)
				return;

			Vector3f control = new Vector3f(CONTROL_DISTANCE,CONTROL_DISTANCE,CONTROL_DISTANCE).multLocal(point.direction);
			
			this.getModel().getLocalTranslation().set((controllable.getLocalScale().mult(point.direction).multLocal(0.5f).addLocal(control)).multLocal(point.flip).add(controllable.getLocalTranslation()));
			this.getModel().updateWorldData();
		
	}
	
	private static String[] keys = new String[]{BoxInterpreter.BOX_EXTENT,BoxInterpreter.LOCALTRANSLATION};

	@Override
	protected String[] getKeys() {
		return keys;
	}

	@Override
	public Action<?> getAction(Type type) throws ActionTypeException {
		if (type == Action.ORIENTATION)
			return orientation;
		else if (type == Action.MOVE_RESTRICTED)
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
			return CubeControlPoint.this;
		}
		
		@Override
		public Vector3f getRestrictedPosition(Vector3f from) {
			//Get the closest grid node to the provided position;
			if (controllable == null)
				return from;

			from = controllable.getRelativeFromWorld(from, from); 

	
			Vector3f scale = controllable.getLocalScale();
			
			Vector3f bottom = controllable.getLocalTranslation().subtract(scale.mult(point.direction).multLocal(0.5f*point.flip));
			from.subtractLocal(bottom);

		//	Quaternion rotation = 	controllable.getWorldRotation();
		//	Vector3f origin = controllable.getWorldTranslation() ;
			//from.subtractLocal(origin);	
			
			cacheUnit.set(getControlDistance());
			//rotation.multLocal(cacheUnit);
			from.subtractLocal(cacheUnit);//Have to account for control distance...
			
			cacheUnit.set(ActionToolSettings.getInstance().getGridUnits().getValue());
			
			//rotation.multLocal(cacheUnit);
			
			from.divideLocal(cacheUnit);
			from.x = Math.round(from.x);
			from.y = Math.round(from.y);
			from.z = Math.round(from.z);
			from.multLocal(cacheUnit);			
			
			cacheUnit.set(getControlDistance());
			//rotation.multLocal(cacheUnit);
			from.addLocal(cacheUnit);//Have to account for control distance...
			
			//from.addLocal(origin);
			
			from.addLocal(bottom);
			from = controllable.getWorldFromRelative(from, from);
			return from;
			
		}	
	}

	private class OrientationInfo extends ControlPoint.OrientationInfoImpl
	{
		Vector3f direction = new Vector3f();
		public OrientationInfo()
		{
		//	orientation = new Orientation(Tool.getCameraDirection().cross(point.direction).cross(point.direction),point.direction);
			orientation = new Orientation(direction.set(CameraTool.getCameraDirection()).crossLocal(point.direction).crossLocal(point.direction),point.direction);
			super.useAxis = true;
		}

		@Override
		public Vector3f getAxis() {
			
			return point.direction;
		}

	}

	

	public static interface ControllableBox extends ControllableModel
	{
		public Vector3f getLocalScale();
		public Vector3f getLocalTranslation();
		public Vector3f getWorldTranslation();
		public Quaternion getWorldRotation();
		public void updateWorldData();
	/*	public Vector3f getXUnit();
		public Vector3f getYUnit();
		public Vector3f getZUnit();*/
		public void signalMoved();
	}

}
