package com.golemgame.mechanical;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import com.golemgame.functional.FunctionRepository;
import com.golemgame.model.ParentModel;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SustainedView;
import com.golemgame.mvc.golems.FunctionSettingsRepositoryInterpreter;
import com.golemgame.mvc.golems.LayerRepositoryInterpreter;
import com.golemgame.mvc.golems.MachineInterpreter;
import com.golemgame.mvc.golems.MachineSpaceInterpreter;
import com.golemgame.mvc.golems.ParticleEffectRepositoryInterpreter;
import com.golemgame.physical.PhysicsEnvironment;
import com.golemgame.structural.DesignViewFactory;
import com.golemgame.structural.structures.particles.ParticleEffectRepository;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.action.mvc.AddComponentAction;
import com.golemgame.tool.action.mvc.MergeComponentAction;
import com.golemgame.tool.action.mvc.RemoveComponentAction;
import com.jme.scene.Spatial;

/**
 * This class contains a tree of physical's that machines should collide their physics against.
 * @author Sam
 *
 */
public class MachineSpace implements Serializable, SustainedView,Actionable
{
	private static final long serialVersionUID = 1L;	
	private ParentModel model = new NodeModel();
//	private ArrayList<Physical> physicals = new ArrayList<Physical>();
	private ArrayList<StructuralMachine> machines= new ArrayList<StructuralMachine>();
	private MachineSpaceSettings machineSpaceSettings;
	private ParticleEffectRepository particleEffectRepository = null;
	private FunctionRepository functionRepository = null;
	private SkyboxView skyboxView;
	private ImageView imageView;
	//private Map<DataType,StructuralMachine> machines = new HashMap<DataType,StructuralMachine>();
	
	public SkyboxView getSkyboxView() {
		return skyboxView;
	}



	public MachineSpaceSettings getMachineSpaceSettings() {
		return machineSpaceSettings;
	}


	
	public MachineSpaceInterpreter getInterpreter() {
		return interpreter;
	}



	/**
	 * Return the collection of environments belonging to the machines in this space.
	 * @return
	 */
	public Collection<PhysicsEnvironment> getEnvironments()
	{
		ArrayList<PhysicsEnvironment> envs = new ArrayList<PhysicsEnvironment>();
		for (StructuralMachine machine:this.getMachines())
		{
			envs.add(machine.getEnvironment());
		}
		return envs;
	}
	


	public void update()
	{
		this.getModel().updateModelData();
	}

	private void addMachine(StructuralMachine machine)
	{
		
		if (!machines.contains(machine))
		{
			//machines.put(machine.getStore(), machine);
			machines.add(machine);	
		}
		
		getModel().addChild(machine.getModel());
		machine.addToSpace(this);
	}
	
	private void removeMachine(StructuralMachine machine)
	{
			
			machines.remove(machine);
			getModel().detachChild(machine.getModel());
			machine.removeFromSpace(this);
	}
	
	
/*	*//**
	 * Add a physical to the general collision space (for example, a floor).
	 * @param physical
	 *//*
	public void addPhysical(Physical physical)
	{
		if (!physicals.contains(physical))
			{
			physicals.add(physical);
			}	
	}*/
	/*
	private NodeModel buildPhysicsModel(final PhysicsNode physicsNode)
	{
		NodeModel physicsModel = new NodeModel()
		{
			private static final long serialVersionUID = 1L;
			
			protected Spatial buildSpatial() {
				return physicsNode;
			}
			private void writeObject(ObjectOutputStream out) throws IOException
			{
				throw new NotSerializableException();
			}
			private void readObject(ObjectInputStream in) throws IOException
			{
				throw new NotSerializableException();
			}
			
		};
		physicsModel.setUpdateLocked(false);
		return physicsModel;
	}
*/
	public ParentModel getModel() {
		return model;
	}
/*
	public Collection<Physical> getPhysicals() {
		return physicals;
	}*/

	public ArrayList<StructuralMachine> getMachines() {
		return machines;
	}


	




	
	public MachineSpace(MachineSpaceInterpreter interpreter)
	{
		this.interpreter = interpreter;
		interpreter.getStore().setSustainedView(this);


	}
	
	public void remove() {
		for(StructuralMachine machine:this.getMachines())
			machine.remove();
	}
	public void refresh() {
		if (machineSpaceSettings == null || !machineSpaceSettings.getStore().equals(interpreter.getSettings()))
		{
			machineSpaceSettings = new MachineSpaceSettings(interpreter.getSettings());
			machineSpaceSettings.refresh();
		}
	
		if (particleEffectRepository == null || !particleEffectRepository.getStore().equals(interpreter.getParticleEffectRepository()))
		{
			particleEffectRepository = new ParticleEffectRepository(interpreter.getParticleEffectRepository());
			particleEffectRepository.refresh();
		}
		
		if (functionRepository == null || !functionRepository.getStore().equals(interpreter.getFunctionRepository()))
		{
			functionRepository = new FunctionRepository(interpreter.getFunctionRepository());
			functionRepository.refresh();
		}
		if (skyboxView == null || !skyboxView.getStore().equals(interpreter.getSkybox()))
		{
			skyboxView = new SkyboxView(interpreter.getSkybox());
			skyboxView.refresh();
		}
		if (imageView == null || !imageView.getStore().equals(interpreter.getImage()))
		{
			imageView = new ImageView(interpreter.getImage());
			imageView.refresh();
		}
		
		//ensure that this model matches the original...
		//for highlevel components, this will be expensive!
		
		//ensure that the machines in this space exactly match those in the store;
		//refresh or create new machines as neccesary
		
		CollectionType machineCollection = this.interpreter.getMachines();
		ArrayList<StructuralMachine> machinesToRemove = new ArrayList<StructuralMachine>();
	
		for(StructuralMachine machine:this.getMachines())
		{
			if (!machineCollection.getValues().contains(machine.getStore()))
			{
				//delete this machine
				machinesToRemove.add(machine);
			}
		}
		
		for(StructuralMachine machine:machinesToRemove)
		{
			this.removeMachine(machine);
		}
		
		for(DataType data:machineCollection.getValues())
		{
			if (!(data instanceof PropertyStore))
				continue;
			boolean exists = false;
			for(StructuralMachine machine:getMachines())
			{
				if(machine.getStore().equals(data))
				{
					exists = true;
					break;
				}
			}
			if(!exists)
			{
				StructuralMachine machine= (StructuralMachine) DesignViewFactory.constructView((PropertyStore)data);
				this.addMachine(machine);
				machine.refresh();
			}
		}
		
	}
	
