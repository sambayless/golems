package com.golemgame.physical.ode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.golemgame.functional.WirePort;
import com.golemgame.functional.component.BComponent;
import com.golemgame.functional.component.BMind;
import com.golemgame.model.texture.TextureTypeKey.TextureShape;
import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.Reference;
import com.golemgame.mvc.View;
import com.golemgame.mvc.golems.PhysicalStructureInterpreter;
import com.golemgame.mvc.golems.SurfacePropertiesInterpreter;
import com.golemgame.mvc.golems.WirePortInterpreter;
import com.golemgame.physical.PhysicsComponent;
import com.golemgame.physical.ode.compile.OdePhysicsEnvironment;
import com.golemgame.structural.DesignViewFactory;
import com.golemgame.structural.MaterialWrapper;
import com.golemgame.structural.StructuralAppearanceEffect;
import com.golemgame.structural.collision.CollisionMember;
import com.golemgame.structural.structures.decorators.PhysicalDecorator;
import com.jme.math.Vector3f;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.material.Material;

public class OdePhysicalStructure extends OdeStructure implements View{
	private PhysicalStructureInterpreter interpreter;	

	private MaterialWrapper materialWrapper;

	private StructuralAppearanceEffect structuralAppearanceEffect;

	private List<PhysicalDecorator> physicalDecorators = new ArrayList<PhysicalDecorator>();
	
	private final List<WirePort> wireports = new ArrayList<WirePort>();
	
	public MaterialWrapper getMaterialWrapper() {
		return materialWrapper;
	}

	public StructuralAppearanceEffect getStructuralAppearanceEffect() {
		return structuralAppearanceEffect;
	}

	
	public List<WirePort> getWirePorts() {
		return wireports;
	}

	public OdePhysicalStructure(PropertyStore store)
	{
		super(store);
		interpreter = new PhysicalStructureInterpreter(store);
		
		refreshDecorators();
		
		this.materialWrapper = new MaterialWrapper(interpreter.getMaterialProperties());
		this.materialWrapper.refresh();

		this.structuralAppearanceEffect = new StructuralAppearanceEffect(interpreter.getAppearanceStore());
		structuralAppearanceEffect.setPreferedShape(getPrefferedTextureShape());
		this.structuralAppearanceEffect.refresh();
	}
	
	protected TextureShape getPrefferedTextureShape()
	{
		return TextureShape.Plane;
	}
	
	public Material getMaterial()
	{
		return this.materialWrapper.constructMaterial();
	}
	
	public boolean isPropagating()
	{
		return true;
	}
	
	private void refreshDecorators() {
		CollectionType decoratorCollection = interpreter.getPhysicalDecorators();
		
		for(DataType data:decoratorCollection.getValues())
		{
			if(! (data instanceof PropertyStore))
					continue;

			PhysicalDecorator component = (PhysicalDecorator) DesignViewFactory.constructView((PropertyStore)data);
			this.physicalDecorators.add(component);
			component.refresh();

		}

	}
	
	public Collection<PhysicalDecorator> getPhysicalDecorators()
	{
		return physicalDecorators;
	}

	public void buildCollidable(CollisionMember physicsCollision) {
		
		
	}
	
	public float buildCollisionGeometries(PhysicsNode physicsNode,Collection<PhysicsComponent> components,
			Vector3f store) {
		return 0f;
	}
	public void buildRelationships( Map<OdePhysicalStructure, PhysicsNode> physicalMap, OdePhysicsEnvironment compiledEnvironment) 
	{
		
	}

	public boolean isStatic() {
		return interpreter.isStatic();
	}
	
	private final static Collection<OdePhysicalStructure> emptySubstructures = new ArrayList<OdePhysicalStructure>();

	public Collection<OdePhysicalStructure> getSubstructures() {
		return emptySubstructures;
	}
	
	public void buildMind(BMind mind, Map<OdePhysicalStructure, PhysicsNode> physicalMap, Map<Reference,BComponent> wireMap, OdePhysicsEnvironment environment ) {
	
		
	}

