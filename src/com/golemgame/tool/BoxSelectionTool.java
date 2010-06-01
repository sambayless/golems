package com.golemgame.tool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.golemgame.model.Model;
import com.golemgame.model.spatial.SpatialModel;
import com.golemgame.states.StateManager;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.information.SelectionInformation;
import com.jme.math.Plane;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.AbstractCamera;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.system.DisplaySystem;

/**
 * This tool forms a box, starting at the mouse selection, and onwards until the mouse is let go.
 * Then it selects all items in that box.
 * @author Sam
 *
 */
public class BoxSelectionTool extends CameraTool{
	
	private Vector2f boxStart =  new Vector2f();
	private Vector2f boxEnd =  new Vector2f();
	protected BoxSelectionEffect selectionEffect = new BoxSelectionEffect();
	
	public boolean isSelected()
	{
		return true;
	}





	public boolean mouseButton(int button, boolean pressed, int x, int y) {
		//on mouse down begin creating the box.
		//on mouse up, switch to the movement tool
	/*	if (StateManager.getToolManager().getCurrentTool().getPrimarySelection() != null)
			return true;*/
		if(!ActionToolSettings.getInstance().getMultipleSelect().isValue())
		{
			this.selectionEffect.setEngage(false);
			return super.mouseButton(button, pressed, x, y);
		}
		if (button == 0)
		{
			if (pressed)
			{
				boxStart.set(x,y);
				boxEnd.set(x,y);
			
				this.selectionEffect.setBounds(boxStart, boxEnd);
				this.selectionEffect.setEngage(true);
				return true;
			}else if (!pressed)
			{
			
				this.selectionEffect.setEngage(false);
		
				// Find and select items as appropriate
				 
				
			/*	SpatialModel selectionBox = new SpatialModelImpl(true)
				{

					private static final long serialVersionUID = 1L;

					@Override
					protected Spatial buildSpatial() {
						Box box = new Box("Selection Box", new Vector3f(), 0.5f,0.5f,0.5f);
						box.setModelBound(new BoundingBox());
						box.updateModelBound();
						//this actually has to be a frustrum, not a box... 
						//box.updateRenderState();
						//box.setCullMode(SceneElement.CULL_ALWAYS);
						return box;
					}
					
				};
				
				selectionBox.setLoadable(false);
				*/
				try{
					/*selectionBox.getLocalScale().set(selectionEffect.getModel().getLocalScale());
					selectionBox.getLocalScale().z = 100;
				
					selectionBox.refreshLockedData();
					selectionBox.getSpatial().updateWorldBound();
					selectionBox.getLocalRotation().set(StateManager.getCameraManager().getCameraRotation());
					selectionBox.updateWorldData();*/
					//StateManager.getRootModel().addChild(selectionBox);
				/*	
					ArrayList<ModelCollision> collisions = new ArrayList<ModelCollision>();
					Set<Model> ignoreSet = new HashSet<Model>();
					ignoreSet.add(selectionBox);
					selectionBox.findCollisions(StateManager.getRootModel(),ignoreSet,collisions );
									Collection<Actionable> actionables = new ArrayList<Actionable>();
					for(ModelCollision collision:collisions)
					{
						if(collision.getTarget().getActionable() != null)
							actionables.add(collision.getTarget().getActionable());
					}*/
					Vector2f topLeft = new Vector2f( (boxStart.x < boxEnd.x)? boxStart.x:boxEnd.x,(boxStart.y > boxEnd.y)? boxStart.y:boxEnd.y);
					Vector2f bottomRight = new Vector2f( (boxStart.x > boxEnd.x)? boxStart.x:boxEnd.x,(boxStart.y < boxEnd.y)? boxStart.y:boxEnd.y);
					
					boxStart.set(Float.MIN_VALUE,Float.MIN_VALUE);//clear these values to prevent accidental reselection, which causes various problems with selection (happens rarely but reproducibly)
					boxEnd.set(Float.MIN_VALUE,Float.MIN_VALUE);
					
					ArrayList<Model> collisions = new ArrayList<Model>();
					Set<Actionable> actionables = new HashSet<Actionable>();//important: sometimes, the collision detection seems to duplicate collisions?
					
					modelsInFrustrum(((SpatialModel)StateManager.getRootModel()).getSpatial(), buildFrustrum(topLeft,bottomRight),collisions);
					
					for(Model collision:collisions)
					{
						if(collision.getActionable() != null)
						{
							Actionable actionable = collision.getActionable();
							boolean isSelectable = false;
							try{
								SelectionInformation info = (SelectionInformation) actionable.getAction(Action.SELECTINFO);
								isSelectable = info.isMultipleSelectable() && info.isSelectable();	
								//ensure the model is selectable before continuing.	
							}catch(ActionTypeException e)
							{
								
							}
							if (isSelectable)
								actionables.add(actionable);
				//			System.out.println(collision.getActionable());
						}
					}
					
					if (!actionables.isEmpty())
					{
					//	System.out.println("SELECTION");
						StateManager.getToolManager().engageCurrentPrimaryTool();
					//
						StateManager.getToolManager().selectActionables(actionables);
					}
				}finally{
				//	selectionBox.detachFromParent();
				}
	
			}
		}else
		{
			return super.mouseButton(button,pressed, x, y);
			
		}
		return false;
	}

