package com.golemgame.tool.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.golemgame.model.Model;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.mvc.PropertyState;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SimplePropertyState;
import com.golemgame.save.direct.mvc.golems.StructureInterpreter;
import com.golemgame.states.StateManager;
import com.golemgame.tool.ActionToolSettings;
import com.golemgame.tool.MovementSelectionEffect;
import com.golemgame.tool.SingleAxisInformation;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.MoveAction;
import com.golemgame.tool.action.RotateAction;
import com.golemgame.tool.action.SelectAction;
import com.golemgame.tool.action.SelectionEffect;
import com.golemgame.tool.action.information.ModelInformation;
import com.golemgame.tool.action.information.MoveRestrictedInformation;
import com.golemgame.tool.action.information.Orientation;
import com.golemgame.tool.action.information.OrientationInformation;
import com.golemgame.tool.action.information.PreferedCoordinateSystemInformation;
import com.golemgame.tool.action.information.PropertyStoreInformation;
import com.golemgame.tool.action.information.ProxyStateInformation;
import com.jme.input.MouseInput;
import com.jme.math.FastMath;
import com.jme.math.Plane;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.system.DisplaySystem;

public class MovementSelectionTool extends CameraSelectionTool{
	protected List<SelectionData> selectedItems = new ArrayList<SelectionData>(1024);
	protected Vector3f startLocation = new Vector3f();	
	protected Plane areaPlane;
	protected SelectionData primarySelection = null;
	protected boolean needsUndo = false;
	protected boolean requireRotation = false;

	protected Orientation cacheOrientation = null;
	protected MovementSelectionEffect selectionEffect = new MovementSelectionEffect();
	protected boolean usesEffect = true;
	protected com.golemgame.tool.selection.Axis curAxis;
	@Override
	public void mouseMovementAction(Vector2f mousePos, boolean left,
			boolean right) {		
		Collection<Actionable> acts = StateManager.getSelectionManager().getSelection();
		Actionable prim = StateManager.getSelectionManager().getPrimarySelection();
		
		if(prim!=null &&! acts.isEmpty() && left)
		{
			moveSelection(mousePos);
		}else
			super.mouseMovementAction(mousePos, left, right);
		
		
	}

