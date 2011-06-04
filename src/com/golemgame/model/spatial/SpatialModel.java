/*******************************************************************************
 * Copyright 2008, 2009, 2010 Sam Bayless.
 * 
 *     This file is part of Golems.
 * 
 *     Golems is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     Golems is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with Golems. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.golemgame.model.spatial;

import java.lang.ref.WeakReference;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import com.golemgame.model.Model;
import com.golemgame.model.ModelCollision;
import com.golemgame.model.ModelData;
import com.golemgame.model.ModelIntersectionData;
import com.golemgame.model.ModelListener;
import com.golemgame.model.ParentModel;
import com.golemgame.model.effect.ModelEffect;
import com.golemgame.model.effect.ModelEffect.ModelEffectType;
import com.golemgame.model.spatial.collision.CentroidCollisionResults;
import com.golemgame.model.texture.TextureController;
import com.golemgame.model.texture.TextureTypeKey.TextureShape;
import com.golemgame.model.texture.spatial.SpatialTextureController;
import com.golemgame.states.StateManager;
import com.golemgame.structural.collision.CollisionManager;
import com.golemgame.structural.collision.CollisionMember;
import com.golemgame.tool.action.Actionable;
import com.golemgame.views.Viewable.ViewMode;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.bounding.CollisionTree;
import com.jme.bounding.CollisionTreeManager;
import com.jme.intersection.BoundingCollisionResults;
import com.jme.intersection.BoundingPickResults;
import com.jme.intersection.CollisionData;
import com.jme.intersection.CollisionResults;
import com.jme.intersection.PickData;
import com.jme.intersection.PickResults;
import com.jme.intersection.TrianglePickResults;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.SharedMesh;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.VBOInfo;
import com.jme.scene.batch.TriangleBatch;
import com.jme.scene.state.RenderState;
import com.jme.system.DisplaySystem;
import com.jme.util.geom.BufferUtils;

public abstract class SpatialModel implements Model {
	private static final long serialVersionUID = 1L;
	/**
	 * Note: Weak HashMaps only store the KEYS weakly - the values are held strongly.
	 * This means that if the value references the key, no cleanup will occur.
	 * 
	 * So: wrap the values in weak references also.
	 */
	private static Map<Spatial, WeakReference< SpatialModel>> nodeMap = new WeakHashMap<Spatial,WeakReference<SpatialModel>>();
	
	private NodeModel parent;
	private boolean visible;
	private boolean deleted;
	private TextureShape[] textureShape = new TextureShape[]{ null,null,null,null,null,null,null,null};
	
	public TextureShape getTextureShape(int layer) {
		return textureShape[layer];
	}
	
	private Collection<ModelListener> listeners = new CopyOnWriteArrayList<ModelListener>();
	private Map<CollisionManager, CollisionMember> collisionMap = new HashMap<CollisionManager, CollisionMember>();
	private Actionable actionable = null;
	//private boolean isLoadable = true;
	
	private transient TextureController textureController = new SpatialTextureController(this);
	
	private transient Spatial spatial;
	
	private Vector3f localTranslation;
	private Vector3f localScale;
	private Quaternion localRotation;
	private boolean isRegistered = false;
	
	protected transient boolean isLoaded = false;
	
	/**
	 * Set the texture shape for a layer. Subclasses may perform more complicated adjustements here.
	 * @param shape
	 */
	public void setTextureShape(TextureShape shape, int layer)
	{
		if(shape==getTextureShape(layer))
			return;
		this.textureShape[layer] = shape;
		setupLayer(getSpatial(),layer);
		
	}	
	
	protected void setupLayer(Spatial s,int layer)
	{
/*		if(s instanceof DiscreteLodNode)
		{
			DiscreteLodNode dlod = (DiscreteLodNode)s;
			for(Spatial c:dlod.getm)
		}
		else */
		if(s instanceof Node)
		{
			Node n = (Node)s;
			if(n.getChildren() == null)
				return;
			else
			{
				for(Spatial c:n.getChildren())
				{
					setupLayer(c,layer);
				}
			}
		}else if (s instanceof SharedMesh)
		{
			   FloatBuffer buf = ((SharedMesh)s).getTarget().getTextureBuffer(0, layer);
				if(buf==null)
				{
					buf = BufferUtils.clone(((SharedMesh)s).getTarget().getTextureBuffer(0,0));
					
					((SharedMesh)s).getTarget().setTextureBuffer(0, buf , layer);
				}
		}else if (s instanceof TriMesh)
		{
		   FloatBuffer buf = ((TriMesh)s).getTextureBuffer(0, layer);
			if(buf==null)
			{
				buf = BufferUtils.clone(((TriMesh)s).getTextureBuffer(0,0));
				
				((TriMesh)s).setTextureBuffer(0, buf , layer);
			}
		}
	}
	
	/**
	 * Spatials intentionally do NOT remember their effects after loading - those must be reestablished by an appearance.
	 */
	private transient Map<ModelEffectType, ModelEffect> effects;
	

	
	
	public TextureController getTextureController() {
		return textureController;
	}

	
