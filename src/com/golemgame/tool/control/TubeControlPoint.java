package com.golemgame.tool.control;

import com.golemgame.mvc.golems.TubeInterpreter;
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
import com.jme.scene.Spatial;

public class TubeControlPoint extends ControlPoint<TubeControlPoint.ControllableTube> {
	private Spatial controlSphere;
	private Geometry facadeSphere;
	//boolean axis;
	//private Vector3f direction = new Vector3f(1,0,0);//the axis along which this node can move
	
	public static enum TubePoint
	{
	
		LEFT(new Vector3f(0,1,0),true), RIGHT(new Vector3f(0,1,0)), RADIUS(new Vector3f(1,0,0)), INNER_RADIUS(new Vector3f(1,0,0),true);
		public Vector3f direction;
		public Vector3f inverse;
		public float flip;
		
		private TubePoint(Vector3f direction)
		{
			this(direction,false);
		}
		
		private TubePoint(Vector3f direction, boolean flip)
		{
			this.flip=flip?-1:1;
			this.direction = direction;
			this.inverse = new Vector3f(1,1,1).subtractLocal(Math.abs(direction.x), Math.abs(direction.y), Math.abs(direction.z));
			
		}
	}
	
	public static TubePoint LEFT = TubePoint.LEFT;
	public static TubePoint RIGHT = TubePoint.RIGHT;
	public static TubePoint RADIUS = TubePoint.RADIUS;
	public static TubePoint INNER_RADIUS = TubePoint.INNER_RADIUS;

