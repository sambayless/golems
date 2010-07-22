package com.golemgame.mvc.golems;


public final class Golems {
	
	public final static String version = "0.56.1";

	public static String getVersion() {
		return version;
	}
	private Golems()
	{		
	}
	

	public static int getRevision(String appValue) {
		try{
			return Integer.valueOf(appValue.substring(appValue.lastIndexOf(".")+1));
		}catch(Exception e)
		{
			System.err.println(e);
			return -1;
		}
	}

	public static int getMinor(String appValue) {
		try{
			int pos1 = appValue.indexOf('.');
			int pos2 = appValue.lastIndexOf('.');
			
	
			return  Integer.valueOf(appValue.substring(pos1+1, pos2));
		}catch(Exception e)
		{
			System.err.println(e);
			return -1;
		}
	}
	public static int getRevision() {
		return getRevision(getVersion());
	}
	public static int getMinor() {
		return getMinor(getVersion());
	}
	public static int getMajor() {
		return getMajor(getVersion());
	}
	public static int getMajor(String appValue) {
		try{
			int pos1 = appValue.indexOf('.');
		
		
	
			return  Integer.valueOf(appValue.substring(0,pos1));
		}catch(Exception e)
		{
			System.err.println(e);
			return -1;
		}
	}
	
	/**
	 * Return -1 if the version is earlier than the current version, 0 if they are the same, and 1 if it is more recent than the current version.
	 * @param version
	 * @return
	 */
	public static int compareVersion(String version)
	{
		int major = getMajor(version);
		int minor = getMinor(version);
		int revision = getRevision(version);
		
		int thisMajor = getMajor(getVersion());
		int thisMinor = getMinor(getVersion());
		int thisRevision = getRevision(getVersion());
		
		if (major<thisMajor )
			return -1;
		if (major>thisMajor)
			return 1;
		
		if (minor<thisMinor )
			return -1;
		if (minor>thisMinor)
			return 1;
		
		if (revision<thisRevision )
			return -1;
		if (revision>thisRevision)
			return 1;
		
		return 0;
	}

}