/*	public void addModelEffect(ModelEffect effect) {
		ModelEffectType[] types = effect.getEffectTypes();
		if (types != null && types.length>0)
		{
			for (ModelEffectType type:types)
			{
				ModelEffect oldEffect = effects.get(type);
				if (oldEffect != null)
					oldEffect.removeModel(this);
				effects.put(type, effect);
			}
			
		}
		
	}*/

	
/*	public void clearModelEffects() {
		for (ModelEffectType type:ModelEffectType.values())
		{
			ModelEffect oldEffect = effects.get(type);
			if (oldEffect != null)
				oldEffect.removeModel(this);
		}
		
	}
*/
	/*
	public void removeModelEffect(ModelEffectType type) {
		ModelEffect oldEffect = effects.get(type);
		if (oldEffect != null)
			oldEffect.removeModel(this);

		
	}*/

	
	public Actionable getActionable() {
		return actionable;
	}
	
	
	public void setActionable(Actionable actionable)
	{
		this.actionable = actionable;
	}

	public static SpatialModel getSpatialModel(Spatial from)
	{
		WeakReference<SpatialModel> ref =  SpatialModel.nodeMap.get(from);
		
		if (ref != null)
			return ref.get();
		else
			return null;
	}
	
	public SpatialModel() {
		super();
		
		effects = new HashMap<ModelEffectType, ModelEffect>();
		
		this.visible = true;
		
	
		//Dont have to lock this, because it isn't attached to the game tree yet.
		this.spatial = buildSpatial();
	
		
		//enableVBO(spatial);//sharedmeshes dont like this...
		
		this.localTranslation = spatial.getLocalTranslation();
		this.localScale = spatial.getLocalScale();
		this.localRotation = spatial.getLocalRotation();
		updateWorldData();
	}
	
	private static void enableVBO(Spatial spatial)
	{
		if (spatial instanceof Node && ((Node)spatial).getChildren()!= null)
		{
			for (Spatial child: ((Node)spatial).getChildren())
				enableVBO(child);
		}else if (spatial instanceof Geometry)
		{
			VBOInfo vbo = new VBOInfo(true);
			vbo.setVBOIndexEnabled(true);
			((Geometry)spatial).setVBOInfo(vbo);
		}
	}
	
	
	public CollisionMember getCollisionMember(CollisionManager from) {
		return collisionMap.get(from);
		
	}

	
	public void clearOtherCollisionManagers(CollisionManager keep) {
		Iterator<CollisionManager> it = collisionMap.keySet().iterator();
		while(it.hasNext())
		{
			CollisionManager manager = it.next();
			if(!manager.equals(keep))
				it.remove();
		}
	}


	public void setCollidable(boolean selectable) {
		this.getSpatial().setIsCollidable(selectable);
		
	}


	public void registerCollisionMember(CollisionMember member) {
		collisionMap.put(member.getCollisionManager(), member);
		
	}

	
	public void removeCollisionMember(CollisionMember member) {
		collisionMap.remove(member.getCollisionManager());
		
	}

	protected abstract Spatial buildSpatial();
	
	/**
	 * Call this after the spatial is created to register it for collision detection
	 */
	protected final void registerSpatial()
	{
		this.isRegistered = true;
		if(this.getSpatial() instanceof Node)
			registerNodeSpatials((Node)this.getSpatial());
		nodeMap.put(this.getSpatial(), new WeakReference<SpatialModel>(this));
	}
	
	private void registerNodeSpatials(Node node)
	{
		if(node.getChildren()!=null)
		{
			for (Spatial child:node.getChildren())
			{
				if(child instanceof Node)
					registerNodeSpatials((Node)child);
				else
					nodeMap.put(child, new WeakReference<SpatialModel>(this));
			}
		}
	}
	
	public final Spatial getSpatial()
	{
		return spatial;
	}

	
	public void detachFromParent() {
		ParentModel parent = this.getParent();
		if(parent!= null)
			parent.detachChild(this);
	}


	
	public boolean removeModelListener(ModelListener listener) {
		return listeners.remove(listener);
	}

	
	public void addModelListener(ModelListener listener) {
		if(!listeners.contains(listener))	
			listeners.add(listener);
	}
	
	
	public Collection<ModelListener> getListeners()
	{
		return listeners;
	}
