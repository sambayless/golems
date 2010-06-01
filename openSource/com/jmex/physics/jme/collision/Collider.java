package com.jmex.physics.jme.collision;

import org.odejava.Body;
import org.odejava.GeomBox;
import org.odejava.Odejava;
import org.odejava.World;
import org.odejava.ode.Ode;

import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.shape.Box;

/**
 * Instantiating this class creates an ODE world, and two instances of each type of colliding object.
 * 
 * @author Sam
 *
 */
public class Collider {
	private World world;
	

	
	
	
	public Collider() {
		super();
	//	Odejava.init();
		//Odejava.init()
		world = new World();
		bodyBox1 = new Body("box1", world);
		bodyBox2 = new Body("box2", world);
		
		geomBox1 = new GeomBox("geomBox1", 1, 1, 1);
		geomBox2 = new GeomBox("geomBox2", 1, 1, 1);
		
		bodyBox1.addGeom(geomBox1);
		bodyBox2.addGeom(geomBox2);
		
		
	}

	public boolean testCollision(Geometry geom1, Geometry geom2)
	{
		if (geom1 instanceof Box && geom2 instanceof Box)
			return testCollision((Box) geom1, (Box) geom2);
			
		  return false;
	}
	
	private Body bodyBox1;
	private Body bodyBox2;
	
	private GeomBox geomBox1;
	private GeomBox geomBox2;
	
//	private Matrix3f _matrix1 = new Matrix3f();
//	private Matrix3f _matrix2 = new Matrix3f();
	
	private Vector3f _vector1 = new Vector3f();
	private Vector3f _vector2 = new Vector3f();
	
	public boolean testCollision(Box box1, Box box2)
	{
		
		bodyBox1.setPosition(box1.getWorldTranslation());
		bodyBox2.setPosition(box2.getWorldTranslation());
		bodyBox1.setQuaternion(box1.getWorldRotation());
		bodyBox2.setQuaternion(box2.getWorldRotation());
		
		Vector3f size1 = _vector1.set(box1.getWorldScale());
		size1.x *= box1.xExtent;
		size1.y *= box1.yExtent;
		size1.z *= box1.zExtent;
		
		geomBox1.setSize(size1);
		
		Vector3f size2 = _vector2.set(box2.getWorldScale());
		size2.x *= box2.xExtent;
		size2.y *= box2.yExtent;
		size2.z *= box2.zExtent;
		geomBox2.setSize(size2);
		
		int c = Ode.dCollide(geomBox1.getId(), geomBox2.getId(), 0, null, 0);
		
		return c != 0;		
	}
	

	
}