	private void moveSelection(Vector2f mousePos
		) {
		Orientation temp = primarySelection.getOrientationInfo().getOrientation();
		if (!temp.equals(cacheOrientation))
		{
			cacheOrientation = new Orientation(temp);
			configureOrientation();
		}
		
		Ray mouseRay = new Ray();
		Vector3f mouseIntersection = new Vector3f();
		DisplaySystem.getDisplaySystem().getPickRay(mousePos,  StateManager.IS_AWT_MOUSE, mouseRay);
		boolean mouseIntersects = mouseRay.intersectsWherePlane(areaPlane, mouseIntersection);//if the mouse ray intersects at all
		
		if(!mouseIntersects)
			return;
		

		
		mouseIntersection.set( temp.getDirection().cross(mouseIntersection.cross(temp.getDirection())));
		
		if(ActionToolSettings.getInstance().getUseSnapSingleAxis().isValue() &! ( (primarySelection.getOrientationInfo().useAxis()) || primarySelection.ignoresSingleAxis() ))
		{
			//float magnitude = ActionToolSettings.getInstance().getSnapSingleAxis().getValue().dot(mouseIntersection.subtract(this.startLocation));
			
			mouseIntersection.subtractLocal(this.startLocation);
			Vector3f[] tests = new Vector3f[]{ Vector3f.UNIT_X, Vector3f.UNIT_Y, Vector3f.UNIT_Z};
			Vector3f greatestAxis = Vector3f.UNIT_X;
			float largestMagnitude =-1;
			
			for (Vector3f axis:tests)
			{
				if (axis.equals( primarySelection.getOrientationInfo().getOrientation().getDirection()))
						continue;
				
				float mag = Math.abs(axis.dot(mouseIntersection));
				if(mag>largestMagnitude)
				{
					largestMagnitude = mag;
					greatestAxis = axis;
				}
			}
			
			largestMagnitude = greatestAxis.dot(mouseIntersection);
			mouseIntersection.set(greatestAxis).multLocal(largestMagnitude).addLocal(this.startLocation);
		}


		Vector3f delta = mouseIntersection.subtract(this.startLocation);
		MoveRestrictedInformation moveRestricted  = null;
		
		if(primarySelection != null)
		{

			
			try{
				moveRestricted=(MoveRestrictedInformation)primarySelection.getActionable().getAction(Action.MOVE_RESTRICTED);
			}catch(ActionTypeException e)
			{
				
			}
		}
		Vector3f primaryPos = primarySelection.getStartingWorldTranslation().add(delta);
		if (ActionToolSettings.getInstance().isRestrictMovement() && moveRestricted != null)
		{
	
			primaryPos = moveRestricted.getRestrictedPosition(primaryPos);
			
		}
		
		delta = primaryPos.subtract(primarySelection.getStartingWorldTranslation());
		/*if (delta.length() > 60f )
		{	
			return;
		}
*/

		for(SelectionData data:this.selectedItems)
		{
				//	long time = System.nanoTime();
					Vector3f startingWorldTranslation = data.getStartingWorldTranslation();
			
						MoveAction 	move= data.getMove().copy();
						Vector3f newPosition = delta.add(startingWorldTranslation);
			
			
					if(data == primarySelection)
						selectionEffect.setTarget(newPosition);
					
					move.setStartingPosition(data.getStartingWorldTranslation());
					move.setPosition(newPosition) ;
					
				//	long time2 = System.nanoTime();
					if (move.doAction())
					{
			
						needsUndo = true;
					//	data.getActionList().mergeAction(move);
	
					}
				//	System.out.println("" + (time2 - time) + "\t" + (System.nanoTime() - time2));
		}
	}
	

	protected void configureOrientation()
	{
		configureOrientation(null);
	}
	
	protected void configureOrientation(Orientation preferedOrientation)
	{

		if (primarySelection==null)
			return;
		areaPlane = new Plane();
	
		
			OrientationInformation orientationInfo =(OrientationInformation) primarySelection.getOrientationInfo();
		
			if (orientationInfo.useAxis())
			{
				Vector3f cameraDir = DisplaySystem.getDisplaySystem().getRenderer().getCamera().getDirection().normalize();
				
				Vector3f horizontal = new Vector3f(Vector3f.UNIT_X);
				if(horizontal.distance(cameraDir) < FastMath.FLT_EPSILON)
				{
					horizontal = new Vector3f(Vector3f.UNIT_Y);
				}
				
				horizontal = cameraDir.cross(horizontal);
				
				
				preferedOrientation = new Orientation(cameraDir,horizontal);
		
			
			}
			
			
			if (preferedOrientation != null)
			{
				curAxis = Axis.xAxis;
				orientationInfo.updateOrientation(preferedOrientation);
			}
			else{
				if (ActionToolSettings.getInstance().getYZAxisAction().isValue())			
				{
					orientationInfo.updateOrientation(OrientationInformation.YZ);			
				}
				else if (ActionToolSettings.getInstance().getXYAxisAction().isValue())
				{	curAxis = Axis.zAxis;
					orientationInfo.updateOrientation(OrientationInformation.XY);					
				}
				else //default 
				{curAxis = Axis.yAxis;
					orientationInfo.updateOrientation(OrientationInformation.XZ);								
				}
			}
		

			Orientation orientation = new Orientation( orientationInfo.getOrientation());
			
			  if (curAxis == Axis.yAxis)
				{
				  ActionToolSettings.getInstance().getSnapSingleAxis().setValue(Vector3f.UNIT_X);
				}else if (curAxis == Axis.zAxis)
				{
					ActionToolSettings.getInstance().getSnapSingleAxis().setValue(Vector3f.UNIT_Y);
				}else
				{//xAxis
					ActionToolSettings.getInstance().getSnapSingleAxis().setValue(Vector3f.UNIT_Z);
				}
			 
			{
				Quaternion rotation = new Quaternion();
		
	
				rotation.fromAxes(orientation.getHorizontal(), orientation.getDirection(), orientation.getHorizontal().cross(orientation.getDirection()));

				selectionEffect.setTargetRotation(rotation);
				if(usesEffect)
					selectionEffect.setEngage(true);
				else
					selectionEffect.setEngage(false);
			}
			primarySelection.getEffect().update();

	
/*		if (orientationInfo.useAxis())
			{
			Vector3f cameraDir = DisplaySystem.getDisplaySystem().getRenderer().getCamera().getDirection();
				areaPlane.setNormal((cameraDir));
		
			
			}else*/
			{
				areaPlane.getNormal().set(orientation.getDirection());//ConstructorTool.pivotNode.getLocalRotation().mult(orientation));
			}
	

		areaPlane.setConstant(-primarySelection.getSelectedModel().getWorldTranslation().dot(areaPlane.normal));	
		updateStartingPositions();
	}
	
