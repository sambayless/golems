package com.golemgame.states.camera;

import com.golemgame.constructor.Updatable;
import com.golemgame.constructor.UpdateManager;
import com.golemgame.constructor.UpdateManager.Stream;
import com.golemgame.states.StateManager;
import com.jme.light.DirectionalLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;

public class FadingCentroidDisplay implements CentroidDisplay{
	
	private Node centroidNode;
	private DirectionalLight light;
	private Updatable fadeUpdate;
	private float updateTime = 0;
	private float beginFade=0;
	private boolean displaying = false;
	private ColorRGBA ambient;   	
	private ColorRGBA diffuse ;
	private ColorRGBA specular ;
	private MaterialState sphereMaterial;
	public FadingCentroidDisplay() {
		super();
		centroidNode = new Node();
		final Sphere sphere = new Sphere("",32,32,.25f);
	

	
		
		centroidNode.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
        CullState cs = DisplaySystem.getDisplaySystem().getRenderer().createCullState();
        cs.setCullMode(CullState.CS_BACK);
        centroidNode.setRenderState(cs);
       // centroidNode.setLightCombineMode(LightState.REPLACE);
 
        light = new DirectionalLight();
        light.setDiffuse( new ColorRGBA(1f,1f,1f, 1.0f  ) );
        light.setAmbient( new ColorRGBA(1f,1f,1f, 1.0f ) );
        light.setSpecular( new ColorRGBA(1f,1f,1f, 1.0f ) );
    
      
        light.setEnabled( true );


        LightState lightState = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
        lightState.setEnabled( true );
        lightState.attach(light);
     
        lightState.setTwoSidedLighting(true);
        centroidNode.setRenderState( lightState );
        centroidNode.updateWorldBound();     

		ZBufferState buf = DisplaySystem.getDisplaySystem().getRenderer().createZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.CF_LEQUAL);
       centroidNode.setLightCombineMode(LightState.REPLACE);
       centroidNode.setRenderState(buf);
    
        centroidNode.updateRenderState();
    	centroidNode.attachChild(sphere);
     
    	
    	AlphaState semiTransparentAlpha = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
		semiTransparentAlpha.setBlendEnabled(true);	
		semiTransparentAlpha.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		semiTransparentAlpha.setDstFunction(AlphaState.SB_ONE_MINUS_SRC_ALPHA);	
		semiTransparentAlpha.setTestEnabled(true);			
		semiTransparentAlpha.setTestFunction(AlphaState.TF_GREATER);		
    	centroidNode.setRenderState(semiTransparentAlpha);
    	 ambient = new ColorRGBA(0.75f,0,0,.25f);    	
    	 diffuse = new ColorRGBA(1,1,1,0.5f);
    	 specular = new ColorRGBA(1,1,1,0.5f);
    	 sphereMaterial = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
	     sphereMaterial.setAmbient(ambient);
	     sphereMaterial.setDiffuse(diffuse);
	     sphereMaterial.setSpecular(specular);
	   //  sphereMaterial.setEmissive(specular);
	     sphereMaterial.setMaterialFace(MaterialState.MF_FRONT_AND_BACK);
	     sphereMaterial.setShininess(128f);
	 
	     sphereMaterial.setColorMaterial(MaterialState.CM_NONE );	
	 	
	     sphere.setRenderState(sphereMaterial);  
	     sphere.updateRenderState();
	     centroidNode.updateGeometricState(0f,true);   
	     
	     fadeUpdate = new Updatable()
	     {
	    	 final float full = 0.25f;
	    	 final float max = 0.25f;
	    	 final float totalTime = 0.5f;
			public void update(float time) {
				updateTime+= time;
				beginFade += time;
		
				if(beginFade>totalTime)
				{
					sphere.setCullMode(SceneElement.CULL_ALWAYS);
					displaying = false;
					UpdateManager.getInstance().remove(this, Stream.GL_RENDER);
				}else{
					sphere.setCullMode(SceneElement.CULL_NEVER);
					
				//	if(updateTime<full)
					{
					//	diffuse.a =( updateTime/full)*max;
					} if (beginFade>full){
						diffuse.a =max - (beginFade-full)/(totalTime - full)*max;
					}else{
						diffuse.a = max;
					}
					sphereMaterial.setDiffuse(diffuse);
					
					
					
				}
				
			}
	    	 
	     };
	     
	     
			UpdateManager.getInstance().add(fadeUpdate, Stream.GL_RENDER);
	}

	public void display() {
		if(!displaying)
		{
			beginFade =0f;
			displaying=true;
			updateTime = 0;
			UpdateManager.getInstance().add(fadeUpdate, Stream.GL_RENDER);
		}else{
			beginFade =0f;
		}
	}
	
	public void update()
	{
		
		if(StateManager.getCameraManager()!=null)
		{
			Vector3f dir = StateManager.getCameraManager().getCameraNormal();
		   //  light.setDiffuse( new ColorRGBA(1f,0f,0f, 0.15f  ) );
		      /*  light.setAmbient( new ColorRGBA(1f,1f,1f, 0.15f ) );
		        light.setSpecular( new ColorRGBA(0f,0f,1f, 0.15f ) );
		    	ambient.set(1f, 1f, 1f, 0f);
		    	specular.set(1f, 1f, 1f, 0f);
			     sphereMaterial.setAmbient(ambient);
			  //   sphereMaterial.setDiffuse(diffuse);
			     sphereMaterial.setSpecular(specular);*/
			light.setDirection(dir.mult(-1f));
		}
		
	}

	public Node getCentroidNode() {
		return centroidNode;
	}
	
}
