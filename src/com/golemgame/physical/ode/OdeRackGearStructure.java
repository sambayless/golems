package com.golemgame.physical.ode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.golemgame.model.spatial.NodeModel;
import com.golemgame.model.spatial.shape.GearTooth;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.RackGearInterpreter;
import com.golemgame.physical.PhysicsComponent;
import com.golemgame.structural.collision.CollisionMember;
import com.golemgame.structural.structures.GearStructure;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.geometry.PhysicsBox;

public class OdeRackGearStructure extends OdeBoxStructure{
	private RackGearInterpreter interpreter;
	
	public OdeRackGearStructure(PropertyStore store) {
		super(store);
		interpreter = new RackGearInterpreter(store);
	}
	

	private transient List<GearTooth> collidableTeeth=null;

	@Override
	public void buildCollidable(CollisionMember collidable) {
		super.buildCollidable(collidable);
		
		float height = interpreter.getToothHeight();
		float width = interpreter.getToothWidth();
		float angle = interpreter.getToothAngle();
	//	int numberOfTeeth = interpreter.getNumberOfTeeth();
		
		float deltaH = 0;
		if(!(width == 0 && angle == 0 ))
			deltaH = GearStructure.exactHeight(height,width,angle,super.interpreter.getExtent().y);

		height += deltaH;
		float fullWidth = width*2f + (float)Math.tan(angle/2f)*height*2f;
		
		float curLength = super.interpreter.getExtent().x;
		float curWidth = super.interpreter.getExtent().z;
		
		int numberOfTeeth;
		if(width == 0 && angle == 0 )
		{
			numberOfTeeth = 0;	//remove all teeth
		
		}else
			numberOfTeeth = (int) Math.floor((curWidth*2f + width)/ (fullWidth));//(int)Math.floor( 2f*FastMath.PI*super.interpreter.getExtent().y)/fullWidth);//interpreter.getNumberOfTeeth();
		
			
		
		
		if(numberOfTeeth <= 0)
			return;
		collidableTeeth= new ArrayList<GearTooth>();
		
		NodeModel toothModel = new NodeModel();
		collidable.getModel().addChild(toothModel);
		//create or remove teeth as needed
		for (int i = 0; i <numberOfTeeth;i++)
		{
			GearTooth tooth = new GearTooth();
			collidable.registerCollidingModel(tooth);
			toothModel.addChild(tooth);			
			collidableTeeth.add(tooth);
		}
		for(GearTooth tooth:collidableTeeth)
		{
			super.getStructuralAppearanceEffect().attachModel(tooth);
			
		}
	//	this.getStructuralAppearanceEffect().applyEffect(toothModel);
		//this.getAppearance().makeCopy(collidableTeeth.toArray(new GearTooth[0]), true);
		toothModel.getLocalTranslation().set(super.physicsModel.getLocalTranslation());
	//	toothModel.getLocalTranslation().y += super.interpreter.getExtent().y;
		toothModel.getLocalRotation().set(super.physicsModel.getLocalRotation());
		//toothModel.getLocalTranslation().y = super.interpreter.getExtent().y*2f;
		toothModel.getLocalScale().y = super.interpreter.getExtent().x*2f;
	//	toothModel.getLocalRotation().multLocal(new Quaternion().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Z));//this rotation is to account for the extra rotation given to the parent cylinder model, that isnt in the design mode view
		toothModel.getLocalRotation().multLocal(new Quaternion().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Z));

		//toothModel.getLocalRotation().multLocal(new Quaternion().fromAngleNormalAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
		