	protected void updateStartingPositions()
	{

		Ray mouseRay = new Ray();
		Vector2f mousePos = new Vector2f (MouseInput.get().getXAbsolute(), MouseInput.get().getYAbsolute());

		DisplaySystem.getDisplaySystem().getPickRay(mousePos,  StateManager.IS_AWT_MOUSE, mouseRay);
		boolean mouseIntersects = mouseRay.intersectsWherePlane(areaPlane, this.startLocation);//if the mouse ray intersects at all
		if(!mouseIntersects)
			this.startLocation.zero();

		//take the zero vector, project it into the plane, and normalize
		Vector3f direction = primarySelection.getOrientationInfo().getOrientation().getDirection();
		startLocation.set( direction.cross(startLocation.cross(direction)));

		
		for (SelectionData data:selectedItems)
		{
			
			try{
				ModelInformation modelInfo=  (ModelInformation)data.getActionable().getAction(Action.MODEL);
				data.getStartingWorldTranslation().set(modelInfo.getCollisionModel().getWorldTranslation());
				
				try{
					PreferedCoordinateSystemInformation coordInfo = (PreferedCoordinateSystemInformation) data.getActionable().getAction(Action.COORDINATE_SYSTEM_INFO);
					data.getStartingWorldTranslation().subtractLocal(coordInfo.getWorldZero());
					data.getStartingWorldRotation().multLocal(coordInfo.getWorldRotation().inverse());
				}catch(ActionTypeException e)
				{
					
				}
				
			}catch(ActionTypeException e)
			{
				
			}
		
			
		}
	}

	

	protected static class SelectionData
	{
		private final static Random random = new Random();
		private Vector3f startingWorldTranslation;
		//private ActionList actionList;
		private SelectAction select;
		private MoveAction move;
		private SelectionEffect effect;
		private final Actionable actionable;
		private Model selectedModel;
		private  OrientationInformation orientationInfo;
		private RotateAction rotate;
		private Quaternion startingWorldRotation;
		private final int hashcode;
		
		private PropertyState originalState;
		
		private final boolean requireRotation;
		
		private boolean ignoresSingleAxis;
		
		private ProxyStateInformation proxyState;
		
		public boolean ignoresSingleAxis()
		{
			return ignoresSingleAxis;
		}
		
	

		public void setOriginalState(PropertyState originalState) {
			this.originalState = originalState;
		}



		public Quaternion getStartingWorldRotation() {
			return startingWorldRotation;
		}

		public RotateAction getRotate() {
			return rotate;
		}

		public Actionable getActionable() {
			return actionable;
		}
		
		
		public int hashCode() {
			return hashcode;
		}

		public boolean hasProxyState()
		{
			return proxyState != null;
		}
		
