package com.golemgame.states.camera.skybox;

import com.jme.scene.Skybox;

/**
 * This class encapsulates an abstract representation of a sky box decoration
 * @author Sam
 *
 */
public class SkyBoxData {
	private static final SkyBoxData nullbox = new SkyBoxData("(None)");
	private final String name;
	@Override
	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public SkyBoxData(String name) {
		super();
		this.name = name;
	}

	public static enum Face
	{
		 NORTH(Skybox.NORTH),EAST(Skybox.EAST),SOUTH(Skybox.SOUTH),WEST(Skybox.WEST), UP(Skybox.UP), DOWN(Skybox.DOWN);
		private final int skyboxenum;

		private Face(int skyboxenum) {
			this.skyboxenum = skyboxenum;
		}

		public int getSkyboxEnum() {
			return skyboxenum;
		}
		
	}
	
	public static SkyBoxData getNullBox()
	{
		return nullbox;
	}
	
	/**
	 * Rebuild this sky box for a given height and width
	 * @param width
	 * @param height
	 */
	public void build(int width, int height)
	{
		
	}
	
	public void apply(Face face,Skybox skybox, int unit)
	{
		//skybox.getSide(face.getSkyboxEnum()).getre
		skybox.removeTexture(face.getSkyboxEnum(),unit);
	}
	
}
