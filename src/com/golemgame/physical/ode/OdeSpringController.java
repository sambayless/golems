package com.golemgame.physical.ode;

import com.golemgame.physical.PhysicsObject;
import com.golemgame.physical.SpringController;
import com.jme.math.FastMath;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.system.DisplaySystem;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.Joint;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.PhysicsUpdateCallback;

public class OdeSpringController implements SpringController{
	private Vector3f position = new Vector3f();
	private Joint springJoint = null;
	//private Joint staticJoint = null;
	public static final float SPRING_STRENGTH = 1000f;
	private DynamicPhysicsNode target = null;
	private volatile boolean newTarget= false;
	//private DynamicPhysicsNode primary = null;
	private float dist = 0f;
	public OdeSpringController(OdePhysicsWorld world) {
		super();
		springJoint =world.getPhysics().createJoint();
	//	primary = world.getPhysics().createDynamicNode();
	//	staticJoint = world.getPhysics().createJoint();
	
		world.getPhysics().addToUpdateCallbacks(new PhysicsUpdateCallback()
		{

			public void afterStep(PhysicsSpace space, float time) {
				
				
			}

			public void beforeStep(PhysicsSpace space, float time) {
				update();
			}
			
		});
	}

	public void setPosition(float x, float y) {
		if(target == null)
			return;
			//System.out.println(dist);
		//setPosition(new Vector3f(5,5,0));
	//	setPosition(new Vector3f(x,y,dist));
		setPosition( DisplaySystem.getDisplaySystem().getWorldCoordinates(new Vector2f(x,y), dist));
		
	}


	public  synchronized void setTarget(PhysicsObject object)
	{
		if(!(object instanceof OdePhysicsObject))
		{
			detach();
			return;
		}
		
		OdePhysicsObject d = (OdePhysicsObject)object;
		if(target!=null)
			detach();
		if(!d.isStatic())
		{
			target = (DynamicPhysicsNode) d.getPhysicsNode();
			
			//Vector3f pos = target.getLocalTranslation();		//	Vector3f norm = StateManager.getCameraManager().getCameraNormal();
			newTarget = true;
			
			//NOTE: Do all the joint settings in the physics thread instead of here...
			
		//	this.setPosition(target.getLocalTranslation());//watch out, this could be worng...
			
			//primary.getLocalTranslation().set(target.getLocalTranslation());
	/*		springJoint.setActive(true);
			float springConstant = SPRING_STRENGTH*target.getMass();
		//	System.out.println(target.getMass());
			springJoint.setSpring(springConstant, 2f*FastMath.sqrt(springConstant*target.getMass()) );//critically damped
			//((OdeJoint)springJoint).setCFM(0);
			//((OdeJoint)springJoint).setERP(0.4f);
			Vector3f oldPos = new Vector3f( target.getLocalTranslation());
			target.getLocalTranslation().set(getPosition(target.getLocalTranslation()));
			springJoint.setAnchor(new Vector3f());
			springJoint.attach(target);
			
			target.getLocalTranslation().set(oldPos);*/
		/*	staticJoint.attach(primary);
			staticJoint.setAnchor(primary.getLocalTranslation());*/
			
		//	((OdeJoint)staticJoint).setCFM(0);
		//	((OdeJoint)staticJoint).setERP(0.8f);
			
			//staticJoint.setSpring(springConstant, 2f*FastMath.sqrt(springConstant*target.getMass()));
			//staticJoint.setSpring(springConstant, 2f*FastMath.sqrt(springConstant*target.getMass()));
		//	springJoint.setAnchor(new Vector3f());
			//springJoint.attach(target);
			//springJoint.setAnchor(primary.getLocalTranslation());
		}
		
	}
	
	public synchronized void detach()
	{
		if(target!=null)
		{
			
		}
		springJoint.setActive(false);
		springJoint.detach();
	//	staticJoint.setActive(false);
	//	staticJoint.detach();
		target=null;
	}
	
	public synchronized void update()
	{
		if(newTarget && target!=null)
		{	
			newTarget = false;
			Vector3f pos = target.getLocalTranslation();		//	Vector3f norm = StateManager.getCameraManager().getCameraNormal();
			
			dist =  DisplaySystem.getDisplaySystem().getScreenCoordinates(pos).z;//0.2f;//pos.dot(norm);
			this.setPosition(target.getLocalTranslation());//watch out, this could be worng...		
			springJoint.setActive(true);
			float springConstant = SPRING_STRENGTH*target.getMass();
			springJoint.setSpring(springConstant, 2f*FastMath.sqrt(springConstant*target.getMass()) );//critically damped
			Vector3f oldPos = new Vector3f( target.getLocalTranslation());
			target.getLocalTranslation().set(getPosition(target.getLocalTranslation()));
			springJoint.setAnchor(new Vector3f());
			springJoint.attach(target);
			
			target.getLocalTranslation().set(oldPos);			
		}else{
		
			if(target!=null)
			{			
				springJoint.detach();
				float springConstant = SPRING_STRENGTH*target.getMass();
				//	System.out.println(target.getMass());
				springJoint.setSpring(springConstant, 2f*FastMath.sqrt(springConstant*target.getMass()) );//critically damped
			
				Vector3f oldPos =new Vector3f( target.getLocalTranslation());
				target.getLocalTranslation().set(getPosition(target.getLocalTranslation()));
				springJoint.setAnchor(new Vector3f());
				springJoint.attach(target);
				
				target.getLocalTranslation().set(oldPos);
				target.updateWorldVectors();
		
			}
		}
/*		if(target!=null)
		{
			springJoint.setAnchor(position);
			System.out.println(position);
		}*/
		
	}
	
	public synchronized Vector3f getPosition(Vector3f store) {
		if(store==null)
			store = new Vector3f();
		store.set(position);
		return store;
	}

	public synchronized void setPosition(Vector3f position) {
		this.position.set(position);
	
	}

}
