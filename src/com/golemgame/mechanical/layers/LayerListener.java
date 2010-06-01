package com.golemgame.mechanical.layers;

public interface LayerListener {
	public void layerState(boolean visible, boolean locked, boolean active);

	public void refreshLayer();

}
