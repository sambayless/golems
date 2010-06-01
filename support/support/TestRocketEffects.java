package support;

import jmetest.effects.TestDynamicSmoker;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.NodeHandler;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Disk;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.util.TextureManager;
import com.jmex.effects.particles.ParticleFactory;
import com.jmex.effects.particles.ParticleMesh;

public class TestRocketEffects  extends SimpleGame {


	    
	  private Node smokeNode;
	  private Vector3f offset = new Vector3f(1f,0,0);
	  private ParticleMesh mesh;

	  /**
	   * Entry point for the test,
	   * @param args
	   */
	  public static void main(String[] args) {
		  TestRocketEffects app = new TestRocketEffects();
	    app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
	    app.start();
	  }

	  /**
	   * builds the trimesh.
	   * @see com.jme.app.SimpleGame#initGame()
	   */
	  protected void simpleInitGame() {
	      cam.setLocation( new Vector3f( 0.0f, 25.0f, 45.0f ) );
	      cam.update();

	      smokeNode = new Node( "Smoker Node" );
	      smokeNode.setLocalTranslation( new Vector3f( 0, 50, -50 ) );

	      input = new NodeHandler( smokeNode, 10f, 1f );

	 
	      Geometry camBox;

	      camBox = new Box("", new Vector3f(), 1,1,1);
	      smokeNode.attachChild( camBox );
	      
	      Disk emitDisc = new Disk( "disc", 6, 6, 1.5f );
	      emitDisc.setLocalTranslation( offset );
	      emitDisc.setCullMode( SceneElement.CULL_ALWAYS );
	      smokeNode.attachChild( emitDisc );
	      rootNode.attachChild( smokeNode );

	      AlphaState as1 = display.getRenderer().createAlphaState();
	      as1.setBlendEnabled( true );
	      as1.setSrcFunction( AlphaState.SB_SRC_ALPHA );
	      as1.setDstFunction( AlphaState.DB_ONE );
	      as1.setTestEnabled( true );
	      as1.setTestFunction( AlphaState.TF_GREATER );
	      as1.setEnabled( true );

	      TextureState ts = display.getRenderer().createTextureState();
	      ts.setTexture(
	              TextureManager.loadTexture(
	                      TestDynamicSmoker.class.getClassLoader().getResource(
	                              "jmetest/data/texture/flaresmall.jpg" ),
	                      Texture.MM_LINEAR_LINEAR,
	                      Texture.FM_LINEAR ) );
	      ts.setEnabled( true );
	     
	      
	      /*
	       * ring
	       * 
	       * min life: 700, max life: 700
	       * min angle: 1, max angle 1
	       * initial size: 0, final 2
	       * 300 particles
	       * init v. = 0.01
	       * 
	       * color: start invisible, go to fully visible green.
	       */
	      /*
	      /*
	       * alien
	       * 
	       * min life: 300, max life: 3000
	       * min angle: 1, max angle 0
	       * initial size: 0.1, final 5
	       * 300 particles
	       * init v. = 0.01
	       * 
	       * color: start invisible, go to slightly visible blue.
	       */
	      /*
	      * magic
	       *  min, max life: 1000
	       * min angle: 1, max angle 0
	       * initial size: 0.1, final 5
	       * 300 particles
	       * init v. = 0.01
	       * 
	       * color: start invisible, go to slightly visible blue.
	       *
	       */
	      
	     //standard rocket
	/*      mesh = ParticleFactory.buildParticles("particles",30);
	      mesh.setEmissionDirection( new Vector3f( 1f, 0f, 0f ) );
	      mesh.setMaximumAngle( 0.3f );
	      mesh.setMinimumAngle(0f);
	      mesh.setSpeed( 1.0f );
	      mesh.setMinimumLifeTime( 100.0f );
	      mesh.setMaximumLifeTime( 1000.0f );
	      mesh.setStartSize( 1.6f );
	      mesh.setEndSize( 15.0f );
	      mesh.setStartColor( new ColorRGBA( 1.0f, 1.0f, 1.0f, 1.0f ) );
	      mesh.setEndColor( new ColorRGBA( 0.6f, 0.2f, 0.0f, 0.0f ) );
	      mesh.setInitialVelocity( 0.01f );
	      
	      mesh.setGeometry( camBox );
	      mesh.setRotateWithScene(true);

	      mesh.forceRespawn();
	      mesh.warmUp( 60 );
	      
	      mesh.setModelBound(new BoundingBox());
	      mesh.updateModelBound();*/
	      
	      //Space shuttle
	      /*
	       *    mesh = ParticleFactory.buildParticles("particles",300);
	      mesh.setEmissionDirection( new Vector3f( 1f, 0f, 0f ) );
	      mesh.setMaximumAngle( 0.2f );
	      mesh.setMinimumAngle(0f);
	      mesh.setSpeed( 1.0f );
	      mesh.setMinimumLifeTime( 300.0f );
	      mesh.setMaximumLifeTime( 1500.0f );
	      mesh.setStartSize( 1.6f );
	      mesh.setEndSize( 15.0f );
	      mesh.setStartColor( new ColorRGBA( 1.0f, 1.0f, 1.0f, 1.0f ) );
	      mesh.setEndColor( new ColorRGBA( 0.6f, 0.2f, 0.0f, 0.0f ) );
	      mesh.setInitialVelocity( 0.05f );
	      
	      mesh.setGeometry( camBox );
	      mesh.setRotateWithScene(true);

	      mesh.forceRespawn();
	      mesh.warmUp( 60 );
	      
	      mesh.setModelBound(new BoundingBox());
	      mesh.updateModelBound();
	       */
	      
	      //sputter
/*	      mesh = ParticleFactory.buildParticles("particles",15);
	      mesh.setEmissionDirection( new Vector3f( 1f, 0f, 0f ) );
	      mesh.setMaximumAngle( 0.4f );
	      mesh.setMinimumAngle(0.2f);
	      mesh.setSpeed( 1.0f );
	      mesh.setMinimumLifeTime( 300.0f );
	      mesh.setMaximumLifeTime( 900.0f );
	      mesh.setStartSize( 1.6f );
	      mesh.setEndSize( 15.0f );
	      mesh.setStartColor( new ColorRGBA( 1.0f, 1.0f, 1.0f, 1.0f ) );
	      mesh.setEndColor( new ColorRGBA( 0.6f, 0.2f, 0.0f, 0.0f ) );
	      mesh.setInitialVelocity( 0.05f );
	      
	      mesh.setGeometry( camBox );
	      mesh.setRotateWithScene(true);

	      mesh.forceRespawn();
	      mesh.warmUp( 60 );
	      
	      mesh.setModelBound(new BoundingBox());
	      mesh.updateModelBound();*/
	      
	      //plasma
	     /* 
	      mesh = ParticleFactory.buildParticles("particles", 10);
	      mesh.setEmissionDirection( new Vector3f( 1f, 0f, 0f ) );
	      mesh.setMaximumAngle( FastMath.HALF_PI );
	      mesh.setMinimumAngle(0.2f);
	      mesh.setSpeed( 1.0f );
	      mesh.setMinimumLifeTime( 100.0f );
	      mesh.setMaximumLifeTime( 300.0f );
	      mesh.setStartSize( 1f );
	      mesh.setEndSize( 5.0f );
	      mesh.setStartColor( new ColorRGBA( 1.0f, 1.0f, 1.0f, 1.0f ) );
	      mesh.setEndColor( new ColorRGBA( 0.2f, 0.2f, 0.4f, 0.0f ) );
	      mesh.setInitialVelocity( 0.001f );
	      
	      mesh.setGeometry( emitDisc );
	      mesh.setRotateWithScene(true);

	      mesh.forceRespawn();
	      mesh.warmUp( 60 );
	      
	      mesh.setModelBound(new BoundingBox());
	      mesh.updateModelBound();*/
	      
	      /*//nova
	       *      mesh = ParticleFactory.buildParticles("particles", 30);
	      mesh.setEmissionDirection( new Vector3f( 1f, 0f, 0f ) );
	      mesh.setMaximumAngle( 0.1f );
	      mesh.setMinimumAngle(0f);
	      mesh.setSpeed( 1.0f );
	      mesh.setMinimumLifeTime( 300.0f );
	      mesh.setMaximumLifeTime( 500.0f );
	      mesh.setStartSize( 1.6f );
	      mesh.setEndSize( 15.0f );
	      mesh.setStartColor( new ColorRGBA( 1.0f, 1.0f, 1.0f, 1.0f ) );
	      mesh.setEndColor( new ColorRGBA( 0.6f, 0.2f, 0.0f, 0.0f ) );
	      mesh.setInitialVelocity( 0.005f );
	      
	      mesh.setGeometry( camBox );
	      mesh.setRotateWithScene(true);

	      mesh.forceRespawn();
	      mesh.warmUp( 60 );
	      
	      mesh.setModelBound(new BoundingBox());
	      mesh.updateModelBound();
	       */
	      
	 /*     
	      mesh = ParticleFactory.buildParticles("particles", 1);
	      mesh.setEmissionDirection( new Vector3f( 1f, 0f, 0f ) );
	      mesh.setMaximumAngle( 0f);
	      mesh.setMinimumAngle(0f);
	      mesh.setSpeed(1.0f );
	      mesh.setMinimumLifeTime( 1 );
	      mesh.setMaximumLifeTime( 1 );
	      mesh.setStartSize( 15f );
	      mesh.setEndSize( 15.0f );
	      mesh.setStartColor( new ColorRGBA( 0.4f, 0.4f, 0.6f, 0.6f ) );
	      mesh.setEndColor( new ColorRGBA( 0.4f, 0.4f, 0.6f, 0.6f ) );
	      mesh.setInitialVelocity( 0f );
	      
	      mesh.setGeometry( emitDisc );
	      mesh.setRotateWithScene(true);*/

	      mesh.forceRespawn();
	      mesh.warmUp( 60 );
	      
	      mesh.setModelBound(new BoundingBox());
	      mesh.updateModelBound();
	      
	      ZBufferState zbuf = display.getRenderer().createZBufferState();
	      zbuf.setWritable( false );
	      zbuf.setEnabled( true );
	      zbuf.setFunction( ZBufferState.CF_LEQUAL );

	      mesh.setRenderState( ts );
	      mesh.setRenderState( as1 );
	      mesh.setRenderState( zbuf );
	      rootNode.attachChild( mesh );
	  }

	}
