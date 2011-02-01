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
package com.golemgame.structural.structures.particles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.jmex.effects.particles.Particle;
import com.jmex.effects.particles.ParticleGeometry;
import com.jmex.effects.particles.ParticleInfluence;
import com.jmex.effects.particles.ParticleMesh;

/**
 * This will be one part of the physical gas system. It will control the paths of released particles.
 * It will receive particle paths from the physics gas; it will assign new particles to each of those paths randomly
 * but with exponential decay over time until at some limit the path is removed.
 * 
 * The gas system will generate paths as the physics particles are moving; a path will be a sequence of vectors representing
 * each position the particle was at at each time step (this may be reduced in some way for efficiency?);
 * 
 * The particle influencer will then direct particles along those paths, ensuring that a) they never go beyond the end of a path (fade out death if they hit that point)
 * and b) that their movement is random and believable around that path.
 * 
 * Finally, the particle influencer will hold in reserve n particles, where n is the number of gas particles.
 * These particles will be released only at the begining of a new path; they will follow the physical gas particles exactly.
 * 
 * It will be the concern of the rocket class to ensure that the number of gas particles is <= the number of visual particles.
 * It will be the responsibility of the rocket class to ensure that the launch velocities, launch times, and directions of the gas particles 
 * match the visual effect settings.
 * 
 * @author Sam
 *
 */
public class PathInfluence extends ParticleInfluence{
	//private static final float MAX_AGE = 0.5f ;//500 ms

	private final ArrayList<ParticlePath> paths = new ArrayList<ParticlePath>();
	private final Random random = new Random();
	private final ParticleMesh source;
	
	private final int numPhysicalParticles;
	
	private Lock pathLock = new ReentrantLock();
	
	private Map<Particle, PathServer> pathMap = new HashMap<Particle, PathServer>();
	public PathInfluence(ParticleMesh source, int numPhysicalParticles) {
		super();
		this.source = source;
		int particles = source.getNumParticles();
		this.numPhysicalParticles = numPhysicalParticles;
		//the first numPhysicalParticles will be only assigned to fresh paths, and will vanish as soon as a path is closed.
	}
	
	
	public void addPath(ParticlePath path)
	{
		pathLock.lock();
		try{
			paths.add(path);
		}finally{
			pathLock.unlock();
		}
	}
	
	@Override
	public void apply(float dt, Particle particle, int index) {
		PathServer currentPath = pathMap.get(particle);
		
		if (currentPath == null || particle.getCurrentAge() == 0)
		{
			ParticlePath path = getPath();
			if (path == null)
			{
				
				particle.kill();
				return;
			}else
			{
				currentPath = path.getPathServer();
				currentPath.setInitialPosition(particle.getPosition());
				pathMap.put(particle, currentPath);
			}
		}
		try{
			currentPath.getNextPosition(particle.getPosition());
		}catch(EndOfPathException e)
		{
			pathMap.remove(particle);
		}
		
	}
	
	private ArrayList<ParticlePath> unfilledPaths = new ArrayList<ParticlePath>();

	private ParticlePath getPath() {
		pathLock.lock();
		try{
			if (unfilledPaths.isEmpty())
				return null;
			
			ParticlePath path = unfilledPaths.get(random.nextInt(unfilledPaths.size()));
			path.addFollower();
			//if (!path.isUnfilled())
				unfilledPaths.remove(path);
			
			return path;
		}finally{
			pathLock.unlock();
		}
	}



	@Override
	public void prepare(ParticleGeometry particleGeom) {
		Collections.sort(paths);
		
	
		//remove any paths that are too old
	/*	while((!paths.isEmpty()) && paths.get(paths.size()-1).getAge() > MAX_AGE )
		{
			paths.remove(paths.size()-1);//remove the last element in paths
		}*/
		
	/*	for(int i = 0;i<particleGeom.getNumParticles();i++)
		{
			Particle p = particleGeom.getParticle(i);
			PathServer path = pathMap.get(p);
			if (path != null && (p.getStatus()!= Particle.ALIVE || p.getCurrentAge() ==0 || ! paths.contains(path.getPath())))
			{
				path.getPath().removeFollower();
				pathMap.remove(p);
			}
		}*/
		unfilledPaths.clear();
		for(ParticlePath path:paths)
		{
			if ( path.isUnfilled() && path.getFollowers()<path.getNumberOfPathPositions())
			{
				this.unfilledPaths.add(path);
			}
		}
		
		
		super.prepare(particleGeom);
		
	}
	
	
	
}
