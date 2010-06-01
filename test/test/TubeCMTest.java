package test;

import java.util.Arrays;

import com.golemgame.physical.ode.OdeTubeStructure;
import com.jme.math.FastMath;

public class TubeCMTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(Arrays.toString( OdeTubeStructure.getCenterOfMass(0,1, FastMath.PI)));
		System.out.println(Arrays.toString( OdeTubeStructure.getCenterOfMass(0,1,2f* FastMath.PI)));
		System.out.println(Arrays.toString( OdeTubeStructure.getCenterOfMass(0.5f,1, FastMath.PI)));
		
		
	}

}
