package com.golemgame.tool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.golemgame.model.Model;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.mvc.PropertyState;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SimplePropertyState;
import com.golemgame.properties.Property;
import com.golemgame.properties.PropertySupplier;
import com.golemgame.properties.PropertyTabFactory;
import com.golemgame.properties.fengGUI.HorizontalTabbedWindow;
import com.golemgame.properties.fengGUI.TabbedWindow;
import com.golemgame.save.direct.mvc.golems.StructureInterpreter;
import com.golemgame.states.GUILayer;
import com.golemgame.states.StateManager;
import com.golemgame.structural.structures.PhysicalStructure;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionList;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.DeleteAction;
import com.golemgame.tool.action.MoveAction;
import com.golemgame.tool.action.RotateAction;
import com.golemgame.tool.action.SelectAction;
import com.golemgame.tool.action.SelectionEffect;
import com.golemgame.tool.action.UndoManager;
import com.golemgame.tool.action.information.FocusInformation;
import com.golemgame.tool.action.information.ModelInformation;
import com.golemgame.tool.action.information.MoveRestrictedInformation;
import com.golemgame.tool.action.information.Orientation;
import com.golemgame.tool.action.information.OrientationInformation;
import com.golemgame.tool.action.information.PhysicalInfoAction;
import com.golemgame.tool.action.information.PreferedCoordinateSystemInformation;
import com.golemgame.tool.action.information.PropertyStoreInformation;
import com.golemgame.tool.action.information.ProxyStateInformation;
import com.golemgame.tool.action.information.SelectionEffectInformation;
import com.golemgame.tool.action.information.StaticMaterialInformation;
import com.golemgame.tool.action.mvc.CopyComponentAction;
import com.jme.input.MouseInput;
import com.jme.math.FastMath;
import com.jme.math.Plane;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.system.DisplaySystem;

public class MovementTool2 implements IActionTool {
	
	protected List<SelectionData> selectedItems = new ArrayList<SelectionData>(1024);
	protected Vector3f startLocation = new Vector3f();	
	protected Plane areaPlane;
	protected SelectionData primarySelection = null;
	protected boolean needsUndo = false;
	protected boolean requireRotation = false;

	protected Orientation cacheOrientation = null;
	protected MovementSelectionEffect selectionEffect = new MovementSelectionEffect();
	protected boolean usesEffect = true;
	private Orientation currentOrientation = new Orientation(  OrientationInformation.XY);

	
	protected enum Axis
	{
		xAxis(), yAxis(),zAxis();
	}
	protected Axis curAxis = Axis.yAxis;
	
	public Collection<Actionable> getSelectedActionables()
	{
		ArrayList<Actionable> actionables = new ArrayList<Actionable>();
		for(SelectionData data:selectedItems)
		{
			actionables.add(data.getActionable());
		}
		return actionables;
	}

	public Actionable getPrimarySelection()
	{
		if (primarySelection != null)
			return  primarySelection.getActionable();
		return null;
	}
	
	public boolean isSelected()
	{
		return primarySelection!= null;
	}
	