/*
	
	public void signalModelChanged() {
		//possibly make this use a static array later, if it can be ensured that no thread issues will occur.
		for (ModelListener listener:listeners.toArray(new ModelListener[listeners.size()] ))
		{
			listener.modelChanged(this);
		}
	}*/

	
	public void signalMoved() {
		refreshLockedData();
		
		for (ModelListener listener:listeners)
		{
			if(listener != null)
				listener.modelMoved(this);
		}
	}
	
	
	public Vector3f getDimensions()
	{
		BoundingVolume bound = this.getSpatial().getWorldBound() ;
		if (bound instanceof BoundingBox)
		{
			BoundingBox box = (BoundingBox)bound;
			this.getSpatial().updateWorldVectors();
			return (new Vector3f(box.xExtent * 2f,box.yExtent * 2f,box.zExtent * 2f));
		}else if (bound instanceof BoundingSphere)
		{
			BoundingSphere sphere = (BoundingSphere)bound;
			this.getSpatial().updateWorldVectors();
			return (new Vector3f(sphere.radius*2f,sphere.radius*2f,sphere.radius*2f));
		}
		StateManager.getLogger().log(Level.WARNING, "Missing Bounding Volume");
		return new Vector3f(1,1,1);
	}

	
	public void updateModelData() {
		this.refreshLockedData();
		this.getSpatial().updateGeometricState(0, true);
		this.getSpatial().updateModelBound();
		updateWorldBoundToRoot(this.getSpatial());
	//	signalModelChanged();
	}

	
	public boolean isParent() {
		return false;
	}


	
	public ModelData getModelData() {
		return new ModelData(this);
	}

	
	public void loadModelData(ModelData data) {
		this.getLocalTranslation().set(data.getLocalTranslation());
		this.getLocalRotation().set(data.getLocalRotation());
		this.getLocalScale().set(data.getLocalScale());
		this.updateWorldData();
		
	}
	
	

	
	public ModelData getWorldData() {
		return new ModelData(this,true);
	}

	
	public void loadWorldData(ModelData data) {
		if (this.getParent() == null)
		{
			this.loadModelData(data);
		}else
		{
			NodeModel parent = this.getParent();
			parent.updateWorldData();

			parent.worldToLocal(data.getLocalTranslation(), this.getLocalTranslation());
			this.getLocalRotation().set(parent.getWorldRotation().inverse()).multLocal(data.getLocalRotation());
			this.getLocalScale().set(data.getLocalScale());
			this.updateWorldData();
			
		}
		
	}

	
	public void loadWorldData(Model from) {

			from.updateWorldData();
			if (this.getParent() == null)
			{
				this.getLocalTranslation().set(from.getLocalTranslation());
				this.getLocalRotation().set(from.getLocalRotation());
				this.getLocalScale().set(from.getLocalScale());
			}else
			{
				this.getParent().updateWorldData();
				this.getParent().worldToLocal(from.getWorldTranslation(), this.getLocalTranslation());
				//this might be backwards
				this.getLocalRotation().set( from.getWorldRotation()).multLocal(this.getParent().getWorldRotation().inverse());
				this.getLocalScale().set(from.getLocalScale());//fix this later?
			}

			this.updateWorldData();
		
	}

	
	public void loadModelData(Model from) {
	

			this.getLocalTranslation().set(from.getLocalTranslation());
			this.getLocalRotation().set(from.getLocalRotation());
			this.getLocalScale().set(from.getLocalScale());

		this.updateWorldData();
	}

	
	public void intersectRay(Ray toTest, ArrayList<ModelIntersectionData> store, boolean useTriangleAccuracy) {
		
		PickResults results;
		if (useTriangleAccuracy)
		{
			results = new TrianglePickResults();
		}else
		{
			results = new BoundingPickResults();
		}
		results.setCheckDistance(true);
		this.getSpatial().findPick(toTest, results);
		store.clear();
		for (int i = 0; i < results.getNumber(); i++)
		{
			PickData data = results.getPickData(i);
			if (!(useTriangleAccuracy &&  (data.getTargetTris() == null || data.getTargetTris().isEmpty())))
			{
				SpatialModel sModel = getSpatialModel(data.getTargetMesh().getParentGeom());
				if (sModel == null)
				{
					sModel = getSpatialModel(data.getTargetMesh().getParentGeom().getParent());

				}
				if (sModel != null)
				{
					sModel.updateWorldData();
					Vector3f intersection = null;
					if (useTriangleAccuracy)
					{
						intersection = intersectWithBatch(toTest,(TriangleBatch)  data.getTargetMesh());
					}
		        	
					store.add(new ModelIntersectionData(sModel, intersection));
				}
			}
		}
	}

	
	public Quaternion getLocalRotation() {
		return this.getSpatial().getLocalRotation();
	}

	
	public Vector3f getLocalScale() {
		return this.getSpatial().getLocalScale();
	}

	
	public Vector3f getLocalTranslation() {
		return this.getSpatial().getLocalTranslation();
	}

	
	public NodeModel getParent() {
		return parent;
	}

	
	public Quaternion getWorldRotation() {
		return this.getSpatial().getWorldRotation();
	}

	
	public Vector3f getWorldTranslation() {
		return this.getSpatial().getWorldTranslation();
	}

	
	public Vector3f getWorldScale() {
		return this.getSpatial().getWorldScale();
	}

	
	
	public boolean isTouchable() {
		return this.getSpatial().isCollidable();
	}


	
	public boolean isVisible() {
		return visible;
	}


	
	public void setLocalRotation(Quaternion rotation) {
		this.getSpatial().setLocalRotation(rotation);
		localRotation = rotation;
		this.refreshLockedData();
	}

	
	public void setLocalScale(Vector3f scale) {
		this.getSpatial().setLocalScale(scale);
		localScale = scale;
		this.refreshLockedData();
	}

	
	public void setLocalTranslation(Vector3f translation) {
		this.getSpatial().setLocalTranslation(translation);
		localTranslation = translation;
		this.refreshLockedData();
	}

	public void updateToWorld()
	{
		updateWorldVectorsToRoot(this.getSpatial());
	}
	
	
	public Vector3f worldToLocal(Vector3f worldTranslation,
			Vector3f store) {
		updateToWorld(getSpatial());
		return getSpatial().worldToLocal(worldTranslation, store);
	}

	
	public Vector3f localToWorld(Vector3f localTranslation,
			Vector3f store) {

		updateToWorld(getSpatial());
		return getSpatial().localToWorld(localTranslation, store);
	}

	public static void updateToWorld(Spatial node)
	{
		if (node == null)
			return;
		
		Node parent = node.getParent();

		updateToWorld(parent);
		
		node.updateWorldVectors();
	}
	
	public static final long countReferencedSpatials()
	{
		return nodeMap.size();
	}

	
	public boolean isCollidable() {
		return getSpatial().isCollidable();
	}


	public void setModelData(Model setFrom) throws ModelTypeException {
		if (setFrom instanceof SpatialModel)
		{
			SpatialModel sModel = (SpatialModel)setFrom;
			this.getSpatial().getLocalTranslation().set(sModel.getSpatial().getLocalTranslation());
			this.getSpatial().getLocalScale().set(sModel.getSpatial().getLocalScale());
			this.getSpatial().getLocalRotation().set(sModel.getSpatial().getLocalRotation());
		}else
			throw new ModelTypeException();
	}

	
	public void setParentModel(ParentModel model)  throws ModelTypeException
	{
		if (model instanceof NodeModel)
		{
			this.parent = (NodeModel)model;
		}else if (model == null)
		{
			this.parent = null;
		}
		else
		{
			throw new ModelTypeException();
		}
	}
	
	public void setTouchable(boolean touchable) {
		this.getSpatial().setIsCollidable(touchable);

	}

	
	public void setViewMode(ViewMode mode) {
		// TODO Auto-generated method stub

	}

	
	
	public void updateWorldData() {
		
		Vector3f oldPos = new Vector3f(this.getWorldTranslation());
		Vector3f oldScale = new Vector3f(this.getWorldScale());
		Quaternion oldRotation = new Quaternion(this.getWorldRotation());
		
	//	updateWorldVectorsToRoot(this.getSpatial());
	
//		this.getSpatial().updateWorldData(0);
		this.refreshLockedData();
		//let this happen later...
		getSpatial().updateWorldVectors();
		//getSpatial().updateRenderState();
		//this.getSpatial().updateGeometricState(0, true);//gets called by refresh locked?
	//	this.getSpatial().updateRenderState();
		//updateWorldBoundToRoot(this.getSpatial());
		
		if (! oldPos.equals(this.getWorldTranslation()) || !(oldRotation.equals(this.getWorldRotation())) || !(oldScale.equals(this.getWorldScale())))
			this.signalMoved();
	}

	
	public void setVisible(boolean visible) 
	{
		this.visible = visible;
		if (visible)
		{
			this.getSpatial().setCullMode(SceneElement.CULL_DYNAMIC);
		}else
		{
			this.getSpatial().setCullMode(SceneElement.CULL_ALWAYS);
		}
	}


	
	public void setWorldTranslation(Vector3f translation) {
		this.refreshLockedData();
		this.getSpatial().updateWorldVectors();
		this.getSpatial().worldToLocal(translation, this.getSpatial().getLocalTranslation());
		this.refreshLockedData();
	}


	/**
	 * Note - this might be not be up to date... use with caution
	 */
	public boolean testCollision(Model collidesWith)
			throws ModelTypeException {

		if (collidesWith instanceof SpatialModel)
		{
			SpatialModel collide = (SpatialModel) collidesWith;
			
			this.getSpatial().updateGeometricState(0,true);
			collide.getSpatial().updateGeometricState(0,true);

			this.getSpatial().updateWorldBound();//have to update world bounds before collision checking
			collide.getSpatial().updateWorldBound();

			CollisionResults results = new CentroidCollisionResults();
			this.getSpatial().findCollisions(collide.getSpatial(), results);

			for (int i = 0; i<results.getNumber();i++)
			{				
				CollisionData data = results.getCollisionData(i);
					if(((data.getTargetMesh() == collide.getSpatial())&&(data.getSourceMesh() ==  this.getSpatial()))  ||  ((data.getTargetMesh() == this.getSpatial())&&(data.getSourceMesh() ==  collide.getSpatial())) )
					{
					
								if (data.getSourceTris().isEmpty() && data.getTargetTris().isEmpty())
									continue;//ensure triangles also collide			
								return true;
							
					}
			}
			return false;
		}else
		{
			throw new ModelTypeException();
		}
		
	
	}
	
	
	/**
	 * This is SLOW.
	 */
	public void findCollisionsConcat(Model collideWith, Set<Model> ignoreList,
			ArrayList<ModelCollision> store) throws ModelTypeException {
		if (!(collideWith instanceof SpatialModel))
			throw new ModelTypeException();
	
	/*	long[] time = new long[4];
		time[0] = System.nanoTime();*/
		SpatialModel collide = (SpatialModel) collideWith;
		
	//	collideWith.updateWorldData();
	//	this.updateWorldData();
		
		Spatial toResolve = this.getSpatial();
		Spatial toCheck = collide.getSpatial();
		
		
		CollisionResults results = new BoundingCollisionResults();
		//only proceed to triangle collision among the bounding results IF not in the ignore list.
		//this is more efficent because this uses a short-cutting trangle collision test that returns after the first colllision is found.
	//	time[1] = System.nanoTime();
		toResolve.findCollisions (toCheck, results);//find all collisions
		//time[2] = System.nanoTime();
		for (int i = 0; i<results.getNumber(); i++)
		{
			CollisionData data = results.getCollisionData(i);
			//if (!data.getTargetTris().isEmpty() && !data.getSourceTris().isEmpty())
			{
				Geometry sourceSpatial = data.getSourceMesh();
				Geometry targetSpatial = data.getTargetMesh();
				
				
			
				SpatialModel sModel = getSpatialModel(sourceSpatial);
				SpatialModel tModel  = getSpatialModel(targetSpatial);
				SpatialModel sourceModel = sModel==this?sModel:tModel;
				SpatialModel targetModel = sModel==this?tModel:sModel;
					
				
				
				if (targetModel != null && sourceModel != null &!( targetModel == sourceModel))
				{
					if(ignoreList!=null && ignoreList.contains(targetModel) )
						continue;
					
					//ensure the trangles also collide
					
					//THIS is the slow call
					if(!trianglesCollide(sourceSpatial, targetSpatial))
						continue;
					//search up the target model's parents until the collision model is found
					//this is to ensure that the collision models are actually members of the intended targets...

					store.add(new ModelCollision(sourceModel, targetModel));
						
					
				}
			}
		}
			/*time[3] = System.nanoTime();
			String times = "SpatialModel\t";
				for (int e = 1;e<time.length;e++)
				{
					times += "\t" + (time[e] - time[e-1])/1000000;
				}
				System.out.println(times);*/
	}

	
	public void findCollisions(Model collideWith, Set<Model> ignoreList, ArrayList<ModelCollision> store) throws ModelTypeException
	{
		store.clear();
		this.findCollisionsConcat(collideWith, ignoreList, store);
	}


	private transient ParentModel deletedParent = null;
	
	
	
	public void delete() {
		if (deleted)
			return;
		this.deleted = true;
		nodeMap.remove(this.getSpatial());
		if (this.getParent() != null)
		{
			deletedParent = this.getParent();
			
			deletedParent.detachChild(this);
		}
		
		
	}

	
	public final boolean isDeleted() {
		return deleted;
	}
	
	
	public boolean undelete() {
		if (!deleted)
			return false;
		
		if (deletedParent != null)
		{
			deletedParent.addChild(this);
			deletedParent= null;
		}
		nodeMap.put(this.getSpatial(), new WeakReference<SpatialModel>(this));
		this.deleted = false;
		return true;
	}

	
	
	/**
	 * An efficient triangle collision test that returns after the first collision is found (rather than testing all collisions).
	 * @param spatial1
	 * @param spatial2
	 * @return
	 */
	public static boolean trianglesCollide(Geometry spatial1,Geometry spatial2)
	{
		if (!(spatial1.isCollidable() && spatial2.isCollidable()))
			return false;
		
		if (CentroidCollisionResults.geoemtriesCollide(spatial1, spatial2))
			return true;
		//long time = System.nanoTime();
			
		/*//	StateManager.getFunctionalState().lock();
			try{
				//for now... this is restricted to just spheres and boxes and capsules, which seem to work flawlessly...
				boolean res = SpatialCollider.getCollider().testCollision(spatial1,spatial2,ODECollider.CollisionMode.PRIMITIVE_ONLY);
				//System.out.println("ODE\t" + (System.nanoTime() - time));
				return res;
			}catch(NotTestableException e)
			{
			//	e.printStackTrace();
			}finally{
			//	StateManager.getFunctionalState().unlock();
			}*/
/*		
		try{
			Vector3f pos = new Vector3f();
			boolean res = JBulletIntersection.getInstance().hasIntersection(spatial1, spatial2, pos);
			System.out.println(res);
			if(res)
			{			
			
				StateManager.getDesignState().getRootNode().attachChild(b);
				b.updateRenderState();
				b.getLocalTranslation().set(pos);
				b.getLocalTranslation().z += (float) Math.random();
			}
			return res;
		}catch(CollisionNotTestableException e)
		{
			
		}*/
		
	//   time = System.nanoTime();
		if ((spatial1.getType() & SceneElement.TRIMESH) == 0 || (spatial2.getType() & SceneElement.TRIMESH) == 0) 
			return false;
		else	
		{
			
			//return TriMeshCollisionTest.hasCollision((TriMesh)spatial1, (TriMesh)spatial2);
			boolean res =  ((TriMesh) spatial1).hasTriangleCollision((TriMesh)spatial2);
			//System.out.println("TRIMESH\t" + (System.nanoTime() - time));
			return res;
		}
	//	return false;
	}
