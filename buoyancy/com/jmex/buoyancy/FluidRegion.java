/*******************************************************************************
 * Copyright 2008, 2009, 2010 Sam Bayless.
 * 
 *     This file is part of Golems.
 * 
 *     Golems is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     Golems is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with Golems. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.jmex.buoyancy;

/**
 * This class defines a region in space filled with a uniform liquid of a particular density.
 * @author Sam Bayless
 *
 */
public abstract class FluidRegion implements IFluidRegion
{

    /**
     * This defines a region that applies no buoyant forces, and has no bounds
     */
    public static final FluidRegion VACUUM = new FluidRegion(0,Float.MAX_VALUE,0)
    {
        
        public boolean checkBounds(BuoyantObject object)
        {
            return true;
        }        
    };
    
    /**
     * This defines a region that has the density of atmosphere at sea level, and has no bounds
     */
    public static final FluidRegion AIR = new FluidRegion(DENSITY_AIR,Float.MAX_VALUE,VISCOSITY_AIR)
    {
        
        public boolean checkBounds(BuoyantObject object)
        {
            return true;
        }        
    };
    
    /**
     * This defines a region that has the density of water, and has a height of 0.
     */
    public static final FluidRegion WATER = new FluidRegion(DENSITY_WATER,0,VISCOSITY_WATER)
    {
        
        public boolean checkBounds(BuoyantObject object)
        {
            return true;
        }        
    };
    
    
    private float fluidDensity;
    
    /**
     * The height of a fluid is a special type of bound; additional forces are applied here, for example to make objects level out at that height.
     */
    private float fluidHeight;
    
    /**
     * This is used to determine how much friction to apply to objects in the fluid. Measure in CGS units (poise).
     */
    private float fluidViscosity = 0.01f;
    
    private boolean enabled = true;
    
    
	public boolean isEnabled() {
		return enabled;
	}


	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}


	
    public float getFluidDensity()
    {
        return fluidDensity;
    }


    
    public void setFluidDensity( float fluidDensity )
    {
        this.fluidDensity = fluidDensity;
    }



    
    public float getFluidHeight()
    {
        return fluidHeight;
    }


    
    public void setFluidHeight( float fluidHeight )
    {
        this.fluidHeight = fluidHeight;
    }

    public FluidRegion( float fluidDensity, float fluidLevel, float fluidViscocity )
    {
        super();
        this.fluidDensity = fluidDensity;
        this.fluidHeight = fluidLevel;
        this.fluidViscosity = fluidViscocity;
        this.enabled = true;
    }


    
    public abstract boolean checkBounds(BuoyantObject object);


    
    
    public float getFluidViscosity()
    {
        return fluidViscosity;
    }



    
    public void setFluidViscosity( float fluidViscocity )
    {
        this.fluidViscosity = fluidViscocity;
    }

    
    public int compareTo( IFluidRegion o )
    {
       float compareDensity = o.getFluidDensity();
       if (fluidDensity == compareDensity)
           return 0;
       else if (fluidDensity>compareDensity)
           return 1;
       else
           return -1;
    }
}