	public void select(Actionable toSelect, Model selectedModel,boolean primary)
			throws FailedToSelectException {
		try{

			needsUndo = false;
			
		//	long time[] = new long[10];
		//	time[0] = System.nanoTime();
			if(!ActionToolSettings.getInstance().getStaticsSelectable().isValue())
			{
				try{
					StaticMaterialInformation staticInfo =(StaticMaterialInformation)	toSelect.getAction(Action.STATIC_INFO);
					if(staticInfo.isStatic())
						throw new FailedToSelectException();
				}catch(ActionTypeException e)
				{
					
				}
		
			}
		//	time[1] = System.nanoTime();
			toSelect.getAction(Action.SELECT).doAction();
			SelectionData selectedData =null;
			for(SelectionData data:this.selectedItems)
			{
				if(data.getActionable().equals(toSelect)) //this could be very slow use map?
				{
					selectedData = data;
				}
			}
		//	time[2] = System.nanoTime();
			if(selectedData==null)
			{
				selectedData = new SelectionData(toSelect,requireRotation);
		//		time[3] = System.nanoTime();
				selectedItems.add(selectedData);
			}//else
		//		time[3] = System.nanoTime();
		//	time[4] = System.nanoTime();
			FocusInformation focus = null;
			try{
				 focus = (FocusInformation) toSelect.getAction(Action.FOCUS);

			}catch(ActionTypeException e){}
		//	
			StateManager.getToolManager().resolveCustomSelection(toSelect);
			
			
			if(primary && primarySelection!=null)
			{
				primarySelection.getEffect().setAction(Action.MOVE);
				primarySelection.getEffect().setEngage(false);
				primarySelection.getEffect().doAction();
				primarySelection = null;
			}
		//	time[5] = System.nanoTime();
			selectedData.getEffect().setInformation(focus, selectedData.getOrientationInfo());

			if(primary)
			{
				primarySelection= selectedData;
				primarySelection.getEffect().setAction(Action.MOVE);
				primarySelection.getEffect().setEngage(true);
				primarySelection.getEffect().doAction();
				//selectionEffect= new MovementSelectionEffect();
				
				usesEffect = true;
				try{
					usesEffect = ((SelectionEffectInformation)primarySelection.getActionable().getAction(Action.SELECT_EFFECT_INFO)).usesStandardEffects();
				}catch(ActionTypeException e)
				{
					
				}
				if(!usesEffect)
				{
					selectionEffect.setEngage(false);
				}else{

				//	selectionEffect.setTarget(primarySelection.getSelectedModel().getWorldTranslation());
					selectionEffect.setTargetModel(primarySelection.getSelectedModel());
					
					if(MouseInput.get().isButtonDown(0))
						selectionEffect.setEngage(true);
				
				}
			
				
			}else if (primarySelection == null)
			{
				primarySelection = selectedData;
			}
			
		
			
			selectedData.getEffect().setAction(Action.SELECT);
			selectedData.getEffect().setEngage(true);
			selectedData.getEffect().doAction();
		//	time[6] = System.nanoTime();
			if(primary)
				configureOrientation();
		//	time[7] = System.nanoTime();
		
			
	
				
		//	System.out.println(times );//

		}catch(ActionTypeException e)
		{
			//deselect();//ensure it is deselected, in case some selection succeeded.
			throw new FailedToSelectException();
		}
	}
	
	private void createNewUndoPoint()
	{
		final Collection<PropertyState> before = new ArrayList<PropertyState>();
		final Collection<PropertyState> after = new ArrayList<PropertyState>();
		
		//problem: control points dont undo this way... or really, any way...
		
		for(SelectionData data:selectedItems)
		{
			if(data.getOriginalState()!=null)
				before.add(data.getOriginalState());
			PropertyState current = data.getCurrentState();
			if(current!=null)
				after.add(current);
			data.setOriginalState(current);
		}
	
		if(needsUndo)
		{
			Action<?> moveAction = new Action()
			{

				@Override
				public String getDescription() {
					return "Move";
				}

				@Override
				public com.golemgame.tool.action.Action.Type getType() {
					return Action.STATE;
				}

				@Override
				public boolean doAction() {
					for(PropertyState state:after)
						state.restore();
					for(PropertyState state:after)
						state.refresh();
					return true;
				}

				@Override
				public boolean undoAction() {
					for(PropertyState state:before)
						state.restore();
					for(PropertyState state:before)
						state.refresh();
					return true;
				}
				
			};
			
			UndoManager.getInstance().addAction(moveAction);
		
		}
		needsUndo = false;
	}
	private int i = 0;
	public void deselect(Actionable actionable) {

		if(!this.getSelectedActionables().contains(actionable))
			return;
		{
			primarySelection = null;
			if(this.primarySelection!=null && this.primarySelection.getActionable() == actionable)
			{
				SelectionData oldPrimary = primarySelection;
				SelectionData newPrimary = null;
		/*		for(SelectionData selection:this.selectedItems)
				{
					if(!selection.equals(primarySelection))
					{
						newPrimary = selection;
						try{
							
							select(newPrimary.getActionable(),newPrimary.getSelectedModel(),true);
							break;
						}catch(FailedToSelectException e)
						{
							
						}
					
					}
				}*/
		
			/*	if(this.primarySelection==oldPrimary)
				{
					this.deselect();//failed to find a new alternate primary selection, just deselect everything
					return;
				}
				*/
			//	this.select(toSelect, selectedModel, primary)
				//this.deselect();//just deselect the whole thing
				//return;
			}
			
			SelectionData data = null;
			for(SelectionData d:this.selectedItems)
			{
				if(d.actionable==actionable)
				{
					data = d;
					break;
				}
			}
			this.getSelectedActionables().remove(actionable);
	

			if(data!= null){
				SelectAction deselect = data.getSelect().copy();
				deselect.setSelect(false);
		
				deselect.doAction();
				data.getEffect().setAction(Action.SELECT);
				data.getEffect().setEngage(false);
				data.getEffect().doAction();
				
				selectedItems.remove(data);
			}
		}
		
	}

