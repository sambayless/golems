package com.golemgame.mvc.golems.transform;

import com.golemgame.mvc.PropertyStore;

/**
 * This class applies a particular transformation to a property store.
 * @author Sam
 *
 */
public abstract class Transformation {
	/**
	 * This is the app version at which this transformation was added to golems
	 */
	private final String versionIntroduced;
	
	private int majorVersion = -1;
	private int minorVersion = -1;
	private int revision = -1;
	
	/**
	 * -1 if unknown, or before 0.54
	 * @return
	 */
	public int getMajorVersion() {
		return majorVersion;
	}

	/**
	 * -1 if unknown, or before 0.54
	 * @return
	 */
	public int getMinorVersion() {
		return minorVersion;
	}

	/**
	 * -1 if unknown, or before 0.54
	 * @return
	 */
	public int getRevision() {
		return revision;
	}

	public void setVersion(int major, int minor, int revision)
	{
		this.majorVersion = major;
		this.minorVersion = minor;
		this.revision = revision;
	}
	
	/**
	 * 
	 * @param versionIntroduced  This is the app version at which this transformation was added to golems
	 */
	public Transformation(String versionIntroduced) {
		super();
		this.versionIntroduced = versionIntroduced;
	}


	public abstract void apply(PropertyStore store);


	@Override
	public String toString() {
		return "Transformation (" + versionIntroduced + ")";
	}
	
	
}
