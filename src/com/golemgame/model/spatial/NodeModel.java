package com.golemgame.model.spatial;

import java.util.Collection;
import java.util.HashSet;

import com.golemgame.model.Model;
import com.golemgame.model.ParentModel;
import com.golemgame.states.StateManager;
import com.golemgame.tool.action.Actionable;
import com.golemgame.util.DeletionRecord;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;

public class NodeModel extends SpatialModel implements ParentModel {
	private static final long serialVersionUID = 1L;

	/**
	 * Whether or not this node is locked to changes 
	 * during the update/render cycle, and must be manually updated.
	 * 
	 */
	private boolean updateLocked = true;
	

	private transient DeletionRecord deletionRecord = null;

	private Collection<Model> children = new HashSet<Model>();
	
	
	protected Spatial buildSpatial() {
		return new Node();
	}
/*
	
	public void signalModelChanged() {

		super.signalModelChanged();
		for (Model model:children)
			model.signalModelChanged();
	}
*/
	
	public void signalMoved() {

		super.signalMoved();
		
		refreshLockedData();
		if(children != null)//this can only happen in weird conditions where you update before innitiating the object...
			for (Model model:children)
				model.signalMoved();
	}


	/**
	 * LOD is disabled by this locking, except when a refresh occurs.
	 * 
	 */
	
	
	public boolean isShareable() {
	
		return false;
	}

	
	public Model makeSharedModel() {
	
		return null;
	}



	
	public boolean isParent() {
		return true;
	}



	public NodeModel() {
		this((Actionable)null);
	}
	
	/*
	public void updateWorldData() {
		this.getNode().updateGeometricState(0, true);
		
	/*	if (this.getNode().getChildren() != null)
			for (Spatial child:this.getNode().getChildren())
			{	
				child.updateWorldVectors();
			}*/
	//}
	
	public NodeModel(Actionable actionable) {
		super();
		if(actionable!= null)
			super.setActionable(actionable);
		registerSpatial();
		this.setUpdateLocked(this.isUpdateLocked());
		refreshLockedData();
	}


	
	
	public void addChild(Model child) throws ModelTypeException{
		if(child==this)
			throw new ModelTypeException("Cannot add a node to itself");
		
		if (child instanceof SpatialModel)
		{
			final SpatialModel sModel = (SpatialModel)child;
			if(!this.children.contains(child)){
			//if (StateManager.getGame().inGLThread())
			//{
				StateManager.getGame().lock();
				try{
					if (children.add(sModel))
					{
						ParentModel sParent = sModel.getParent();
						if (sParent != null && sParent != this)
						{
							
							sModel.getParent().detachChild(sModel);
							sParent.refreshLockedData();
						}
						sModel.setParentModel(this);
						this.getNode().attachChild(sModel.getSpatial());
						sModel.getSpatial().setLocks(this.getNode().getLocks());
					//	sModel.refreshLockedData();//happens in updatew world
						sModel.getSpatial().updateRenderState();
						child.updateWorldData();//update the child, instead of this (MUCH faster in many cases)!
						
					}else if (sModel.getParent() != this)
					{
						sModel.setParentModel(this);
						//System.out.println("");
					}
				}finally
				{
					StateManager.getGame().unlock();
				}
			}
			
/*			}else
			{
				try {
					StateManager.getGame().executeInGL(new Callable<Object>()
							{

								
								public Object call() throws Exception {
									if (children.add(sModel))
									{
										if (sModel.getParent() != null && sModel.getParent() != NodeModel.this)
										{
											sModel.getParent().detachChild(sModel);
										}
										sModel.setParentModel(NodeModel.this);
										NodeModel.this.getNode().attachChild(sModel.getSpatial());
										
										sModel.getSpatial().updateRenderState();
										NodeModel.this.updateWorldData();
										
									}
									return null;
								}
						
							});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}*/
		}else
			throw new ModelTypeException();
	}

	
	public void detachChild(Model child) throws ModelTypeException{
		if (child instanceof SpatialModel)
		{
			if(this.children.contains(child)){
			
				StateManager.getGame().lock();
				try{
					SpatialModel sModel = (SpatialModel)child;
					if (children.remove(sModel))
					{
						
						
					}
					sModel.setParentModel(null);
					this.getNode().detachChild(sModel.getSpatial());
					this.refreshLockedData();
				}finally
				{
					StateManager.getGame().unlock();
				}
				}
		}else
			throw new ModelTypeException();
		
	}

	
	public Collection<Model> getChildren() {
		return children;
	}





