package com.golemgame.physical.ode;

import com.golemgame.physical.PhysicsComponent;
import com.golemgame.physical.PhysicsInteractionManager;
import com.golemgame.states.StateManager;
import com.jme.input.InputHandler;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.system.DisplaySystem;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.contact.ContactInfo;
import com.jmex.physics.geometry.PhysicsRay;
import com.jmex.physics.impl.ode.OdeCollisionGroup;
import com.jmex.physics.impl.ode.geometry.OdeRay;

public class OdePhysicsInteractionManager extends PhysicsInteractionManager {

	/*private OdePhysicsWorld physicsWorld;
	private StaticPhysicsNode rayNode;
	private PhysicsRay ray;
	private InputHandler pickHandler;
	private OdeCollisionGroup collisionGroup;*/
	
	public OdePhysicsInteractionManager(final OdePhysicsWorld physicsWorld) {
		super(physicsWorld);
	//	this.physicsWorld=physicsWorld;
		
	//	ArrayList<CollisionGroup> groups = new ArrayList<CollisionGroup>( physicsWorld.getPhysics().getCollisionGroups());
		
	//	collisionGroup = 	(OdeCollisionGroup)physicsWorld.getPhysics().createCollisionGroup("Pick Group");
	//	collisionGroup.collidesWith(((OdePhysicsSpace)physicsWorld.getPhysics()).getDefaultCollisionGroup(), true);
		
	/*	for(CollisionGroup g:groups)
		{
			g.collidesWith(collisionGroup, false);
			if(g.getName().equalsIgnoreCase("default"))
				collisionGroup.collidesWith(g, true);
			else
				collisionGroup.collidesWith(g, false);
		}
		*/
		
		//collisionGroup.collidesWith(((OdePhysicsSpace)physicsWorld.getPhysics()).get, false)
		
	/*	rayNode = physicsWorld.getPhysics().createStaticNode();
		ray = rayNode.createRay("Pick Ray");
		OdeRay odeRay = (OdeRay)ray;
		odeRay.getOdeGeom().setCategoryBits(1<<3);
		odeRay.getOdeGeom().setCollideBits(1<<3);
		
	//	rayNode.setCollisionGroup(collisionGroup);
		rayNode.setActive(false);
		//PhysicsSpatialMonitor.getInstance().registerGhost(ray);
		//PhysicsSpatialMonitor.getInstance().registerGhost(rayNode);
	        pickHandler = new InputHandler();
	        pickHandler.setEnabled(false);
	        
           pickHandler.addAction( new InputAction(){
        	   public void performAction( InputActionEvent evt ) {
                   final ContactInfo info = (ContactInfo) evt.getTriggerData();
                   float dist = info.getPenetrationDepth() ;
                   
                   PhysicsCollisionGeometry other = info.getGeometry1() == ray ? info.getGeometry2() : info.getGeometry1();
                   if (dist < minPickDistance && getCollisionDetectionRequirements().meetsRequirements(physicsWorld.getPhysicsComponent(other), dist))
                   {
                       minPickDistance =dist;
                       target = other;
                   }
               }
           }  , ray.getCollisionEventHandler(), false );*/
        
        /*
		//add a listener to physics collisions
		PhysicsSpatialMonitor.getInstance().addPhysicsCollisionListener(rayNode, new PhysicsCollisionListener()
		{
			public void collisionOccured(ContactInfo info, PhysicsSpatial source) {
				
			}			
		});*/
		
	}

/*	@Override
	public PhysicsComponent pickDynamicPhysics(Vector2f location){
		  target = null;
		final  Vector2f mLocation = new Vector2f(location);//copy for private use so it cant be changed
		//StateManager.getFunctionalState().blockingExecuteInPhysics(new Runnable()
		//CRASHES ON TRIMESH SELECTION!!!
		//{
		StateManager.getFunctionalState().acquireGraphicsAndPhysicsLocks();
		try{
			Ray pickRay = new Ray();
	
		//	public void run() {
				DisplaySystem.getDisplaySystem().getPickRay(mLocation, StateManager.IS_AWT_MOUSE, pickRay);
	
		        ray.getLocalTranslation().set( pickRay.getOrigin() );
		        ray.getLocalScale().set( pickRay.getDirection() ).multLocal( 100000f );
		        ray.updateWorldVectors();
		        target = null;
		      
		        minPickDistance = Float.POSITIVE_INFINITY;
		        pickHandler.setEnabled( true );	        
		        physicsWorld.getPhysics().pick( ray );
		       
		        pickHandler.update( 0f );
		        pickHandler.setEnabled( false );
		  

		//	}		
		}finally{
			StateManager.getFunctionalState().releaseGraphicsAndPhysicsLocks();
		}
		//});
		
		if(target!=null)
		{
			PhysicsComponent comp = physicsWorld.getPhysicsComponent(target);
			return comp;
		}
		return null;
	}*/
	
/*	private float minPickDistance = Float.MAX_VALUE;
	private PhysicsCollisionGeometry target = null;
*/

	
  
}
