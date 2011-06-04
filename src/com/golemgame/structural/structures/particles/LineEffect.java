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

import com.golemgame.model.spatial.LockedUpdateNode;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.LineEffectInterpreter;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.TransformMatrix;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;
import com.jme.scene.Line;
import com.jme.scene.Spatial;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.effects.particles.ParticleFactory;
import com.jmex.effects.particles.ParticleGeometry;
import com.jmex.effects.particles.ParticleLines;
import com.jmex.effects.particles.WanderInfluence;

public class LineEffect {

	  private Vector3f offset = new Vector3f(1f,0,0);
	  private ParticleLines mesh;
	
	  private LockedUpdateNode node = new LockedUpdateNode();
	  
	  private LineEffectInterpreter interpreter;
	  
	public LineEffect(PropertyStore properties) {
		super();
		
			interpreter = new LineEffectInterpreter(properties);
		      

		     AlphaState as1 = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
		     
		     if (interpreter.isLuminous())
		     {
		      as1.setBlendEnabled( true );
		      as1.setSrcFunction( AlphaState.SB_SRC_ALPHA );
		      as1.setDstFunction( AlphaState.DB_ONE );
		      as1.setTestEnabled( true );
		      as1.setTestFunction( AlphaState.TF_GREATER );
		      as1.setEnabled( true );
		     }else
		     {		    
		    	 as1.setBlendEnabled(true);
		    	 as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		    	 as1.setDstFunction(AlphaState.SB_ONE_MINUS_SRC_ALPHA);					
		    	 as1.setTestEnabled(true);						
		    	 as1.setTestFunction(AlphaState.TF_ALWAYS);	
		    	 as1.setEnabled(true);
		     }
	/*	     
		      TextureState ts =  DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		      if (interpreter.isLuminous())
			     {
		    	  ts.setTexture(
		              TextureManager.loadTexture(
		                      getClass().getClassLoader().getResource(
		                              "com/golemgame/data/textures/flaresmall.png" ),
		                      Texture.MM_LINEAR_LINEAR,
		                      Texture.FM_LINEAR ) );
		      		ts.setEnabled( true );
			     }else
			     {	
			         ts.setTexture(
				              TextureManager.loadTexture(
				                      getClass().getClassLoader().getResource(
				                              "com/golemgame/data/textures/flarealpha.png" ),
				                      Texture.MM_LINEAR_LINEAR,
				                      Texture.FM_LINEAR ) );
				      ts.setEnabled( true );
			     }
		     */
		      mesh = ParticleFactory.buildLineParticles("lines",interpreter.getNumberOfParticles());
		      
		  /*    mesh.setLineWidth(3);
		      mesh.setMode(Line.SEGMENTS);
		      mesh.setAntialiased(true);
		      mesh.setParticleOrientation(FastMath.HALF_PI); // Particle Lines
		                                                        // are horizontal by
		                                                        // default, this
		                                                        // turns it vertical
		      mesh.setEmissionDirection(new Vector3f(0, 1, 0));
		      mesh.setOriginOffset(new Vector3f(0, 0, 0));
		      mesh.setInitialVelocity(.006f);
		      mesh.setStartSize(4f);
		      mesh.setEndSize(1.5f);
		      mesh.setMinimumLifeTime(1250f);
		      mesh.setMaximumLifeTime(1800f);
		      mesh.setVelocityAligned(true);
		   //   mesh.setCameraFacing(false);
		 //     mesh.setParticleSpinSpeed(3 * FastMath.PI); // rotate 3pi times (1.5 rotations) per second
		      mesh.setStartColor(new ColorRGBA(1, 0, 0, 1));
		      mesh.setEndColor(new ColorRGBA(0, 1, 0, 0));
		        mesh.setMaximumAngle(0f);
		        mesh.getParticleController().setControlFlow(false);
		        mesh.warmUp(120);*/
		        
		      mesh.setLineWidth(interpreter.getLineWidth());
		      mesh.setMode(Line.SEGMENTS);
		      mesh.setAntialiased(true);
		      mesh.setEmissionDirection( new Vector3f( 0f, 1f, 0f ) );
		      mesh.setVelocityAligned(true);
		   //   mesh.setParticleOrientation(0.6f);
		   //   mesh.setParticleSpinSpeed(1f);
		   //   mesh.setEmitType(ParticleGeometry.PT_LINE);
		      mesh.setMaximumAngle(interpreter.getMaxAngle());
		      mesh.setMinimumAngle(interpreter.getMinAngle());
		      mesh.setSpeed(1f);
		      mesh.setMinimumLifeTime(interpreter.getMinLifeSpan());
		      mesh.setMaximumLifeTime(interpreter.getMaxLifeSpan());
		      
		      mesh.setStartColor(interpreter.getStartColor() );
		      mesh.setEndColor( interpreter.getEndColor() );
		      mesh.setInitialVelocity( interpreter.getInitialVelocity() );
		      mesh.setRotateWithScene(true);
		 
		      mesh.setCameraFacing(true);
		      
		      mesh.setStartSize(interpreter.getInitialSize());
		      mesh.setEndSize(interpreter.getFinalSize());
		      mesh.setControlFlow(true);
		    
		  
		   //   mesh.setReleaseRate(0);
		   //   mesh.setControlFlow(true);
		    // 
		    
		   
		      
		   //   mesh.setModelBound(new BoundingBox());
		  //    mesh.updateModelBound();
		      
	

		      ZBufferState zbuf = DisplaySystem.getDisplaySystem().getRenderer().createZBufferState();
		      zbuf.setWritable( false );
		      zbuf.setEnabled( true );
		      zbuf.setFunction( ZBufferState.CF_LEQUAL );

		    //  mesh.setRenderState( ts );
		      mesh.setRenderState( as1 );
		      mesh.setRenderState( zbuf );
		  
		    /*  PathInfluence pathInfluence = new PathInfluence(mesh,5);
		      
		       generator = new RandomPathGenerator(pathInfluence, mesh, 5);
		      mesh.addInfluence(pathInfluence);*/
		      
		      
		      node.attachChild(mesh);
		      mesh.setModelBound(new BoundingSphere());
		      mesh.updateModelBound();
	}

	
	public void init()
	{
	//	mesh.setReleaseVariance(0)
		mesh.setControlFlow(true);
		mesh.setCreateVisibleParticles(false);
		mesh.setReleaseRate(Integer.MAX_VALUE);
		 
		   mesh.randomizeLifeTimes();
		// mesh.warmUp(6 );
		   this.node.manualUpdate(0.1f,true);
		   this.node.manualUpdate(0.1f,true);
	
		 //  
		   mesh.setReleaseRate(0);
		   
		   mesh.setCreateVisibleParticles(true);
	}

