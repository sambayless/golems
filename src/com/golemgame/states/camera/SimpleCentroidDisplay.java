package com.golemgame.states.camera;

import com.golemgame.states.StateManager;
import com.jme.bounding.BoundingSphere;
import com.jme.light.DirectionalLight;
import com.jme.light.PointLight;
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

public class SimpleCentroidDisplay implements CentroidDisplay{
	
	private Node centroidNode;
	private DirectionalLight light;
	public SimpleCentroidDisplay() {
		super();
		centroidNode = new Node();
		Sphere sphere = new Sphere("",32,32,1f);
	
	
	
		
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
    	ColorRGBA ambient = new ColorRGBA(0,174f/255f,242f/255f,.2f);
    	ColorRGBA diffuse = new ColorRGBA(1,1f,1,0.5f);
    	ColorRGBA specular = new ColorRGBA(0,174f/255f,242f/255f,.2f);
    	MaterialState buttonMaterial = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
	     buttonMaterial.setAmbient(ambient);
	     buttonMaterial.setDiffuse(diffuse);
	     buttonMaterial.setSpecular(specular);
	     buttonMaterial.setMaterialFace(MaterialState.MF_FRONT_AND_BACK);
	     buttonMaterial.setShininess(0);
	     buttonMaterial.setColorMaterial(MaterialState.CM_NONE );	
	 	
	     sphere.setRenderState(buttonMaterial);  
	     sphere.updateRenderState();
	     centroidNode.updateGeometricState(0f,true);   
	}

	public void display() {
		
	}
	
	public void update()
	{
		if(StateManager.getCameraManager()!=null)
		{
			Vector3f dir = StateManager.getCameraManager().getCameraNormal();
		
			light.setDirection(dir.mult(-1f));
		}
		
	}

	public Node getCentroidNode() {
		return centroidNode;
	}
	
}
