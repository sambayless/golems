package test;

import javax.swing.JFileChooser;

import com.jme.intersection.Intersection;
import com.jme.math.Vector3f;


public class TestIntersection {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFileChooser z = new JFileChooser();
		z.showSaveDialog(null);
		Vector3f a = new Vector3f(-0.1277684f,2.1343315f,0.122862354f);
		Vector3f b = new Vector3f(0.11659971f,2.361037f,0.122862376f);
		Vector3f c = new Vector3f(0.21620041f,2.2536764f,-0.23069102f);
		Vector3f d = new Vector3f(-0.5554124f,1.7375966f,0.12286234f);
		Vector3f e = new Vector3f(-0.31104434f,1.9643021f,0.122862354f);
		Vector3f f = new Vector3f(-0.21144362f,1.8569417f,-0.23069105f);
		
		
		if (Intersection.intersection(a, b, c, d, e, f)) 
		{
			System.out.println("Without offset, these triangles intersect.");
		}else
			System.out.println("Without offset, these triangles fail to intersect.");
		
		Vector3f offset = new Vector3f(10,0,0);
		if (Intersection.intersection(a.add(offset), b.add(offset), c.add(offset), d.add(offset), e.add(offset), f.add(offset))) 
		{
			System.out.println("With offset, these triangles intersect.");
		}else
			System.out.println("With offset, these triangles fail to intersect.");
	}

}
