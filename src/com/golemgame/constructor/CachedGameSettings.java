package com.golemgame.constructor;

import java.util.prefs.Preferences;

import com.jme.system.GameSettings;
import com.jme.system.PreferencesGameSettings;

/**
 * This class wraps a game settings class.
 * It caches the setting's so that you don't refer to the system registry (or the equivalent on Mac/UNIX) each time you read a value.
 * When changes are made, those changes write through to the backing PreferencesGameSettings.
 * @author Sam Bayless
 *
 */
public class CachedGameSettings implements GameSettings {	
	private final  GameSettings settings;
	private int alphaBits;
	private int depth;
	private int depthBits;
	private int framerate;
	private int frequency;
	private int height;
	private String renderer;
	private int samples;
	private int stencilBits;
	private int width;
	private boolean fullScreen;
	private boolean music;
	private boolean sfx;
	private boolean vsync;

	public CachedGameSettings(Preferences node) {
		  this( new PreferencesGameSettings(node));
	}
	
	public CachedGameSettings(GameSettings gameSettings) {
		  settings = gameSettings;
		  refresh();	
	}

	public void refresh() {
		this.alphaBits = settings.getAlphaBits();
		this.depth = settings.getDepth();
		this.depthBits = settings.getDepthBits();
		this.framerate = settings.getFramerate();
		this.frequency = settings.getFrequency();
		this.height = settings.getHeight();
		this.renderer = settings.getRenderer();
		this.samples = settings.getSamples();
		this.stencilBits = settings.getStencilBits();
		this.width = settings.getWidth();
		this.fullScreen = settings.isFullscreen();
		this.music = settings.isMusic();
		this.sfx = settings.isSFX();
		this.vsync = settings.isVerticalSync();
	}

	
	public void clear() throws Exception {
		settings.clear();
		refresh();	
	}

	
	public String get(String name, String defaultValue) {
		//these ones has to fall through to the preference settings to work properly
		return settings.get(name, defaultValue);
	}

	
	public int getAlphaBits() {
		return alphaBits;
	
	}

	
	public boolean getBoolean(String name, boolean defaultValue) {
		return settings.getBoolean(name, defaultValue);
	}

	
	public byte[] getByteArray(String name, byte[] bytes) {
		return settings.getByteArray(name, bytes);
	}

	
	public int getDepth() {
		return depth;
	}

	
	public int getDepthBits() {
		return depthBits;
	}

	
	public double getDouble(String name, double defaultValue) {
		return settings.getDouble(name, defaultValue);
	}

	
	public float getFloat(String name, float defaultValue) {
		return settings.getFloat(name, defaultValue);
	}

	
	public int getFramerate() {
		return framerate;
	}

	
	public int getFrequency() {
		return frequency;
	}

	
	public int getHeight() {
		return height;
	}

	
	public int getInt(String name, int defaultValue) {
		return settings.getInt(name, defaultValue);
	}

	
	public long getLong(String name, long defaultValue) {
		return settings.getLong(name, defaultValue);
	}

	
	public Object getObject(String name, Object obj) {
		return settings.getObject(name, obj);
	}

	
	public String getRenderer() {
		return renderer;
	}

	
	public int getSamples() {
		return samples;
	}

	
	public int getStencilBits() {
		return stencilBits;
	}

	
	public int getWidth() {
		return width;
	}

	
	public boolean isFullscreen() {
		return fullScreen;
	}

	
	public boolean isMusic() {
		return music;
	}

	
	public boolean isSFX() {
		return sfx;
	}

	
	public boolean isVerticalSync() {
		return vsync;
	}

	
	public void set(String name, String value) {
		settings.set(name, value);
		
	}

	
	public void setAlphaBits(int alphaBits) {
		settings.setAlphaBits(alphaBits);
		this.alphaBits = alphaBits;
	}

	
	public void setBoolean(String name, boolean value) {
		settings.setBoolean(name, value);
		
	}

	
	public void setByteArray(String name, byte[] bytes) {
		settings.setByteArray(name, bytes);
		
	}

	
	public void setDepth(int depth) {
		settings.setDepth(depth);
		this.depth = depth;
	}

	
	public void setDepthBits(int depthBits) {
		settings.setDepthBits(depthBits);
		this.depthBits = depthBits;
	}

	
	public void setDouble(String name, double value) {
		settings.setDouble(name, value);

	}

	
	public void setFloat(String name, float value) {
		settings.setFloat(name, value);

	}

	
	public void setFramerate(int framerate) {
		settings.setFramerate(framerate);
		this.framerate = framerate;
	}

	
	public void setFrequency(int frequency) {
		settings.setFrequency(frequency);
		this.frequency = frequency;
	}

	
	public void setFullscreen(boolean fullscreen) {
		settings.setFullscreen(fullscreen);
		this.fullScreen = fullscreen;
	}

	
	public void setHeight(int height) {
		settings.setHeight(height);
		this.height = height;
	}

	
	public void setInt(String name, int value) {
		settings.setInt(name, value);

	}

	
	public void setLong(String name, long value) {
		settings.setLong(name, value);

	}

	
	public void setMusic(boolean musicEnabled) {
		settings.setMusic(musicEnabled);
		this.music = musicEnabled;
		
	}

	
	public void setObject(String name, Object obj) {
		settings.setObject(name, obj);

	}

	
	public void setRenderer(String renderer) {
		settings.setRenderer(renderer);
		this.renderer = renderer;
	}

	
	public void setSFX(boolean sfxEnabled) {
		settings.setSFX(sfxEnabled);
		this.sfx = sfxEnabled;
	}

	
	public void setSamples(int samples) {
		settings.setSamples(samples);
		this.samples = samples;
	}

	
	public void setStencilBits(int stencilBits) {
		settings.setStencilBits(stencilBits);
		this.stencilBits = stencilBits;
	}

	
	public void setVerticalSync(boolean vsync) {
		settings.setVerticalSync(vsync);
		this.vsync = vsync;
	}

	
	public void setWidth(int width) {
		settings.setWidth(width);
		this.width = width;
	}

}