		public SelectionData(Actionable actionable,boolean requireRotation) throws ActionTypeException {
		
			super();
			this.requireRotation= requireRotation;
		//	long time[] = new long[10];
		//	time[0] = System.nanoTime();
			this.hashcode = random.nextInt();
			
	
			try{
				proxyState = (ProxyStateInformation)actionable.getAction(Action.Type.PROXY_PROPERTY_STATE);
			}catch(ActionTypeException e)
			{
				proxyState =null;
			}
			
			
			
		//	time[1] = System.nanoTime();
			this.actionable = actionable;
			this.originalState = getCurrentState();
		//	actionList = new ActionList("Move");
		//	time[2] = System.nanoTime();
			startingWorldTranslation = new Vector3f();
			startingWorldRotation = new Quaternion();
		//	time[3] = System.nanoTime();
			select = (SelectAction)actionable.getAction(Action.SELECT);
		//	time[4] = System.nanoTime();
			move = (MoveAction)actionable.getAction(Action.MOVE);
		//	time[5] = System.nanoTime();
			
			//this.initialState = ((StateInformation) actionable.getAction(Action.STATE_INFO)).getState();
			
		
			try{
				effect = (SelectionEffect)actionable.getAction(Action.SELECTIONEFFECT);
			}catch(ActionTypeException e)
			{
				effect = dummyEffect;
			}
		//	time[6] = System.nanoTime();
			try{
				selectedModel = ((ModelInformation)actionable.getAction(Action.MODEL)).getCollisionModel();
			}catch(ActionTypeException e)
			{
				selectedModel = dummyModel;
			}
		//	time[7] = System.nanoTime();
			orientationInfo = (OrientationInformation) actionable.getAction(Action.ORIENTATION);
			
		//	time[8] = System.nanoTime();
			try{
				rotate = (RotateAction) actionable.getAction(Action.ROTATE);
				
			}catch(ActionTypeException e)
			{
				if(requireRotation)
					throw e;
			}
			
			try{
				SingleAxisInformation axisInfo = (SingleAxisInformation)actionable.getAction(Action.SINGLE_AXIS_INFO);
				
				this.ignoresSingleAxis = ! axisInfo.useSingleAxis();
				
			}catch(ActionTypeException e)
			{
				//do nothing
			}
			
			
		//	time[9] = System.nanoTime();


		//	String times  = "Selecting: ";
			
			//	for(long t:time)
			//		times+="\t" + (t);
					
			//	System.out.println(times );//
			
		}
		public PropertyState getOriginalState() {
			return originalState;
		}

		public PropertyState getCurrentState()
		{
			
			if(hasProxyState())
			{
				return proxyState.getCurrentState();
			}
			
			String[] keys;
			if (this.requireRotation)
			{			
				keys = new String[]{StructureInterpreter.LOCALTRANSLATION,StructureInterpreter.LOCALROTATION};
			}else
			{
				keys = new String[]{StructureInterpreter.LOCALTRANSLATION};
			}
			
			try {//add the original store
				PropertyStore store = (( ((PropertyStoreInformation) actionable.getAction(Action.PROPERTY_STORE) ).getStore()));
				
				return new SimplePropertyState(store,keys);
			
			}catch(ActionTypeException e)
			{
				//this is fine...
			}
			return null;
		}

		public SelectionData(Actionable actionable) throws ActionTypeException {
			this(actionable,false);
		}
		
		
		public OrientationInformation getOrientationInfo() {
			return orientationInfo;
		}
		public Vector3f getStartingWorldTranslation() {
			return startingWorldTranslation;
		}
	
	/*	public ActionList getActionList() {
			return actionList;
		}*/
		public SelectAction getSelect() {
			return select;
		}

		public MoveAction getMove() {
			return move;
		}
		
		
	
		public SelectionEffect getEffect() {
			return effect;
		}
	
		public Model getSelectedModel() {
			return selectedModel;
		}

		private static final SelectionEffect dummyEffect = new SelectionEffect()
		{

			
			public void setAction(Type type) {
				// TODO Auto-generated method stub
				
			}

			
			protected Object clone() throws CloneNotSupportedException {
				// TODO Auto-generated method stub
				return super.clone();
			}
			
		};
		
		private static final Model dummyModel = new NodeModel();

		
	}
}
