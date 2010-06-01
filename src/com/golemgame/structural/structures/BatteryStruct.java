package com.golemgame.structural.structures;

import java.util.Collection;

import com.golemgame.functional.FunctionSettings;
import com.golemgame.functional.WirePort;
import com.golemgame.model.Model;
import com.golemgame.model.ParentModel;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.model.spatial.SpatialModel;
import com.golemgame.model.spatial.SpatialModelImpl;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.BatteryInterpreter;
import com.golemgame.mvc.golems.functions.FunctionSettingsInterpreter.FunctionType;
import com.golemgame.properties.Property;
import com.golemgame.structural.collision.CollisionMember;
import com.golemgame.structural.collision.NonPropagatingCollisionMember;
import com.jme.bounding.BoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Cylinder;


public class BatteryStruct extends FunctionalStructure   {
	private static final long serialVersionUID = 1L;
	


	
	private WirePort output;
	private WirePort input;
	private SpatialModel visualSource;
	private NodeModel model;
	private CollisionMember collisionMember;
	private Model[] controlledModels;
	
	private FunctionSettings settings;
	
	//private UnivariateRealFunction function;
	
	//private FunctionType functionType = FunctionType.Function;
	
/*	private float threshold = 0;
	private boolean thresholdInverted;
	
	private boolean interactWithUser = false;
	private int interactionKeyCode;*/
/*	public int getInteractionKeyCode() {
		return interpreter.getInteractionKey();
	}

	
	public SwitchType getSwitchType()
	{
		return interpreter.getSwitchType();
	}
	
	public void setSwitchInteractionType (SwitchType type)
	{
		interpreter.setSwitchType(type);
		interpreter.refresh();
	}*/
/*	
	public void setInteractionKeyCode(int interactionKeyCode) {
		interpreter.setInteractionKey(interactionKeyCode);
		interpreter.refresh();
	}
*/
	
	public FunctionType getFunctionType() {
		return settings.getFunctionType();
	}

	
	
	
	@Override
	public void refresh() {
		super.refresh();
	//	this.input.setReference(interpreter.getInput());
		//this.output.setReference(interpreter.getOutput());

		
		if (this.settings == null || ! this.settings.getStore().equals(interpreter.getFunctionStore()))
		{
			this.settings = new FunctionSettings(interpreter.getFunctionStore());	
			
		}
		settings.setMinX(0f);//esnure this is the case... fix for some save files...
		settings.refresh();
		
		
	}

	public void setFunctionType(FunctionType functionType) {
		this.settings.setFunctionType(functionType);
		interpreter.refresh();
		
	}
	private BatteryInterpreter interpreter;
	public BatteryStruct( PropertyStore store) {
		super(store);
		this.interpreter = new BatteryInterpreter(store);
		this.model = new NodeModel(this);
		//settings = new FunctionSettings(0,1,-1,1,true);
		
	
		//this.function = new PolynomialFunction(new double[]{1});
		
		//leeping these anon versions for serialization backwards compatability
		this.visualSource = new SpatialModelImpl(true)
		{
			private static final long serialVersionUID = 1L;
			
			protected Spatial buildSpatial() {
				Node cylNode = new Node();
				Cylinder main = new Cylinder("Battery", 30, 30, 0.3f,1f, true);
				cylNode.attachChild(main);
				cylNode.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_X);
				
		
				cylNode.setModelBound(new BoundingBox());
				cylNode.updateModelBound();
				
				return cylNode;
			}
			
	
		};
	

