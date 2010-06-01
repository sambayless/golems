package com.golemgame.states;

/**
 * This class just determines how much memory various attributes of the program should be allowed, given an input 
 * maximum memory.
 * @author Sam
 *
 */
public class MemoryManager {
	public static final long MEGABYTE = 1024*1024;
	
	//Note: there are probably about 96 mB of allocated overhead for the GC, etc.
	
	public static long getMaximumMemory()
	{
		return GeneralSettings.getInstance().getMaxMemory().getValue()* MEGABYTE;
	}
	
	public static long getPhysicsMemory()
	{
		if(getMaximumMemory()/MEGABYTE<= 64)
		{
			return 4;
		}		
		else if(getMaximumMemory()/MEGABYTE<= 128)
		{
			return 16;
		}
		else if(getMaximumMemory()/MEGABYTE<= 512)
			return 32;
		else
			return 128; 
		
		
		//return 16;// getMemory(2);
		//4*(long)Math.pow(2, 25) == 128
		//return 128 * MEGABYTE;//8*(long)Math.pow(2, 25);
	//	return 4*(long)Math.pow(2, 25);
		
	}

	public static long getDirectMemory()
	{
		return Math.max(getMaximumMemory()/MEGABYTE,64);// getMemory(1);
		//return 128 * MEGABYTE;
	}
	
	public static long getHeapMemory()
	{
		//plan for the first 64 megabytes, most memory goes to heap, only 16 to physics and 16 to direct (64 mb min memory requirements then)
		//for the next 64, the same (32 32 64)
		//after that, pour it onto the other two evenly, with only 16 out of every 64 going to heap
		return Math.max(getMaximumMemory()/MEGABYTE,64);// 256;// getMemory(0);

	}
	
	private static long getMemory(int type)
	{
		
		long total = getMaximumMemory()/MEGABYTE;
		long absoluteTotal= total;
		if(total<64)
			total = 64;//minimum requirements
		
		long heap =0;
		long direct =0;
		long physics=0;
		
		total -= 64;
		{
			heap += 28;//leave 4 megs always unassigned for unknown jvm uses
			direct += 16;
			physics += 16;
		}
		long nxt = (long)Math.min(total, 128+64);
		if(nxt>0)
		{
			total-= nxt;
			
			heap += (int) nxt/2;
			direct += (int) nxt/4;
			physics += (int) nxt/4;
		}
		
		//now divvy up the rest
		heap += total / 4;
		direct += (int) total*(24)/(64);
		physics += (int) total*(24)/(64);
	
		if (type == 0)
			return absoluteTotal ;
		if (type == 1)
			return direct;
		else
			return physics;
	}
	
	//public static int get
	
}
