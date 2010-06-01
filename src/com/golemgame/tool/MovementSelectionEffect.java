package com.golemgame.tool;


import com.golemgame.constructor.Updatable;
import com.golemgame.constructor.UpdatableAdapter;
import com.golemgame.constructor.UpdateManager;
import com.golemgame.model.Model;
import com.golemgame.model.ParentModel;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.model.spatial.SpatialModelImpl;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Disk;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;


public class MovementSelectionEffect implements ToolSelectionEffect {
	
	private boolean engaged = false;//new AtomicBoolean(false);

	private Vector3f targetExtent = new Vector3f();
	private Vector3f startingExtent = new Vector3f();
	private NodeModel model ;
	private Quaternion startingRotation = new Quaternion();
	private Quaternion targetRotation = new Quaternion();
	
	
	private float elapsedTime = 0;
	
	/**
	 * Total time it takes to expand to the final size
	 */
	public final static float EXPAND_TIME= 0.25f;
	
	public MovementSelectionEffect() {
		super();
		model = new NodeModel();
		model.setUpdateLocked(false);
		model.addChild( new SpatialModelImpl(false)
		{
			private static final long serialVersionUID = 1L;
			@Override
			protected Spatial buildSpatial() {
			//	super.setLoadable(false);
			
				Disk disk = new Disk("constant", 30, 60, 0.5f);
				
				TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
				
				Texture t = getTexture();
				ts.setTexture(t);
				
				t.setApply(Texture.AM_MODULATE);
				disk.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_X);
				disk.setRenderState(ts);
			
				
				
				disk.setCullMode(SceneElement.CULL_NEVER);
				CullState cs = DisplaySystem.getDisplaySystem().getRenderer().createCullState();
				cs.setCullMode(CullState.CS_NONE);
				disk.setRenderState(cs);
				
				AlphaState alpha = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
				alpha.setBlendEnabled(true);
				alpha.setSrcFunction(AlphaState.SB_SRC_ALPHA);
				alpha.setDstFunction(AlphaState.SB_ONE_MINUS_SRC_ALPHA);
				alpha.setTestEnabled(true);		
				alpha.setTestFunction(AlphaState.TF_GREATER);
				disk.setRenderState(alpha);
				disk.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
				
			/*	MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				ms.setAmbient(new ColorRGBA(1f,1,1,0.1f));
			
				ms.setMaterialFace(MaterialState.MF_FRONT_AND_BACK);
				disk.setRenderState(ms);*/
				
				LightState ls = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
				disk.setRenderState(ls);
				disk.setLightCombineMode(LightState.OFF);
				
				disk.setModelBound(new BoundingBox());
				disk.updateModelBound();
				disk.updateRenderState();
				
				
				return disk;
			}
		});
		this.getModel().setVisible(false);
	}

	
	protected Texture getTexture()
	{
		return TextureManager.loadTexture(this.getClass().getClassLoader().getResource("com/golemgame/data/textures/misc/disktext.png"), Texture.MM_LINEAR_LINEAR,Texture.FM_LINEAR);
		
	}

	public ParentModel getModel() {
		return model;
	}


	/**
	 * Engage this effect, if it is not already engaged.
	 */
	public void setEngage(boolean engaged)
	{
		this.engaged = engaged;
		//if(this.engaged.compareAndSet(!engaged, engaged))
		{
			this.getModel().setVisible(true);
			doEngage(engaged);
		}
		getModel().refreshLockedData();
	}
	
	protected void doEngage(boolean engage)
	{
		
	//	this.getModel().setLocalRotation(targetRotation);
		ToolSelectionEffectManager.getInstance().setCurrentEffect(this);
		elapsedTime= 0;
		startingRotation.set(getModel().getLocalRotation());
	
		startingExtent.set(model.getLocalScale());
		
		UpdateManager.getInstance().add( this.animation);
	}
	
	public void setTargetRotation(Quaternion targetRotation) {
		this.targetRotation.set(targetRotation);
		getModel().refreshLockedData();
		//this.getModel().setLocalRotation(targetRotation);
	}
	
	public void forceDisengage()
	{
		UpdateManager.getInstance().remove(this.animation);
		
		this.model.setVisible(false);
		this.model.detachFromParent();
	}
	
	public boolean isEngaged()
	{
		return this.engaged;//.get();
	}
	

	/**
	 * Center of the effect, in world coordinates
	 * @param center
	 */
	public void setTarget(Vector3f center)
	{
		this.model.getLocalTranslation().set(center);
		getModel().refreshLockedData();
	}
	
	public void setBounds(Vector3f extent)
	{
		
		float maxExtent = 0;
		if (extent.x> extent.y)
		{	if(extent.x>extent.z)
				maxExtent =extent.x;
			else
				maxExtent = extent.z;
		}else
		{	if(extent.y>extent.z)
				maxExtent =extent.y;
			else
				maxExtent = extent.z;
		}
		this.targetExtent.set(maxExtent,maxExtent,maxExtent);
		getModel().refreshLockedData();
	}
	
	protected Vector3f getTargetExtent()
	{
		return this.targetExtent;
	}
	
	public float getElapsedTime() {
		return elapsedTime;
	}
	private final Vector3f _store = new Vector3f();
	protected void doUpdate(float time)
	{
		Vector3f goalExtent = (isEngaged()?targetExtent:Vector3f.ZERO);
		elapsedTime += time *2;
	
		//while this size is < target extent, expand
		if ((elapsedTime<EXPAND_TIME))
		{
			Vector3f grow =_store;
			grow.set(goalExtent).subtractLocal(startingExtent);

			grow.multLocal(elapsedTime/EXPAND_TIME);
			model.getLocalScale().set(startingExtent).addLocal(grow);
			
			getModel().getLocalRotation().slerp(startingRotation, targetRotation, (getElapsedTime()/EXPAND_TIME));
			
	
					
		}else	
		{
			getModel().getLocalRotation().set(targetRotation);
			model.getLocalScale().set(goalExtent);
			UpdateManager.getInstance().remove(animation);
		}
		getModel().refreshLockedData();
	}

	private Updatable animation = new UpdatableAdapter()
	{
		@Override
		public void update(float time) {
			doUpdate(time);
		}	
	};

	public void setTargetModel(Model selectedModel) {
		this.setTarget(selectedModel.getWorldTranslation());
		setBounds(selectedModel.getDimensions().add(1f,1f,1f));
		//really this should be changed so that the selection size is constant screen size - ie, its only dependant 
		//on your zoom... but visibility issues...
		
	}
	
	
	
}