		{
			//update all the teeth...
		//	float radiansPerTooth = FastMath.TWO_PI/((float)numberOfTeeth);
			
			GearTooth firstTooth = collidableTeeth.get(0);
			firstTooth.rebuild(height, width, angle);
		
	
			
			//float radius = super.interpreter.getRadius()-deltaH*1.3f;
			
			//evenly disperse the teeth around the circle.
			
			float spacing = fullWidth;// numberOfTeeth>1? curWidth*2f/((float)(numberOfTeeth-1)):0f;
			float x = 0;
			for(int i = 0;i<numberOfTeeth;i++)
			{
				GearTooth tooth = collidableTeeth.get(i);
				tooth.setHasSlopes(angle>0f);
				tooth.setHasWidth(width>0f);
				tooth.copyFrom(firstTooth);
				
				
				tooth.getLocalTranslation().set( super.interpreter.getExtent().getY(),0,x - curWidth + (fullWidth-width)/2f);
				x+= spacing;
				tooth.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Y);
				
				tooth.updateWorldData();
			}
			toothModel.updateWorldData();
		}			
	}



	@Override
	public float buildCollisionGeometries(PhysicsNode physicsNode,Collection<PhysicsComponent> components,
			Vector3f store) {
		if(collidableTeeth == null || collidableTeeth.isEmpty())
			return super.buildCollisionGeometries(physicsNode,components, store);
		float volume =  super.buildCollisionGeometries(physicsNode,components, store);
	//	float originalVolume = volume;
		float angle = interpreter.getToothAngle();
		float width = super.interpreter.getExtent().getX()*2f;
		Collection<PhysicsCollisionGeometry> geoms = new ArrayList<PhysicsCollisionGeometry>();
	
		Node toothModel = new Node();
		toothModel.getLocalTranslation().set(super.physicsModel.getLocalTranslation());
		//toothModel.getLocalTranslation().y += super.interpreter.getExtent().y;
		toothModel.getLocalRotation().set(super.physicsModel.getLocalRotation());
	//	toothModel.getLocalRotation().multLocal(new Quaternion().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Z));//this rotation is to account for the extra rotation given to the parent cylinder model, that isnt in the design mode view
		toothModel.getLocalRotation().multLocal(new Quaternion().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Z));

	//	toothModel.getLocalRotation().multLocal(new Quaternion().fromAngleNormalAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
		//toothModel.getLocalRotation().multLocal(new Quaternion().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Y));

		float density =  this.getMaterial().getDensity();
		
		toothModel.getLocalScale().setY(super.interpreter.getExtent().getX()*2f);
		toothModel.updateWorldVectors();
		float toothHeight = interpreter.getToothHeight();
		float toothWidth = interpreter.getToothWidth();
		for (GearTooth tooth:collidableTeeth)
		{			
			Node tempNode = new Node();
			toothModel.attachChild(tempNode);
			tempNode.getLocalTranslation().set(tooth.getLocalTranslation());
			tempNode.getLocalRotation().set(tooth.getLocalRotation());
			tempNode.updateWorldVectors();
			
			if (angle<FastMath.HALF_PI || toothWidth>0f)
			{
				if (toothWidth>0)
				{
					PhysicsBox box=physicsNode.createBox("box");
					box.getLocalTranslation().set(tooth.getTopBox().getLocalTranslation());
	
					box.getLocalScale().set(tooth.getTopBox().getLocalScale());
				
					tempNode.localToWorld( box.getLocalTranslation(),  box.getLocalTranslation() );
				    tempNode.getWorldRotation().mult(box.getLocalRotation(),box.getLocalRotation());
		
					physicsNode.attachChild(box);
					geoms.add(box);
					volume += box.getVolume()*density;
				}
				//}else
				 
				if(angle>0 ){
					
					
	
					
					PhysicsBox leftBox=physicsNode.createBox("leftbox");
					leftBox.getLocalTranslation().set(tooth.getLeftBox().getLocalTranslation());
					leftBox.getLocalRotation().set(tooth.getLeftBox().getLocalRotation());
					leftBox.getLocalScale().set(tooth.getLeftBox().getLocalScale());
					physicsNode.attachChild(leftBox);
					tempNode.localToWorld( leftBox.getLocalTranslation(),  leftBox.getLocalTranslation() );
				    tempNode.getWorldRotation().mult(leftBox.getLocalRotation(),leftBox.getLocalRotation());
		
					geoms.add(leftBox);
					volume += leftBox.getVolume()*density;
					
					PhysicsBox rightBox=physicsNode.createBox("rightbox");
					rightBox.getLocalTranslation().set(tooth.getRightBox().getLocalTranslation());
					rightBox.getLocalRotation().set(tooth.getRightBox().getLocalRotation());
					rightBox.getLocalScale().set(tooth.getRightBox().getLocalScale());
					tempNode.localToWorld( rightBox.getLocalTranslation(),  rightBox.getLocalTranslation() );
				    tempNode.getWorldRotation().mult(rightBox.getLocalRotation(),rightBox.getLocalRotation());
		
					physicsNode.attachChild(rightBox);
					geoms.add(rightBox);
					volume += rightBox.getVolume()*density;

				}	
			}else
			{ //special case that can be represented by just one box per tooth
				
			
				float delta = GearStructure.exactHeight(toothHeight,0f,angle,super.interpreter.getExtent().getY());
				
				PhysicsBox box=physicsNode.createBox("box");
				box.getLocalTranslation().set(tooth.getTopBox().getLocalTranslation());
				box.getLocalTranslation().z = 0f;

				float scale = FastMath.sqrt(2f*FastMath.sqr((toothHeight+delta)));
				
				box.getLocalScale().set(scale,1f,scale);
				//box.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI/2f, Vector3f.UNIT_Y);
				
				tempNode.localToWorld( box.getLocalTranslation(),  box.getLocalTranslation() );
			    tempNode.getWorldRotation().mult(box.getLocalRotation(),box.getLocalRotation());
	
				physicsNode.attachChild(box);
				geoms.add(box);
				volume += box.getVolume()*density;
			}
		}
		
		for(PhysicsCollisionGeometry geom:geoms)
		{
			geom.getLocalScale().setY(width);
		//	geom.getLocalTranslation().addLocal(physicsModel.getLocalTranslation());
		//	geom.getLocalRotation().multLocal(physicsModel.getLocalRotation());
			//geom.getLocalRotation().multLocal(new Quaternion().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_X));
		}
		
	/*	if(collidableTeeth.size()==1)
		{//have to offcenter the center of mass
			store.zero();
			//store.divideLocal(originalVolume);
			Vector3f component = new Vector3f();
			System.out.println(store);
			for (PhysicsCollisionGeometry g:geoms)
			{
				g.getWorldScale().set(g.getLocalScale());//temporary
				component.set(g.getLocalTranslation()).multLocal(g.getVolume()*density);
				store.addLocal(component);
			}
			
			//store.divideLocal(geoms.size()+1f);
		}*/
	//	System.out.println(store);
		return volume;
	}


}
