package com.golemgame.structural.structures;

import com.golemgame.model.Model;
import com.golemgame.model.spatial.shape.ConeFacade;
import com.golemgame.model.spatial.shape.ConeModel;
import com.golemgame.model.spatial.shape.CylinderModel;
import com.golemgame.model.texture.TextureTypeKey.TextureShape;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.ConeInterpreter;

public class ConeStructure extends CylinderStructure {
	private static final long serialVersionUID = 1L;
	private static final float RADIUS = 0.5f;
	private static final float HEIGHT = 1f;
	
	private ConeInterpreter interpreter;
	public ConeStructure(PropertyStore store) {
		super(store);
		this.interpreter = new ConeInterpreter(store);
	}
	@Override
	protected TextureShape getPrefferedShape() {
		return TextureShape.Cone;
	}

	protected CylinderModel buildModel() {
		return new ConeModel(true);
	}

	protected Model buildFacade() {
		return new ConeFacade();
	}

	

}
