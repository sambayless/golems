package com.golemgame.model.quality;

public interface ModelQualityDelegate {
	/**
	 * Set the quality of the model to some value on a scale, with 0 being highest possible quality.
	 * Quality decreases as the values decrease below zero.
	 * @param quality
	 * @return TODO
	 */
	public boolean setQuality(int quality);
}