	private final TubePoint point;
	private OrientationInformation orientation;
	public TubeControlPoint(TubePoint point) 
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
		this.orientation = new OrientationInfo();

	}
	
	final float MIN_THICKNESS = 0.1f;
	private static final float CONTROL_DISTANCE=1f;
	
	private static final Vector3f control = new Vector3f(CONTROL_DISTANCE,CONTROL_DISTANCE,CONTROL_DISTANCE);
	@Override
	protected Vector3f getControlDistance() {
		return control;
		
	}


	
	
	@Override
	protected void move(ControllableTube toControl, Vector3f position) {
		if (toControl == null )
			return;


		
		 Vector3f localDirection = toControl.getLocalRotation().mult(point.direction);
		Vector3f scale = toControl.getLocalScale();
		Vector3f old = new Vector3f(scale);

		Vector3f pos = new Vector3f( position);

		float stretch;

		Vector3f bottom = toControl.getLocalTranslation().subtract(toControl.getLocalRotation().mult(scale.mult(point.direction)).multLocal(0.5f*point.flip));
		
	
		if (point != RADIUS)
			pos.subtractLocal(bottom);
		
		//stretch= Math.abs((pos.dot(point.direction)- CONTROL_DISTANCE*point.flip)  );

	
		stretch= pos.dot(localDirection)*point.flip - CONTROL_DISTANCE;
		
		if (stretch < MIN_THICKNESS/2f)
			stretch = MIN_THICKNESS/2f;
		
		 Vector3f mod = new Vector3f();
			
	 	 
	 	 if (point==RADIUS)
	 	 {
	 	
	 		toControl.setOuterRadius(stretch);
	 	 }else if (point==INNER_RADIUS)
	 	 {
		 	//	stretch = controllable.getValidatedScale(stretch,INNER_RADIUS);
	 		 	
		 		toControl.setInnerRadius(stretch);
		 }else
		 {
			 
			 float oldHeight = toControl.getHeight();
			 toControl.setHeight(stretch);
		 
			 mod.set(point.direction);
			  
			mod.multLocal((stretch -oldHeight)*point.flip*.5f)  ;
			toControl.getWorldRotation().multLocal(mod);
			toControl.setLocalTranslation(toControl.getWorldTranslation().add(mod));
	 	 }
	 	 
	 	 toControl.updateModel();
		 
	
		 {	 
			 if (super.siblings != null)
			 {	
				for (ControlPoint control:siblings)
					control.updatePosition();
			 }else
				 this.updatePosition();
		 }
	}
	
	

	
	@Override
	public void updatePosition()
	{			
		if (controllable == null)
			return;
	
		
		 Vector3f localDirection = controllable.getLocalRotation().mult(point.direction);
			Vector3f control = new Vector3f(CONTROL_DISTANCE,CONTROL_DISTANCE,CONTROL_DISTANCE).multLocal(localDirection);

		if (point == RADIUS)
		{
			//	this.getModel().getLocalTranslation().set((controllable.getLocalRotation().mult(controllable.getLocalScale().mult(point.direction)).multLocal(0.5f).addLocal(control).add(controllable.getLocalTranslation())));
			Vector3f pos = this.getModel().getLocalTranslation();
			pos.set(point.direction);
			pos.multLocal(point.flip);
			pos.multLocal(controllable.getOuterRadius());
			pos.x += CONTROL_DISTANCE;
			controllable.getLocalRotation().multLocal(pos);
			
			pos.addLocal(controllable.getLocalTranslation());
			
			
		}else if (point == TubePoint.INNER_RADIUS)
		{
			
			Vector3f pos = this.getModel().getLocalTranslation();
			pos.set(point.direction);
			pos.multLocal(point.flip);
			pos.multLocal(controllable.getInnerRadius());
			pos.x -= CONTROL_DISTANCE;
			controllable.getLocalRotation().multLocal(pos);
			
			pos.addLocal(controllable.getLocalTranslation());
	
		//	this.getModel().getLocalTranslation().set((controllable.getLocalRotation().mult(controllable.getLocalScale().mult(point.direction)).multLocal(0.5f).addLocal(control)).multLocal(point.flip).add(controllable.getLocalTranslation()));

		}
		else
		{
			this.getModel().getLocalTranslation().set((controllable.getLocalRotation().mult(controllable.getLocalScale().mult(point.direction)).multLocal(0.5f).addLocal(control)).multLocal(point.flip).add(controllable.getLocalTranslation()));
			
			//this.getLocalTranslation().set(toControl.getLocalScale().mult(point.direction).multLocal(0.5f).addLocal(control.multLocal(1f)).add(toControl.getLocalTranslation()));
		}
		
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
	
	private static String[] keys = new String[]{TubeInterpreter.RADIUS_INNER,TubeInterpreter.CYL_HEIGHT, TubeInterpreter.CYL_RADIUS,TubeInterpreter.LOCALTRANSLATION};

	@Override
	protected String[] getKeys() {
		return keys;
	}

	private final MoveRestrictedInfo moveRestricted = new MoveRestrictedInfo();
	private final Vector3f cacheUnit = new Vector3f();
	protected class MoveRestrictedInfo extends MoveRestrictedInformation
	{
		@Override
		public Actionable getControlled() {
			return TubeControlPoint.this;
		}
		
		@Override
		public Vector3f getRestrictedPosition(Vector3f from) {
			//Get the closest grid node to the provided position;
			if (controllable == null)
				return from;
			
			if (point == RADIUS)
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
				
				controllable.getParent().updateWorldData();
				controllable.updateWorldData();

				from = controllable.getParent().worldToLocal(from,from);
				
				Vector3f scale = controllable.getLocalScale();
			

				Vector3f bottom = controllable.getLocalTranslation().subtract(controllable.getLocalRotation().mult(scale.mult(point.direction)).multLocal(0.5f*point.flip));
				
				from.subtractLocal(bottom);

				cacheUnit.set(getControlDistance());
				from.subtractLocal(cacheUnit);//Have to account for control distance...
				
				cacheUnit.set(ActionToolSettings.getInstance().getGridUnits().getValue());
				
				from.divideLocal(cacheUnit);
				from.x = Math.round(from.x);
				from.y = Math.round(from.y);
				from.z = Math.round(from.z);
				from.multLocal(cacheUnit);			
				
				cacheUnit.set(getControlDistance());

				from.addLocal(cacheUnit);//Have to account for control distance...

				from.addLocal(bottom);
				from = controllable.getParent().localToWorld(from,from);
				return from;
			}			
		}	
	}
	
	
	// = new OrientationInfo();

	private class OrientationInfo extends ControlPoint.OrientationInfoImpl
	{
		public OrientationInfo()
		{		

			orientation = new Orientation(CameraTool.getCameraDirection().cross(point.direction).cross(point.direction),point.direction);
			super.useAxis = true;
		}

		@Override
		public Vector3f getAxis() {
			
			return point.direction;
		}



	}


	public static interface ControllableTube extends ControllableModel
	{

		public Vector3f getLocalScale();
		
		public void setInnerRadius(float stretch);
		public float getInnerRadius();
		
		public void setOuterRadius(float stretch);
		public float getOuterRadius();
		
		public void setHeight(float stretch);
		public float getHeight();
		
		public void setLocalTranslation(Vector3f localTranslation);
	
		public Vector3f getLocalTranslation();
		public Quaternion getLocalRotation();
		public Vector3f getWorldTranslation();
		public Quaternion getWorldRotation();
		public void updateWorldData();
		
	}
	
}