	public void deselect() {
	
		
		this.selectionEffect.forceDisengage();
		if(primarySelection!=null)
		{
			primarySelection.getEffect().setAction(Action.MOVE);
			primarySelection.getEffect().setEngage(false);
			primarySelection.getEffect().doAction();
			
		}

		this.primarySelection= null;


		
		SelectionData[] selections  = this.selectedItems.toArray(new SelectionData[this.selectedItems.size()]);
	
	
		for(SelectionData data:selections)
		{
	
			SelectAction deselect = data.getSelect().copy();
			deselect.setSelect(false);
	
			deselect.doAction();
	
			data.getEffect().setAction(Action.SELECT);
			data.getEffect().setEngage(false);
			data.getEffect().doAction();
			
	
		
		}
	
		createNewUndoPoint();
		
		
		
		
		selectedItems.clear();
		
	}

	protected Action<?> getAdditionalActions(SelectionData[] selections)
	{
		return null;
	}
	
	
	
	public void showPrimaryEffect(boolean show) {
		if(this.primarySelection!=null && selectionEffect!=null && usesEffect)
		{
			if(show)
			{
				//selectionEffect.doEngage(true);
			}
			else
			{
				selectionEffect.forceDisengage();
				createNewUndoPoint();
			}
		}
	}

	public boolean mouseButton(int button, boolean pressed, int x, int y) {
		if( primarySelection==null || button != 0)
		{
			//dont know what the impact of this is 
			return StateManager.getToolPool().getCameraTool().mouseButton(button,pressed, x, y);
		
		}
		
		if (button== 0 &! pressed)
		{

			createNewUndoPoint();
		}

		return false;
	}

	
	public void mouseMovementAction(Vector2f mousePos, boolean left,
			boolean right) {
		if(!left || primarySelection==null)
		{
			//dont know what the impact of this is 
			StateManager.getToolPool().getCameraTool().mouseMovementAction(mousePos, left, right);
			return;
		}
		
		Orientation temp = primarySelection.getOrientationInfo().getOrientation();
		if (!temp.equals(cacheOrientation) || primarySelection.usesCameraPlane() )
		{
			cacheOrientation = new Orientation(temp);
			configureOrientation();
		}
		
		Ray mouseRay = new Ray();
		Vector3f mouseIntersection = new Vector3f();
		DisplaySystem.getDisplaySystem().getPickRay(mousePos,  StateManager.IS_AWT_MOUSE, mouseRay);
		boolean mouseIntersects = mouseRay.intersectsWherePlane(areaPlane, mouseIntersection);//if the mouse ray intersects at all
		
	//	System.out.println(startLocation + "\t" + mouseIntersection + "\t" );
		
		if(!mouseIntersects)
			return;
		
		if(!primarySelection.usesCameraPlane())
			mouseIntersection.set( currentOrientation.getDirection().cross(mouseIntersection.cross(currentOrientation.getDirection())));
		
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

	
	public void scrollMove(int wheelDelta, int x, int y) {
		StateManager.getToolPool().getCameraTool().scrollZoom(wheelDelta);
		
	}

	public void focus()
	{
		if (primarySelection == null)
		{
			StateManager.getToolPool().getCameraTool().standardFocus();
		}else
		{
			try{
				Vector3f center = ((FocusInformation)primarySelection.getActionable().getAction(Action.FOCUS)).getCenterVector();
				StateManager.getToolPool().getCameraTool().focus(center);
			}catch(ActionTypeException ex)
			{
				StateManager.getToolPool().getCameraTool().standardFocus();

			}
		}
	}
	public void copy()
	{
		
		if (primarySelection==null )
		{
			return;
		}
/*		long[] times = new long[6];
		int t = 0;*/
		/*Set<Model> collisionIgnore = new HashSet<Model>();
		for(SelectionData data:selectedItems)
		{
			try{
				CollisionMember member = ((CollisionInformation)	data.getActionable().getAction(Action.COLLISION_INFO)).getCollisionMember();
				collisionIgnore.addAll(member.getCollisionModels());
			}catch(ActionTypeException e2)
			{
				
			}
		}*/
	//	times [t++] = System.nanoTime();
		CopyComponentAction copy;
		try {
			copy = (CopyComponentAction) StateManager.getStructuralMachine().getAction(Action.COPY_COMPONENT);
	
			//copy.setProperties(UndoManager.UndoProperties.DEPENDENT);
			
			//Map<Actionable,Copyable<?>> copyMap = new HashMap<Actionable,Copyable<?>>();
			//List<CopyComponentAction> copyActions = new ArrayList<CopyComponentAction>();
		//	times [t++] = System.nanoTime();
			for(SelectionData data:selectedItems)
			{
				Actionable selectedActionable = data.getActionable();
				
				//		System.out.println(selectedItems.size());
					
						//copy.setCollisionIgnoreList(collisionIgnore);
						try {//add the original store
							copy.addComponent(( ((PropertyStoreInformation) selectedActionable.getAction(Action.PROPERTY_STORE) ).getStore()));
						}catch(ActionTypeException e)
						{
							//this is fine...
						}catch (Exception e) {
							// TODO Auto-generated catch block
							StateManager.logError(e);
						}
						
				
				
			}
		//	times [t++] = System.nanoTime();
			try{
			if (copy.doAction())						
			{
			
				UndoManager.getInstance().addAction(copy);
	
			}
			}catch(Exception e)
			{
				StateManager.logError(e);
			}	
/*			times [t++] = System.nanoTime();
			String time = "Copy:\t";
			for (int i = 1; i< t;i++)
			{
				time+= times[i] - times[i-1] + "\t";
			}
			System.out.println(time);*/
		} catch (ActionTypeException e) {
			// TODO Auto-generated catch block
			StateManager.logError(e);
		}
	}
	public void delete()
	{
	/*	long[] times = new long[6];
		int t = 0;*/
	//	times [t++] = System.nanoTime();
		ArrayList<SelectionData> selectedData = new ArrayList<SelectionData>(selectedItems);
		deselect();
	//	times [t++] = System.nanoTime();
		//StateManager.getGame().lock();
		try{
		ActionList deleteList = new ActionList("Delete");
			for(SelectionData data:selectedData )
			{
				Actionable selectedActionable = data.getActionable();
				try{
					DeleteAction delete =(DeleteAction) selectedActionable.getAction(Action.DELETE);
		/*			RemoveComponentAction delete;
					
					delete = (RemoveComponentAction) StateManager.getStructuralMachine().getAction(Action.REMOVE_COMPONENT);
					
					delete.setComponent(((PropertyStoreInformation)selectedActionable.getAction(Action.PROPERTY_STORE)).getStore());
					*/
			
					deleteList.add(delete);
					
				}catch(Exception exception)
				{
					
				}			
			}
			deselect();
			if (deleteList.doAction())
				UndoManager.getInstance().addAction(deleteList);
			
		}finally{
	//		StateManager.getGame().unlock();
		}
		/*	times [t++] = System.nanoTime();
			String time = "Delete:\t";
			for (int i = 1; i< t;i++)
			{
				time+= times[i] - times[i-1] + "\t";
			}
			System.out.println(time);*/
	}
	public void properties()
	{
	/*	if (primarySelection==null )
		{
			return;
		}
		*/
/*		if(selectedItems.size() == 1)
		{
			try{
			
				PropertiesAction properties = (PropertiesAction)primarySelection.getActionable().getAction(Action.PROPERTIES);
				deselect();
				properties.doAction();

				
			}catch(ActionTypeException exception)
			{
				//dont do anything.
				//exception.printStackTrace();
			}			
				
		}else if (selectedItems.size() > 1)*/
		{
			//if multiple objects are supplied, then a) ensure that atleast one of them
			//can be use both material and color effects
			
			//b: show those properties pages with details for the primary selection
			
			//c: apply those properties to all selected items
			
			//this is a hack approach..
			
			//changing the way this works: Functional items like sources, modifiers are also physical now... they can have appearances too!
		//	PhysicalStructure physicalStructure = null;
		//	ArrayList<PhysicalStructure> physicalList = new ArrayList<PhysicalStructure>();
			
			
			Collection<Property> properties = new ArrayList<Property>();
			PhysicalStructure primary = null;
			try{
				PhysicalStructure physical;
				if(primarySelection != null && primarySelection.getActionable()!= null && (physical = ((PhysicalInfoAction) primarySelection.getActionable().getAction(Action.PHYSICAL_INFO)).getPhysicalStructure()) != null)
				{
					primary = physical;
					
				}
			}catch(ActionTypeException ex){}
			
				for (SelectionData data:selectedItems)
				{
					if (data.getActionable() instanceof PropertySupplier)
					{
						PropertySupplier p = (PropertySupplier)data.getActionable();
						properties.addAll(p.getPropertySet());
					}
					
				/*	PhysicalStructure physical;
					try{
					if((physical = ((PhysicalInfoAction) data.getActionable().getAction(Action.PHYSICAL_INFO)).getPhysicalStructure()) != null)
					{
						if(physicalStructure == null)
							physicalStructure = physical;
						physicalList.add(physical);
					}
					}catch(ActionTypeException ex){}*/
					
				}
			
				if(properties.isEmpty())
					return;
				
				this.deselect();
				
					GUILayer layer = GUILayer.getLoadedInstance();

				//only show one properties window for a specific structure at any one time.
			
					TabbedWindow window = new HorizontalTabbedWindow();
					window.setTitle("Properties");
					
				
					
					PropertyTabFactory factory = new PropertyTabFactory();
					factory.populateWindow(window, properties);
					
					if(primary != null)
						primary.populateProperties(window);
					
					window.display(layer.getDisplay());
					
		
				
			
				
		/*	if(physicalStructure!=null)
			{
				deselect();
				//display 
				HorizontalTabbedWindow window = new HorizontalTabbedWindow();
				MaterialPropertiesTab tab = new MaterialPropertiesTab();
				tab.setStructural(physicalStructure);
				tab.setStructureList(physicalList);
				window.addTab(tab);
				TextureTab textureTab =new TextureTab();
				textureTab.setStructure(physicalStructure);
				textureTab.setStructureList(physicalList);
				window.addTab(textureTab);
				
				window.display(GUILayer.getLoadedInstance().getDisplay());
				
				
				
			}*/
		}
	}
	public void xyPlane(boolean value)
	{
		if (primarySelection==null )
			return;

		if (value)
		{
			curAxis = Axis.zAxis;
			configureOrientation(OrientationInformation.XY);
		}
		else
		{
			curAxis = Axis.yAxis;
			configureOrientation();
		}
	}
	public void yzPlane(boolean value)
	{
		if (primarySelection==null )
			return;
		if (value)
		{
			curAxis = Axis.xAxis;
			
			configureOrientation(OrientationInformation.YZ);
		}
		else
		{
			curAxis = Axis.yAxis;
			configureOrientation();
		}
	}
	public void xzPlane(boolean value)
	{
		
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
			
			if(primarySelection.usesCameraPlane())
			{
				 orientation = new Orientation ( DisplaySystem.getDisplaySystem().getRenderer().getCamera().getDirection().normalize(), DisplaySystem.getDisplaySystem().getRenderer().getCamera().getLeft().normalize());
			 }
			
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
				if(usesEffect && MouseInput.get().isButtonDown(0))
					selectionEffect.setEngage(true);
				else
					selectionEffect.setEngage(false);
			}
			primarySelection.getEffect().update();
			currentOrientation = orientation;
	
/*		if (orientationInfo.useAxis())
			{
			Vector3f cameraDir = DisplaySystem.getDisplaySystem().getRenderer().getCamera().getDirection();
				areaPlane.setNormal((cameraDir));
		
			
			}else*/
			if(primarySelection.usesCameraPlane())
			{
				areaPlane.getNormal().set(orientation.getDirection());//ConstructorTool.pivotNode.getLocalRotation().mult(orientation));
				startLocation.set(primarySelection.getOrigin());
				//areaPlane.getNormal().set(DisplaySystem.getDisplaySystem().getRenderer().getCamera().getDirection());//ConstructorTool.pivotNode.getLocalRotation().mult(orientation));
				areaPlane.setConstant(-primarySelection.getOrigin().dot(areaPlane.normal));	
			}else{
				areaPlane.getNormal().set(orientation.getDirection());//ConstructorTool.pivotNode.getLocalRotation().mult(orientation));
				areaPlane.setConstant(-primarySelection.getSelectedModel().getWorldTranslation().dot(areaPlane.normal));	
			}
	

		
		updateStartingPositions();
	}
	
