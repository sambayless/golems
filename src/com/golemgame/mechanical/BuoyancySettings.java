package com.golemgame.mechanical;

import java.io.Serializable;

import com.jmex.buoyancy.IFluidRegion;

public class BuoyancySettings  implements Serializable{

	private static final long serialVersionUID = 1L;

	private boolean enabled = false;
	private float fluidDensity = IFluidRegion.DENSITY_WATER;
	private float fluidViscotiy = IFluidRegion.VISCOSITY_WATER;
	private float fluidHeight = 0;
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public float getFluidDensity() {
		return fluidDensity;
	}
	public void setFluidDensity(float fluidDensity) {
		this.fluidDensity = fluidDensity;
	}
	public float getFluidViscotiy() {
		return fluidViscotiy;
	}
	public void setFluidViscotiy(float fluidViscotiy) {
		this.fluidViscotiy = fluidViscotiy;
	}
	public float getFluidHeight() {
		return fluidHeight;
	}
	public void setFluidHeight(float fluidHeight) {
		this.fluidHeight = fluidHeight;
	}
	
	

	public BuoyancySettings() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
