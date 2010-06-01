package com.golemgame.structural.structures.particles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.jme.math.Vector3f;
import com.jmex.effects.particles.ParticleMesh;

public class RandomPathGenerator {
	//generates random paths, at random times.
	private static final float pathCreationProbability = 0.1f;
	private static final float pathDeathProbability = 0.001f;
	private static final Random random = new Random();
	private static final float maxAcceleration = 20f;
	
	private final PathInfluence toControl;
	private final ParticleMesh meshToControl;
	private final int maxPathCreators;
	
	private ArrayList<RandomPath> pathCreators = new ArrayList<RandomPath>();
	
	public RandomPathGenerator(PathInfluence toControl,ParticleMesh meshToControl, int pathCreators) {
		super();
		this.toControl = toControl;
		this.meshToControl = meshToControl;
		this.maxPathCreators = pathCreators;
	}


	
	public void update(float tpf)
	{
		//randomly kill a path
	/*	if (!pathCreators.isEmpty()){
			if (random.nextFloat()<pathDeathProbability)
			{
				int index = random.nextInt(pathCreators.size());
				RandomPath path = pathCreators.get(index );
				path.closePath();
				pathCreators.remove(index);//don't advance i in this case
			}		
		}
		*/
		
		Iterator<RandomPath> creatorIterator = pathCreators.iterator();
		while(creatorIterator.hasNext())
		{
			RandomPath path = creatorIterator.next();
			if (path.tooOld())
			{
				creatorIterator.remove();
			}
		}
			
		
		//randomly create some paths
		if (pathCreators.size()<maxPathCreators)
		{
			//if (random.nextFloat()<pathCreationProbability)
			{
				Vector3f dir = new Vector3f();
				this.meshToControl.getRandomVelocity(dir);
				
				Vector3f initialPosition = new Vector3f();
			
				//initialPosition.set(meshToControl.getWorldTranslation()).addLocal(meshToControl.getOriginCenter()).addLocal(meshToControl.getOriginOffset());
				RandomPath path = new RandomPath(dir,initialPosition, Integer.MAX_VALUE,5* random.nextFloat());
				toControl.addPath(path);
				pathCreators.add(path);
			}
		}
		
		for (RandomPath path:pathCreators)
		{
			path.update(tpf);
		}
	}
	

	
	private static Vector3f acceleration = new Vector3f();
	/**
	 * An individual random path
	 * @author Sam
	 *
	 */
	private class RandomPath extends ParticlePath
	{
		
		public RandomPath(Vector3f initialDirection, Vector3f initialPosition, int maxFollowers, float lifespan) {
			super(initialDirection,maxFollowers);
			this.currentPosition.set(initialPosition);
			this.currentVelocity.set(initialDirection);
			this.lifespan = lifespan;
		}

		private final float lifespan;
		
		
		
		private Vector3f currentPosition = new Vector3f();
		private Vector3f currentVelocity = new Vector3f();
		
		
		public Vector3f getNextPosition(float tpf)
		{
			acceleration.x = random.nextFloat()-0.5f;
			acceleration.y = random.nextFloat()-0.5f;
			acceleration.z = random.nextFloat()-0.5f;
			acceleration.normalizeLocal();
			acceleration.multLocal(random.nextFloat()*maxAcceleration*tpf);
			
			currentVelocity.addLocal(acceleration);
			acceleration.set(currentVelocity);//resuse this to save space
			currentPosition.addLocal(acceleration.multLocal(1f));
			return new Vector3f(currentPosition);
		}
		
		public void update(float tpf)
		{
			super.update(tpf);
			super.addToPath(getNextPosition(tpf));
			
		}
		
		public boolean tooOld()
		{
			return super.getAge()>lifespan;
		}
	}
	
}
