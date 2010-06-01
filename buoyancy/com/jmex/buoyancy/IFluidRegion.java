package com.jmex.buoyancy;

/**
 * <code>IFluidRegion</code> defines a volume or open ended space which contains a fluid of uniform density.
 * A region (optionally) defines the height of the fluid. This height is always relative to the direction of gravity, 
 * and the fluid is assumed to fill up the volume entirely to that height, forming a flat surface.
 * A region also performs bounds checking on <code>BuoyantObject</code>s to determine whether they are within the fluid or not.
 * 
 * @author Sam Bayless
 *
 */
public interface IFluidRegion extends Comparable<IFluidRegion> {
	//these are some common viscosities, measure in cgs units (poise).
	public static final float VISCOSITY_AIR = 0.0000176f;
	public static final float VISCOSITY_WATER = 0.01002f;//at 20 degrees
	public static final float VISCOSITY_CORN_SYRUP = 13.806f;
	
	
    /*
     * A list of useful densities in g/cm^3;
     */
    
	/**
	 * The density of water at sea level (in g/cm^3)
	 */
	public static final float DENSITY_WATER = 1.00f;  

	/**
	 * The density of air at sea level (20 degrees C)
	 */
	public static final float DENSITY_AIR =  0.001292f;
	
	//Common Materials
	public static final float DENSITY_BENZENE  =   0.900f;
	public static final float DENSITY_BLOOD =  1.600f;
	public static final float DENSITY_BRASS =  8.600f;
	public static final float DENSITY_CONCRETE =   2f;
	public static final float DENSITY_ETHANAOL  =   0.810f;
	public static final float DENSITY_GLYCERIN  =  1.260f;
	public static final float DENSITY_STYROFOAM = 0.1f;
	public static final float DENSITY_ICE  =   0.92f;
	public static final float DENSITY_SEA_WATER   =   1.030f;

	//Gases
	public static final float DENSITY_HYDROGEN  =  0.00008988f;
	public static final float DENSITY_HELIUM    = 0.0001785f;
	public static final float DENSITY_OXYGEN = 0.001429f  ;
		    
	//Metals
	public static final float DENSITY_ALUMINUM  = 2.70f;   
	public static final float DENSITY_ZINC  =  7.13f  ;	
	public static final float DENSITY_IRON =    07.8740f ; 
	public static final float DENSITY_COPPER =  8.96f  ;
	public static final float DENSITY_SILVER =  10.49f  ;
	public static final float DENSITY_LEAD =    11.36f  ;
	public static final float DENSITY_MERCURY =  13.55f  ;
	public static final float DENSITY_GOLD = 19.32f;
	

	
	/**
	 * The density of a vacuum (in g/cm^3)
	 */
	public static final float VACUUM = 0f;
	/**
	 * Set whether this fluid region should be applied to buoyant objects.
	 * @param enabled Whether or not to attempt to apply this region during the update step.
	 */
	public abstract void setEnabled(boolean enabled);
	
	/**
	 * If the region is not enabled, then it will be skipped during the <code>Buoyancy</code> update step.
	 * @return
	 */
	public abstract boolean isEnabled();
	
	/**
	 * Get the density of this fluid (in g/cm^3).
	 * @return The density of this fluid (in g/cm^3).
	 */
	public abstract float getFluidDensity();

	/**
	 * Set the density of the fluid in cgs units.
	 * @param density Density of the medium in (in g/cm^3)
	 */
	public abstract void setFluidDensity(float fluidDensity);

	/**
	 * Get the height of the fluid, relative to the direction of gravity. The fluid is assumed to form a flat surface at that height.
	 * @return The height of the fluid.
	 */
	public abstract float getFluidHeight();

	/**
	 * Set the height (in coordinates relative to gravity) of the fluid, or Float.MAX_VALUE for no height (meaning that the fluid extends upwards indefinitely.
	 * @param fluidHeight
	 */
	public abstract void setFluidHeight(float fluidHeight);

	/**
	 * Apply any type of bounds check to determine if the object is in this fluid or not.
	 * For example, if the fluid only exists in a container, this check could determine whether or not the object is within that container.
	 * Note that if this region defines a fluid height, it is not necessary (nor recommended) to check for that upper bound here.
	 * @param object The buoyant object to test.
	 * @return True if the Buoyancy model should attempt to apply forces to this object. Note that the buoyancy model will also test to see if the object is lower than the fluid height of this region,
	 * so it is not necessary to do so in this method.
	 */
	public abstract boolean checkBounds(BuoyantObject object);

	/**
	 * The viscosity of this fluid. This (and the fluid density) determine the amount of friction applied to objects in the liquid.
	 * @return The viscosity of the fluid (in poise).
	 */
	public abstract float getFluidViscosity();

	/**
	 * Set the viscosity of the fluid. Viscosity is measured in poise, where P = g/(cm*s).
	 * @param fluidViscosity
	 */
	public abstract void setFluidViscosity(float fluidViscosity);
}
