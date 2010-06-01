package com.golemgame.tool.control;


import com.golemgame.mvc.golems.PyramidInterpreter;
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

public class PyramidControlPoint extends ControlPoint<PyramidControlPoint.ControllablePyramid> {
	private Geometry controlSphere;
	private Geometry facadeSphere;
	private OrientationInformation orientation;
	private PyramidPoint point;
	
	public static enum PyramidPoint
	{
		PEAK(new Vector3f(0,1,0)),WIDTH(new Vector3f(1,0,0)), BASE(new Vector3f(0,0,1)),BOTTOM(new Vector3f(0,1,0));
		
		public Vector3f direction;
		public Vector3f inverse;
		
		private PyramidPoint(Vector3f direction)
		{
			this.direction = direction;
			this.inverse = new Vector3f(1,1,1).subtractLocal(direction);
		}


		public Vector3f getDirection() {
			return direction;
		}
	}
	
	public static PyramidPoint PEAK = PyramidPoint.PEAK;
	public static PyramidPoint WIDTH = PyramidPoint.WIDTH;
	public static PyramidPoint BASE = PyramidPoint.BASE;
	public static PyramidPoint BOTTOM = PyramidPoint.BOTTOM;
	

	
	public PyramidControlPoint(PyramidPoint point) 
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

	private Quaternion cacheRotation = null;
	private Quaternion compareRotation = null;
	private Vector3f localDirection = null;
	
	@Override
	protected void move(ControllablePyramid toControl, Vector3f moveTo) {
		if (toControl == null )
			return;
		
		
		

			Vector3f scale =  new Vector3f(toControl.getLocalScale());
			controllable.getScaleOffset().inverse().multLocal(scale);
			Vector3f old = new Vector3f(scale);

			Vector3f pos = new Vector3f( moveTo);

			Vector3f direction = new Vector3f( point.direction);
			//toControl.getLocalRotation().multLocal(direction);
			float stretch;
			if (point == PEAK)
			{
				Vector3f bottom = toControl.getLocalTranslation().subtract(scale.mult(direction).multLocal(0.5f));
				pos.subtractLocal(bottom);
				stretch= (pos.dot(direction)  - CONTROL_DISTANCE);
				
				//stretch= pos.dot(toControl.getLocalRotation().mult(direction)) - CONTROL_DISTANCE;
			
			}
			else if (point ==BOTTOM)
			{
				
				Vector3f top = toControl.getLocalTranslation().add(scale.mult(direction).multLocal(0.5f));
				pos.set(top.subtract(pos));
				stretch= (pos.dot(direction)  - CONTROL_DISTANCE);
			
				
			}
			else
				stretch= pos.dot(direction) - CONTROL_DISTANCE;
			
			//System.out.println(stretch + " " + moveTo + toControl.getLocalTranslation());
				
			if (stretch < MIN_THICKNESS/2f)
				stretch = MIN_THICKNESS/2f;
			
			 Vector3f mod = new Vector3f();
			 mod.set(direction);
		 	 mod.multLocal(stretch);			

			 //the local translation starts at 0,0,0 when the scale is 1, 1,1
			 
			 if (point == PEAK )
			 {
		
		 		 
				  mod.set(point.direction);
			 	 mod.multLocal(stretch);
			 	 scale.multLocal(point.inverse).addLocal(mod);//.add(0f,+CONTROL_DISTANCE/2f,0f));		
			 	
				 toControl.getLocalTranslation().addLocal(toControl.getLocalRotation().mult(scale.subtract(old)).divideLocal(2f).multLocal(1));
				// mod.multLocal(2f);
				// scale.multLocal(point.inverse).addLocal(mod);//.add(0f,+CONTROL_DISTANCE/2f,0f));			 
				 //toControl.getLocalTranslation().addLocal(scale.subtract(old).divideLocal(2f));
			 }else if (point == BOTTOM)
			 {
				// mod.multLocal(2f);
				 scale.multLocal(point.inverse).addLocal(mod);//.add(0f,CONTROL_DISTANCE/2f,0f));			 
				 toControl.getLocalTranslation().addLocal(scale.subtract(old).divideLocal(-2f));

			 }else
			 {
				 scale.multLocal(point.inverse).addLocal(mod.multLocal(2f));			 
			// toControl.getLocalTranslation().addLocal(scale.subtract(old).divideLocal(2f));

			 }
			 controllable.getScaleOffset().multLocal(scale);
			 toControl.getLocalScale().set(scale);
			 controllable.updateModel();
			 
			 if (super.siblings != null)
			 {	
				for (ControlPoint control:siblings)
					control.updatePosition();
			 }else
				 this.updatePosition();
	}