//	static Box b = new Box("",new Vector3f(), 0.01f,0.01f,3f);
	/**
	 * Note: Avoid calling this - it is almost certainly not neccesary.
	 * @param node
	 */
	protected static void updateToLeaf(Node node)	
	{
		if (node == null)
			return;
		node.updateWorldVectors();
		if (node.getQuantity() == 0)
			return;
		for (Spatial child:((Node)node).getChildren())
		{
			if (child instanceof Node)
				updateToLeaf((Node)child);
			else
				child.updateWorldVectors();
		}
		
			
	}
	

	protected static void updateWorldBoundToRoot(Spatial toUpdate)
	{
		toUpdate.updateWorldBound();
		if (toUpdate.getParent() != null)
			updateWorldBoundToRoot(toUpdate.getParent());
	}
	
	protected static void updateWorldVectorsToRoot(Spatial toUpdate)
	{
		if (toUpdate.getParent() != null)
			updateWorldVectorsToRoot(toUpdate.getParent());
		toUpdate.updateWorldVectors();
	}

	private static Vector3f tempLoc = new Vector3f();
	protected static Vector3f intersectWithBatch(Ray ray, TriangleBatch geom)
	{
		Vector3f store = new Vector3f(Float.MAX_VALUE,Float.MAX_VALUE,Float.MAX_VALUE);
		 float lastDist = Float.MAX_VALUE;

		 ArrayList<Integer> triList = new ArrayList<Integer>();
		findTrianglePick(geom,ray,triList);
		Vector3f[] vertices = new Vector3f[3];

		geom.getParentGeom().getParent().updateWorldBound();
		geom.getParentGeom().updateWorldVectors();
		for (int curTri = 0; curTri<triList.size(); curTri++)
		{
			geom.getTriangle(triList.get(curTri), vertices);
			for(Vector3f vertex:vertices)
				geom.getParentGeom().localToWorld(vertex,vertex);//hopefully this doesnt change the geom's data
			float tempDist;
			ray.intersectWhere(vertices[0],vertices[1],vertices[2],tempLoc);
			tempDist = tempLoc.distance(DisplaySystem.getDisplaySystem().getRenderer().getCamera().getLocation());
			if (tempDist<lastDist)
			{
				store.set(tempLoc);
				lastDist = tempDist;
			}
			
		}
		 
		return store;
	}
	
	/**
	 * Returns an array filled with the index numbers of the triangles hit
	 * @param batch
	 * @param toTest
	 * @param results
	 */
    protected static void findTrianglePick(TriangleBatch batch, Ray toTest, ArrayList<Integer> results) {
    	if(!batch.getParentGeom().isCollidable())
    		return;
    	
    	batch.getParentGeom().updateWorldVectors();
    	
    	batch.updateWorldBound();
        if (batch.getWorldBound().intersects(toTest)) {
        	CollisionTree ct = CollisionTreeManager.getInstance().getCollisionTree(batch);
        	if(ct != null) {
	            ct.getBounds().transform(
	                    batch.getParentGeom().getWorldRotation(), batch.getParentGeom().getWorldTranslation(), batch.getParentGeom().getWorldScale(),
	                    ct.getWorldBounds());
	            ct.intersect(toTest, results);
	            
        	}
        }
    }
    

    /**
	 * Give the supplied spatial to another node, preserving the world vectors (scale, translation, rotation) of the node
	 * @param toGive
	 * @param receive
	 */
	public static void giveSpatialAway(Spatial toGive, Node receive)
	{		
		//Node oldParent = toGive.getParent();
		 Quaternion worldOriginalRotation = new Quaternion();
		 Quaternion checkRotationTemp = new Quaternion();
		 Vector3f worldTranslationTemp = new Vector3f();	
		 Quaternion worldRotationTemp = new Quaternion();
		 
		toGive.updateWorldVectors();

		worldTranslationTemp.set(toGive.getWorldTranslation());
		worldOriginalRotation.set (toGive.getWorldRotation());
		
	//	Vector3f axis = new Vector3f();
	//	System.out.println(":" + toGive);
	//	System.out.println("pre angle: " +  toGive.getWorldRotation().toAngleAxis(axis) + "\t" + axis);
		
		receive.updateWorldVectors();
		checkRotationTemp.set(receive.getWorldRotation());
		(worldRotationTemp.set(checkRotationTemp)).inverseLocal();
		
	/*	if (!checkRotationTemp.equals(receive.getWorldRotation()))//only update if neccesary
		{
			checkRotationTemp.set(receive.getWorldRotation());
			(worldRotationTemp.set(checkRotationTemp)).inverseLocal();//cache this for efficiency
		}//if this right?? yes*/
		//why would this be called twice for a single collision? possibly because the node is first added to a new group (Wasn't previously in one) then combined
		
	//	StateManager.getGame().lock();
	//	try{
			receive.attachChild(toGive);//attach all of this object's children to the collision object
	//	}finally
	//	{
	//		StateManager.getGame().unlock();
	//	}
		//(this automatically removes the child from their previous node)
		
	//	toGive.updateWorldVectors();//parent is already in sync w/ world
		toGive.getLocalRotation().set( worldRotationTemp).multLocal(worldOriginalRotation) ;//worldOriginalRotation.multLocal(worldRotationTemp));//rotate the object to exactly counter the world rotation of the parent object, then rotate it forwards again to its original world rotation
	
		//receive.updateWorldVectors();
		toGive.getLocalTranslation().set(receive.worldToLocal(worldTranslationTemp, worldTranslationTemp));
		toGive.updateWorldVectors();
		
	//	ConstructorTool.updateToWorld(toGive);
		//the angle is the same, but the axis is different!
	//	System.out.println("post angle: " + toGive.getWorldRotation().toAngleAxis(axis) + "\t" + axis);
	}
	
	
	
	

	
	public BoundingVolume getWorldBound() {
		if (getSpatial().getWorldBound() == null)
		{
			getSpatial().updateModelBound();
			getSpatial().updateWorldBound();
			
			if (getSpatial().getWorldBound() == null)
			{
				getSpatial().setModelBound(new BoundingBox());
				getSpatial().updateModelBound();
				getSpatial().updateWorldBound();
				if(getSpatial().getWorldBound() == null)
				{
					StateManager.getLogger().warning("Spatial has null bound!");
					BoundingBox c = new BoundingBox();
					c.setCenter(new Vector3f(100,100,100));
					return c;
				}
			
			}
			
			
		}
		return getSpatial().getWorldBound();
	}


	public boolean isLoadable() {
		return true;//this.isLoadable;
	}


	
	public void setLoadable(boolean isLoadable) {
		//this.isLoadable = isLoadable;
	}
	
	public void refreshLockedData() {
	//	if (this.getSpatial().getLocks() > 0)
			this.getSpatial().refreshBranch();
/*		int locks = this.getSpatial().getLocks();
		this.getSpatial().unlock();
		StateManager.getGame().lock();
		try{
		this.getSpatial().updateGeometricState(0, true);
		}finally{
			StateManager.getGame().unlock();
		}
		this.getSpatial().setLocks(locks);*/
		
	}
	
	/**
	 * Apply a render state to the given 'position' in this spatial, where position is defined by the spatial.
	 * All spatials are required to support at least position 0, but may choose to interpret it as they wish.
	 * In particular, this function may do nothing if position >0
	 * @param state
	 * @param position
	 */
	public void applyRenderState(RenderState state,int position)
	{
		if (position == 0)
		{
			this.getSpatial().setRenderState(state);
			this.getSpatial().updateRenderState();
		}
	}
	/**
	 * Return the number of positions (including 0)
	 * @return
	 */
	public int getNumberOfPositions()
	{
		return 1;
	}
}
