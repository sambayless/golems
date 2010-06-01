package com.golemgame.structural.structures;

import java.util.Collection;
import java.util.Iterator;

import com.golemgame.functional.WirePort;
import com.golemgame.model.Model;
import com.golemgame.model.ParentModel;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.model.spatial.SpatialModel;
import com.golemgame.model.spatial.shape.BoxModel;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.InputInterpreter;
import com.golemgame.properties.Property;
import com.golemgame.properties.Property.PropertyType;
import com.golemgame.structural.collision.CollisionMember;
import com.golemgame.structural.collision.NonPropagatingCollisionMember;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;

public class InputStructure  extends FunctionalStructure{
	private static final long serialVersionUID = 1L;

	private WirePort functionOutput;

	private SpatialModel boxModel;
	
	private NodeModel model;
	private CollisionMember collisionMember;
	private Model[] controlledModels;
	

	private Model[] visualModels;
	


	

	private InputInterpreter interpreter;
	public InputStructure(PropertyStore store) {
		super(store);
		this.interpreter = new InputInterpreter(store);
		this.model = new NodeModel(this);

		boxModel = new BoxModel(true);
		
		
		this.getModel().addChild(boxModel);
	
		
		functionOutput = new WirePort(this,false);
		
	
		functionOutput.getModel().getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI,Vector3f.UNIT_Z);

		
	
		
		
		functionOutput.getModel().getLocalTranslation().x = 0.5f;
		functionOutput.getModel().updateWorldData();
		
		
		
		super.registerWirePort(functionOutput);	

		

		model.addChild(functionOutput.getModel());

		
		this.collisionMember = new NonPropagatingCollisionMember(model,this.getActionable());
		collisionMember.registerCollidingModel(boxModel);

		boxModel.setActionable(this);
		controlledModels = new Model[]{boxModel};
		visualModels = new Model[]{boxModel};
		
		
		super.initialize();

	}

	
	@Override
	public Collection<Property> getPropertySet() {
		 Collection<Property> properties = super.getPropertySet();
		 Iterator<Property> it = properties.iterator();
		 while(it.hasNext())
		 {
			Property n = it.next();
			 if((n.getPropertyType()== PropertyType.MATERIAL) ||(n.getPropertyType()== PropertyType.PHYSICAL))
			 {
				 it.remove();
			 }
		 }
 
		 properties.add(new Property(Property.PropertyType.INPUT,this.interpreter.getStore()));
		 properties.add(new Property(Property.PropertyType.INPUT_DEVICE,this.interpreter.getInputDevice()));
		return properties;
	}


	protected Model[] getControlledModels() {

		return controlledModels;
	}

	
	protected CollisionMember getStructuralCollisionMember() {

		return collisionMember;
	}

	
	protected Model[] getAppearanceModels() {
		return visualModels;
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
}
