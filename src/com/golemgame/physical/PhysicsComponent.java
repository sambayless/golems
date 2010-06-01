package com.golemgame.physical;

import com.golemgame.model.spatial.SpatialModel;
import com.golemgame.mvc.golems.SurfacePropertiesInterpreter.SurfaceType;
import com.golemgame.physical.sound.SoundComponent;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

public interface PhysicsComponent {
	public PhysicsObject getParent();
	public Vector3f getWorldTranslation(Vector3f store);
	public Vector3f getLocalTranslation(Vector3f store);
	public Quaternion getLocalRotation(Quaternion store);
	public SpatialModel getSpatial();
	public SoundComponent getSoundComponent();
	public SurfaceType getSoundSurface();
}
