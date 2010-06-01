package com.golemgame.physical.ode;

import java.util.Collection;

import com.golemgame.model.Model;
import com.golemgame.model.texture.TextureTypeKey.TextureShape;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GhostInterpreter;
import com.golemgame.physical.PhysicsComponent;
import com.golemgame.structural.StructuralAppearanceEffect;
import com.golemgame.structural.collision.CollisionMember;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsNode;

public class OdeGhostStructure extends OdeStructure {

	private StructuralAppearanceEffect structuralAppearanceEffect;
	private final GhostInterpreter interpreter;
	protected GhostInterpreter getInterpreter() {
		return interpreter;
	}
	public OdeGhostStructure(PropertyStore store) {
		super(store);
		this.interpreter = new GhostInterpreter(store);
		
		this.structuralAppearanceEffect = new StructuralAppearanceEffect(interpreter.getAppearanceStore());
		structuralAppearanceEffect.setPreferedShape(getPrefferedShape());
		this.structuralAppearanceEffect.refresh();
	}
	public void buildCollidable(CollisionMember collidable) {
	
	}
	
	protected TextureShape getPrefferedShape() {
		return TextureShape.Plane;
	}
	
	public StructuralAppearanceEffect getStructuralAppearanceEffect() {
		return structuralAppearanceEffect;
	}

	
	private Vector3f parentTranslation;
	private Quaternion parentRotation;
	
	private Model sensorField = null;
	
	public Model getSensorField() {
		return sensorField;
	}
	protected void setSensorField(Model sensorField) {
		this.sensorField = sensorField;
	}
	public Vector3f getParentTranslation() {
		return parentTranslation;
	}
	public void setParentTranslation(Vector3f parentTranslation) {
		this.parentTranslation = parentTranslation;
	}
	public Quaternion getParentRotation() {
		return parentRotation;
	}
	public void setParentRotation(Quaternion parentRotation) {
		this.parentRotation = parentRotation;
	}

	public float buildCollisionGeometries(PhysicsNode physicsNode,Collection<PhysicsComponent> components,
			Vector3f store) {
		
		return 0;
	}
	public PhysicsCollisionGeometry getGhost() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public void setShowSensorField(boolean show)
	{
		if(this.getSensorField()!=null)
			this.getSensorField().setVisible(show);
	}
}