		SpatialModel wirePortModel = new SpatialModelImpl(true)
		{
			private static final long serialVersionUID = 1L;
			
			protected Spatial buildSpatial() {
				Node cylNode = new Node();
				Cylinder top = new Cylinder("Battery", 5, 5, 0.15f,0.1f, true);
				//top.getLocalTranslation().z -= top.getHeight()/2f;
				top.setCullMode(SceneElement.CULL_ALWAYS);
				cylNode.attachChild(top);
				cylNode.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_X);
				
				cylNode.setModelBound(new BoundingBox());
				cylNode.updateModelBound();
				return cylNode;
			}

		};
	
		SpatialModel wirePortFacade = new SpatialModelImpl(true)
		{
			private static final long serialVersionUID = 1L;
			
			protected Spatial buildSpatial() {
				Node cylNode = new Node();
				Cylinder top = new Cylinder("Battery", 30, 30, 0.15f,0.1f, true);
				//top.getLocalTranslation().z -= top.getHeight()/2f;
				cylNode.attachChild(top);
				cylNode.setModelBound(new BoundingBox());
				cylNode.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_X);
				
				cylNode.updateModelBound();
				cylNode.setIsCollidable(false);
				return cylNode;
			}
	
			
		};
		
		
		
		 wirePortModel = new BatteryWirePortModel();
		 wirePortFacade = new BatteryWirePortFacade();
		visualSource = new BatteryModel();
		
		
		
		
		
		this.getModel().addChild(visualSource);
		output = new WirePort(this,false,wirePortModel,wirePortFacade);
		output.getModel().getLocalTranslation().y = 0.5f + 0.095f/2f;//offset it upwards
		output.getModel().updateWorldData();
		model.addChild(output.getModel());
		
		input = new WirePort(this, true,new BatteryInputModel());
		input.getModel().getLocalTranslation().x = 0.25f + 0.1f/2f;//offset it upwards
		input.getModel().updateWorldData();
		model.addChild(input.getModel());
	
		super.registerWirePort(input);
		super.registerWirePort(output);
		
	
			this.collisionMember = new NonPropagatingCollisionMember(model,this.getActionable());
			collisionMember.registerCollidingModel(visualSource);
	
			

		
		visualSource.setActionable(this);
		controlledModels = new Model[]{visualSource};
		super.initialize();
		
	

	}

	
	public static class BatteryModel extends SpatialModelImpl
	{
		
		private static final long serialVersionUID = 1L;
		
		
		public BatteryModel() {
			super(true);
	
		}


	

		
		protected Spatial buildSpatial() {
			Node cylNode = new Node();
			Cylinder main = new Cylinder("Battery", 30, 30, 0.3f,1f, true);
			cylNode.attachChild(main);
			cylNode.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_X);
			
	
			cylNode.setModelBound(new BoundingBox());
			cylNode.updateModelBound();
			
			return cylNode;
		}
		
	};
	
	public static class BatteryWirePortModel extends SpatialModelImpl
	{
		private static final long serialVersionUID = 1L;
		
		
		public BatteryWirePortModel() {
			super(true);
			
		}



		
		protected Spatial buildSpatial() {
			Node cylNode = new Node();
			Cylinder top = new Cylinder("Battery", 5, 5, 0.15f,0.1f, true);
			//top.getLocalTranslation().z -= top.getHeight()/2f;
			top.setCullMode(SceneElement.CULL_ALWAYS);
			cylNode.attachChild(top);
			cylNode.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_X);
			
			cylNode.setModelBound(new BoundingBox());
			cylNode.updateModelBound();
			return cylNode;
		}
	};
	
	public static class BatteryWirePortFacade extends SpatialModelImpl
	{

		public BatteryWirePortFacade() {
			super(false);
		
		}
	
		private static final long serialVersionUID = 1L;
		
		protected Spatial buildSpatial() {
			Node cylNode = new Node();
			Cylinder top = new Cylinder("Battery", 30, 30, 0.15f,0.1f, true);
			//top.getLocalTranslation().z -= top.getHeight()/2f;
			cylNode.attachChild(top);
			cylNode.setModelBound(new BoundingBox());
			cylNode.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_X);
			
			cylNode.updateModelBound();
			cylNode.setIsCollidable(false);
			return cylNode;
		}
	};
	
	public static class BatteryInputModel extends SpatialModelImpl
	{
		private static final long serialVersionUID = 1L;
		
		
		public BatteryInputModel() {
			super(true);
			
		}


		protected Spatial buildSpatial() {
			Node switchNode = new Node();
			Box box = new Box("Switch", new Vector3f(), 0.025f,0.3f,0.1f);
			switchNode.attachChild(box);
			switchNode.setModelBound(new BoundingBox());
			switchNode.updateModelBound();
			return switchNode;
		}
	};
	
	
	protected Model[] getControlledModels() {

		return controlledModels;
	}

	
	protected CollisionMember getStructuralCollisionMember() {

		return collisionMember;
	}

	
	public ParentModel getModel() {

		return model;
	}

	
	public boolean isMindful() {
		return true;
	}

	
	public boolean isPhysical() {
		return false;
	}

	




	@Override
	public Collection<Property> getPropertySet() {
		 Collection<Property> properties = super.getPropertySet();

		 properties.add(new Property(Property.PropertyType.FUNCTION_TIMED,this.interpreter.getFunctionStore()));		 
		 properties.add(new Property(Property.PropertyType.BATTERY,this.interpreter.getStore()));		 
		 return properties;
	}
	

	



	
/*	public float getThreshold() {
		return interpreter.getThreshold();
	}

	public void setThreshold(float t) {
		if(t>1f)
			t = 1f;
		else if (t < -1f)
			t = -1f;
		
		interpreter.setThreshold(t);
		interpreter.refresh();
	}*/

/*
	public boolean isInteractWithUser() {
		return interpreter.interactsWithUser();
	}

	public void setInteractWithUser(boolean interactWithUser) {
		interpreter.setInteractsWithUser(interactWithUser);
	}


	public KeyboardInteractionType getInteractionType() {
		return interpreter.getInteractionType();
	}

	public void setInteractionType(KeyboardInteractionType interactionType) {
		interpreter.setInteractionType(interactionType);
	}
*/
}
