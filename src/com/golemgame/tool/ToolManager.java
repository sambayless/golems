package com.golemgame.tool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.golemgame.model.Model;
import com.golemgame.model.ModelIntersectionData;
import com.golemgame.settings.ActionSettingsListener;
import com.golemgame.settings.SettingChangedEvent;
import com.golemgame.settings.SettingsListener;
import com.golemgame.states.StateManager;
import com.golemgame.tool.IActionTool.FailedToSelectException;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.ControlAction;
import com.golemgame.tool.action.CustomSelectionAction;
import com.golemgame.tool.action.ModifyAction;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.action.information.GroupInformation;
import com.golemgame.tool.action.information.ModelInformation;
import com.golemgame.tool.action.information.SelectionInformation;
import com.golemgame.tool.action.information.SelectionPriorityInformation;
import com.golemgame.util.ControlledListener;
import com.golemgame.util.event.ListenerList;
import com.golemgame.util.input.InputLayer;
import com.jme.input.MouseInputListener;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.system.DisplaySystem;

public class ToolManager implements  ControlledListener{


	private  final IActionTool defaultTool = StateManager.getToolPool().getDefaultTool();

	private Model currentSelectedModel = null;
	private IActionTool currentTool= defaultTool;
	private IActionTool primaryTool = defaultTool;
	
	private ListenerList<SelectionListener> selectionListeners = new ListenerList<SelectionListener>();


	private boolean selectionEnabled = true;
	
	public void attachListener(SelectionListener listener)
	{
		selectionListeners.addListener(listener);
	}
	
	public void removeListener(SelectionListener listener)
	{
		selectionListeners.removeListener(listener);
	}
	
	public boolean hasListener(SelectionListener listener)
	{
		return selectionListeners.contains(listener);
	}
	
	public ToolManager() {
		super();
		ActionToolSettings.getInstance().getAssignGroup().addSettingsListener(new ActionSettingsListener()
		{

			public void valueChanged(SettingChangedEvent<Object> e) {
				StateManager.getStructuralMachine().getGroupManager().constructGroup();
				
			}

		
			
		});
	}
	

	public IActionTool getPrimaryTool() {
		return primaryTool;
	}

	public void setPrimaryTool(IActionTool primaryTool) {
		if(primaryTool == null)
			this.primaryTool = defaultTool;
		else
			this.primaryTool = primaryTool;
	}
	
	public boolean isSelectionEnabled() {
		return selectionEnabled;
	}

	/**
	 * Set whether selection of objects should be attempted on mouse clicks.
	 * @param selectionEnabled
	 */
	public void setSelectionEnabled(boolean selectionEnabled) {
		this.selectionEnabled = selectionEnabled;
	}
	
	/**
	 * Hold most recently updated state of button presses.
	 * Although mouse listener is single threaded, MouseInput.isButtonDown() is NOT;
	 * tools that access it may occasionally receive information that is more up to date than the most recent button press
	 * event, leading to undefined behaviour.
	 */
	private boolean[] buttonState = new boolean[3];
	
	protected CustomSelectionAction customSelection= null;

	private CustomSelectionAction getCustomSelection() {
		return customSelection;
	}

	private void setCustomSelection(CustomSelectionAction customSelection) {
		this.customSelection = customSelection;
	}
	private SettingsListener<Boolean> rotateListener = new SettingsListener<Boolean>()
	{
	
		
		public void valueChanged(SettingChangedEvent<Boolean> e) {
			if (e.getNewValue())
			{
				setCurrentTool(StateManager.getToolPool().getRotationTool());
			}else
			{
				setCurrentTool(StateManager.getToolPool().getMovementTool());
			}
			
		}
		
	};

	public void resolveCustomSelection(Actionable toSelect)
	{
		CustomSelectionAction customSelection = StateManager.getToolManager().getCustomSelection();
		if(customSelection!= null)
		{
			if (customSelection.selectionOccurs(toSelect))
			{
				customSelection= null;
				 StateManager.getToolManager().setCustomSelection(customSelection);
			}
			
		}
		
		try{
			CustomSelectionAction tCustom = (CustomSelectionAction) toSelect.getAction(Action.CUSTOM_SELECTION);
			if((customSelection !=  null) && (! customSelection.getControlled().equals(tCustom.getControlled())))
			{
				customSelection.forceFinish();
			
			}
			StateManager.getToolManager().setCustomSelection(tCustom);
		}catch (ActionTypeException e)
		{
			//customSelection = null;
		}
		
	}
	


