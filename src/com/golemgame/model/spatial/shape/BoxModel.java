package com.golemgame.model.spatial.shape;

import java.nio.FloatBuffer;

import com.golemgame.model.spatial.SpatialModelImpl;
import com.golemgame.model.texture.TextureTypeKey.TextureShape;
import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.util.geom.BufferUtils;

public class BoxModel extends SpatialModelImpl{
	private static final long serialVersionUID = 1L;
	private static final float INIT_SIZE = 1f;

	public BoxModel() {
		super();
	}

	public BoxModel(boolean registerSpatial) {
		super(registerSpatial);
	}
	
	protected Spatial buildSpatial() {

		Spatial	boxModel  = new Box("box", new Vector3f(), INIT_SIZE/2f,INIT_SIZE/2f,INIT_SIZE/2f);		 

	//	boxModel.getLocalTranslation().zero();
		//boxModel.getLocalRotation().loadIdentity();
	//	boxModel.getLocalScale().set(1,1,1);
		boxModel.setModelBound(new BoundingBox());
		boxModel.updateModelBound();
		
		return boxModel;
	}

	@Override
	public void setTextureShape(TextureShape shape, int layer) {
		if(shape==getTextureShape(layer))
			return;
		super.setTextureShape(shape, layer);
		
		if(shape == TextureShape.Box)
		{
		     FloatBuffer buf = ((TriMesh)getSpatial()).getTextureBuffer(0, layer);
				if(buf==null)
				{
					buf = BufferUtils.clone(((TriMesh)getSpatial()).getTextureBuffer(0,0));
					
					 ((TriMesh)getSpatial()).setTextureBuffer(0, buf , layer);
				}
		        buf.rewind();
		        for (int i = 0; i < 6; i++) {
		            float top = i / 6f;
		            float bottom = (i + 1) / 6f;
		            float[] tex = new float[] { 1, bottom, 0, bottom, 0, top, 1, top };
		            buf.put(tex);
		        }
		        buf.rewind();
		        ((TriMesh)getSpatial()).setTextureBuffer(0, buf,layer);
		}else{
					FloatBuffer buf = ((TriMesh)getSpatial()).getTextureBuffer(0, layer);
					if(buf==null)
					{
						buf = BufferUtils.clone(((TriMesh)getSpatial()).getTextureBuffer(0,0));
						
						 ((TriMesh)getSpatial()).setTextureBuffer(0, buf , layer);
					}
					buf.rewind();
					for (int i = 0; i < 6; i++) {
					    buf.put(1).put(0);
						buf.put(0).put(0);
						buf.put(0).put(1);
						buf.put(1).put(1);
					}
					buf.rewind();
		}
	}

}