	public Spatial getSpatial() {
		return node;
	}

	public void setGeom(Geometry geom) {
		mesh.setGeomBatch(geom.getBatch(0));
	}
	
	
//	private  static PropertyStore defaultEffect =null;
	




	public void update(float time) {
	//	 mesh.forceRespawn();
		//generator.update(time);
		
	    //mesh.setRotMatrix(node.getWorldRotation().toRotationMatrix());
	  //  mesh.setUpVector(Vector3f.UNIT_Z);
	//    mesh.setLeftVector(node.getWorldRotation().mult( Vector3f.UNIT_X));
	//    mesh.setVelocityAligned(true);
	//    System.out.println(mesh.getLeftVector());
		this.node.manualUpdate(time,false);
		

	}




	public void setDirection(Vector3f dir) {
		mesh.setEmissionDirection(dir);

	}

	public void setOffset(Vector3f offset)
	{
		  mesh.setOriginOffset(offset);
	}


/*	public void setSpeed(float percent) {
		mesh.setReleaseRate(Math.round(percent* mesh.getNumParticles()));
		
	}*/


	public void setEngagement(float percent) {
		
	//	mesh.setReleaseRate( Math.round(mesh.getNumParticles() * percent));
		if(FastMath.abs(percent) >0.1f)
		{			
			mesh.setReleaseRate(Integer.MAX_VALUE);
			mesh.setInitialVelocity(interpreter.getInitialVelocity() * percent);
			mesh.setStartSize(interpreter.getInitialSize()*percent);
			mesh.setEndSize(interpreter.getFinalSize()*percent);
			
		}else
		{
			//mesh.setControlFlow(true);
			mesh.setReleaseRate(0);
		}
	
		//mesh.setReleaseRate(Integer.MAX_VALUE);
	}


	public void disengage() {
		setEngagement(0);
	
	}


}