	@Override
	public void updatePosition()
	{			
			
			if (controllable == null)
				return;
				
			if(point == PEAK)
			{
				if (!controllable.getLocalRotation().equals(compareRotation))
				{
					compareRotation = new Quaternion().set(controllable.getLocalRotation());
					cacheRotation = controllable.getLocalRotation().inverse();
					localDirection = compareRotation.mult(point.direction);
					orientation.updateOrientation();
				}
				Vector3f control = new Vector3f(CONTROL_DISTANCE,CONTROL_DISTANCE,CONTROL_DISTANCE).multLocal(localDirection);
				int flip = 1;
				this.getModel().getLocalTranslation().set((controllable.getLocalRotation().mult(controllable.getLocalScale().mult(point.direction)).multLocal(0.5f).addLocal(control)).multLocal(flip).add(controllable.getLocalTranslation()));
				
				
				return;
			}
			
			
			Vector3f control = new Vector3f(CONTROL_DISTANCE,CONTROL_DISTANCE,CONTROL_DISTANCE).multLocal(point.direction);
			Vector3f scale = new Vector3f(controllable.getLocalScale());
			controllable.getScaleOffset().inverse().multLocal(scale);
			if (point == PEAK)
			{
				this.getModel().getLocalTranslation().set(scale.mult(point.direction).multLocal(0.5f).addLocal(control.multLocal(1f)).add(controllable.getLocalTranslation()));
			}else if (point == BOTTOM)
			{
				this.getModel().getLocalTranslation().set(scale.mult(point.direction).multLocal(-0.5f).addLocal(control.multLocal(-1f)).add(controllable.getLocalTranslation()));
					//toControl.getLocalTranslation().add(toControl.getLocalScale().mult(point.direction).multLocal(-0.5f).addLocal(control.multLocal(-1f))));
			}else
			{
				this.getModel().getLocalTranslation().set((scale.mult(point.direction).multLocal(0.5f).addLocal(control))).addLocal(controllable.getLocalTranslation());

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

	private static String[] keys = new String[]{PyramidInterpreter.PYRAMID_SCALE,PyramidInterpreter.LOCALTRANSLATION};

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
			return PyramidControlPoint.this;
		}
		
		@Override
		public Vector3f getRestrictedPosition(Vector3f from) {
			//Get the closest grid node to the provided position;
			if (controllable == null)
				return from;
			
			if (point == PEAK)
			{
				controllable.getParent().updateWorldData();
				controllable.updateWorldData();

				from = controllable.getParent().worldToLocal(from,from);
				
				Vector3f scale = controllable.getLocalScale();
				if (!controllable.getLocalRotation().equals(compareRotation))
				{
					compareRotation = new Quaternion().set(controllable.getLocalRotation());
					cacheRotation = controllable.getLocalRotation().inverse();
					localDirection = compareRotation.mult(point.direction);
					orientation.updateOrientation();
				}		
	
				int flip = 1;
				Vector3f bottom = controllable.getLocalTranslation().subtract(compareRotation.mult(scale.mult(point.direction)).multLocal(0.5f*flip));
				
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
			}else if (point == BOTTOM)
			{
				
				controllable.getParent().updateWorldData();
				controllable.updateWorldData();

				from = controllable.getParent().worldToLocal(from,from);
				
				Vector3f scale = controllable.getLocalScale();
				if (!controllable.getLocalRotation().equals(compareRotation))
				{
					compareRotation = new Quaternion().set(controllable.getLocalRotation());
					cacheRotation = controllable.getLocalRotation().inverse();
					localDirection = compareRotation.mult(point.direction);
					orientation.updateOrientation();
				}		
	
				int flip = 1;
				Vector3f bottom = controllable.getLocalTranslation().subtract(compareRotation.mult(scale.mult(point.direction)).multLocal(0.5f*flip));
				
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
			}else
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
			}			
		}	
	}
	

	private class OrientationInfo extends ControlPoint.OrientationInfoImpl
	{
		public OrientationInfo()
		{
			orientation = new Orientation(CameraTool.getCameraDirection().cross(point.direction).cross(point.direction),point.direction);
			super.useAxis=true;
		}

		@Override
		public Vector3f getAxis() {
			
			return point.direction;
		}



	};
	public static interface ControllablePyramid extends ControllableModel
	{
		public Quaternion getScaleOffset();
		public Vector3f getLocalScale();
		public Vector3f getLocalTranslation();
		public Quaternion getLocalRotation();
		public Vector3f getWorldTranslation();
		public Quaternion getWorldRotation();
	//	public PyramidInterpreter getInterpreter();
		public void updateWorldData();
	}
}