	private MachineSpaceInterpreter interpreter;

	public PropertyStore getStore() {
		return interpreter.getStore();
	}



	public StructuralMachine createMachine() {		
		//construct a machine model, add it to this model, create a view for it, and return that view
		StructuralMachine machine = new StructuralMachine(interpreter.constructMachine());
		this.addMachine(machine);
		return machine;
	}



	public Action<?> getAction(Type type) throws ActionTypeException {
		if (type == Type.ADD_COMPONENT)
		{
			return new AddComponent();
		}else if (type == Type.REMOVE_COMPONENT)
		{
			return new RemoveComponent();
		}else if(type== Type.MERGE_COMPONENT)
			return new MergeComponent(interpreter);
		throw new ActionTypeException();
	}
	
	private class AddComponent extends AddComponentAction
	{

		@Override
		public boolean doAction() {
			interpreter.addMachine(this.getComponent());
			refresh();
			return true;
		}

		@Override
		public boolean undoAction() {
			interpreter.removeMachine(this.getComponent());
			refresh();
			return true;
		}
		
	}
	
	
	private class RemoveComponent extends RemoveComponentAction
	{

		@Override
		public boolean doAction() {
			interpreter.removeMachine(this.getComponent());
			refresh();
			return true;
		}

		@Override
		public boolean undoAction() {
			interpreter.addMachine(this.getComponent());
			refresh();
			return true;
		}
		
	}
	
	private static class MergeComponent extends MergeComponentAction
	{

		
			private final MachineSpaceInterpreter interpreter;
			
			public MergeComponent(MachineSpaceInterpreter interpreter) {
				super();
				this.interpreter = interpreter;
			}
			@Override
			public boolean doAction() {
				MachineSpaceInterpreter machineSpace = new MachineSpaceInterpreter(this.getComponent());
				
				FunctionSettingsRepositoryInterpreter myFunctionRepository = new FunctionSettingsRepositoryInterpreter(interpreter.getFunctionRepository());
			
				FunctionSettingsRepositoryInterpreter functionInterp = new FunctionSettingsRepositoryInterpreter( machineSpace.getFunctionRepository());
				for(DataType store: functionInterp.getFunctions().getValues())
				{
					if(store instanceof PropertyStore)
					{
						myFunctionRepository.addFunction((PropertyStore)store);
					}
				}
				myFunctionRepository.refresh();
				
				ParticleEffectRepositoryInterpreter myParticleRepository = new ParticleEffectRepositoryInterpreter(interpreter.getParticleEffectRepository());
				
				ParticleEffectRepositoryInterpreter particleInterp = new ParticleEffectRepositoryInterpreter( machineSpace.getParticleEffectRepository());
				for(DataType store: particleInterp.getParticleEffects().getValues())
				{
					if(store instanceof PropertyStore)
					{
						myParticleRepository.addParticleEffect((PropertyStore)store);
					}
				}
				myParticleRepository.refresh();
				interpreter.refresh();
				return true;
			}

			@Override
			public boolean undoAction() {
			
				MachineSpaceInterpreter machineSpace = new MachineSpaceInterpreter(this.getComponent());
				
				FunctionSettingsRepositoryInterpreter myFunctionRepository = new FunctionSettingsRepositoryInterpreter(interpreter.getFunctionRepository());
			
				FunctionSettingsRepositoryInterpreter functionInterp = new FunctionSettingsRepositoryInterpreter( machineSpace.getFunctionRepository());
				for(DataType store: functionInterp.getFunctions().getValues())
				{
					if(store instanceof PropertyStore)
					{
						myFunctionRepository.removeFunction((PropertyStore)store);
					}
				}
				myFunctionRepository.refresh();
				
				ParticleEffectRepositoryInterpreter myParticleRepository = new ParticleEffectRepositoryInterpreter(interpreter.getParticleEffectRepository());
				
				ParticleEffectRepositoryInterpreter particleInterp = new ParticleEffectRepositoryInterpreter( machineSpace.getParticleEffectRepository());
				for(DataType store: particleInterp.getParticleEffects().getValues())
				{
					if(store instanceof PropertyStore)
					{
						myParticleRepository.removeParticleEffect((PropertyStore)store);
					}
				}
				myParticleRepository.refresh();
				interpreter.refresh();
				return true;
			}
			
		
	}
	


	public ParticleEffectRepository getParticleEffectRepository() {

		return this.particleEffectRepository;
	}



	public FunctionRepository getFunctionRepository() {
		return functionRepository;
	}



	public Spatial getSpatial() {
		return ((NodeModel)model).getSpatial();
	}



	public ImageView getImageView() {
		return imageView;
	}

	
	
	
	
	
}