	public ITool getFailedSelectionTool()
	{
		return StateManager.getToolPool().getCameraTool();
	}
	public void forceDeselect()
	{
		this.selectionListeners.clear();
		
		if(this.getCurrentTool()!=null)
			this.getCurrentTool().deselect();
		else
			this.getFailedSelectionTool().deselect();
		
		if(customSelection != null)
		{	
			customSelection.forceFinish();
			customSelection = null;
		}
	}
	
	public void deselect(Actionable actionable)
	{
		try {
			Model model =( (ModelInformation) actionable.getAction(Action.MODEL)).getCollisionModel();

			if(this.getCurrentTool()!=null)
				this.getCurrentTool().deselect(actionable);
		} catch (ActionTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public IActionTool getCurrentTool()
	{
		if(currentTool == null)
			currentTool = StateManager.getToolPool().getDefaultTool();
		return currentTool;
	}
	private SettingsListener<Boolean> modifyListener = new SettingsListener<Boolean>()
	{

		
		public void valueChanged(SettingChangedEvent<Boolean> event) {
			Model currentModel = currentSelectedModel;
			if(currentSelectedModel == null)
				return;
			Actionable selectedActionable = currentModel.getActionable();
			if(selectedActionable != null)
			{
				try{
					ModifyAction action = (ModifyAction) selectedActionable.getAction(Action.MODIFY);
					action.setModify(event.getNewValue());
					action.doAction();
				}catch(ActionTypeException e)
				{
					try{
						ControlAction action = (ControlAction) selectedActionable.getAction(Action.CONTROL);
						if(event.getNewValue())
						{
							action.doAction();
							action.setVisible(true);
						}else
						{
							
							action.setResolve(true);
							action.doAction();
							action.setVisible(false);
						}
					}catch(ActionTypeException ex)
					{
						
					}
				}
		
			}
			
		}
		
	};
	public synchronized void setCurrentTool(IActionTool tool)
	{
		Collection<Actionable> selectionsToTransfer = null;
	
		if(currentTool!=null)
		{
			selectionsToTransfer = currentTool.getSelectedActionables();
			currentTool.deselect();
		}

		if(tool == null)
		{
			currentTool = defaultTool;
		}else{
			currentTool = tool;
		}
		Actionable selectedActionable ;
		if(currentTool != null )
		{
			try{
				
				if(selectionsToTransfer!=null)
				{
					for(Actionable actionable:selectionsToTransfer)
					{
						try{
							Model model = ((ModelInformation) actionable.getAction(Action.MODEL)).getCollisionModel();
							currentTool.select(actionable,model,false);
						}catch(ActionTypeException e){}
						catch(FailedToSelectException e){}
					}
				}
				if (currentSelectedModel!=null && (selectedActionable= currentSelectedModel.getActionable() )!= null)
				{
					currentTool.select(selectedActionable,currentSelectedModel,true);
				}
			}catch(FailedToSelectException e)
			{
				currentTool.deselect();
			}
		}


	}
	private Lock listenLock = new ReentrantLock();
	
	 
	public void setListening(boolean listening)
	{
		 listenLock.lock();
		 try{
		if( listening)
		{	
			for (int i = 0;i<buttonState.length;i++)
				buttonState[i]=false;
	
			InputLayer.get().addMouseListener(mouseListener,InputLayer.TOOL_LAYER);	
			ActionToolSettings.getInstance().getModify().addSettingsListener(modifyListener,InputLayer.TOOL_LAYER);
			ActionToolSettings.getInstance().getRotate().addSettingsListener(rotateListener,InputLayer.TOOL_LAYER);

			
			ActionToolSettings.getInstance().getXYAxisAction().addSettingsListener(xyAxisListener,InputLayer.TOOL_LAYER);
			ActionToolSettings.getInstance().getYZAxisAction().addSettingsListener(yzAxisListener,InputLayer.TOOL_LAYER);

			ActionToolSettings.getInstance().getFocus().addSettingsListener(focusListener,InputLayer.TOOL_LAYER);
			ActionToolSettings.getInstance().getDelete().addSettingsListener(deleteListener,InputLayer.TOOL_LAYER);
			ActionToolSettings.getInstance().getCopy().addSettingsListener(copyListener,InputLayer.TOOL_LAYER);
			ActionToolSettings.getInstance().getProperties().addSettingsListener(propertiesListener,InputLayer.TOOL_LAYER);
	
	
		}else{
			for (int i = 0;i<buttonState.length;i++)
				buttonState[i]=false;
			getCurrentTool().deselect();
			
			InputLayer.get().removeMouseListener(mouseListener,InputLayer.TOOL_LAYER);
			ActionToolSettings.getInstance().getModify().removeSettingsListener(modifyListener);
			ActionToolSettings.getInstance().getRotate().removeSettingsListener(rotateListener);

			
			ActionToolSettings.getInstance().getXYAxisAction().removeSettingsListener(xyAxisListener);
			ActionToolSettings.getInstance().getYZAxisAction().removeSettingsListener(yzAxisListener);
			ActionToolSettings.getInstance().getFocus().removeSettingsListener(focusListener);
			ActionToolSettings.getInstance().getDelete().removeSettingsListener(deleteListener);
			ActionToolSettings.getInstance().getCopy().removeSettingsListener(copyListener);
			ActionToolSettings.getInstance().getProperties().removeSettingsListener(propertiesListener);
		
			
	
		}	
		 }finally{
			 listenLock.unlock();
		 }
	}

	private static class ActionablePriorityPair implements Comparable<ActionablePriorityPair>
	{
		private final int priority;
		private final Actionable actionable;
		private final ModelIntersectionData intersection;
		
		public ActionablePriorityPair(Actionable actionable,ModelIntersectionData intersection, int priority) {
			super();
			this.actionable = actionable;
			this.intersection = intersection;
			this.priority = priority;
		}


		public ModelIntersectionData getIntersection() {
			return intersection;
		}


		public int getPriority() {
			return priority;
		}


		public Actionable getActionable() {
			return actionable;
		}


		
		public int compareTo(ActionablePriorityPair o) {
			if ( this.priority> o.getPriority())
				return 1;
			if ( this.priority < o.getPriority())
				return -1;
					
			return 0;
		}
		
	}
	 
	
	public void selectActionables(Collection<Actionable> actionables)
	{
		boolean allowDeselect = false;
		
		if(getSelectedActionables().containsAll(actionables))
			allowDeselect = true;//only allow deselect if every single member of actionables is already selected
		
		for (Actionable actionable:actionables)
		{
			try {
				Model model =( (ModelInformation) actionable.getAction(Action.MODEL)).getCollisionModel();
				attemptToSelect(actionable,model,allowDeselect);
			} catch (ActionTypeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private boolean attemptToSelect(IActionTool tool, List<ModelIntersectionData> pickResults)
	{
		return attemptToSelect(tool, pickResults,true);
	}
			
	private boolean attemptToSelect(IActionTool tool, List<ModelIntersectionData> pickResults, boolean allowDeselect)
	{
		//find each actionable, and sort by priority
		
		ArrayList<ActionablePriorityPair> actionables = new ArrayList<ActionablePriorityPair>();
		
		for (ModelIntersectionData intersectionData:pickResults)
		{

			Actionable actionable =intersectionData.getModel().getActionable();
			if(actionable != null)
			{
				int priority = 0;
				try{
					priority = ((SelectionPriorityInformation) actionable.getAction(Type.SELECTION_PRIORITY)).getSelectionPriority();
				}catch (ActionTypeException e)
				{

				}
				actionables.add(new ActionablePriorityPair(actionable,intersectionData,-priority)); //invert the priority so that it sorts right
			}
		}
		
		Collections.sort(actionables);

		
		
		for (ActionablePriorityPair actionPair:actionables)
		{
			if (attemptToSelect(actionPair.getActionable(),actionPair.getIntersection().getModel(),allowDeselect))
				return true;
					
					
		
		}	
		return false;
	}
	
	private boolean attemptToSelect(Actionable actionable, Model selectedModel, boolean allowDeselect) {
		
		if (actionable != null)
		{		
			
			boolean allowMultipleSelect = true;
			try{
				SelectionInformation info = (SelectionInformation)actionable.getAction(Action.SELECTINFO);
				if (!info.isSelectable())
					return false;
				
				allowMultipleSelect = info.isMultipleSelectable();
			
			}catch (ActionTypeException e)
			{
				//do nothing - if no selection info is provided, continue 
			}
			
			//Interject with onetime selection listener here
			
			if(listenerIntercepts(actionable,selectedModel))
				return true;
			
			if(!allowMultipleSelect)
			{
				if (getCurrentTool() != null)
					getCurrentTool().deselect();
			}
			
			if(! ActionToolSettings.getInstance().getMultipleSelect().isValue() &&! this.getSelectedActionables().contains(actionable))
			{	//only do this if the selected item is null or is not included in the current selection.

				if(getCurrentTool() != null)
					getCurrentTool().deselect();
				currentSelectedModel = null;
			}
			
			try{

					{

						
						boolean deselect = false;
						if (allowDeselect && ActionToolSettings.getInstance().getMultipleSelect().isValue() && this.getSelectedActionables().contains(actionable))
						{
							deselect = true;
						}
						//attempt to select the main item first
						if(deselect){
							getCurrentTool().deselect(actionable);
						}else{
							getCurrentTool().select(actionable, selectedModel,true);
						}
						
						if(ActionToolSettings.getInstance().isGroupSelectionMode())
						{
							try{
							//	long time = System.nanoTime();
								List<Actionable> groupList =((GroupInformation)	actionable.getAction(Action.GET_GROUP)).getGroupMembers(ActionToolSettings.getInstance().getStaticsSelectable().isValue());
							//	System.out.println(System.nanoTime()-time);
							//	time = System.nanoTime();
								if(groupList!=null)
								{
									for(Actionable act:groupList)
									{
										try{
											if(deselect)
											{
												getCurrentTool().deselect(act);
											}else{
												getCurrentTool().select(act, selectedModel,false);
											}
										}catch(FailedToSelectException e)
										{

										}
									}
								}
								
							//	System.out.println(System.nanoTime()-time);
							}catch(ActionTypeException e)
							{
								
							}
						}
						
						if(!ActionToolSettings.getInstance().getMultipleSelect().isValue())
						{
							if (getCurrentTool() != null)
							{
								if (getCurrentTool().getSelectedActionables() != null)
								{
									if (!getCurrentTool().getSelectedActionables().contains(actionable))
									{
										getCurrentTool().deselect();//dont deselect if the thing to be selected is part of the selection already (this is a more natural behavior)
									}
								}
							}
						}
						
						//select the main item again after, so it stays primary perhaps?
						if(deselect){
							getCurrentTool().deselect(actionable);
						}else{
							getCurrentTool().select(actionable, selectedModel,true);
						}
						
						if(!deselect)
						{
							this.currentSelectedModel=selectedModel;
							
							try{//this used to use the selectedmodels actionable - changed to method passed actionable.. dont know what the effect of this is
								ModifyAction action = (ModifyAction) actionable.getAction(Action.MODIFY);
								action.setModify(ActionToolSettings.getInstance().getModify().isValue());
								action.doAction();
							}catch(ActionTypeException e)
							{
								try{//this used to use the selectedmodels actionable - changed to method passed actionable.. dont know what the effect of this is
									ControlAction action = (ControlAction)actionable.getAction(Action.CONTROL);
									if(ActionToolSettings.getInstance().getModify().isValue())
									{
										action.doAction();
										
										action.setVisible(true);
									}else
									{
										action.undoAction();
										action.setVisible(false);
									}
								}catch(ActionTypeException ex)
								{
									
								}
							}
						}
				}
				return true;
			}catch(FailedToSelectException e)
			{
				
			}
		}
		return false;
	}

	/**
	 * If there is an interception listener attached to the tool manager, it gets a chance to prevent a tool from
	 * selecting an item.
	 * 
	 * @param actionable
	 * @param selectedModel
	 * @return true if the item should not be selected by the current tool; false for normal behaviour
	 */
	private boolean listenerIntercepts(Actionable actionable,
			Model model) {
		
		for(SelectionListener l:selectionListeners)
			if(l.select(actionable, model))
				return true;
		
		return false;
	}

	private MouseInputListener mouseListener = new MouseInputListener()
	{
		public void onButton(int button, boolean pressed, int x, int y) 
		{		
			buttonState[button] = pressed;
	
			if(currentSelectedModel != null)
			{
			/*	if (!getCurrentTool().mouseButton(button, pressed, x, y))			
				{				
				//	return;//allow tools to bypass standard selection system, and intercept mouse clicks
				}*/
			}else
			{
				if (!getFailedSelectionTool().mouseButton(button, pressed, x, y))			
				{				
					return;//allow tools to bypass standard selection system, and intercept mouse clicks
				}
			}


			if (button == 0 && pressed)// && buttonReleased){
			{
			
			/*	if(! ActionToolSettings.getInstance().getMultipleSelect().isValue())//only do this if the selected item is null or is not included in the current selection.
				{
					if(getCurrentTool() != null)
						getCurrentTool().deselect();
					currentSelectedModel = null;
				}*/
				if (selectionEnabled)
				{
		
					currentSelectedModel = null;
					Vector2f mousePos = new Vector2f(x,y);
					Ray pickRay = new Ray();
	
					DisplaySystem.getDisplaySystem().getPickRay(mousePos, StateManager.IS_AWT_MOUSE, pickRay);
	
					ArrayList<ModelIntersectionData> pickResults = new ArrayList<ModelIntersectionData>();
					StateManager.getRootModel().intersectRay(pickRay, pickResults, true);      
	
			
					
					if(!attemptToSelect(getCurrentTool(),pickResults))
					{
						//if we are in multiple select mode, dont deselect here. (this is to allow the camera to be panned while multiple selecting)
						if (!ActionToolSettings.getInstance().getMultipleSelect().isValue())
						{
							getCurrentTool().deselect();
							currentSelectedModel = null;
						}
						
						//if (!ActionToolSettings.getInstance().getMultipleSelect().isValue())
						//{//if in multiple select mode, instead form a box
							getFailedSelectionTool().deselect();
						
							getFailedSelectionTool().mouseButton(button, pressed, x, y);
						
					}else
					{
						getCurrentTool().showPrimaryEffect(true);
						getFailedSelectionTool().deselect();
					}
				}else
				{
				
					getCurrentTool().deselect();
					currentSelectedModel = null;
					getFailedSelectionTool().deselect();
					getFailedSelectionTool().mouseButton(button, pressed, x, y);
				}
				
			

			}else if (button == 0 &! pressed &! ActionToolSettings.getInstance().getMultipleSelect().isValue())
			{
				getCurrentTool().showPrimaryEffect(false);
				try{
					if(getCurrentTool().getPrimarySelection()!=null){
						SelectionInformation info = (SelectionInformation)getCurrentTool().getPrimarySelection().getAction(Action.SELECTINFO);
						//this isnt really the intended meaning of the multiple selected field, but it will restore natural behaviour for now.
						
						if(!info.isMultipleSelectable())
						{
							if(getCurrentTool() != null)
								getCurrentTool().deselect();
							currentSelectedModel = null;
						}
					}
				}catch (ActionTypeException e)
				{
					//do nothing - if no selection info is provided, continue 
				}
				
			}else
			{
				getCurrentTool().showPrimaryEffect(false);
				if (getCurrentTool() != null)
					getCurrentTool().mouseButton(button, pressed, x, y);
				else
					getFailedSelectionTool().mouseButton(button, pressed, x, y);
				
			}
		
		}

	
		public void onMove(int xDelta, int yDelta, int newX, int newY)
		{
			if(currentSelectedModel != null)//changed this may have unintended effects.
			{
				getCurrentTool().mouseMovementAction(new Vector2f(newX, newY),buttonState[0],buttonState[1]);
			}else
			{
				getFailedSelectionTool().mouseMovementAction(new Vector2f(newX, newY),buttonState[0],buttonState[1]);
			}
		}
		
		public void onWheel(int wheelDelta, int x, int y) 
		{
			if(currentSelectedModel != null)
			{
				getCurrentTool().scrollMove( wheelDelta,  x,  y);
			}else
			{
				getFailedSelectionTool().scrollMove( wheelDelta,  x,  y);
			}
			
		}
	

	};


	public boolean isButtonDown(int button)
	{
		return buttonState[button];
	}
	private ActionSettingsListener focusListener = new ActionSettingsListener()
	{
		
		public void valueChanged(SettingChangedEvent<Object> e) {
			if(getCurrentTool()!=null)
				getCurrentTool().focus();
			else
				getFailedSelectionTool().focus();
		}
	};
	
	private ActionSettingsListener copyListener = new ActionSettingsListener()
	{
		
		public void valueChanged(SettingChangedEvent<Object> e) {
			if(getCurrentTool()!=null)
				getCurrentTool().copy();
			else
				getFailedSelectionTool().copy();
		}
	};
	
	private ActionSettingsListener deleteListener = new ActionSettingsListener()
	{
		
		public void valueChanged(SettingChangedEvent<Object> e) {
			if(getCurrentTool()!=null)
				getCurrentTool().delete();
			else
				getFailedSelectionTool().delete();
		}
	};
	
	private ActionSettingsListener propertiesListener = new ActionSettingsListener()
	{
		
		public void valueChanged(SettingChangedEvent<Object> e) {
			if(getCurrentTool()!=null)
				getCurrentTool().properties();
			else
				getFailedSelectionTool().properties();
		}
	};
	

	protected SettingsListener<Boolean> xyAxisListener = new SettingsListener<Boolean>()
	{
		
		
		public void valueChanged(SettingChangedEvent<Boolean> e) {
					
		
				if(getCurrentTool()!=null)
					getCurrentTool().xyPlane(e.getNewValue());
				else
					getFailedSelectionTool().xyPlane(e.getNewValue());
			
		}
	
	};
	
	protected SettingsListener<Boolean> yzAxisListener = new SettingsListener<Boolean>()
	{
		
		
		public void valueChanged(SettingChangedEvent<Boolean> e) {
					
		
				if(getCurrentTool()!=null)
					getCurrentTool().yzPlane(e.getNewValue());
				else
					getFailedSelectionTool().yzPlane(e.getNewValue());
			
		}
	
	};
	
	
	protected SettingsListener<Boolean> xzAxisListener = new SettingsListener<Boolean>()
	{
		public void valueChanged(SettingChangedEvent<Boolean> e) {
				if(getCurrentTool()!=null)
					getCurrentTool().xzPlane(e.getNewValue());
				else
					getFailedSelectionTool().xzPlane(e.getNewValue());
			
		}
	};

	public Collection<Actionable> getSelectedActionables() {
		if (currentTool == null)
			return null;
		
		
		return currentTool.getSelectedActionables();
	}
	


	public void engageCurrentPrimaryTool() {
		setCurrentTool(getPrimaryTool());
		
	}

/*
	public void cycleSelection() {
		//this.getCurrentTool().createNewUndoPoint();
		ArrayList<Actionable> selection = new ArrayList<Actionable> (this.getSelectedActionables());
		Actionable prim = this.getCurrentTool().getPrimarySelection();
		Model model= null;
		
		try{
			
			model =((ModelInformation)  prim.getAction(Action.MODEL)).getCollisionModel();
		}catch(ActionTypeException e)
		{
			
		}
		this.getCurrentTool().deselect();
		
		this.selectActionables(selection);
		try {
			this.getCurrentTool().select(prim,model, true);
		} catch (FailedToSelectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
			
	}*/
}