	public void getWires(Collection<PropertyStore> wires) {
		for(DataType store:interpreter.getOutputs().getValues())
		{
			if(store.getType()==DataType.Type.PROPERTIES)
			{
				WirePortInterpreter interpreter = new WirePortInterpreter((PropertyStore)store);
				
				for(DataType wire:interpreter.getWires().getValues())
				{
					if(wire.getType()==DataType.Type.PROPERTIES)
					{
						wires.add((PropertyStore)wire);
					}
				}
				
			}
		}
		//for now, there are no wires in inputs.. but just in case:
		for(DataType store:interpreter.getInputs().getValues())
		{
			if(store.getType()==DataType.Type.PROPERTIES)
			{
				WirePortInterpreter interpreter = new WirePortInterpreter((PropertyStore)store);
				
				for(DataType wire:interpreter.getWires().getValues())
				{
					if(wire.getType()==DataType.Type.PROPERTIES)
					{
						wires.add((PropertyStore)wire);
					}
				}
				
			}
		}
		
	}
	
	public void buildSound(OdePhysicsComponent physicsComponent)
	{
		SurfacePropertiesInterpreter surfaceInterp = new SurfacePropertiesInterpreter(interpreter.getSurfaceProperties());
		if(!surfaceInterp.isEnabled())
			return;
		
		physicsComponent.setSoundSurface(surfaceInterp.getSoundSurface());
		physicsComponent.setLinearResonanceScalar(this.getLinearResonanceScalar());
	}
	
/*	private static DistanceModel inverseDistance = new InverseSquareDistanceModel();
	public SoundComponent constructSoundComponent(Scene scene,OdePhysicsComponent physicsComponent)
	{
		SurfacePropertiesInterpreter surfaceInterp = new SurfacePropertiesInterpreter(interpreter.getSurfaceProperties());
		if(!surfaceInterp.isEnabled())
			return null;
		
		physicsComponent.setSoundSurface(surfaceInterp.getSoundSurface());
		physicsComponent.setLinearResonanceScalar(this.getLinearResonanceScalar());
		ModalData data = OdeSoundManager.getModalData(surfaceInterp.getSoundSurface());
		if (data==null)
			return null;
		
		
		SoundComponent comp = getSoundComponent(physicsComponent);
		if(comp==null)
			return null;

		
		comp.setBody(new Body());
		
		WhiteFunction whitefun = new WhiteFunction();
		FunctionSurface whitesurf = new FunctionSurface(scene);
		
		whitesurf.setFun(whitefun); // White noise surface texture.
		whitesurf.setContactMasterGain(32000.0f); 
		whitesurf.setCutoffFreqAtRoll(10.0f); 												
		whitesurf.setCutoffFreqRate(1000.0f); 												
		whitesurf.setCutoffFreq2AtRoll(10.0f); 
		whitesurf.setCutoffFreq2Rate(1000.0f);
		whitesurf.setContactDirectGain(0.0f); 											
		whitesurf.setContactAmpLimit(1000);
		
		comp.getBody().setSurface(whitesurf);
		comp.getBody().setDistanceModel(inverseDistance);
	
	
		if(data!=null)
		{
			ModalResonator mr  = new ModalResonator(scene);
			mr.setData(data);
		
			mr.setQuietLevel(1.0f); // Determines at what rms envelope level
		// a resonator will be
		// faded out when no longer in contact, to save cpu.
		// Make bigger to save more cpu, but possibly truncate decays
		// notceably.

			// mr.setnActiveModes(10); // Can trade detail for speed.
			mr.setAuxAmpScale(0.01f);
			float freq = comp.getFrequencyScale();
			mr.setAuxFreqScale(freq/2f);
		//	System.out.println(freq);
			//mr.setAuxFreqScale(0.6f/comp1.getMass());
			mr.setAuxDampScale(1f);
			
			comp.getBody().setResonator(mr); // NB Possible to have several bodies using
								// one res for efficiency.
		
		}
		comp.getBody().setSurface(whitesurf);
	
		comp.getBody().setCurrentDistance(Float.NaN);
		
		return comp;
	}*/
	
	public float getLinearResonanceScalar()
	{
		return 1f;
	}

	@Override
	public void remove() {
		super.remove();
		materialWrapper.remove();
		structuralAppearanceEffect.remove();
		for (PhysicalDecorator p :physicalDecorators)
			p.remove();
		
		physicalDecorators.clear();
		for(WirePort w:wireports)
			w.remove();
		wireports.clear();
		materialWrapper = null;
	}
	
}