	public BoundingVolume getWorldBound() {
		if (getSpatial().getWorldBound() == null)
		{
		//	if (getChildren().isEmpty())
		//		return null;//this is allowed to happen
			
			getSpatial().updateModelBound();
			getSpatial().updateWorldBound();
			
			if (getSpatial().getWorldBound() == null)
			{
				getSpatial().setModelBound(new BoundingBox());
				getSpatial().updateModelBound();
				getSpatial().updateWorldBound();
				if(getSpatial().getWorldBound() == null)
				{
				//	StateManager.getLogger().warning("Spatial has null bound!");
					BoundingBox c = new BoundingBox();
					c.setCenter(this.getWorldTranslation());
					c.setCenter(new Vector3f(100,100,100));
					return c;
				}
			
			}
			
			
		}
		return getSpatial().getWorldBound();
	}

	public void centerOn(Model centerOn) throws ModelTypeException {
		if (centerOn instanceof NodeModel)
		{
			NodeModel centerNode = (NodeModel)centerOn;
			recenter(centerNode.getNode().getWorldTranslation());
		}else
			throw new ModelTypeException();
		
	}

	
	
	public void detachAllChildren() {
		StateManager.getGame().lock();
		try{
		children.clear();
		this.getNode().detachAllChildren();
		}finally{
			StateManager.getGame().unlock();
		}
	}







	
	public void recenter(Vector3f center)
	{
		this.updateWorldData();
	
	/*	for (Spatial child:getNode().getChildren())
		{
			child.getLocalTranslation().subtractLocal(center);
			child.updateWorldVectors();
			child.updateWorldBound();
		}
		Vector3f rotCenter = this.getNode().getLocalRotation().inverse().mult(center, new Vector3f());
		this.getNode().getLocalTranslation().addLocal(rotCenter);
		this.updateWorldData();
	
		*/
	
		Vector3f translation =this.getWorldTranslation().subtract(center);
		this.getParent().worldToLocal(center, this.getLocalTranslation());
		this.updateWorldData();
	
		this.getLocalRotation().inverse().mult(translation, translation);
		for (Model model:this.getChildren())
		{
			model.getLocalTranslation().addLocal(translation);
			model.updateWorldData();
		}
	
	
		
	}
	


	public Node getNode() {
		return (Node)getSpatial();
	}



	
	public void delete() {
		if (isDeleted())
			return;
		deletionRecord = new DeletionRecord();
		this.getNode().unlock();//must unlock before deletion or the renderer will ignore the change.
		for (Model model:children.toArray(new Model[children.size()]))
		{
			deletionRecord.add(model);
			model.delete();
		}
		
		super.delete();
	
	}


	
	
	public void updateWorldData() {
		super.updateWorldData();
/*		if(this.getNode().getChildren()!= null)
		{
			for(Spatial child:this.getNode().getChildren())
				child.updateWorldVectors();
		}*/
	}
	
	public boolean undelete() 
	{
		if (!isDeleted())
			return false;
		
		if (deletionRecord != null)
		{
			deletionRecord.undelete();
		}
		this.setUpdateLocked(updateLocked);
		return super.undelete();
		
	}
	
	public boolean isUpdateLocked() {
		return updateLocked;
	}
	
	//private final static int defaultLocks = SceneElement.LOCKED_MESH_DATA & SceneElement.LOCKED_SHADOWS & SceneElement.LOCKED_BRANCH;
	
	public void setUpdateLocked(boolean updateLocked) {
		this.updateLocked = updateLocked;
		//setting meshes to locked means that a display list is created for the objects in this node.
		//http://www.jmonkeyengine.com/jmeforum/index.php?topic=7173.0 important detail about shared node there.
		if (updateLocked)
		{
			int locks =0;
			locks |= SceneElement.LOCKED_BOUNDS;
			locks |= SceneElement.LOCKED_BRANCH;
		//	locks |= SceneElement.LOCKED_MESH_DATA;
			locks |= SceneElement.LOCKED_TRANSFORMS;
			//locks |= SceneElement.LOCKED_SHADOWS;
			this.getNode().setLocks(locks);
			
			
		}else
		{
			this.getNode().unlock();
		//	this.getNode().unlock();
		
			//this.getNode().setLocks( SceneElement.LOCKED_BRANCH);
		}
		
		/*
		 * problems: 
		 * 1) textures
		 * 2) locking disables LOD. Can deal with this by having LOD'd objects in the 
		 * qualtiy manager run through update cycles, occasionally (say every 3 or 5 frames).
		 * 
		 * For physics, lock all the static objects
		 * Possibly also lock dynamic objects that aren't moving? Possibly not worth it.
		 * 
		 * 
		 */
		
		/*
		 *Remaining questiosn:
		 *why does the spatial model fail 
		 */
		
		
	}
	
	

	
	
}