	protected void updateStartingPositions()
	{
		if(!primarySelection.usesCameraPlane()){
			Ray mouseRay = new Ray();			
			Vector2f mousePos = new Vector2f (MouseInput.get().getXAbsolute(), MouseInput.get().getYAbsolute());	
			DisplaySystem.getDisplaySystem().getPickRay(mousePos,  StateManager.IS_AWT_MOUSE, mouseRay);			
			boolean mouseIntersects = mouseRay.intersectsWherePlane(areaPlane, this.startLocation);//if the mouse ray intersects at all
			if(!mouseIntersects)
				this.startLocation.zero();
		//take the zero vector, project it into the plane, and normalize
	
		
		Vector3f direction =currentOrientation.getDirection();
		startLocation.set( direction.cross(startLocation.cross(direction)));
		}
		
		for (SelectionData data:selectedItems)
		{
			
			try{
				ModelInformation modelInfo=  (ModelInformation)data.getActionable().getAction(Action.MODEL);
				if(!data.usesCameraPlane()){
					data.getStartingWorldTranslation().set(modelInfo.getCollisionModel().getWorldTranslation());
				}else{
					data.getStartingWorldTranslation().set(data.getOrigin());
				}
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
		
		private boolean usesCameraPlane = false;
		public boolean usesCameraPlane() {
			return usesCameraPlane;
		}

		private Vector3f origin = Vector3f.ZERO;
		
		public Vector3f getOrigin() {
			return origin;
		}

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
			
			this.usesCameraPlane = orientationInfo.use2DCameraPlane();
			if(this.usesCameraPlane ==true)
				this.origin = orientationInfo.getOrigin();
			
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
