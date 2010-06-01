package com.golemgame.tool;


import com.golemgame.states.StateManager;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;



public class Tool
{
	private static final long serialVersionUID = 1L;

	protected static Vector3f xyOrientation = new Vector3f(0,0,1);
	protected static Vector3f yzOrientation = new Vector3f(1,0,0);
	
	private String name;
		
	protected Tool(String name) {
		super();
		this.name = name;

	}


	//public abstract MouseInputListener getMouseInputListener();
	//public abstract KeyInputListener getKeyInputListener();
	public String getName()
	{
		return name;
	}
	
	public void setCursor()
	{
		
	}

	
	
	private static Vector3f worldTranslationTemp = new Vector3f();	
	
	private static Quaternion worldOriginalRotation = new Quaternion();
	private static Quaternion checkRotationTemp = new Quaternion();
	private static  Quaternion worldRotationTemp = new Quaternion().set(checkRotationTemp).inverseLocal();
	
	
	/**
	 * Give the supplied spatial to another node, preserving the world vectors (scale, translation, rotation) of the node
	 * @param toGive
	 * @param receive
	 */
	public static void giveSpatialAway(Spatial toGive, Node receive)
	{		
		//Node oldParent = toGive.getParent();
		StateManager.getGame().lock();
		try{
		toGive.updateWorldVectors();

		worldTranslationTemp.set(toGive.getWorldTranslation());
		worldOriginalRotation.set (toGive.getWorldRotation());
		
	//	Vector3f axis = new Vector3f();
	//	System.out.println(":" + toGive);
	//	System.out.println("pre angle: " +  toGive.getWorldRotation().toAngleAxis(axis) + "\t" + axis);
		
		receive.updateWorldVectors();
		if (!checkRotationTemp.equals(receive.getWorldRotation()))//only update if neccesary
		{
			checkRotationTemp.set(receive.getWorldRotation());
			(worldRotationTemp.set(checkRotationTemp)).inverseLocal();//cache this for efficiency
		}//if this right?? yes
		//why would this be called twice for a single collision? possibly because the node is first added to a new group (Wasn't previously in one) then combined
		receive.attachChild(toGive);//attach all of this object's children to the collision object
		//(this automatically removes the child from their previous node)
		
	//	toGive.updateWorldVectors();//parent is already in sync w/ world
		toGive.getLocalRotation().set( worldRotationTemp).multLocal(worldOriginalRotation) ;//worldOriginalRotation.multLocal(worldRotationTemp));//rotate the object to exactly counter the world rotation of the parent object, then rotate it forwards again to its original world rotation
	
		//receive.updateWorldVectors();
		toGive.getLocalTranslation().set(receive.worldToLocal(worldTranslationTemp, worldTranslationTemp));
		toGive.updateWorldVectors();
		}finally{
			StateManager.getGame().unlock();
		}
	//	ConstructorTool.updateToWorld(toGive);
		//the angle is the same, but the axis is different!
	//	System.out.println("post angle: " + toGive.getWorldRotation().toAngleAxis(axis) + "\t" + axis);
	}
	
	

	public static void updateToWorld(Spatial node)
	{
		if (node == null)
			return;
		
		Node parent = node.getParent();
		if(parent == node)
			return;
		updateToWorld(parent);
		
		node.updateWorldVectors();
	}
	
	public static void updateToLeaf(Node node)	
	{
		if (node == null)
			return;
		node.updateWorldVectors();
		if (node.getQuantity() == 0)
			return;
		for (Spatial child:((Node)node).getChildren())
		{
			if(child == node)
				continue;
			if (child instanceof Node)
				updateToLeaf((Node)child);
			else
				child.updateWorldVectors();
		}
		
			
	}


	public static Vector3f getXYOrientation() {
		return xyOrientation;
	}

}