	public void mouseMovementAction(Vector2f mousePos, boolean left,
			boolean right) {
		if(!ActionToolSettings.getInstance().getMultipleSelect().isValue())
		{
			super.mouseMovementAction(mousePos, left, right);
			return;
		}
		if (left)
		{
		boxEnd.set(mousePos);
		this.selectionEffect.setBounds(boxStart, boxEnd);
		}else
		{
			super.mouseMovementAction(mousePos, left, right);

		}
	}
	
	/**
	 * Build a frustrum in the perspective of the camera, but starting with the rectangle
	 * defined by the two vectors
	 * @param topLeft
	 * @param bottomRight
	 * @return
	 */
	public Plane[] buildFrustrum(Vector2f topLeft, Vector2f bottomRight)
	{
		AbstractCamera camera =(AbstractCamera) DisplaySystem.getDisplaySystem().getRenderer().getCamera();

		
		Plane left = new Plane();
		Plane right = new Plane();
		Plane bottom = new Plane();
		Plane top = new Plane();
		Plane near = new Plane();
		Plane far = new Plane();
	
		
		 
	    near.setConstant(camera.worldPlane[AbstractCamera.NEAR_PLANE].getConstant());
        near.setNormal(camera.worldPlane[AbstractCamera.NEAR_PLANE].getNormal());
        
        far.setConstant(camera.worldPlane[AbstractCamera.FAR_PLANE].getConstant());
        far.setNormal(camera.worldPlane[AbstractCamera.FAR_PLANE].getNormal());        
        
		
		//construct 4 pick rays, one for each corner
		//use them to create the planes.
		
		 Ray topLeftRay = new Ray();		
		 DisplaySystem.getDisplaySystem().getPickRay(topLeft,StateManager.IS_AWT_MOUSE, topLeftRay);
		 Ray bottomRightRay = new Ray();		
		 DisplaySystem.getDisplaySystem().getPickRay(bottomRight,StateManager.IS_AWT_MOUSE, bottomRightRay);
		 Ray topRightRay = new Ray();		
		 DisplaySystem.getDisplaySystem().getPickRay(new Vector2f(bottomRight.x,topLeft.y),StateManager.IS_AWT_MOUSE, topRightRay);
		 Ray bottomLeftRay = new Ray();		
		 DisplaySystem.getDisplaySystem().getPickRay(new Vector2f(topLeft.x, bottomRight.y),StateManager.IS_AWT_MOUSE, bottomLeftRay);
				
		
		 
		 Vector3f leftPerp = topLeftRay.origin.subtract(bottomLeftRay.origin);
		 leftPerp.normalizeLocal();
		 left.setNormal(topLeftRay.getDirection().cross(leftPerp));
		left.setConstant(topLeftRay.origin.dot(left.getNormal()));
		
		 Vector3f rightPerp = topRightRay.origin.subtract(bottomRightRay.origin);
		 rightPerp.normalizeLocal();
		 right.setNormal(rightPerp.cross(topRightRay.getDirection()));
		 right.setConstant(topRightRay.origin.dot(right.getNormal()));
	
        
        
		 Vector3f topPerp = topLeftRay.origin.subtract(topRightRay.origin);
		 topPerp.normalizeLocal();
		 top.setNormal(topPerp.cross(topLeftRay.getDirection()));
		 top.setConstant(topLeftRay.origin.dot(top.getNormal()));
		
		 Vector3f bottomPerp = bottomLeftRay.origin.subtract(bottomRightRay.origin);
		 bottomPerp.normalizeLocal();
		 bottom.setNormal(bottomRightRay.getDirection().cross(bottomPerp));
		 bottom.setConstant(bottomRightRay.origin.dot(bottom.getNormal()));
        
        
    //    left.setConstant(camera.worldPlane[AbstractCamera.LEFT_PLANE].getConstant());
    //    left.setNormal(camera.worldPlane[AbstractCamera.LEFT_PLANE].getNormal());        
        
     //   right.setConstant(camera.worldPlane[AbstractCamera.RIGHT_PLANE].getConstant());
   //     right.setNormal(camera.worldPlane[AbstractCamera.RIGHT_PLANE].getNormal());
        
   //   top.setConstant(camera.worldPlane[AbstractCamera.TOP_PLANE].getConstant());
    //   top.setNormal(camera.worldPlane[AbstractCamera.TOP_PLANE].getNormal());
        
    //   bottom.setConstant(camera.worldPlane[AbstractCamera.BOTTOM_PLANE].getConstant());
   //     bottom.setNormal(camera.worldPlane[AbstractCamera.BOTTOM_PLANE].getNormal());
          
        //push the far frustrum way back
      //  far.setConstant(-100000);
       // near.setConstant(0);
        
        //frustrum left is the left side of the selection box.
        
   //   left.setConstant(left.getConstant() *( bottomRight.x / DisplaySystem.getDisplaySystem().getWidth() * 2f));
     //  right.setConstant(right.getConstant() * topLeft.x / DisplaySystem.getDisplaySystem().getWidth()* 2f);
      //  top.setConstant(top.getConstant() * topLeft.y/ DisplaySystem.getDisplaySystem().getHeight()* 2f);
     //   bottom.setConstant(bottom.getConstant() * bottomRight.y/ DisplaySystem.getDisplaySystem().getHeight()* 2f);
        //left.setConstant(0);
        //right.setConstant(right.getConstant() * 0.5f);
        return new Plane[]{left, right, top, bottom, near, far};
	}
	
	public void modelsInFrustrum(Spatial root,Plane[] frustrum, ArrayList<Model> models)
	{
		if (root.getWorldBound() == null)
		{
/*			Model m = SpatialModel.getSpatialModel(root);
			if (m != null)
			{
				models.add(m);
			}*/
			return;
		}
		boolean onPlane = false;
		for (Plane plane:frustrum)
		{
			int side = root.getWorldBound().whichSide(plane);
			
			if (side == Plane.NEGATIVE_SIDE)
				return;
			if (side == Plane.NO_SIDE)
				onPlane = true;
		}
		if (! onPlane)
		{
			Model m = SpatialModel.getSpatialModel(root);
			if (m != null)
			{
				models.add(m);
			}
		}
		if (root instanceof Node && ((Node)root).getChildren() != null)
		{
			for (Spatial child:((Node)root).getChildren())
			{
				if (child != null)
					modelsInFrustrum(child,frustrum,models);
			}
		}
		
	}
	
}
