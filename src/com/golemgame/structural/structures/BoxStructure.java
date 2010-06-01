package com.golemgame.structural.structures;

import java.util.Collection;

import com.golemgame.model.Model;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.model.spatial.shape.BoxModel;
import com.golemgame.model.texture.TextureTypeKey.TextureShape;
import com.golemgame.mvc.PropertyState;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SimplePropertyState;
import com.golemgame.mvc.golems.BoxInterpreter;
import com.golemgame.mvc.golems.SpatialInterpreter;
import com.golemgame.properties.Property;
import com.golemgame.structural.collision.CollisionMember;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.ControlAction;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.control.CubeControlPoint;
import com.golemgame.tool.control.CubeControlPoint.ControllableBox;
import com.jme.math.Vector3f;

public class BoxStructure extends PhysicalStructure  {
	private static final long serialVersionUID =1L;
	private NodeModel model;

	protected VisualBox visualBox;
	private CollisionMember collisionMember;
	//private CollisionMember physicalCollision;
	
	private static CubeControlPoint[] controlPoints = new CubeControlPoint[]{
		new CubeControlPoint(CubeControlPoint.LEFT),new CubeControlPoint(CubeControlPoint.FORWARD),new CubeControlPoint(CubeControlPoint.TOP)
		,new CubeControlPoint(CubeControlPoint.RIGHT),new CubeControlPoint(CubeControlPoint.BACKWARD),new CubeControlPoint(CubeControlPoint.BOTTOM)};

	
	private Model[] controlledModels;

	private BoxInterpreter interpreter;
	public BoxStructure(PropertyStore store) {
		super(store);
		this.interpreter = new BoxInterpreter(store);
		this.visualBox = new VisualBox(this);
		this.model = new NodeModel(this);
		this.getModel().addChild(visualBox);

	

			this.collisionMember = new CollisionMember(model,this.getActionable());
			collisionMember.registerCollidingModel(visualBox);
		
		//if (!isFloor)
			visualBox.setActionable(this);
		
		
		controlledModels = new Model[]{visualBox};
		
		initialize();
		
	}
	
	@Override
	protected TextureShape getPrefferedShape() {
		return TextureShape.Box;
	}



	public BoxInterpreter getInterpreter() {
		return interpreter;
	}



	protected Model[] getControlledModels()
	{
		return controlledModels;
	}
	
	
	protected CollisionMember getStructuralCollisionMember() {
		return collisionMember;
	}


	
	public NodeModel getModel() {
		return model;
	}
	
	
	
	
	protected Vector3f getScale() {
		return visualBox.getLocalScale();
	}


	
	protected void setScale(Vector3f scale) {
		visualBox.getLocalScale().set(scale);
	}

	
	
	public void refreshController() {
		refresh();
		
//		super.refresh();
	}

	

	
	@Override
	public Collection<Property> getPropertySet() {
		 Collection<Property> properties = super.getPropertySet();
		 properties.add( new Property(Property.PropertyType.SCALE_BOX,this.interpreter.getStore()));
		 
		return properties;
	}



	public Action<?> getAction(Type type) throws ActionTypeException {
		if (type == Type.CONTROL)
		{
			return new Control();
		}else
			return super.getAction(type);
	}





	
	@Override
	public void refresh() {
		super.getStructuralAppearanceEffect().setPreferedShape(TextureShape.Box);
		this.visualBox.getLocalScale().set(interpreter.getExtent()).multLocal(2f);
	
		visualBox.updateWorldData();
		
		super.refresh();

	}
	
	protected class Control extends ControlAction
	{
		/*
		 * New structure: 
		 * Control turns on, or off, control mode for an object.
		 * When control is turned on, the control displays itself. 
		 * When turned off, the controls remove themselves.
		 * When a control is turned on, it automatically cleans up if it was previously on.
		 * Controls cause changes via the interface of ControllableModels.
		 * When the control is turned off, IT decides whether or not to send an update message to the model.
		 * 
		 */



		
		public boolean doAction() 
		{
		
			for (CubeControlPoint control:controlPoints)
			{
			
				control.enable(visualBox);
				control.setControlSet(controlPoints);
			}
			if (resolve)
			{
				
				centerModels();
				visualBox.updateWorldData();
				collisionMemberStateChange();
			}
			super.doAction();
			return true;
		}



		
		public boolean undoAction() {
			if (!resolve)
			{
				for (CubeControlPoint control:controlPoints)
				{
					control.disable();
				}
			}else
			{	
					centerModels();
					visualBox.updateWorldData();
					collisionMemberStateChange();
			}
			super.undoAction();
			return true;
		}
		
		
		public Actionable getControlled() {
		
			return BoxStructure.this;
		}

		
		public void setVisible(boolean visible) {
			for (CubeControlPoint control:controlPoints)
			{
				control.setVisible(visible);
			}
			
		}

		
		public Actionable[] getControlPoints() {
			return controlPoints;
		}

	}
	
	public static class VisualBox extends BoxModel implements ControllableBox
	{
		private static final long serialVersionUID = 1L;
		public static final float INIT_SIZE = 1f;
	//	private static Box box = null;
		
		private BoxStructure owner;
		
		public BoxInterpreter getInterpreter() {
			return owner.interpreter;
		}

		public PropertyState getCurrentState() {
			return new SimplePropertyState(owner.interpreter.getStore(),new String[]{BoxInterpreter.BOX_EXTENT,SpatialInterpreter.LOCALTRANSLATION,SpatialInterpreter.LOCALSCALE,SpatialInterpreter.LOCALROTATION});
		}

		public void updateModel() {

			owner.interpreter.setExtent(getLocalScale().divide(2f));
			owner.centerModels();
			owner.visualBox.updateWorldData();
			owner.interpreter.getLocalTranslation().set(owner.getModel().getLocalTranslation());
			
			owner.refreshController();
			
		}

		public VisualBox(BoxStructure owner) {
			super();
			this.owner = owner;
			
			registerSpatial();
			
		}
		

	/*	protected Spatial buildSpatial() {
			if (box == null)
			{
				box = new Box("box", new Vector3f(), INIT_SIZE/2f,INIT_SIZE/2f,INIT_SIZE/2f);		 
			}
			
			Spatial	boxModel  = new Box("box", new Vector3f(), INIT_SIZE/2f,INIT_SIZE/2f,INIT_SIZE/2f);		 

		
			boxModel.getLocalTranslation().zero();
			boxModel.getLocalRotation().loadIdentity();
			boxModel.getLocalScale().set(1,1,1);
			boxModel.setModelBound(new BoundingBox());
			boxModel.updateModelBound();
			
			return boxModel;
		}
*/
		

		
		public Vector3f getLocalTranslation() {
			// TODO Auto-generated method stub
			return super.getLocalTranslation();
		}


		
		public Vector3f getRelativeFromWorld(Vector3f worldTranslation,
				Vector3f store) {
			updateWorldData();
			return getParent().worldToLocal(worldTranslation, store);
		}

		
		public Vector3f getWorldFromRelative(Vector3f localTranslation,
				Vector3f store) {

			updateWorldData();
			return  getParent().localToWorld(localTranslation, store);
		}
		
		public PropertyStore getPropertyStore() {
			return owner.interpreter.getStore();
		}

	
	
	}

	
	

	
}
